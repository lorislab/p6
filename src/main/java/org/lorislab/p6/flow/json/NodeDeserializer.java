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
package org.lorislab.p6.flow.json;

import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.NodeType;
import org.lorislab.p6.flow.model.activity.CallActivity;
import org.lorislab.p6.flow.model.event.EndEvent;
import org.lorislab.p6.flow.model.event.StartEvent;
import org.lorislab.p6.flow.model.gateway.ExclusiveGateway;
import org.lorislab.p6.flow.model.gateway.InclusiveGateway;
import org.lorislab.p6.flow.model.gateway.ParallelGateway;
import org.lorislab.p6.flow.model.task.ServiceTask;

import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.util.EnumMap;

public class NodeDeserializer implements JsonbDeserializer<Node> {

    private static EnumMap<NodeType, Class<? extends Node>> MAPPING = new EnumMap<>(NodeType.class);

    static {
        // events
        MAPPING.put(NodeType.START_EVENT, StartEvent.class);
        MAPPING.put(NodeType.END_EVENT, EndEvent.class);

        // tasks
        MAPPING.put(NodeType.SERVICE_TASK, ServiceTask.class);

        // gateway
        MAPPING.put(NodeType.PARALLEL_GATEWAY, ParallelGateway.class);
        MAPPING.put(NodeType.INCLUSIVE_GATEWAY, InclusiveGateway.class);
        MAPPING.put(NodeType.EXCLUSIVE_GATEWAY, ExclusiveGateway.class);

        // activities
        MAPPING.put(NodeType.CALL_ACTIVITY, CallActivity.class);
    }

    @Override
    public Node deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        parser.next();
        String tmp = parser.getString();
        parser.next();

        NodeType type;
        try {
            type = NodeType.valueOf(tmp);
        } catch (Exception ex) {
            throw new JsonbException("Cannot deserialize object for not valide type " + tmp);
        }

        Class<? extends Node> clazz = MAPPING.get(type);
        if (clazz == null) {
            throw new JsonbException("Cannot deserialize object for the type " + type);
        }
        return ctx.deserialize(clazz.asSubclass(Node.class), parser);
    }
}
