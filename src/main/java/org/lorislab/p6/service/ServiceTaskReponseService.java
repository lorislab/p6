package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.config.ConfigService;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.NodeType;
import org.lorislab.p6.flow.model.task.ServiceTask;
import org.lorislab.p6.flow.model.task.Task;
import org.lorislab.p6.flow.model.task.TaskType;
import org.lorislab.p6.jpa.model.ProcessInstance;
import org.lorislab.p6.jpa.model.ProcessToken;
import org.lorislab.p6.jpa.service.ProcessTokenService;
import org.lorislab.p6.model.RuntimeProcess;

import javax.ejb.*;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.MessageListener;

@Slf4j
@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/" + ConfigService.QUEUE_RESPONSE),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
        }
)
public class ServiceTaskReponseService implements MessageListener {

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    @EJB
    private ProcessTokenService processTokenService;

    @EJB
    private RuntimeProcessService runtimeProcessService;

    @EJB
    private TaskExecutionService taskExecutionService;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void onMessage(Message message) {
        try {
            String processId = message.getStringProperty(ConfigService.MSG_PROCESS_ID);
            String processVersion = message.getStringProperty(ConfigService.MSG_PROCESS_VERSION);
            String processInstanceId = message.getStringProperty(ConfigService.MSG_PROCESS_INSTANCE_ID);
            String tokenId = message.getStringProperty(ConfigService.MSG_PROCESS_TOKEN_ID);
            String serviceTaskName = message.getStringProperty(ConfigService.MSG_PROCESS_TOKEN_SERVICE_TASK);

            ProcessToken token = processTokenService.loadByGuid(tokenId);
            ServiceTask serviceTask = null;
            RuntimeProcess runtimeProcess = null;

            if (token != null) {
                ProcessInstance processInstance = token.getProcessInstance();
                runtimeProcess = runtimeProcessService.getRuntimeProcess(processInstance.getProcessId(), processInstance.getProcessVersion());
                if (runtimeProcess != null) {
                    Node node = runtimeProcess.getNode(token.getNodeName());
                    if (node.getNodeType() == NodeType.TASK) {
                        Task task = (Task) node;
                        if (task.getTaskType() == TaskType.SERVICE_TASK) {
                            serviceTask = (ServiceTask) task;
                        }
                    }
                }
            } else {
                log.error("No token found for the response {} {} {} {} {}", processId, processVersion, processInstanceId, tokenId, serviceTaskName);
            }

            if (serviceTask != null) {
                String data = message.getBody(String.class);
                taskExecutionService.completeServiceTask(token, runtimeProcess, serviceTask, data);
            } else {
                log.error("No service task found the response {} {} {} {} {}", processId, processVersion, processInstanceId, tokenId, serviceTaskName);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error processing the service task response", ex);
        }
    }
}
