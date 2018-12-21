package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.config.ConfigService;
import org.lorislab.p6.flow.model.Sequence;
import org.lorislab.p6.flow.model.event.Event;
import org.lorislab.p6.flow.model.gateway.Gateway;
import org.lorislab.p6.flow.model.gateway.ParallelGateway;
import org.lorislab.p6.jpa.model.ProcessInstance;
import org.lorislab.p6.jpa.model.ProcessToken;
import org.lorislab.p6.jpa.model.enums.ProcessTokenStatus;
import org.lorislab.p6.jpa.service.ProcessTokenService;
import org.lorislab.p6.model.RuntimeProcess;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.*;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class GatewayExecutionService {

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    @EJB
    private ProcessTokenService processTokenService;

    public void execute(ProcessToken token, RuntimeProcess runtimeProcess, Gateway gateway) throws Exception {
        switch (gateway.getGatewayType()) {
            case PARALLEL:
                switch (gateway.getSequenceFlow()) {
                    case DIVERGING:
                        parallelDiverging(token, runtimeProcess, (ParallelGateway) gateway);
                        break;
                    case CONVERGING:
                        parallelConverging(token, runtimeProcess, (ParallelGateway) gateway);
                        break;
                }
                break;
            default:
                log.error("No supported gateway type: {}", gateway.getGatewayType());
        }
    }

    private void parallelConverging(ProcessToken token, RuntimeProcess runtimeProcess, ParallelGateway gateway) throws Exception {

        // 1. find the token for the gateway
        // 1a. if not existis create new

        // 2. add the token as a parent.
        // 2a. merge the parameter

        // 3. if parent tokens == from names -> move to next

    }

    private void parallelDiverging(ProcessToken token, RuntimeProcess runtimeProcess, ParallelGateway gateway) throws Exception {
        Sequence seq = runtimeProcess.getFlow().getSequence().get(gateway.getName());
        if (seq != null && seq.getTo() != null) {

            ProcessInstance processInstance = token.getProcessInstance();

            Queue tokenQueue = context.createQueue(ConfigService.QUEUE_TOKEN);
            JMSProducer producer = context.createProducer();

            for (String to : seq.getTo()) {

                // create token
                ProcessToken child = new ProcessToken();
                child.setNodeName(to);
                child.setPreviousName(token.getNodeName());
                child.getParents().add(token.getGuid());
                child.setProcessInstance(token.getProcessInstance());
                processTokenService.create(child);

                // send token message
                Message tokenMessage = context.createMessage();
                tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_ID, processInstance.getProcessId());
                tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_VERSION, processInstance.getProcessVersion());
                tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_INSTANCE_ID, processInstance.getGuid());
                tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_TOKEN_ID, child.getGuid());
                producer.send(tokenQueue, tokenMessage);
            }

        }

        // update the token status to finished
        processTokenService.updateTokenStatus(token.getGuid(), ProcessTokenStatus.FINISHED);
    }
}
