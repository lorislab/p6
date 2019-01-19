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

import lombok.*;
import org.lorislab.p6.flow.model.activity.CallActivity;
import org.lorislab.p6.flow.model.event.EndEvent;
import org.lorislab.p6.flow.model.event.StartEvent;
import org.lorislab.p6.flow.model.gateway.ExclusiveGateway;
import org.lorislab.p6.flow.model.gateway.Gateway;
import org.lorislab.p6.flow.model.gateway.InclusiveGateway;
import org.lorislab.p6.flow.model.gateway.ParallelGateway;
import org.lorislab.p6.flow.model.task.ScriptTask;
import org.lorislab.p6.flow.model.task.ServiceTask;

import javax.json.*;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTransient;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@ToString(onlyExplicitlyIncluded = true)
@JsonbPropertyOrder({"processId","processVersion", "start", "nodes","sequence"})
public class ProcessFlow {

    @ToString.Include
    private String processId;

    @ToString.Include
    private String processVersion;

    private List<Node> nodes = new ArrayList<>();

    public JsonObject toJson() {
        JsonArrayBuilder list = Json.createArrayBuilder();
        nodes.stream().map(Node::toJson).forEach(list::add);

        return Json.createObjectBuilder().
                add("processId", this.processId).
                add("processVersion", this.processVersion).
                add("nodes", list.build()).
                build();
    }

    private static EnumMap<NodeType, Function<JsonObject, ? extends Node>> MAPPING = new EnumMap<>(NodeType.class);

    static {
        // events
        MAPPING.put(NodeType.START_EVENT, StartEvent::fromJson);
        MAPPING.put(NodeType.END_EVENT, EndEvent::fromJson);

        // tasks
        MAPPING.put(NodeType.SERVICE_TASK, ServiceTask::fromJson);
        MAPPING.put(NodeType.SCRIPT_TASK, ScriptTask::fromJson);

        // gateway
        MAPPING.put(NodeType.PARALLEL_GATEWAY, ParallelGateway::fromJson);
        MAPPING.put(NodeType.INCLUSIVE_GATEWAY, InclusiveGateway::fromJson);
        MAPPING.put(NodeType.EXCLUSIVE_GATEWAY, ExclusiveGateway::fromJson);

        // activities
        MAPPING.put(NodeType.CALL_ACTIVITY, CallActivity::fromJson);
    }

    public static ProcessFlow fromJson(JsonObject json) {
        ProcessFlow result = new ProcessFlow();
        result.processId = json.getString("processId");
        result.processVersion = json.getString("processVersion");
        JsonArray nodes = json.getJsonArray("nodes");
        if (nodes != null) {
             for (JsonObject obj : nodes.getValuesAs(JsonValue::asJsonObject)) {
                NodeType type = Node.nodeType(obj);
                Node n = MAPPING.get(type).apply(obj);
                result.nodes.add(n);
            }
        }
        return result;
    }

}
