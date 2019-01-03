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

import org.junit.Test;
import org.lorislab.p6.client.service.ClientJsonService;
import org.lorislab.p6.flow.model.task.ServiceTask;
import org.lorislab.p6.json.ServerJsonService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JsonSerializerTest {

    @Test
    public void serilizeExampleTest() {

        Map<String, Object> data = new HashMap<>();
        data.put("key1", "1234");
        data.put("key2", 1234);
        data.put("key3", new ArrayList<String>(Arrays.asList("1","2")));
        data.put("key4", null);
        ServiceTask task = new ServiceTask();
        task.setName("ASD");
        data.put("serviceTask", task);

        String tmp2 = ClientJsonService.saveData(data);
        System.out.println(tmp2);


        Map<String, Object> nt = ServerJsonService.loadData(tmp2);
        System.out.println(nt);
        for (Map.Entry<String, Object> e : nt.entrySet()) {
            System.out.println(e.getKey() + " - " +  e.getValue());
        }
        String tmp3 = ServerJsonService.saveData(nt);
        System.out.println(tmp3);

        Map<String, Object> data2 = ClientJsonService.loadData(tmp2);
        for (Map.Entry<String, Object> e : data2.entrySet()) {
            System.out.println(e.getKey() + " - " + e.getValue());
        }



    }
}
