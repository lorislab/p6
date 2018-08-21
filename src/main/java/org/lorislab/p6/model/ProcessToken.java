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
package org.lorislab.p6.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.lorislab.jee.jpa.model.Persistent;

/**
 *
 * @author andrej
 */
@Getter
@Setter
@Entity
@Table(name = "P6_PROCESS_TOKEN")
public class ProcessToken extends Persistent {
    
    private static final long serialVersionUID = -5539935583814618436L;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSTANCE")
    private ProcessInstance instance;
    
    @Column(name = "parent")
    private String parent;
    
    @Column(name = "CONTENT")
    private byte[] content;
    
    @Column(name = "NODE_ID")
    private String nodeId;
}
