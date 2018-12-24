package org.lorislab.p6.json;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ServerData {

    private Map<String, Object> data = new HashMap<>();


}
