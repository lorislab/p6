/*
 * Copyright 2018 lorislab.
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
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.lorislab.p6.bpmn2.Definitions;
import org.lorislab.p6.bpmn2.TEndEvent;
import org.lorislab.p6.bpmn2.TFlowElement;
import org.lorislab.p6.bpmn2.TFlowNode;
import org.lorislab.p6.bpmn2.TProcess;
import org.lorislab.p6.bpmn2.TRootElement;
import org.lorislab.p6.bpmn2.TSequenceFlow;
import org.lorislab.p6.bpmn2.TStartEvent;
import org.lorislab.p6.model.ProcessContent;
import org.lorislab.p6.model.ProcessDefinition;
import org.lorislab.p6.runtime.model.EndNode;
import org.lorislab.p6.runtime.model.RuntimeNode;
import org.lorislab.p6.runtime.model.RuntimeProcess;
import org.lorislab.p6.runtime.model.StartNode;
import org.lorislab.p6.service.ProcessDefinitionService;

/**
 *
 * @author andrej
 */
@Path("deployment")
public class DeploymentRsService {

    @Context
    protected Providers providers;

    @EJB
    private ProcessDefinitionService service;

    private RuntimeProcess deployProcess(TProcess process) {

        RuntimeProcess rp = new RuntimeProcess();
        rp.setId(process.getId());
        rp.setName(process.getName());

        Map<String, TSequenceFlow> sequences = new HashMap<>();
        for (JAXBElement<? extends TFlowElement> f : process.getFlowElements()) {
            TFlowElement flow = (TFlowElement) f.getValue();
            if (flow instanceof TSequenceFlow) {
                TSequenceFlow task = (TSequenceFlow) flow;
                sequences.put(task.getId(), task);
            }
        }
        for (JAXBElement<? extends TFlowElement> f : process.getFlowElements()) {
            TFlowElement flow = (TFlowElement) f.getValue();
            if (flow instanceof TFlowNode) {

                if (flow instanceof TStartEvent) {
                    StartNode sn = new StartNode();
                    sn.setId(flow.getId());
                    sn.setName(flow.getName());
                    TFlowNode task = (TFlowNode) flow;
                    for (QName out : task.getOutgoings()) {
                        TFlowNode target = (TFlowNode) sequences.get(out.getLocalPart()).getTargetRef();
                        sn.getOutgoings().add(target.getId());
                    }
                    rp.setStart(sn);
                } else {

                    RuntimeNode node;
                    if (flow instanceof TEndEvent) {
                        node = new EndNode();
                    } else {
                        node = new RuntimeNode();
                    }
                    node.setId(flow.getId());
                    node.setName(flow.getName());
                    node.setClazz(flow.getClass().getName());
                    rp.getNodes().put(node.getId(), node);

                    TFlowNode task = (TFlowNode) flow;
                    for (QName out : task.getOutgoings()) {
                        TFlowNode target = (TFlowNode) sequences.get(out.getLocalPart()).getTargetRef();
                        node.getOutgoings().add(target.getId());
                    }
                    for (QName in : task.getIncomings()) {
                        TFlowNode target = (TFlowNode) sequences.get(in.getLocalPart()).getSourceRef();
                        node.getIncomings().add(target.getId());
                    }
                }
            }
        }

        return rp;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public long deploy(Definitions definitions) throws Exception {
        long result = -1;
        for (JAXBElement<? extends TRootElement> e : definitions.getRootElements()) {
            if (e.getValue() instanceof TProcess) {
                RuntimeProcess rp = deployProcess((TProcess) e.getValue());

                byte[] xml;
                byte[] flow;

                MessageBodyWriter<Definitions> xmbw = providers.getMessageBodyWriter(Definitions.class, null, null, MediaType.APPLICATION_XML_TYPE);
                try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
                    xmbw.writeTo(definitions, definitions.getClass(), null, null, MediaType.APPLICATION_XML_TYPE, null, b);
                    xml = b.toByteArray();
                }

                MessageBodyWriter<RuntimeProcess> mbw = providers.getMessageBodyWriter(RuntimeProcess.class, null, null, MediaType.APPLICATION_JSON_TYPE);

                try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
                    mbw.writeTo(rp, rp.getClass(), null, null, MediaType.APPLICATION_JSON_TYPE, null, b);
                    flow = b.toByteArray();
                }

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
