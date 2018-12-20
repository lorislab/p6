package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.jee.exception.ServiceException;
import org.lorislab.p6.config.ConfigService;
import org.lorislab.p6.jpa.model.ProcessDefinition;
import org.lorislab.p6.jpa.model.ProcessDeployment;
import org.lorislab.p6.jpa.service.ProcessDefinitionService;
import org.lorislab.p6.jpa.service.ProcessDeploymentService;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import java.util.List;

@Slf4j
@Startup
@Singleton
public class StartupService {

    @Inject
    private JMSContext context;

    @EJB
    private ProcessDeploymentService processDeploymentService;

    @PostConstruct
    public void init() {
        try {
            List<ProcessDeployment> deployments = processDeploymentService.findAll(null, null);
            Queue cmd = context.createQueue(ConfigService.QUEUE_CMD);
            JMSProducer producer = context.createProducer();
            for (int i=0; i<deployments.size(); i++) {
                ProcessDeployment dep = deployments.get(i);
                Message msg = context.createMessage();
                msg.setStringProperty(ConfigService.MSG_CMD, ConfigService.CMD_DEPLOY);
                msg.setStringProperty(ConfigService.MSG_PROCESS_DEF_GUID, dep.getProcessDefinitionGuid());
                msg.setStringProperty(ConfigService.MSG_PROCESS_ID, dep.getProcessId());
                msg.setStringProperty(ConfigService.MSG_PROCESS_VERSION, dep.getProcessVersion());
                producer.send(cmd, msg);
            }
        } catch (Exception se) {
            log.error("Error find the latest process definitions from the database.", se);
            throw new RuntimeException("Error find the latest process definitions!");
        }
    }
}
