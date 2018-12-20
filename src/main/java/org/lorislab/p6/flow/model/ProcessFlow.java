package org.lorislab.p6.flow.model;

import lombok.Data;
import org.lorislab.p6.flow.model.event.EndEvent;
import org.lorislab.p6.flow.model.event.StartEvent;

import javax.json.bind.annotation.JsonbPropertyOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonbPropertyOrder({"processId","processVersion", "start", "nodes","sequence"})
public class ProcessFlow {

    private String processId;

    private String processVersion;

    private Map<String, Node> nodes = new HashMap<>();

    private List<String> start = new ArrayList<>();

    private Map<String, Sequence> sequence = new HashMap<>();

    public StartEvent createStartEvent(String name) {
        start.add(name);
        return updateNode(new StartEvent(), name);
    }

    public CallActivity createCallActivity(String name, Node ... from) {
        return updateNode(new CallActivity(), name, from);
    }

    public ServiceTask createServiceTask(String name, Node ... from) {
        return updateNode(new ServiceTask(), name, from);
    }

    public GatewayNode createGatewayNode(String name, Node ... from) {
        return updateNode(new GatewayNode(), name, from);
    }

    public EndEvent createEndEvent(String name, Node ... from) {
        return updateNode(new EndEvent(), name, from);
    }

    private <T extends Node> T updateNode(T node, String name, Node ... from) {
        node.setName(name);
        nodes.put(name, node);
        Sequence seq = new Sequence();
        sequence.put(name, seq);
        if (from != null) {
            seq.addDirectionFrom(from);
            for (Node f : from) {
                Sequence s = sequence.get(f.getName());
                s.addDirectionTo(node);
            }
        }
        return node;
    }
}
