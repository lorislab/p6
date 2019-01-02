package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.flow.model.event.EndEvent;
import org.lorislab.p6.flow.model.event.StartEvent;
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

    public void startEvent(ProcessToken token, RuntimeProcess runtimeProcess, StartEvent event) throws Exception {

        ProcessInstance processInstance = token.getProcessInstance();
        String nextNodeName = runtimeProcess.getNextNodeName(event.getName());

        // update token
        token.setNodeName(nextNodeName);
        processTokenService.update(token);

        // send token message
        tokenService.sendTokenMessage(processInstance, token);
    }

    public void endEvent(ProcessToken token, RuntimeProcess runtimeProcess, EndEvent event) throws Exception {

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
