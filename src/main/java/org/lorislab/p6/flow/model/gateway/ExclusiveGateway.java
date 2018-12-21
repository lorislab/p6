package org.lorislab.p6.flow.model.gateway;

import lombok.Data;

@Data
public class ExclusiveGateway extends Gateway {

    public ExclusiveGateway() {
        super(GatewayType.EXCLUSIVE);
    }
}
