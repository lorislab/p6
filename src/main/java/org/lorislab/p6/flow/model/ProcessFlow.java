/*
 * Copyright 2018 lorislab.org.
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

import lombok.Data;
import lombok.ToString;
import org.lorislab.p6.flow.model.activity.CallActivity;
import org.lorislab.p6.flow.model.event.EndEvent;
import org.lorislab.p6.flow.model.event.StartEvent;
import org.lorislab.p6.flow.model.gateway.Gateway;
import org.lorislab.p6.flow.model.gateway.ParallelGateway;
import org.lorislab.p6.flow.model.task.ServiceTask;

import javax.json.bind.annotation.JsonbPropertyOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ToString(onlyExplicitlyIncluded = true)
@JsonbPropertyOrder({"processId","processVersion", "start", "nodes","sequence"})
public class ProcessFlow {

    @ToString.Include
    private String processId;

    @ToString.Include
    private String processVersion;

    private Map<String, Node> nodes = new HashMap<>();

    private List<String> start = new ArrayList<>();

    private Map<String, Sequence> sequence = new HashMap<>();

    public String getNextNodeName(String name) {
        Sequence seq = sequence.get(name);
        return seq.next();
    }

    public StartEvent createStartEvent(String name) {
        start.add(name);
        return updateNode(new StartEvent(), name);
    }

    public CallActivity createCallActivity(String name, Node ... from) {
        return updateNode(new CallActivity(), name, from);
    }

    public ServiceTask createServiceTask(String name, Node ... from) {
        return updateNode(new ServiceTask(), name, from);
    }

    public ParallelGateway createParallelGatewayNode(String name, Node ... from) {
        return updateNode(new ParallelGateway(), name, from);
    }

    public EndEvent createEndEvent(String name, Node ... from) {
        return updateNode(new EndEvent(), name, from);
    }

    private <T extends Node> T updateNode(T node, String name, Node ... from) {
        node.setName(name);
        nodes.put(name, node);
        Sequence seq = new Sequence();
        sequence.put(name, seq);
        if (from != null) {
            seq.addDirectionFrom(from);
            for (Node f : from) {
                Sequence s = sequence.get(f.getName());
                s.addDirectionTo(node);
            }
        }
        return node;
    }
}
