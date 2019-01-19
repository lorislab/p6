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
package org.lorislab.p6.flow.json;

import org.lorislab.p6.flow.model.ProcessFlow;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.stream.JsonGenerator;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class JsonProcessFlowService {

    private static final Map<String,Object> CONFIG;

    static {
        CONFIG = new HashMap<>();
        CONFIG.put(JsonGenerator.PRETTY_PRINTING, true);
    }

    public static byte[] saveProcessFlow(ProcessFlow data) {
        StringWriter sw = new StringWriter();
        try (JsonWriter writer = Json.createWriterFactory(CONFIG).createWriter(sw)) {
            writer.writeObject(data.toJson());
        }
        String tmp = sw.toString();
        return tmp.getBytes(StandardCharsets.UTF_8);
    }

    public static ProcessFlow loadProcessFlow(byte[] data) {
        String tmp = new String(data, StandardCharsets.UTF_8);
        return loadProcessFlow(tmp);
    }

    public static ProcessFlow loadProcessFlow(String data) {
        ProcessFlow result = null;
        if (data != null) {
            try (JsonReader jsonReader = Json.createReader(new StringReader(data))) {
                JsonObject jobj = jsonReader.readObject();
                result = ProcessFlow.fromJson(jobj);
            }
        }
        return result;
    }
}
