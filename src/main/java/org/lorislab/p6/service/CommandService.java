package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.config.ConfigService;
import org.lorislab.p6.flow.json.JsonProcessFlowService;
import org.lorislab.p6.flow.model.ProcessFlow;
import org.lorislab.p6.jpa.model.ProcessDefinition;
import org.lorislab.p6.jpa.model.ProcessDeployment;
import org.lorislab.p6.jpa.model.ProcessInstance;
import org.lorislab.p6.jpa.model.ProcessToken;
import org.lorislab.p6.jpa.model.enums.ProcessInstanceStatus;
import org.lorislab.p6.jpa.model.enums.ProcessTokenStatus;
import org.lorislab.p6.jpa.service.ProcessDefinitionService;
import org.lorislab.p6.jpa.service.ProcessDeploymentService;
import org.lorislab.p6.jpa.service.ProcessInstanceService;
import org.lorislab.p6.model.RuntimeProcess;

import javax.ejb.*;
import javax.inject.Inject;
import javax.jms.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/" + ConfigService.QUEUE_CMD),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
        }
)
public class CommandService implements MessageListener {

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    @EJB
    private ProcessDefinitionService processDefinitionService;

    @EJB
    private RuntimeProcessService runtimeProcessService;

    @EJB
    private ProcessInstanceService processInstanceService;

    @EJB
    private ProcessDeploymentService processDeploymentService;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void onMessage(Message message) {
        String cmd = null;
        try {
            cmd = message.getStringProperty(ConfigService.MSG_CMD);
            switch (cmd) {
                case ConfigService.CMD_DEPLOY:
                    deploy(message);
                    break;
                case ConfigService.CMD_START_PROCESS:
                    startProcess(message);
                    break;
                case ConfigService.CMD_SEND_EVENT:
                    sendEvent(message);
                    break;
                case ConfigService.CMD_SEND_MESSAGE:
                    sendMessage(message);
                    break;
                default:
                    log.error("Not supported command '{}' in the command message.", cmd);
            }
        } catch (Exception ex) {
            log.error("Error executeGateway the command " + cmd, ex);
        }
    }

    private void deploy(Message message) throws Exception {
        String guid = message.getStringProperty(ConfigService.MSG_PROCESS_DEF_GUID);
        ProcessDefinition def = processDefinitionService.loadByGuid(guid);
        String data = new String(def.getContent().getData(), StandardCharsets.UTF_8);
        ProcessFlow flow = JsonProcessFlowService.loadProcessFlow(data);
        def.setContent(null);
        RuntimeProcess process = new RuntimeProcess(def, flow);
        runtimeProcessService.addRuntimeProcess(process);
    }

    private void sendEvent(Message message) throws Exception {
        log.error("###################### NOT SUPPORTED: sendEvent ###################");
    }

    private void sendMessage(Message message) throws Exception {
        log.error("###################### NOT SUPPORTED: sendMessage ###################");
    }

    private void startProcess(Message message) throws Exception {
        String processId = message.getStringProperty(ConfigService.MSG_PROCESS_ID);
        String processInstanceId = message.getStringProperty(ConfigService.MSG_PROCESS_INSTANCE_ID);

        ProcessDeployment deployment = processDeploymentService.findByProcessId(processId);
        if (deployment == null) {
            log.error("No process found in the deployment for the process id: {}", processId);
            return;
        }

        RuntimeProcess process = runtimeProcessService.getRuntimeProcess(deployment.getProcessId(), deployment.getProcessVersion());
        if (process != null) {
            List<String> nodes = process.getFlow().getStart();
            if (nodes != null && !nodes.isEmpty()) {

                Queue tokenQueue = context.createQueue(ConfigService.QUEUE_TOKEN);
                JMSProducer producer = context.createProducer();

                ProcessInstance instance = new ProcessInstance();
                instance.setGuid(processInstanceId);
                instance.setStatus(ProcessInstanceStatus.IN_EXECUTION);
                instance.setProcessId(process.getDefinition().getProcessId());
                instance.setProcessDefinitionGuid(process.getDefinition().getGuid());
                instance.setProcessVersion(process.getDefinition().getProcessVersion());

                for (String node : nodes) {
                    // create token
                    ProcessToken token = new ProcessToken();
                    token.setNodeName(node);
                    token.setStartNodeName(node);
                    token.setStatus(ProcessTokenStatus.IN_EXECUTION);
                    token.setProcessInstance(instance);
                    instance.getTokens().add(token);

                    // send token message
                    Message tokenMessage = context.createMessage();
                    tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_ID, instance.getProcessId());
                    tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_VERSION, instance.getProcessVersion());
                    tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_INSTANCE_ID, instance.getGuid());
                    tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_TOKEN_ID, token.getGuid());
                    producer.send(tokenQueue, tokenMessage);
                }

                // saveProcessFlow the process instance
                processInstanceService.create(instance);

            } else {
                log.error("No start events devfined for the process id: {}", processId);
            }
        } else {
            log.error("No runtime process found for the process id: {}", processId);
        }
    }
}
