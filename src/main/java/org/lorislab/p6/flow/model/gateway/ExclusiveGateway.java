package org.lorislab.p6.flow.model.gateway;

import lombok.Data;
import lombok.ToString;
import org.lorislab.p6.flow.model.NodeType;

@Data
@ToString(callSuper = true)
public class ExclusiveGateway extends Gateway {

    public ExclusiveGateway() {
        super(NodeType.EXCLUSIVE_GATEWAY);
    }
}
