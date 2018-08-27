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
package org.lorislab.p6.deployment;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBElement;
import org.lorislab.jee.exception.ServiceException;
import org.lorislab.p6.bpmn2.Definitions;
import org.lorislab.p6.bpmn2.TProcess;
import org.lorislab.p6.bpmn2.TRootElement;
import org.lorislab.p6.model.ProcessContent;
import org.lorislab.p6.model.ProcessDefinition;
import org.lorislab.p6.rs.ModelSerializerService;
import org.lorislab.p6.runtime.model.RuntimeProcess;
import org.lorislab.p6.service.ProcessDefinitionService;

/**
 *
 * @author andrej
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class DeploymentService {
    
    @EJB
    private ProcessDefinitionService service;

    @EJB
    private ModelSerializerService serializer;
    
    private DefaultBpmnReader reader = new DefaultBpmnReader();
            
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public long deploy(Definitions definitions) throws ServiceException {
        long result = -1;
        for (JAXBElement<? extends TRootElement> e : definitions.getRootElements()) {
            if (e.getValue() instanceof TProcess) {
                RuntimeProcess rp = reader.create((TProcess) e.getValue());

                byte[] xml;
                byte[] flow;

                xml = serializer.definitionsToByte(definitions);

                flow = serializer.toByte(rp);
                
                ProcessDefinition pd = service.loadById(rp.getId());
                if (pd == null) {
                    pd = new ProcessDefinition();
                    pd.setId(rp.getId());
                    ProcessContent content = new ProcessContent();
                    content.setParent(pd);
                    content.setProcessVersion("1.0.0");
                    pd.getContents().add(content);
                    pd.setActive(content.getGuid());
                    content.setFlow(flow);
                    content.setXml(xml);
                    pd = service.create(pd);
                } else {
                    ProcessContent content = pd.getContents().iterator().next();
                    content.setFlow(flow);
                    content.setXml(xml);
                    pd = service.update(pd);
                }
                result = pd.getGuid();
            }
        }
        return result;        
    }
}
