package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.config.ConfigService;
import org.lorislab.p6.flow.model.task.ServiceTask;
import org.lorislab.p6.flow.model.task.Task;
import org.lorislab.p6.jpa.model.ProcessInstance;
import org.lorislab.p6.jpa.model.ProcessToken;
import org.lorislab.p6.jpa.service.ProcessTokenService;
import org.lorislab.p6.json.ServerJsonService;
import org.lorislab.p6.model.RuntimeProcess;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TaskExecutionService {

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    @EJB
    private ProcessTokenService processTokenService;

    public void execute(ProcessToken token, RuntimeProcess runtimeProcess, Task task) throws Exception {
        switch (task.getTaskType()) {
            case SERVICE_TASK:
                serviceTask(token, runtimeProcess, (ServiceTask) task);
                break;
            default:
                log.error("No supported task type: {}", task.getTaskType());
        }
    }

    public void completeServiceTask(ProcessToken token, RuntimeProcess runtimeProcess, ServiceTask task, String response) throws Exception {


        if (response != null && !response.isBlank()) {
            Map<String, Object> data = new HashMap<>();
            if (token.getData() != null) {
                String tmp = new String(token.getData(), StandardCharsets.UTF_8);
                if (!tmp.isBlank()) {
                    data = ServerJsonService.loadData(tmp);
                }
            }
            Map<String, Object> newData = ServerJsonService.loadData(response);
            data.putAll(newData);
            String tmp = ServerJsonService.saveData(data);
            token.setData(tmp.getBytes(StandardCharsets.UTF_8));
        }


        ProcessInstance processInstance = token.getProcessInstance();
        String next = runtimeProcess.getFlow().getNextNodeName(task.getName());

        // create token
        token.setPreviousName(token.getNodeName());
        token.setNodeName(next);
        processTokenService.update(token);

        // send token message
        Queue tokenQueue = context.createQueue(ConfigService.QUEUE_TOKEN);
        JMSProducer producer = context.createProducer();
        Message tokenMessage = context.createMessage();
        tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_ID, processInstance.getProcessId());
        tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_VERSION, processInstance.getProcessVersion());
        tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_INSTANCE_ID, processInstance.getGuid());
        tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_TOKEN_ID, token.getGuid());
        producer.send(tokenQueue, tokenMessage);
    }

    private void serviceTask(ProcessToken token, RuntimeProcess runtimeProcess, ServiceTask task) throws Exception {

        ProcessInstance processInstance = token.getProcessInstance();

        String content = null;
        if (token.getData() != null) {
            content = new String(token.getData(), StandardCharsets.UTF_8);
        }
        TextMessage request = context.createTextMessage(content);
        request.setStringProperty(ConfigService.MSG_PROCESS_ID, processInstance.getProcessId());
        request.setStringProperty(ConfigService.MSG_PROCESS_VERSION, processInstance.getProcessVersion());
        request.setStringProperty(ConfigService.MSG_PROCESS_INSTANCE_ID, processInstance.getGuid());
        request.setStringProperty(ConfigService.MSG_PROCESS_TOKEN_ID, token.getGuid());
        request.setStringProperty(ConfigService.MSG_PROCESS_TOKEN_SERVICE_TASK, token.getNodeName());

        // send request to the application.
        Queue queue = context.createQueue(ConfigService.QUEUE_REQUEST);
        context.createProducer().send(queue, request);
    }
}
