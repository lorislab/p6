/*
 * Copyright 2018 lorislab.org.
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
package org.lorislab.p6.runtime.service;

import java.util.UUID;
import javax.jms.JMSContext;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.Queue;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lorislab.p6.test.DeploymentConfig;

@RunWith(Arquillian.class)
public class ExecutionServiceServerTest {

    @Inject
    private JMSContext context;

    @Resource(lookup = DeploymentConfig.QUEUE_EXECUTION_JNDI)
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
