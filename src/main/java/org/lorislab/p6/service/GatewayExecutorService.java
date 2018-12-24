package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.flow.model.Sequence;
import org.lorislab.p6.flow.model.gateway.Gateway;
import org.lorislab.p6.flow.model.gateway.ParallelGateway;
import org.lorislab.p6.jpa.model.ProcessInstance;
import org.lorislab.p6.jpa.model.ProcessToken;
import org.lorislab.p6.jpa.model.enums.ProcessTokenStatus;
import org.lorislab.p6.jpa.service.ProcessInstanceService;
import org.lorislab.p6.jpa.service.ProcessTokenService;
import org.lorislab.p6.json.ServerJsonService;
import org.lorislab.p6.runtime.RuntimeProcess;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class GatewayExecutorService {

    @EJB
    private ProcessTokenService processTokenService;

    @EJB
    private ProcessInstanceService processInstanceService;

    @EJB
    private TokenService tokenService;

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
            gatewayToken = ServerJsonService.mergeData(gatewayToken, token);
            gatewayToken.getParents().add(token.getGuid());
            gatewayToken.setPreviousName(token.getNodeName());
        }

        Sequence sequence = runtimeProcess.getFlow().getSequence().get(gateway.getName());
        List<String> from = sequence.getFrom();
        Set<String> parents = gatewayToken.getParents();
        log.info("FROM: {} PARENT: {}", from.size(), parents.size());
        if (from.size() == parents.size()) {
            gatewayToken.setNodeName(sequence.next());

            // send token message
            tokenService.sendTokenMessage(processInstance, gatewayToken);
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

            // create token
            List<ProcessToken> children = new ArrayList<>(seq.getTo().size());
            for (String to : seq.getTo()) {
                ProcessToken child = new ProcessToken();
                child.setNodeName(to);
                child.setPreviousName(token.getNodeName());
                child.getParents().add(token.getGuid());
                child.setProcessInstance(token.getProcessInstance());
                child.setData(token.getData());
                child = processTokenService.create(child);
                children.add(child);
            }

            // send token message
            tokenService.sendTokenMessages(processInstance, children);
        }

        // update the token status to finished
        processTokenService.updateTokenStatus(token.getGuid(), ProcessTokenStatus.FINISHED);
    }
}
