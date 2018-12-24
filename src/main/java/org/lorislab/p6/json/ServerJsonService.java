package org.lorislab.p6.json;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.util.Map;

public class ServerJsonService {

    private static final Jsonb JSON;

    static {
        JsonbConfig config = new JsonbConfig()
                .withFormatting(true);
        JSON = JsonbBuilder.create(config);
    }

    public static Map<String, Object> loadData(String data) {
        ServerData serverData = JSON.fromJson(data, ServerData.class);
        return serverData.getData();
    }

    public static String saveData(Map<String, Object> data) {
        ServerData serverData = new ServerData();
        serverData.setData(data);
        return JSON.toJson(serverData);
    }
}
