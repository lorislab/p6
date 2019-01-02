package org.lorislab.p6.runtime;

import lombok.Getter;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.NodeType;
import org.lorislab.p6.flow.model.ProcessFlow;
import org.lorislab.p6.flow.model.Sequence;
import org.lorislab.p6.jpa.model.ProcessDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuntimeProcess {

    @Getter
    private ProcessDefinition definition;

    private ProcessFlow flow;

    private Map<String, Node> nodes = new HashMap<>();

    @Getter
    private List<Node> start = new ArrayList<>();

    public RuntimeProcess(ProcessDefinition definition, ProcessFlow flow) {
        this.definition = definition;
        this.flow = flow;
        for (Node n : flow.getNodes()) {
            if (n.getNodeType() == NodeType.START_EVENT) {
                start.add(n);
            }
            nodes.put(n.getName(), n);
        }
    }

    public String getNextNodeName(String name) {
        Sequence seq = getSequence(name);
        if (seq != null) {
            return seq.next();
        }
        return null;
    }

    public Sequence getSequence(String name) {
        return flow.getSequence().get(name);
    }

    public Node getNode(String name) {
        return nodes.get(name);
    }

    @Override
    public String toString() {
        return definition.getProcessId();
    }
}
