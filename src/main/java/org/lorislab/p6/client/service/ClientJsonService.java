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
package org.lorislab.p6.client.service;

import org.lorislab.p6.client.json.ClientData;
import org.lorislab.p6.client.json.DataItem;
import org.lorislab.p6.client.json.DataItemDeserializer;
import org.lorislab.p6.client.json.DataItemSerializer;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.util.HashMap;
import java.util.Map;

public class ClientJsonService {

    private static final Jsonb JSON;

    static {
        JsonbConfig config = new JsonbConfig()
                .withFormatting(true)
                .withDeserializers(new DataItemDeserializer())
                .withSerializers(new DataItemSerializer());
        JSON = JsonbBuilder.create(config);
    }

    public static Map<String, Object> loadData(String data) {
        ClientData clientData = JSON.fromJson(data, ClientData.class);
        Map<String, Object > result = new HashMap<>();
        for (Map.Entry<String, DataItem> e : clientData.getData().entrySet()) {
            Object d = null;
            if (e.getValue() != null) {
                d = e.getValue().getData();
            }
            result.put(e.getKey(), d);
        }
        return result;
    }

    public static String saveData(Map<String, Object> data) {
        ClientData clientData = new ClientData();
        for (Map.Entry<String, Object> e : data.entrySet()) {
            DataItem item = new DataItem();
            item.setData(e.getValue());
            clientData.getData().put(e.getKey(), item);
        }
        return JSON.toJson(clientData);
    }
}
