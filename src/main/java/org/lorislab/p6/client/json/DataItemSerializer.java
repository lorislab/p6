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
package org.lorislab.p6.client.json;

import org.lorislab.p6.client.json.DataItem;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

public class DataItemSerializer implements JsonbSerializer<DataItem> {

    @Override
    public void serialize(DataItem node, JsonGenerator generator, SerializationContext ctx) {
        if (node.getData() != null) {
            generator.writeStartObject();
            ctx.serialize(node.getData().getClass().getName(), node.getData(), generator);
            generator.writeEnd();
        } else {
            generator.writeNull();
        }
    }
}