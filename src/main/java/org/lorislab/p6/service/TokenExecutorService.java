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
import org.lorislab.p6.flow.model.event.EndEvent;
import org.lorislab.p6.flow.model.event.StartEvent;
import org.lorislab.p6.flow.model.gateway.Gateway;
import org.lorislab.p6.flow.model.gateway.ParallelGateway;
import org.lorislab.p6.flow.model.task.ServiceTask;
import org.lorislab.p6.jpa.model.ProcessInstance;
import org.lorislab.p6.jpa.model.ProcessToken;
import org.lorislab.p6.jpa.service.ProcessInstanceService;
import org.lorislab.p6.jpa.service.ProcessTokenService;
import org.lorislab.p6.runtime.RuntimeProcess;
import org.lorislab.p6.runtime.RuntimeProcessService;
import org.lorislab.p6.service.exception.JMSRetryException;

import javax.ejb.*;
import javax.jms.*;

import static org.lorislab.p6.flow.model.gateway.SequenceFlow.CONVERGING;

@Slf4j
@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/" + ConfigService.QUEUE_TOKEN),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
        }
)
public class TokenExecutorService implements MessageListener {

    @EJB
    private RuntimeProcessService runtimeProcessService;

    @EJB
    private ProcessTokenService processTokenService;

    @EJB
    private ProcessInstanceService processInstanceService;

    @EJB
    private EventExecutorService eventExecutionService;

    @EJB
    private TaskExecutorService taskExecutionService;

    @EJB
    private GatewayExecutorService gatewayExecutionService;

    @EJB
    private ProcessSingletonService processSingletonService;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void onMessage(Message message) {
        int retry = 0;
        try {
            if (message.propertyExists(ConfigService.JMS_RETRY_COUNT)) {
                retry = message.getIntProperty(ConfigService.JMS_RETRY_COUNT);
            }

            String guid = message.getStringProperty(ConfigService.MSG_PROCESS_TOKEN_ID);

            ProcessToken token = processTokenService.loadByGuid(guid);
            ProcessInstance processInstance = token.getProcessInstance();
            RuntimeProcess runtimeProcess = runtimeProcessService.getRuntimeProcess(processInstance.getProcessId(), processInstance.getProcessVersion());

            Node node = runtimeProcess.getNode(token.getNodeName());
            log.info("Execute token: " + node);
            switch (node.getNodeType()) {
                case START_EVENT:
                    eventExecutionService.startEvent(token, runtimeProcess, (StartEvent) node);
                    break;
                case END_EVENT:
                    eventExecutionService.endEvent(token, runtimeProcess, (EndEvent) node);
                    break;
                case SERVICE_TASK:
                    taskExecutionService.serviceTask(token, runtimeProcess, (ServiceTask) node);
                    break;
                case PARALLEL_GATEWAY:
                    ParallelGateway pg = (ParallelGateway) node;
                    switch (pg.getSequenceFlow()) {
                        case CONVERGING:
                            processSingletonService.sendSingletonMessage(message);
                            break;
                        case DIVERGING:
                            gatewayExecutionService.parallelDiverging(token, runtimeProcess, pg);
                            break;
                        default:
                            log.error("No supported parallel sequence flow: {}", pg.getSequenceFlow());
                    }
                    break;
                default:
                    log.error("No supported node type: {}", node.getNodeType());
            }
        } catch (Exception ex) {
            if (retry < ConfigService.MAX_REDELIVERY_COUNT) {
                log.error("Error executeGateway the token. '{}' Retry: {} ", ex.getMessage(), retry);
                throw new JMSRetryException("Error executeGateway the token. Retry: " + retry);
            }
            log.error("Error executeGateway the token. Retry: " + retry, ex);
            throw new RuntimeException("Error executeGateway the token. Retry: " + retry);
        }
    }

}
