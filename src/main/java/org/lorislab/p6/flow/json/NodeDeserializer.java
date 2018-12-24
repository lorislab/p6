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

import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;

public class NodeDeserializer implements JsonbDeserializer<Node> {
    @Override
    public Node deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        parser.next();

        String className = parser.getString();
        parser.next();

        try {
            return ctx.deserialize(Class.forName(className).asSubclass(Node.class), parser);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new JsonbException("Cannot deserialize object.");
        }
    }
}
