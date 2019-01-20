/*
 * Copyright 2019 lorislab.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lorislab.p6.flow.model;

import org.junit.jupiter.api.Test;
import org.lorislab.p6.flow.json.JsonProcessFlowService;
import org.lorislab.p6.flow.model.event.StartEvent;
import org.lorislab.p6.flow.model.gateway.ParallelGateway;
import org.lorislab.p6.flow.model.gateway.SequenceFlow;
import org.lorislab.p6.flow.model.task.ServiceTask;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.stream.JsonGenerator;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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

    @Test
    public void serilizeTest() {
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


        ProcessFlow flow = builder.build();
        JsonObject obj = flow.toJson();

        Map<String,Object> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);

        StringWriter sw = new StringWriter();
        try (JsonWriter writer = Json.createWriterFactory(config).createWriter(sw)) {
            writer.writeObject(obj);
        }
        String out = sw.toString();
        System.out.println(out);

        ProcessFlow loaded;
        try (JsonReader jsonReader = Json.createReader(new StringReader(out))) {
            JsonObject jobj = jsonReader.readObject();
            loaded = ProcessFlow.fromJson(jobj);
        }
        System.out.println(loaded.toJson());
    }
}
