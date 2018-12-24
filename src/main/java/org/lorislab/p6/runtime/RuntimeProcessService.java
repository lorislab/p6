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
