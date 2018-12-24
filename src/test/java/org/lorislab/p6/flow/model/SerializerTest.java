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
        ProcessFlow p = new ProcessFlow();
        p.setProcessId("org.lorislab.p6.example.Test1");
        p.setProcessVersion("1.0.0");

        StartEvent s = p.createStartEvent("start");
        ServiceTask n1 = p.createServiceTask("service1", s);
        ParallelGateway g1 = p.createParallelGatewayNode("gateway1", n1);
        g1.setSequenceFlow(SequenceFlow.DIVERGING);
        ServiceTask n3 = p.createServiceTask("service3", g1);
        ServiceTask n4 = p.createServiceTask("service4", g1);
        ParallelGateway g2 = p.createParallelGatewayNode("gateway2", n3, n4);
        g2.setSequenceFlow(SequenceFlow.CONVERGING);
        p.createEndEvent("end", g2);


        byte[] tmp = JsonProcessFlowService.saveProcessFlow(p);
        String tmp2 = new String(tmp, StandardCharsets.UTF_8);
        System.out.println(tmp2);

        ProcessFlow pp2 = JsonProcessFlowService.loadProcessFlow(tmp2);
    }
}
