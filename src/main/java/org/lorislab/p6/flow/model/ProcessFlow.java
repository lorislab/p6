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

import lombok.*;
import org.lorislab.p6.flow.model.activity.CallActivity;
import org.lorislab.p6.flow.model.event.EndEvent;
import org.lorislab.p6.flow.model.event.StartEvent;
import org.lorislab.p6.flow.model.gateway.Gateway;
import org.lorislab.p6.flow.model.gateway.ParallelGateway;
import org.lorislab.p6.flow.model.task.ServiceTask;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTransient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ToString(onlyExplicitlyIncluded = true)
@JsonbPropertyOrder({"processId","processVersion", "start", "nodes","sequence"})
public class ProcessFlow {

    @ToString.Include
    private String processId;

    @ToString.Include
    private String processVersion;

    private List<Node> nodes = new ArrayList<>();

    private Map<String, Sequence> sequence = new HashMap<>();

}
