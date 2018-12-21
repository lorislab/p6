package org.lorislab.p6.flow.model.gateway;

import lombok.Data;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.NodeType;

import java.util.Map;

@Data
public class Gateway extends Node {

    private SequenceFlow sequenceFlow = SequenceFlow.UNSPECIFIED;

    private GatewayType gatewayType;

    private String defaultSequence;

    private Map<String, String> condition;

    public Gateway(GatewayType gatewayType) {
        super(NodeType.GATEWAY);
        this.gatewayType = gatewayType;
    }

}
