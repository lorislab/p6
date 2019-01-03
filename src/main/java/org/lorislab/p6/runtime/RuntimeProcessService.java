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

package org.lorislab.p6.runtime;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.runtime.RuntimeProcess;

import javax.ejb.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Singleton
public class RuntimeProcessService {

    private Map<String, RuntimeProcess> processes = new ConcurrentHashMap<>();

    public void addRuntimeProcess(RuntimeProcess process) {
        String key = createKey(process.getDefinition().getProcessId(),process.getDefinition().getProcessVersion());
        processes.put(key, process);
    }

    public RuntimeProcess getRuntimeProcess(String processId, String processVersion) {
        String key = createKey(processId, processVersion);
        return processes.get(key);
    }

    private String createKey(String processId, String processVersion) {
        return processId + "#" + processVersion;
    }
}
