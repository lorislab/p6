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

import java.util.ArrayList;
import java.util.List;
import org.lorislab.p6.bpmn2.TProcess;
import org.lorislab.p6.bpmn2.TStartEvent;
import org.lorislab.p6.flow.FlowProcess;
import org.lorislab.p6.flow.events.EventDefinition;
import org.lorislab.p6.flow.events.StartEvent;
import org.lorislab.p6.flow.events.StartEventType;

/**
 *
 * @author andrej
 */
public class DeploymentReader {
    
    
    public static FlowProcess loadFlowProcess(TProcess process) {
        if (process == null) {
            return null;
        }
        FlowProcess result = new FlowProcess();
        
        return result;
    }
    
    public static StartEvent loadStartEvent(TStartEvent event) {
        if (event == null) {
            return null;
        }   
        List<EventDefinition> definitions = new ArrayList<>();

        //TODO: load the event definitions
//        event.getEventDefinitionReves()
//        event.getEventDefinitions()
                
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
