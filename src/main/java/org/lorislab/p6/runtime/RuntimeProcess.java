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

import lombok.Getter;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.NodeType;
import org.lorislab.p6.flow.model.ProcessFlow;
import org.lorislab.p6.flow.model.Sequence;
import org.lorislab.p6.jpa.model.ProcessDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuntimeProcess {

    @Getter
    private ProcessDefinition definition;

    private ProcessFlow flow;

    private Map<String, Node> nodes = new HashMap<>();

    @Getter
    private List<Node> start = new ArrayList<>();

    public RuntimeProcess(ProcessDefinition definition, ProcessFlow flow) {
        this.definition = definition;
        this.flow = flow;
        for (Node n : flow.getNodes()) {
            if (n.getNodeType() == NodeType.START_EVENT) {
                start.add(n);
            }
            nodes.put(n.getName(), n);
        }
    }

    public String getNextNodeName(String name) {
        Sequence seq = getSequence(name);
        if (seq != null) {
            return seq.next();
        }
        return null;
    }

    public Sequence getSequence(String name) {
        return flow.getSequence().get(name);
    }

    public Node getNode(String name) {
        return nodes.get(name);
    }

    @Override
    public String toString() {
        return definition.getProcessId();
    }
}
