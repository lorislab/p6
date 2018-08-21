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

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name = "P6_PROCESS_A_ERROR")
public class ProcessActivityError extends Persistent {
    
    private static final long serialVersionUID = -9001451728900151636L;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "ACTIVITY")
    private ProcessActivity activity;
    
    @Column(name = "CREATIONDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    
    @Column(name = "RETRY")
    private Long retry;
    
    @Column(name = "ERROR")
    private byte[] error;
    
    /**
     * Marks the entity as created.
     */
    @PrePersist
    public void prePersist() {
        Date date = new Date();
        creationDate = date;
    }    
}
