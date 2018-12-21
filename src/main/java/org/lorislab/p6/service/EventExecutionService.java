package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.config.ConfigService;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.event.Event;
import org.lorislab.p6.jpa.model.ProcessInstance;
import org.lorislab.p6.jpa.model.ProcessToken;
import org.lorislab.p6.jpa.model.enums.ProcessInstanceStatus;
import org.lorislab.p6.jpa.model.enums.ProcessTokenStatus;
import org.lorislab.p6.jpa.service.ProcessInstanceService;
import org.lorislab.p6.jpa.service.ProcessTokenService;
import org.lorislab.p6.model.RuntimeProcess;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.*;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class EventExecutionService {

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    @EJB
    private ProcessTokenService processTokenService;

    @EJB
    private ProcessInstanceService processInstanceService;

    public void execute(ProcessToken token, RuntimeProcess runtimeProcess, Event event) throws Exception {

        switch (event.getEventType()) {
            case START:
                startEvent(token, runtimeProcess, event);
                break;
            case END:
                endEvent(token, runtimeProcess, event);
                break;
            default:
                log.error("No supported event type: {}", event.getEventType());
        }
    }

    private void startEvent(ProcessToken token, RuntimeProcess runtimeProcess, Event event) throws Exception {

        ProcessInstance processInstance = token.getProcessInstance();

        String nextNodeName = runtimeProcess.getFlow().getNextNodeName(event.getName());

        // send token message
        token.setNodeName(nextNodeName);
        processTokenService.update(token);

        Queue tokenQueue = context.createQueue(ConfigService.QUEUE_TOKEN);
        JMSProducer producer = context.createProducer();
        Message tokenMessage = context.createMessage();
        tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_ID, processInstance.getProcessId());
        tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_VERSION, processInstance.getProcessVersion());
        tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_INSTANCE_ID, processInstance.getGuid());
        tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_TOKEN_ID, token.getGuid());
        producer.send(tokenQueue, tokenMessage);
    }

    private void endEvent(ProcessToken token, RuntimeProcess runtimeProcess, Event event) throws Exception {
        ProcessInstance processInstance = token.getProcessInstance();
        processInstance.setStatus(ProcessInstanceStatus.FINISHED);
        processInstanceService.update(processInstance);
        token.setPreviousName(token.getNodeName());
        token.setNodeName(event.getName());
        token.setStatus(ProcessTokenStatus.FINISHED);
        processTokenService.update(token);
    }
}
