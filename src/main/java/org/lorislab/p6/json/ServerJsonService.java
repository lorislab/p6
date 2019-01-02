package org.lorislab.p6.json;

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

    public static Map<String, Object> loadData(String data) {
        ServerData serverData = JSON.fromJson(data, ServerData.class);
        return serverData.getData();
    }

    public static String saveData(Map<String, Object> data) {
        ServerData serverData = new ServerData();
        serverData.setData(data);
        return JSON.toJson(serverData);
    }

    public static ProcessToken mergeData(ProcessToken token , String input) {
        if (input != null && !input.isBlank()) {
            Map<String, Object> data = new HashMap<>();
            if (token.getData() != null) {
                String tmp = new String(token.getData(), StandardCharsets.UTF_8);
                if (!tmp.isBlank()) {
                    data = ServerJsonService.loadData(tmp);
                }
            }
            Map<String, Object> newData = ServerJsonService.loadData(input);
            data.putAll(newData);
            String tmp = ServerJsonService.saveData(data);
            token.setData(tmp.getBytes(StandardCharsets.UTF_8));
        }
        return token;
    }

    public static ProcessToken mergeData(ProcessToken token, ProcessToken from) {
        if (from.getData() != null) {
            Map<String, Object> data = new HashMap<>();
            if (token.getData() != null) {
                String tmp = new String(token.getData(), StandardCharsets.UTF_8);
                if (!tmp.isBlank()) {
                    data = ServerJsonService.loadData(tmp);
                }
            }

            String tmp = new String(from.getData(), StandardCharsets.UTF_8);
            Map<String, Object> newData = ServerJsonService.loadData(tmp);
            data.putAll(newData);
            String resultData = ServerJsonService.saveData(data);
            token.setData(resultData.getBytes(StandardCharsets.UTF_8));
        }
        return token;
    }
}
