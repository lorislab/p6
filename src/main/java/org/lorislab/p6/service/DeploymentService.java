/*
 * Copyright 2018 lorislab.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.jee.jpa.exception.ConstraintException;
import org.lorislab.p6.flow.json.JsonProcessFlowService;
import org.lorislab.p6.flow.model.ProcessFlow;
import org.lorislab.p6.config.ConfigService;
import org.lorislab.p6.jpa.model.ProcessContent;
import org.lorislab.p6.jpa.model.ProcessDefinition;
import org.lorislab.p6.jpa.model.ProcessDeployment;
import org.lorislab.p6.jpa.service.ProcessDefinitionService;
import org.lorislab.p6.jpa.service.ProcessDeploymentService;
import org.lorislab.p6.model.RuntimeProcess;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.*;
import java.nio.charset.StandardCharsets;

@Slf4j
@MessageDriven(name = "DeploymentService",
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/" + ConfigService.QUEUE_DEPLOY),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
        }
)
public class DeploymentService implements MessageListener {

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    @EJB
    private ProcessDeploymentService processDeploymentService;

    @EJB
    private ProcessDefinitionService processDefinitionService;

    @EJB
    private RuntimeProcessService runtimeProcessService;

    @Override
    public void onMessage(Message message) {
        try {
            int retry = 0;
            if (message.propertyExists(ConfigService.JMS_RETRY_COUNT) ) {
                retry = message.getIntProperty(ConfigService.JMS_RETRY_COUNT);
            }

            String application = message.getStringProperty(ConfigService.MSG_APP_NAME);
            String module = message.getStringProperty(ConfigService.MSG_MODULE_NAME);
            log.info("Start deployment {} - {}", application, module);
            String data = message.getBody(String.class);

            ProcessFlow flow = JsonProcessFlowService.loadProcessFlow(data);
            String processId = flow.getProcessId();
            String processVersion = flow.getProcessVersion();

            ProcessDeployment deployment = processDeploymentService.findByProcessId(processId);
            if (deployment != null && processVersion.equals(deployment.getProcessVersion())) {
                log.info("Process definition for the process ID '{}' and the version '{}' is deployed.", processId, processVersion);
                return;
            }

            ProcessDefinition processDefinition= new ProcessDefinition();
            processDefinition.setApplication(application);
            processDefinition.setModule(module);
            processDefinition.setProcessId(processId);
            processDefinition.setProcessVersion(processVersion);
            ProcessContent content = new ProcessContent();
            content.setData(data.getBytes(StandardCharsets.UTF_8));
            processDefinition.setContent(content);
            content.setProcessDefinition(processDefinition);

            boolean updateDeployment = false;
            if (deployment == null) {
                deployment = new ProcessDeployment();
                deployment.setProcessDefinitionGuid(processDefinition.getGuid());
                deployment.setProcessId(processDefinition.getProcessId());
                deployment.setProcessVersion(processDefinition.getProcessVersion());
                updateDeployment = true;
            } else {
                // check the version.
                if (versionUpdateNeeded(processVersion, deployment.getProcessVersion())) {
                    deployment.setProcessVersion(processVersion);
                    updateDeployment = true;
                }
            }

            try {

                // create new process definition and content
                processDefinitionService.create(processDefinition);

                // saveProcessFlow or update the deployment information
                if (updateDeployment) {
                    if (deployment.isPersisted()) {
                        processDeploymentService.update(deployment);
                    } else {
                        processDeploymentService.create(deployment);
                    }

                    RuntimeProcess process = new RuntimeProcess(processDefinition, flow);
                    runtimeProcessService.addRuntimeProcess(process);
                }

            } catch (ConstraintException ce) {
                log.error("Error executeGateway the deployment becouse of constraints: {}. Retry {}", ce.getConstraints(), retry);
                throw new RuntimeException("Error executeGateway the deployment becouse of constraints. Start the retry " + retry);
            } catch (Exception ex) {
                log.error("Process eployment retry " + retry + " error: " + ex.getMessage(), ex);
                throw new RuntimeException("Deployment error. Start the retry " + retry);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Compare two version strings. Return true only if the newVersion is greater then the stored one so a version update is needed
     * @param newVersion new deployed version
     * @param storedVersion stored version in database
     * @return true only if the newVersion is greater then the stored one
     */
    private boolean versionUpdateNeeded(String newVersion, String storedVersion) {

        String ver1 = newVersion;
        int ver1_tmp = newVersion.indexOf("-SNAPSHOT");
        if (ver1_tmp != -1) {
            ver1 = newVersion.substring(0, ver1_tmp);
        }

        String ver2 = storedVersion;
        boolean snapshot = false;
        int ver2_tmp = storedVersion.indexOf("-SNAPSHOT");
        if (ver2_tmp != -1) {
            ver2 = storedVersion.substring(0, ver2_tmp);
            snapshot = true;
        }

        String[] vals1 = ver1.split("\\.");
        String[] vals2 = ver2.split("\\.");

        int i = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }

        // compare first non-equal ordinal number
        if (i < vals1.length && i < vals2.length) {
            int diff = convertVersionNumberToIntAndCompare(vals1[i],vals2[i]);
            return (diff > 0);
        } else {
            // the strings are equal or one string is a substring of the other
            // or snapshot
            // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
            if (snapshot) {
                return true;
            }
            return (vals1.length > vals2.length);
        }
    }

    /**
     * Return true if the newVersion is greater then stored version.
     * @param newVersion new version
     * @param storedVersion stored version from DB
     * @return true if the newVersion is greater then stored version.
     */
    private int convertVersionNumberToIntAndCompare(String newVersion, String storedVersion) {
        try {
            Integer uploaded = Integer.valueOf(newVersion);
            Integer stored = Integer.valueOf(storedVersion);
            return uploaded.compareTo(stored);
        } catch (Exception e) {
            return newVersion.compareTo(storedVersion);
        }
    }
}
