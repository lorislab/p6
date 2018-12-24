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
import org.lorislab.p6.util.DeploymentVersionUtil;

import javax.ejb.*;
import javax.jms.*;
import java.nio.charset.StandardCharsets;

@Slf4j
@MessageDriven(name = "DeploymentExecutorService",
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/" + ConfigService.QUEUE_DEPLOY),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
        }
)
public class DeploymentExecutorService implements MessageListener {

    @EJB
    private ProcessDeploymentService processDeploymentService;

    @EJB
    private ProcessDefinitionService processDefinitionService;

    @EJB
    private CommandService commandService;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
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
                if (DeploymentVersionUtil.versionUpdateNeeded(processVersion, deployment.getProcessVersion())) {
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
                    commandService.cmdDeploy(deployment);
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

}
