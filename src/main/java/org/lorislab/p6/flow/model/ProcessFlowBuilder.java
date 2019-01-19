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

package org.lorislab.p6.flow.model;

import org.lorislab.p6.flow.model.activity.CallActivity;
import org.lorislab.p6.flow.model.event.EndEvent;
import org.lorislab.p6.flow.model.event.StartEvent;
import org.lorislab.p6.flow.model.gateway.ParallelGateway;
import org.lorislab.p6.flow.model.task.ServiceTask;

public class ProcessFlowBuilder {

    private ProcessFlow flow = new ProcessFlow();

    public ProcessFlow build() {
        return flow;
    }

    public void setProcessId(String processId) {
        flow.setProcessId(processId);
    }

    public void setProcessVersion(String processVersion) {
        flow.setProcessVersion(processVersion);
    }

    public StartEvent createStartEvent(String name) {
        return updateNode(new StartEvent(), name);
    }

    public CallActivity createCallActivity(String name, Node... from) {
        return updateNode(new CallActivity(), name, from);
    }

    public ServiceTask createServiceTask(String name, Node... from) {
        return updateNode(new ServiceTask(), name, from);
    }

    public ParallelGateway createParallelGatewayNode(String name, Node... from) {
        return updateNode(new ParallelGateway(), name, from);
    }

    public EndEvent createEndEvent(String name, Node... from) {
        return updateNode(new EndEvent(), name, from);
    }

    private <T extends Node> T updateNode(T node, String name, Node... from) {
        node.setName(name);
        flow.getNodes().add(node);
        if (from != null) {
            node.getSequence().addDirectionFrom(from);
            for (Node f : from) {
                f.getSequence().addDirectionTo(node);
            }
        }
        return node;
    }

}
