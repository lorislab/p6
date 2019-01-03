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
import org.lorislab.p6.flow.model.NodeType;
import org.lorislab.p6.flow.model.task.ServiceTask;
import org.lorislab.p6.jpa.model.ProcessInstance;
import org.lorislab.p6.jpa.model.ProcessToken;
import org.lorislab.p6.jpa.service.ProcessTokenService;
import org.lorislab.p6.runtime.RuntimeProcess;
import org.lorislab.p6.runtime.RuntimeProcessService;

import javax.ejb.*;
import javax.jms.Message;
import javax.jms.MessageListener;

@Slf4j
@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/" + ConfigService.QUEUE_RESPONSE),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
        }
)
public class TaskReponseService implements MessageListener {

    @EJB
    private ProcessTokenService processTokenService;

    @EJB
    private RuntimeProcessService runtimeProcessService;

    @EJB
    private TaskExecutorService taskExecutorService;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void onMessage(Message message) {
        try {
            String processId = message.getStringProperty(ConfigService.MSG_PROCESS_ID);
            String processVersion = message.getStringProperty(ConfigService.MSG_PROCESS_VERSION);
            String processInstanceId = message.getStringProperty(ConfigService.MSG_PROCESS_INSTANCE_ID);
            String tokenId = message.getStringProperty(ConfigService.MSG_PROCESS_TOKEN_ID);
            String serviceTaskName = message.getStringProperty(ConfigService.MSG_PROCESS_TOKEN_SERVICE_TASK);

            ProcessToken token = processTokenService.loadByGuid(tokenId);
            ServiceTask serviceTask = null;
            RuntimeProcess runtimeProcess = null;

            if (token != null) {
                ProcessInstance processInstance = token.getProcessInstance();
                runtimeProcess = runtimeProcessService.getRuntimeProcess(processInstance.getProcessId(), processInstance.getProcessVersion());
                if (runtimeProcess != null) {
                    Node node = runtimeProcess.getNode(token.getNodeName());
                    if (node.getNodeType() == NodeType.SERVICE_TASK) {
                        serviceTask = (ServiceTask) node;
                    }
                }
            } else {
                log.error("No token found for the response {} {} {} {} {}", processId, processVersion, processInstanceId, tokenId, serviceTaskName);
            }

            if (serviceTask != null) {
                String data = message.getBody(String.class);
                taskExecutorService.completeServiceTask(token, runtimeProcess, serviceTask, data);
            } else {
                log.error("No service task found the response {} {} {} {} {}", processId, processVersion, processInstanceId, tokenId, serviceTaskName);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error processing the service task response", ex);
        }
    }
}
