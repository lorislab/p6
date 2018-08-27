/*
 * Copyright 2018 lorislab.org
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
package org.lorislab.p6.rs;

import java.io.ByteArrayOutputStream;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.Queue;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import org.lorislab.p6.model.ProcessInstance;
import org.lorislab.p6.model.ProcessToken;
import org.lorislab.p6.runtime.model.RuntimeProcess;
import org.lorislab.p6.runtime.service.ExecutionStartService;
import org.lorislab.p6.service.ProcessInstanceService;

/**
 *
 * @author andrej
 */
@Stateless
@Path("process")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProcessRsService {
    
    @Inject
    private JMSContext context;
 
    @Resource(lookup = "java:/jms/queue/p6.execution")
    private Queue queue;
    
    @EJB
    private ExecutionStartService service;
    
    @EJB
    private ProcessInstanceService piService;
    
    @EJB
    private ModelSerializerService serializer;
    
    @POST
    @Path("{id}/start")
    public void startProcess(@PathParam("id") String id, Object value) throws Exception {
        
        byte[] content = serializer.toByte(value);
        
        RuntimeProcess rp = service.getRuntimeProcess(id);
        
        ProcessInstance pi = new ProcessInstance();
        pi.setProcessId(id);
        
        if (rp.getStart() != null) {
            ProcessToken pt = new ProcessToken();
            pt.setInstance(pi);
            pt.setContent(content);
            pt.setNodeId(rp.getId());
            pi.getTokens().add(pt);
            context.createProducer().send(queue, pt.getGuid());
        }
        
        piService.create(pi);
        
        
    }
}
