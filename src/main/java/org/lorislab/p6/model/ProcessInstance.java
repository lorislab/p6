/*
 * Copyright 2018 lorislab.org
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
package org.lorislab.p6.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.lorislab.jee.jpa.model.BusinessTraceable;

/**
 *
 * @author andrej
 */
@Getter
@Setter
@Entity
@Table(name = "P6_PROCESS_INSTANCE")
@SequenceGenerator(name = "GEN_GUID", sequenceName = "P6_PROCESS_INST_SEQ", allocationSize = 1, initialValue = 1)
@NamedEntityGraphs({
    @NamedEntityGraph(name = "ProcessInstance.load", attributeNodes = { @NamedAttributeNode("tokens")})
})
public class ProcessInstance extends BusinessTraceable {
    
    private static final long serialVersionUID = 3015539788872131053L;
    
    @Column(name = "PROCESS_ID")
    private String processId;
    
    @Column(name = "PROCESS_DEFINITION")
    private Long processDefinition;
    
    @Column(name = "PROCESS_CONTENT")
    private String processContent;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "instance", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<ProcessToken> tokens = new HashSet<>();
    
}
