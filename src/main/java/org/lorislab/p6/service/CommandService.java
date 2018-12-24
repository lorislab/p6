package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.config.ConfigService;
import org.lorislab.p6.jpa.model.ProcessDeployment;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.*;
import java.util.List;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CommandService {

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    public void cmdDeploy(List<ProcessDeployment> deployments) throws Exception {
        if (deployments != null && !deployments.isEmpty()) {
            Queue cmd = context.createQueue(ConfigService.QUEUE_CMD);
            JMSProducer producer = context.createProducer();
            for (int i = 0; i < deployments.size(); i++) {
                ProcessDeployment deployment = deployments.get(i);
                Message msg = createDeploymentMessage(deployment);
                producer.send(cmd, msg);
            }
        }
    }

    public void cmdDeploy(ProcessDeployment deployment) throws Exception {
        Queue cmd = context.createQueue(ConfigService.QUEUE_CMD);
        JMSProducer producer = context.createProducer();
        Message msg = createDeploymentMessage(deployment);
        producer.send(cmd, msg);
    }

    private Message createDeploymentMessage(ProcessDeployment deployment) throws Exception {
        Message msg = context.createMessage();
        msg.setStringProperty(ConfigService.MSG_CMD, ConfigService.CMD_DEPLOY);
        msg.setStringProperty(ConfigService.MSG_PROCESS_DEF_GUID, deployment.getProcessDefinitionGuid());
        msg.setStringProperty(ConfigService.MSG_PROCESS_ID, deployment.getProcessId());
        msg.setStringProperty(ConfigService.MSG_PROCESS_VERSION, deployment.getProcessVersion());
        return msg;
    }
}
