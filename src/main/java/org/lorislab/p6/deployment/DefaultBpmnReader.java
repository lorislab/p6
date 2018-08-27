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

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.lorislab.p6.bpmn2.Definitions;
import org.lorislab.p6.bpmn2.TEndEvent;
import org.lorislab.p6.bpmn2.TFlowElement;
import org.lorislab.p6.bpmn2.TFlowNode;
import org.lorislab.p6.bpmn2.TProcess;
import org.lorislab.p6.bpmn2.TSequenceFlow;
import org.lorislab.p6.bpmn2.TStartEvent;
import org.lorislab.p6.runtime.model.EndNode;
import org.lorislab.p6.runtime.model.Definition;
import org.lorislab.p6.runtime.model.RuntimeNode;
import org.lorislab.p6.runtime.model.RuntimeProcess;
import org.lorislab.p6.runtime.model.StartNode;

/**
 *
 * @author andrej
 */
public class DefaultBpmnReader {

    public Definition create(Definitions definitions) {
        if (definitions == null) {
            return null;
        }
        Definition result = new Definition();
        result.setId(definitions.getId());
        result.setName(definitions.getName());
        return result;
    }

    public RuntimeProcess create(TProcess process) {
        if (process == null) {
            return null;
        }
        RuntimeProcess result = new RuntimeProcess();

        result.setId(process.getId());
        result.setName(process.getName());

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
                    result.setStart(sn);
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
                    result.getNodes().put(node.getId(), node);

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
        return result;
    }

}
