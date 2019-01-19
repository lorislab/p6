/*
 * Copyright 2019 lorislab.org.
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
import org.lorislab.p6.config.ConfigService;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.jpa.model.ProcessDeployment;
import org.lorislab.p6.jpa.model.ProcessInstance;
import org.lorislab.p6.jpa.model.ProcessToken;
import org.lorislab.p6.jpa.model.enums.ProcessInstanceStatus;
import org.lorislab.p6.jpa.model.enums.ProcessTokenStatus;
import org.lorislab.p6.jpa.service.ProcessDeploymentService;
import org.lorislab.p6.jpa.service.ProcessInstanceService;
import org.lorislab.p6.runtime.RuntimeProcess;
import org.lorislab.p6.runtime.RuntimeProcessService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CommandService {

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    @EJB
    private ProcessDeploymentService processDeploymentService;

    @EJB
    private ProcessInstanceService processInstanceService;

    @EJB
    private RuntimeProcessService runtimeProcessService;

    @EJB
    private TokenService tokenService;

    public String startProcess(String processId, String processInstanceId, Map<String, Object> data) throws Exception {
        String tmp = ServerJsonService.toString(data);
        return startProcess(processId, processInstanceId, tmp);
    }

    public String startProcess(String processId, String processInstanceId, String data) throws Exception {

        ProcessDeployment deployment = processDeploymentService.findByProcessId(processId);
        if (deployment == null) {
            log.error("No process found in the deployment for the process id: {}", processId);
            return null;
        }

        String result = null;
        RuntimeProcess process = runtimeProcessService.getRuntimeProcess(deployment.getProcessId(), deployment.getProcessVersion());
        if (process != null) {
            List<Node> nodes = process.getStart();
            if (nodes != null && !nodes.isEmpty()) {

                // create process instance
                ProcessInstance instance = new ProcessInstance();
                if (processInstanceId != null && !processInstanceId.isBlank()) {
                    instance.setGuid(processInstanceId);
                }
                instance.setStatus(ProcessInstanceStatus.IN_EXECUTION);
                instance.setProcessId(process.getDefinition().getProcessId());
                instance.setProcessDefinitionGuid(process.getDefinition().getGuid());
                instance.setProcessVersion(process.getDefinition().getProcessVersion());

                // create start tokens
                List<ProcessToken> tokens = new ArrayList<>(nodes.size());
                for (Node node : nodes) {
                    // create token
                    ProcessToken token = new ProcessToken();
                    token.setNodeName(node.getName());
                    token.setStartNodeName(node.getName());
                    token.setStatus(ProcessTokenStatus.IN_EXECUTION);
                    token.setProcessInstance(instance);
                    instance.getTokens().add(token);
                    token = ServerJsonService.mergeData(token, data);
                    tokens.add(token);
                }

                // send token message
                tokenService.sendTokenMessages(instance, tokens);

                // saveProcessFlow the process instance
                instance = processInstanceService.create(instance);
                result = instance.getGuid();

            } else {
                log.error("No start events devfined for the process id: {}", process.getDefinition().getProcessId());
            }
            log.info("Starting the process {}. Process instance id: {}", processId, processInstanceId);
        } else {
            log.error("No runtime process found for the process id: {}", processId);
        }

        return result;
    }

    public void cmdStart(List<ProcessDeployment> deployments) throws Exception {
        if (deployments != null && !deployments.isEmpty()) {
            Queue cmd = context.createQueue(ConfigService.QUEUE_CMD);
            JMSProducer producer = context.createProducer();
            for (int i = 0; i < deployments.size(); i++) {
                ProcessDeployment deployment = deployments.get(i);
                Message msg = createStartMessage(deployment);
                producer.send(cmd, msg);
            }
        }
    }

    public void cmdStart(ProcessDeployment deployment) throws Exception {
        Queue cmd = context.createQueue(ConfigService.QUEUE_CMD);
        JMSProducer producer = context.createProducer();
        Message msg = createStartMessage(deployment);
        producer.send(cmd, msg);
    }

    private Message createStartMessage(ProcessDeployment deployment) throws Exception {
        Message msg = context.createMessage();
        msg.setStringProperty(ConfigService.MSG_CMD, ConfigService.CMD_START);
        msg.setStringProperty(ConfigService.MSG_PROCESS_DEF_GUID, deployment.getProcessDefinitionGuid());
        msg.setStringProperty(ConfigService.MSG_PROCESS_ID, deployment.getProcessId());
        msg.setStringProperty(ConfigService.MSG_PROCESS_VERSION, deployment.getProcessVersion());
        return msg;
    }
}
