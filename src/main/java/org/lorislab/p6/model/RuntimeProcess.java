package org.lorislab.p6.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.ProcessFlow;
import org.lorislab.p6.jpa.model.ProcessDefinition;


@Data
@AllArgsConstructor
public class RuntimeProcess {

    private ProcessDefinition definition;

    private ProcessFlow flow;

    public Node getNode(String name) {
        return flow.getNodes().get(name);
    }
}
