package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.config.ConfigService;
import org.lorislab.p6.jpa.model.ProcessInstance;
import org.lorislab.p6.jpa.model.ProcessToken;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.*;
import java.util.List;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TokenService {

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    public void sendTokenMessages(ProcessInstance processInstance, List<ProcessToken> tokens) throws Exception {
        Queue tokenQueue = context.createQueue(ConfigService.QUEUE_TOKEN);
        JMSProducer producer = context.createProducer();
        for (ProcessToken token : tokens) {
            Message tokenMessage = createTokenMessage(processInstance, token);
            producer.send(tokenQueue, tokenMessage);
        }
    }

    public void sendTokenMessage(ProcessInstance processInstance, ProcessToken token) throws Exception {
        Queue tokenQueue = context.createQueue(ConfigService.QUEUE_TOKEN);
        JMSProducer producer = context.createProducer();
        Message tokenMessage = createTokenMessage(processInstance, token);
        producer.send(tokenQueue, tokenMessage);
    }

    private Message createTokenMessage(ProcessInstance processInstance, ProcessToken token) throws Exception {
        Message tokenMessage = context.createMessage();
        tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_ID, processInstance.getProcessId());
        tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_VERSION, processInstance.getProcessVersion());
        tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_INSTANCE_ID, processInstance.getGuid());
        tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_TOKEN_ID, token.getGuid());
        log.info("\n#######################\n{} -> {}\n#######################", token.getPreviousName(), token.getNodeName());
        return tokenMessage;
    }
}
