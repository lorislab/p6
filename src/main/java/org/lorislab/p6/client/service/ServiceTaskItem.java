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

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.HashMap;
import java.util.Map;

@Builder
public class ServiceTaskItem {

    @Getter
    private String processId;

    @Getter
    private String processVersion;

    @Getter
    private String processInstanceId;

    @Getter
    private String serviceTaskName;

    @Getter
    private String tokenId;

    @Singular
    private Map<String, Object> parameters = new HashMap<>();

    @Singular
    @Getter(value = AccessLevel.PACKAGE)
    private Map<String, Object> results = new HashMap<>();


    public <T> T getParameter(String name) {
        return (T) parameters.get(name);
    }

    public void addResult(String name, Object value) {
        results.put(name, value);
    }

    public void addResults(Map<String, Object> data) {
        if (data != null) {
            results.putAll(data);
        }
    }

}
