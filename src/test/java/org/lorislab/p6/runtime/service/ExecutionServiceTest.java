package org.lorislab.p6.runtime.service;

import java.util.UUID;
import javax.jms.JMSContext;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.Queue;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ExecutionServiceTest {

    @Inject
    private JMSContext context;

    @Resource(lookup = "java:/jms/queue/p6.execution")
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
