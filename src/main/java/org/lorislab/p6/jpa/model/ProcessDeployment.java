package org.lorislab.p6.jpa.model;

import lombok.Getter;
import lombok.Setter;
import org.lorislab.jee.jpa.model.PersistentTraceable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "P6_PROCESS_DEPLOYMENT")
public class ProcessDeployment extends PersistentTraceable {

    @Column(name = "PROCESS_DEF_GUID")
    private String processDefinitionGuid;

    @Column(name = "PROCESS_ID")
    private String processId;

    @Column(name = "PROCESS_VERSION")
    private String processVersion;

}
