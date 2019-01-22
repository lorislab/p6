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

package org.lorislab.p6.rs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.lorislab.jee.jpa.model.PersistentTraceable;
import org.lorislab.jee.rs.model.PersistentTraceableDTO;
import org.lorislab.p6.jpa.model.ProcessInstance;
import org.lorislab.p6.jpa.model.enums.ProcessTokenStatus;

import javax.persistence.*;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ProcessTokenDTO extends PersistentTraceableDTO {

    private static final long serialVersionUID = 4640527800158274395L;

    private Set<String> parents = new HashSet<>();

    private String startNodeName;

    private String nodeName;

    private String previousName;

    private ProcessTokenStatus status;

    private byte[] data;

    private String processInstanceId;
}
