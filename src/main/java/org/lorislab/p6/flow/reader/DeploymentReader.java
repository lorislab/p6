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
package org.lorislab.p6.flow.reader;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.JAXBElement;
import org.lorislab.p6.bpmn2.Definitions;
import org.lorislab.p6.bpmn2.TConditionalEventDefinition;
import org.lorislab.p6.bpmn2.TEventDefinition;
import org.lorislab.p6.bpmn2.TExpression;
import org.lorislab.p6.bpmn2.TItemDefinition;
import org.lorislab.p6.bpmn2.TMessageEventDefinition;
import org.lorislab.p6.bpmn2.TProcess;
import org.lorislab.p6.bpmn2.TRootElement;
import org.lorislab.p6.bpmn2.TSignal;
import org.lorislab.p6.bpmn2.TSignalEventDefinition;
import org.lorislab.p6.bpmn2.TStartEvent;
import org.lorislab.p6.bpmn2.TTimerEventDefinition;
import org.lorislab.p6.flow.FlowProcess;
import org.lorislab.p6.flow.events.EventDefinition;
import org.lorislab.p6.flow.events.Signal;
import org.lorislab.p6.flow.events.SignalEventDefinition;
import org.lorislab.p6.flow.events.StartEvent;
import org.lorislab.p6.flow.events.StartEventType;
import org.lorislab.p6.flow.events.TimerEventDefinition;
import org.lorislab.p6.runtime.model.Definition;

/**
 *
 * @author andrej
 */
public class DeploymentReader {
    
    public static List<FlowProcess> loadDefinitios(Definitions definition) {
        
        List<TProcess> processes = new ArrayList<>();
        List<JAXBElement<? extends TRootElement>> ele = definition.getRootElements();
        if (ele != null) {
            for (JAXBElement<? extends TRootElement> e : ele) {
                TRootElement root = e.getValue();
                if (root instanceof TProcess) {
                    processes.add((TProcess) root);
                } else if (root instanceof TSignal) {
                    
                } else if (root instanceof TItemDefinition) {
                    
                }
            }
        }
        
        if (!processes.isEmpty()) {
            List<FlowProcess> result = new ArrayList<>(processes.size());
            for (TProcess process : processes) {
                loadFlowProcess(process);
            }
            return result;
        }
        return null;
    }
    
    public static Signal loadSignal(TSignal signal) {
        if (signal == null) {
            return null;
        }
        Signal result = new Signal();
        result.setId(signal.getId());
        result.setName(signal.getName());
        signal.getStructureRef();
        return result;
    }
    
    public static FlowProcess loadFlowProcess(TProcess process) {
        if (process == null) {
            return null;
        }
        
        
        FlowProcess result = new FlowProcess();
        
        return result;
    }
    
    public static EventDefinition loadEventDefinition(TEventDefinition def) {
        if (def == null) {
            return null;
        }
        if (def instanceof TTimerEventDefinition) {
            TTimerEventDefinition te = (TTimerEventDefinition) def;
            TimerEventDefinition result = new TimerEventDefinition();
            result.setId(te.getId());
            result.setTimeDate(loadDate(te.getTimeDate()));
            result.setTimeCycle(loadDate(te.getTimeCycle()));
            result.setTimeDuration(loadDate(te.getTimeDuration()));
            return result;
        }
        if (def instanceof TSignalEventDefinition) {
            TSignalEventDefinition se = (TSignalEventDefinition) def;
            
            se.getSignalRef();
            SignalEventDefinition result = new SignalEventDefinition();
            result.setId(se.getId());
            return result;
        }
        if (def instanceof TMessageEventDefinition) {
            
        }
        if (def instanceof TConditionalEventDefinition) {
            
        }
        throw new RuntimeException("Not supported event definition: " + def.getClass().getName());
    }
    
    public static Date loadDate(TExpression expression) {
        if (expression == null || expression.getContent() == null || expression.getContent().isEmpty()) {
            return null;
        }
        // FIXME: expression to date multiline
        Date result = Date.from(OffsetDateTime.parse ( expression.getContent().get(0).toString() ).toInstant());        
        return result;
    }
    
    public static StartEvent loadStartEvent(TStartEvent event) {
        if (event == null) {
            return null;
        }   
        List<EventDefinition> definitions = new ArrayList<>();

        //TODO: load the event definitions
//        event.getEventDefinitionReves()

        List<JAXBElement<? extends TEventDefinition>> defs = event.getEventDefinitions();
        if (defs != null) {
            defs.stream().filter(Objects::nonNull).forEach((d) -> loadEventDefinition(d.getValue()));
        }
                
        StartEventType type = StartEventType.NONE;
        if (definitions.size() > 1) {
            if (event.isParallelMultiple()) {
                type = StartEventType.PARALLEL_MULTIPLE;
            } else {
                type = StartEventType.MULTIPLE;
            }
        } else if (definitions.size() == 1) { 
            EventDefinition def = definitions.get(0);
            switch (def.getType()) {
                case MESSAGE:
                    type = StartEventType.MESSAGE;
                    break;
                case TIMER:
                    type = StartEventType.TIMER;
                    break;
                case SIGNAL:
                    type = StartEventType.SIGNAL;
                    break;
                case CONDITIONAL:
                    type = StartEventType.CONDITIONAL;
                    break;
                default:
                    throw new RuntimeException("Not supported event definition type " + def.getType());
            }
        }
        StartEvent result = new StartEvent(type);        
        result.setId(event.getId());
        result.setName(event.getName());
        result.setInterrupting(event.isIsInterrupting());
        // catch
        result.setParallelMultiple(event.isParallelMultiple());
        
        if (!definitions.isEmpty()) {
            definitions.forEach(e -> result.getDefinitions().put(e.getId(), e));
        }
        return result;
    }
}
