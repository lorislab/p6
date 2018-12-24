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

import org.lorislab.p6.flow.model.ProcessFlow;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;


public class JsonProcessFlowService {

    private static final Jsonb JSON;

    static {
        JsonbConfig config = new JsonbConfig()
                .withFormatting(true)
                .withSerializers(new NodeSerializer())
                .withDeserializers(new NodeDeserializer());
        JSON = JsonbBuilder.create(config);
    }

    public static String saveProcessFlow(ProcessFlow data) {
        return JSON.toJson(data);
    }

    public static ProcessFlow loadProcessFlow(String data) {
        return JSON.fromJson(data, ProcessFlow.class);
    }
}
