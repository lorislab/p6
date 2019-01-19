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
package org.lorislab.p6.flow.model.gateway;

import lombok.Data;
import lombok.ToString;
import org.lorislab.p6.flow.model.NodeType;
import org.lorislab.p6.flow.model.event.StartEvent;

import javax.json.JsonObject;

@Data
@ToString(callSuper = true)
public class ExclusiveGateway extends Gateway {

    public ExclusiveGateway() {
        super(NodeType.EXCLUSIVE_GATEWAY);
    }

    public static ExclusiveGateway fromJson(JsonObject json) {
        ExclusiveGateway result = new ExclusiveGateway();
        result.updateJson(json);
        return result;
    }
}
