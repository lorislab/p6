package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.config.ConfigService;
import org.lorislab.p6.flow.model.Sequence;
import org.lorislab.p6.flow.model.gateway.Gateway;
import org.lorislab.p6.flow.model.gateway.ParallelGateway;
import org.lorislab.p6.jpa.model.ProcessInstance;
import org.lorislab.p6.jpa.model.ProcessToken;
import org.lorislab.p6.jpa.model.enums.ProcessTokenStatus;
import org.lorislab.p6.jpa.service.ProcessInstanceService;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class GatewayExecutionService {

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    @EJB
    private ProcessTokenService processTokenService;

    @EJB
    private ProcessInstanceService processInstanceService;

    public void executeGateway(ProcessToken token, RuntimeProcess runtimeProcess, Gateway gateway) throws Exception {
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
        ProcessInstance processInstance = token.getProcessInstance();

        ProcessToken gatewayToken = processTokenService.findByNodeNameAndProcessInstance(gateway.getName(), processInstance.getGuid());
        if (gatewayToken == null) {
            gatewayToken = new ProcessToken();
            gatewayToken.setStartNodeName(gateway.getName());
            gatewayToken.setNodeName(gateway.getName());
            gatewayToken.getParents().add(token.getGuid());
            gatewayToken.setProcessInstance(token.getProcessInstance());
            gatewayToken.setData(token.getData());
            gatewayToken.setProcessInstance(processInstance);
        } else {

            if (token.getData() != null) {
                Map<String, Object> data = new HashMap<>();
                if (gatewayToken.getData() != null) {
                    String tmp = new String(gatewayToken.getData(), StandardCharsets.UTF_8);
                    if (!tmp.isBlank()) {
                        data = ServerJsonService.loadData(tmp);
                    }
                }

                String tmp = new String(token.getData(), StandardCharsets.UTF_8);
                Map<String, Object> newData = ServerJsonService.loadData(tmp);
                data.putAll(newData);
                String resultData = ServerJsonService.saveData(data);
                gatewayToken.setData(resultData.getBytes(StandardCharsets.UTF_8));
            }

            gatewayToken.getParents().add(token.getGuid());
            gatewayToken.setPreviousName(token.getNodeName());
        }

        Sequence sequence = runtimeProcess.getFlow().getSequence().get(gateway.getName());
        List<String> from = sequence.getFrom();
        Set<String> parents = gatewayToken.getParents();
        log.info("FROM: {}", from.size());
        log.info("PARENT: {}", parents.size());
        if (from.size() == parents.size()) {
            gatewayToken.setNodeName(sequence.next());

            Queue tokenQueue = context.createQueue(ConfigService.QUEUE_TOKEN);
            JMSProducer producer = context.createProducer();
            Message tokenMessage = context.createMessage();
            tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_ID, processInstance.getProcessId());
            tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_VERSION, processInstance.getProcessVersion());
            tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_INSTANCE_ID, processInstance.getGuid());
            tokenMessage.setStringProperty(ConfigService.MSG_PROCESS_TOKEN_ID, gatewayToken.getGuid());
            producer.send(tokenQueue, tokenMessage);
        }


        if (gatewayToken.isPersisted()) {
            processTokenService.update(gatewayToken);
        } else {
            processTokenService.create(gatewayToken);
        }
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
                child.setData(token.getData());
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
