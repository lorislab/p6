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

import lombok.Data;

import javax.json.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class Sequence {

    private List<String> from = new ArrayList<>();

    private List<String> to = new ArrayList<>();

    public String next() {
        return to.get(0);
    }

    public Sequence() {}

    public Sequence(JsonObject json) {
        if (json != null) {
            JsonArray jfrom = json.getJsonArray("from");
            if (jfrom != null) {
                from = jfrom.getValuesAs(JsonString::getString);
            }
            JsonArray jto = json.getJsonArray("to");
            if (jto != null) {
                to = jto.getValuesAs(JsonString::getString);
            }
        }
    }

    public void addDirectionTo(Node ... nodes) {
        if (nodes != null && nodes.length > 0) {
            for (Node node : nodes) {
                to.add(node.getName());
            }
        }
    }

    public void addDirectionFrom(Node ... nodes) {
        if (nodes != null && nodes.length > 0) {
            for (Node node : nodes) {
                from.add(node.getName());
            }
        }
    }

    public JsonObject toJson() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
            if (from != null && !from.isEmpty()) {
                builder.add("from", Json.createArrayBuilder(from).build());
            }
        if (to != null && !to.isEmpty()) {
            builder.add("to", Json.createArrayBuilder(to).build());
        }
        return builder.build();
    }
}
