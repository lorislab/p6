package org.lorislab.p6.flow.model;

import org.lorislab.p6.flow.model.activity.CallActivity;
import org.lorislab.p6.flow.model.event.EndEvent;
import org.lorislab.p6.flow.model.event.StartEvent;
import org.lorislab.p6.flow.model.gateway.ParallelGateway;
import org.lorislab.p6.flow.model.task.ServiceTask;

public class ProcessFlowBuilder {

    private ProcessFlow flow = new ProcessFlow();

    public ProcessFlow build() {
        return flow;
    }

    public void setProcessId(String processId) {
        flow.setProcessId(processId);
    }

    public void setProcessVersion(String processVersion) {
        flow.setProcessVersion(processVersion);
    }

    public StartEvent createStartEvent(String name) {
        return updateNode(new StartEvent(), name);
    }

    public CallActivity createCallActivity(String name, Node... from) {
        return updateNode(new CallActivity(), name, from);
    }

    public ServiceTask createServiceTask(String name, Node... from) {
        return updateNode(new ServiceTask(), name, from);
    }

    public ParallelGateway createParallelGatewayNode(String name, Node... from) {
        return updateNode(new ParallelGateway(), name, from);
    }

    public EndEvent createEndEvent(String name, Node... from) {
        return updateNode(new EndEvent(), name, from);
    }

    private <T extends Node> T updateNode(T node, String name, Node... from) {
        node.setName(name);
        flow.getNodes().add(node);
        Sequence seq = new Sequence();
        flow.getSequence().put(name, seq);
        if (from != null) {
            seq.addDirectionFrom(from);
            for (Node f : from) {
                Sequence s = flow.getSequence().get(f.getName());
                s.addDirectionTo(node);
            }
        }
        return node;
    }

}
