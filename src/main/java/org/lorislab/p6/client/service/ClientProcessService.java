package org.lorislab.p6.client.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.config.ConfigService;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.TextMessage;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ClientProcessService {

    private Yaml yaml;

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    @PostConstruct
    private void init() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);
        log.info("Start up the client executor service!");
    }

    public String startProcess(String processId, Map<String, Object> data) throws Exception {
        String processInstanceId = UUID.randomUUID().toString();

        TextMessage msg = context.createTextMessage();
        msg.setStringProperty(ConfigService.MSG_CMD, ConfigService.CMD_START_PROCESS);
        msg.setStringProperty(ConfigService.MSG_PROCESS_INSTANCE_ID, processInstanceId);
        msg.setStringProperty(ConfigService.MSG_PROCESS_ID, processId);
        sendMessage(msg, data);

        return processInstanceId;
    }

    public String sendMessage(String processInstanceId, Map<String, Object> data) throws Exception {
        String messageId = UUID.randomUUID().toString();

        TextMessage msg = context.createTextMessage();
        msg.setStringProperty(ConfigService.MSG_CMD, ConfigService.CMD_SEND_MESSAGE);
        msg.setStringProperty(ConfigService.MSG_PROCESS_INSTANCE_ID, processInstanceId);
        msg.setStringProperty(ConfigService.MSG_PROCESS_MESSAGE_ID, messageId);
        sendMessage(msg, data);

        return messageId;
    }


    public String sendEvent(String processInstanceId, Object data) throws Exception {
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
            String content = yaml.dump(data);
            msg.setText(content);
        }
        Queue queue = context.createQueue(ConfigService.QUEUE_CMD);
        context.createProducer().send(queue, msg);
    }
}
