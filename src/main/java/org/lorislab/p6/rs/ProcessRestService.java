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

package org.lorislab.p6.rs;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.config.ConfigService;
import org.lorislab.p6.service.ServerJsonService;
import org.lorislab.p6.service.CommandService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Path("process")
public class ProcessRestService {

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    @EJB
    private CommandService commandService;

    @POST
    @Path("start/{processId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public String startProcess(@PathParam("processId") String processId, Map<String, Object> data) throws Exception {
        return commandService.startProcess(processId, null, data);
    }

    @POST
    @Path("message/{processInstanceId}")
    public String sendMessage(@PathParam("processInstanceId") String processInstanceId, Map<String, Object> data) throws Exception {
        String messageId = UUID.randomUUID().toString();
        TextMessage msg = context.createTextMessage();
        msg.setStringProperty(ConfigService.MSG_CMD, ConfigService.CMD_SEND_MESSAGE);
        msg.setStringProperty(ConfigService.MSG_PROCESS_INSTANCE_ID, processInstanceId);
        msg.setStringProperty(ConfigService.MSG_PROCESS_MESSAGE_ID, messageId);
        sendMessage(msg, data);
        return messageId;
    }

    @POST
    @Path("event/{processInstanceId}")
    public String sendEvent(@PathParam("processInstanceId") String processInstanceId, Object data) throws Exception {
        String eventId = UUID.randomUUID().toString();
        TextMessage msg = context.createTextMessage();
        msg.setStringProperty(ConfigService.MSG_CMD, ConfigService.CMD_SEND_MESSAGE);
        msg.setStringProperty(ConfigService.MSG_PROCESS_INSTANCE_ID, processInstanceId);
        msg.setStringProperty(ConfigService.MSG_PROCESS_EVENT_ID, eventId);
        Map<String, Object> tmp = Map.of(ConfigService.DATA_KEY_EVENT_DATA, data);
        sendMessage(msg, tmp);
        return eventId;
    }

    private void sendMessage(TextMessage msg, Map<String, Object> data) throws Exception {
        if (data != null) {
            String content = ServerJsonService.toString(data);
            msg.setText(content);
        }
        Queue queue = context.createQueue(ConfigService.QUEUE_CMD);
        context.createProducer().send(queue, msg);
    }
}
