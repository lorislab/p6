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
package org.lorislab.p6.flow.model.activity;

import lombok.Data;
import lombok.ToString;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.NodeType;
import org.lorislab.p6.flow.model.gateway.ParallelGateway;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

@Data
@ToString(callSuper = true)
public class CallActivity extends Node {

    private String activity;

    public CallActivity() {
        super(NodeType.CALL_ACTIVITY);
    }

    @Override
    public JsonObjectBuilder toJson() {
        JsonObjectBuilder builder = super.toJson();
        builder.add("activity", activity);
        return builder;
    }

    public static CallActivity fromJson(JsonObject json) {
        CallActivity result = new CallActivity();
        result.updateJson(json);
        result.activity = json.getString("activity");
        return result;
    }
}
