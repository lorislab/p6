package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.model.RuntimeProcess;

import javax.ejb.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Singleton
public class RuntimeProcessService {

    private Map<String, RuntimeProcess> processes = new ConcurrentHashMap<>();

    public void addRuntimeProcess(RuntimeProcess process) {
        processes.put(process.getProcessId(), process);
    }

    public RuntimeProcess getRuntimeProcess(String processId) {
        return processes.get(processId);
    }

}
