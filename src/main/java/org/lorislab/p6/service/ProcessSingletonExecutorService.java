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
import org.lorislab.p6.config.MessageProperties;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.gateway.ParallelGateway;
import org.lorislab.p6.jpa.model.ProcessInstance;
import org.lorislab.p6.jpa.model.ProcessToken;
import org.lorislab.p6.jpa.service.ProcessTokenService;
import org.lorislab.p6.runtime.RuntimeProcess;
import org.lorislab.p6.runtime.RuntimeProcessService;
import org.lorislab.p6.service.exception.JMSRetryException;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

@Slf4j
@MessageDriven(name = "ProcessSingletonExecutorService",
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/" + MessageProperties.QUEUE_SINGLETON),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
                @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "1")
        }
)
public class ProcessSingletonExecutorService implements MessageListener {

    @EJB
    private RuntimeProcessService runtimeProcessService;

    @EJB
    private ProcessTokenService processTokenService;

    @EJB
    private GatewayExecutorService gatewayExecutorService;

    @Override
    public void onMessage(Message message) {
        int retry = 0;
        try {
            if (message.propertyExists(MessageProperties.JMS_RETRY_COUNT)) {
                retry = message.getIntProperty(MessageProperties.JMS_RETRY_COUNT);
            }

            String guid = message.getStringProperty(MessageProperties.MSG_PROCESS_TOKEN_ID);

            ProcessToken token = processTokenService.loadByGuid(guid);
            ProcessInstance processInstance = token.getProcessInstance();
            RuntimeProcess runtimeProcess = runtimeProcessService.getRuntimeProcess(processInstance.getProcessId(), processInstance.getProcessVersion());

            Node node = runtimeProcess.getNode(token.getNodeName());
            switch (node.getNodeType()) {
                case PARALLEL_GATEWAY:
                    ParallelGateway pg = (ParallelGateway) node;
                    switch (pg.getSequenceFlow()) {
                        case CONVERGING:
                            gatewayExecutorService.parallelConverging(token, runtimeProcess, pg);
                            break;
                        default:
                            log.error("No supported parallel singlenton sequence flow: {}", pg.getSequenceFlow());
                    }
                    break;
                default:
                    log.error("No supported singleton executor for node type: {}", node.getNodeType());
            }
        } catch (Exception ex) {
            if (retry < MessageProperties.MAX_REDELIVERY_COUNT) {
                log.error("Error execute singleton gateway the token. '{}' Retry: {} ", ex.getMessage(), retry);
                throw new JMSRetryException("Error execute singleton gateway the token. Retry: " + retry);
            }
            log.error("Error execute singleton gateway the token. Retry: " + retry, ex);
            throw new RuntimeException("Error execute singleton gateway the token. Retry: " + retry);
        }
    }
}
