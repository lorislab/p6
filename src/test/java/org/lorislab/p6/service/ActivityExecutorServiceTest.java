package org.lorislab.p6.service;

import java.util.UUID;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lorislab.p6.test.AbstractTest;

@RunWith(Arquillian.class)
public class ActivityExecutorServiceTest extends AbstractTest {

    @Inject
    private JMSContext context;

    @Resource(lookup = "java:/jms/queue/p6.activity")
    private Queue queue;

    @Test
    public void sendMessage() throws Exception {
        context.createProducer().send(queue, UUID.randomUUID().toString());
//        String text = UUID.randomUUID().toString();
//        try (JMSContext context = connectionFactory.createContext()) {
//            Queue input = context.createQueue("input");
//            Queue output = context.createQueue("output");
//            JMSProducer producer = context.createProducer();
//            Message msg = context.createTextMessage(text);
//            msg.setStringProperty("TEST_VALUE", text);
//            producer.send(input, msg);           
//            
//            try (JMSConsumer consumer = context.createConsumer(output)) {
//                Message result = consumer.receive(5000L);
//                Assert.assertNotNull(result);
//                Assert.assertTrue(result instanceof TextMessage);
//                TextMessage t = (TextMessage) result;
//                Assert.assertEquals(text, t.getText());
//            }
//        }
    }
}
