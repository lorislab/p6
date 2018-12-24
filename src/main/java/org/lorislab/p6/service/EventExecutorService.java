package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.flow.model.event.Event;
import org.lorislab.p6.jpa.model.ProcessInstance;
import org.lorislab.p6.jpa.model.ProcessToken;
import org.lorislab.p6.jpa.model.enums.ProcessInstanceStatus;
import org.lorislab.p6.jpa.model.enums.ProcessTokenStatus;
import org.lorislab.p6.jpa.service.ProcessInstanceService;
import org.lorislab.p6.jpa.service.ProcessTokenService;
import org.lorislab.p6.runtime.RuntimeProcess;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class EventExecutorService {

    @EJB
    private ProcessTokenService processTokenService;

    @EJB
    private ProcessInstanceService processInstanceService;

    @EJB
    private TokenService tokenService;

    public void execute(ProcessToken token, RuntimeProcess runtimeProcess, Event event) throws Exception {

        switch (event.getEventType()) {
            case START:
                startEvent(token, runtimeProcess, event);
                break;
            case END:
                endEvent(token, runtimeProcess, event);
                break;
            default:
                log.error("No supported event type: {}", event.getEventType());
        }
    }

    private void startEvent(ProcessToken token, RuntimeProcess runtimeProcess, Event event) throws Exception {

        ProcessInstance processInstance = token.getProcessInstance();
        String nextNodeName = runtimeProcess.getFlow().getNextNodeName(event.getName());

        // update token
        token.setNodeName(nextNodeName);
        processTokenService.update(token);

        // send token message
        tokenService.sendTokenMessage(processInstance, token);
    }

    private void endEvent(ProcessToken token, RuntimeProcess runtimeProcess, Event event) throws Exception {

        // update the process instance
        ProcessInstance processInstance = token.getProcessInstance();
        processInstance.setStatus(ProcessInstanceStatus.FINISHED);
        processInstanceService.update(processInstance);

        // update token
        token.setPreviousName(token.getNodeName());
        token.setNodeName(event.getName());
        token.setStatus(ProcessTokenStatus.FINISHED);
        processTokenService.update(token);
    }
}
