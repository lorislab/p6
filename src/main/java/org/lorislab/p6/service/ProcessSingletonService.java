package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.config.ConfigService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.*;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ProcessSingletonService {

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    public void sendSingletonMessage(Message message) throws Exception {
        Queue singletonQueue = context.createQueue(ConfigService.QUEUE_SINGLETON);
        context.createProducer().send(singletonQueue, message);
    }
}
