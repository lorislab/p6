package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.config.ConfigService;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.ProcessFlow;
import org.lorislab.p6.flow.model.event.Event;
import org.lorislab.p6.flow.model.gateway.Gateway;
import org.lorislab.p6.flow.model.task.ServiceTask;
import org.lorislab.p6.flow.model.task.Task;
import org.lorislab.p6.jpa.model.ProcessDefinition;
import org.lorislab.p6.jpa.model.ProcessInstance;
import org.lorislab.p6.jpa.model.ProcessToken;
import org.lorislab.p6.jpa.model.enums.ProcessInstanceStatus;
import org.lorislab.p6.jpa.service.ProcessDefinitionService;
import org.lorislab.p6.jpa.service.ProcessInstanceService;
import org.lorislab.p6.jpa.service.ProcessTokenService;
import org.lorislab.p6.model.RuntimeProcess;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.inject.Inject;
import javax.jms.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/" + ConfigService.QUEUE_TOKEN),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
        }
)
public class TokenExecutionService implements MessageListener {

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    @EJB
    private RuntimeProcessService runtimeProcessService;

    @EJB
    private ProcessTokenService processTokenService;

    @EJB
    private ProcessInstanceService processInstanceService;

    @EJB
    private EventExecutionService eventExecutionService;

    @EJB
    private TaskExecutionService taskExecutionService;

    @EJB
    private GatewayExecutionService gatewayExecutionService;

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
                    gatewayExecutionService.execute(token, runtimeProcess, (Gateway) node);
                    break;
                default:
                    log.error("No supported node type: {}", node.getNodeType());
            }
        } catch (Exception ex) {
            log.error("Error execute the token. Retry: " + retry, ex);
            throw new RuntimeException("Error execute the token. Retry: " + retry);
        }
    }

}
