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
import lombok.RequiredArgsConstructor;

import javax.json.*;
import javax.json.bind.annotation.JsonbTransient;

@Data
@RequiredArgsConstructor()
public abstract class Node {

    private String name;

    private Sequence sequence = new Sequence();

    private NodeType nodeType;

    public Node(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public Node(NodeType nodeType, JsonObject json) {
        this(nodeType);
        name = json.getString("name");
        JsonObject jseq = json.getJsonObject("sequence");
        if (jseq != null) {
            sequence = new Sequence(jseq);
        }
    }

    public JsonObjectBuilder toJson() {
        return Json.createObjectBuilder().
                add("name", this.name).
                add("nodeType", this.nodeType.name()).
                add("sequence", sequence.toJson());
    }

    protected void updateJson(JsonObject json) {
        name = json.getString("name");
        JsonObject jseq = json.getJsonObject("sequence");
        if (jseq != null) {
            sequence = new Sequence(jseq);
        }
    }

    public static NodeType nodeType(JsonObject json) {
        return NodeType.valueOf(json.getString("nodeType"));
    }
}
