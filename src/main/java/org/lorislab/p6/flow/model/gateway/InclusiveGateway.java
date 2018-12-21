package org.lorislab.p6.flow.model.gateway;

import lombok.Data;

@Data
public class InclusiveGateway extends Gateway {

    public InclusiveGateway() {
        super(GatewayType.INCLUSIVE);
    }
}
