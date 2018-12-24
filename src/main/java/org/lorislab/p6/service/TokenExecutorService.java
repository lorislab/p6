package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.config.ConfigService;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.event.Event;
import org.lorislab.p6.flow.model.gateway.Gateway;
import org.lorislab.p6.flow.model.task.Task;
import org.lorislab.p6.jpa.model.ProcessInstance;
import org.lorislab.p6.jpa.model.ProcessToken;
import org.lorislab.p6.jpa.service.ProcessInstanceService;
import org.lorislab.p6.jpa.service.ProcessTokenService;
import org.lorislab.p6.runtime.RuntimeProcess;
import org.lorislab.p6.runtime.RuntimeProcessService;
import org.lorislab.p6.service.exception.JMSRetryException;

import javax.ejb.*;
import javax.jms.*;

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
            switch (node.getNodeType()) {
                case EVENT:
                    eventExecutionService.execute(token, runtimeProcess, (Event) node);
                    break;
                case TASK:
                    taskExecutionService.execute(token, runtimeProcess, (Task) node);
                    break;
                case GATEWAY:
                    gatewayExecutionService.executeGateway(token, runtimeProcess, (Gateway) node);
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
