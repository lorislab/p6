package org.lorislab.p6.flow.model.gateway;

import lombok.Data;
import lombok.ToString;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.NodeType;

import java.util.Map;

@Data
@ToString(callSuper = true)
public abstract class Gateway extends Node {

    private SequenceFlow sequenceFlow = SequenceFlow.UNSPECIFIED;

    private String defaultSequence;

    @ToString.Exclude
    private Map<String, String> condition;

    public Gateway(NodeType nodeType) {
        super(nodeType);
    }

}
