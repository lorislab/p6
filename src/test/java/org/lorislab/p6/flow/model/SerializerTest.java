package org.lorislab.p6.flow.model;

import org.junit.Test;
import org.lorislab.p6.flow.json.JsonProcessFlowService;
import org.lorislab.p6.flow.model.event.StartEvent;
import org.lorislab.p6.flow.model.gateway.ParallelGateway;
import org.lorislab.p6.flow.model.gateway.SequenceFlow;
import org.lorislab.p6.flow.model.task.ServiceTask;

import java.nio.charset.StandardCharsets;

public class SerializerTest {

    @Test
    public void serilizeExampleTest() {
        ProcessFlowBuilder builder = new ProcessFlowBuilder();
        builder.setProcessId("org.lorislab.p6.example.Test1");
        builder.setProcessVersion("1.0.0");

        StartEvent s = builder.createStartEvent("start");
        ServiceTask n1 = builder.createServiceTask("service1", s);
        ParallelGateway g1 = builder.createParallelGatewayNode("gateway1", n1);
        g1.setSequenceFlow(SequenceFlow.DIVERGING);
        ServiceTask n3 = builder.createServiceTask("service3", g1);
        ServiceTask n4 = builder.createServiceTask("service4", g1);
        ParallelGateway g2 = builder.createParallelGatewayNode("gateway2", n3, n4);
        g2.setSequenceFlow(SequenceFlow.CONVERGING);
        builder.createEndEvent("end", g2);


        byte[] tmp = JsonProcessFlowService.saveProcessFlow(builder.build());
        String tmp2 = new String(tmp, StandardCharsets.UTF_8);
        System.out.println(tmp2);

        ProcessFlow pp2 = JsonProcessFlowService.loadProcessFlow(tmp2);
    }
}
