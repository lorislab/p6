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
