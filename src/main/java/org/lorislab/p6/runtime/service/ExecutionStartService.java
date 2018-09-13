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
package org.lorislab.p6.runtime.service;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.lorislab.jee.exception.ServiceException;
import org.lorislab.p6.model.ProcessContent;
import org.lorislab.p6.model.ProcessDefinition;
import org.lorislab.p6.rs.ModelSerializerService;
import org.lorislab.p6.runtime.model.RuntimeProcess;
import org.lorislab.p6.service.ProcessDefinitionService;

/**
 *
 * @author andrej
 */
@Singleton
@Startup
public class ExecutionStartService {

    @EJB
    private ModelSerializerService serializer;
        
    @EJB
    private ProcessDefinitionService service;

    private final Map<String, RuntimeProcess> processes = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void start() {
        try {
            List<ProcessDefinition> defs = service.loadAll();
            if (defs != null) {
                for (ProcessDefinition def : defs) {
                    ProcessContent content = def.getContents().iterator().next();
                    RuntimeProcess rp = serializer.fromByte(content.getFlow(), RuntimeProcess.class);
                    processes.put(def.getId(), rp);
                }
            }
        } catch (ServiceException ex) {
            ex.printStackTrace();
        }
    }
    
    public RuntimeProcess getRuntimeProcess(String id) {
        return processes.get(id);
    }
}
