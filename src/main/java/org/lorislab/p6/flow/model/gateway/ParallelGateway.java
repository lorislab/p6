package org.lorislab.p6.flow.model.gateway;

import lombok.Data;

@Data
public class ParallelGateway extends Gateway {

    public ParallelGateway() {
        super(GatewayType.PARALLEL);
    }
}
