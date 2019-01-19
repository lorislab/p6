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

package org.lorislab.p6.service;

import org.lorislab.p6.jpa.model.ProcessToken;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ServerJsonService {

    private static final Jsonb JSON;

    static {
        JsonbConfig config = new JsonbConfig()
                .withFormatting(true);
        JSON = JsonbBuilder.create(config);
    }

    public static String toString(Object data) {
        if (data != null) {
            return JSON.toJson(data);
        }
        return null;
    }

    private static Map fromString(String data) {
        if (data != null) {
            return JSON.fromJson(data, Map.class);
        }
        return null;
    }


    public static Map fromToken(ProcessToken token) {
        Map data = new HashMap<>();
        if (token != null && token.getData() != null) {
            String tmp = new String(token.getData(), StandardCharsets.UTF_8);
            data = fromString(tmp);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public static ProcessToken mergeData(ProcessToken token, Map newData) {
        if (newData != null && token != null) {
            Map data = fromToken(token);
            data.putAll(newData);
            String tmp = toString(data);
            token.setData(tmp.getBytes(StandardCharsets.UTF_8));
        }
        return token;
    }

    public static ProcessToken mergeData(ProcessToken token, String json) {
        return mergeData(token, fromString(json));
    }

    public static ProcessToken mergeData(ProcessToken token, ProcessToken from) {
        return mergeData(token, fromToken(from));
    }
}
