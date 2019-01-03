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

package org.lorislab.p6.jpa.model;

import lombok.Getter;
import lombok.Setter;
import org.lorislab.jee.jpa.model.PersistentTraceable;
import org.lorislab.p6.jpa.model.enums.ProcessInstanceStatus;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "P6_PROCESS_INSTANCE")
public class ProcessInstance extends PersistentTraceable {

    private static final long serialVersionUID = -598431936165734978L;

    @Column(name = "PROCESS_PARENT_GUID")
    private String processInstanceParentGuid;

    @Column(name = "PROCESS_DEF_GUID")
    private String processDefinitionGuid;

    @Column(name = "PROCESS_ID")
    private String processId;

    @Column(name = "PROCESS_VERSION")
    private String processVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private ProcessInstanceStatus status;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "processInstance",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true)
    private Set<ProcessToken> tokens = new HashSet<>();

}
