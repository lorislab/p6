package org.lorislab.p6.flow.model.gateway;

import lombok.Data;
import org.lorislab.p6.flow.model.NodeType;

@Data
public class ParallelGateway extends Gateway {

    public ParallelGateway() {
        super(NodeType.PARALLEL_GATEWAY);
    }
}
