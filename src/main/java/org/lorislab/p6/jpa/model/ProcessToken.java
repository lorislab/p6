package org.lorislab.p6.jpa.model;

import lombok.Getter;
import lombok.Setter;
import org.lorislab.jee.jpa.model.PersistentTraceable;
import org.lorislab.p6.jpa.model.enums.ProcessTokenStatus;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "P6_PROCESS_TOKEN")
@NamedEntityGraphs({
        @NamedEntityGraph(name = "ProcessToken.load", attributeNodes = {
                @NamedAttributeNode("processInstance"), @NamedAttributeNode("parents")
        })
})
public class ProcessToken extends PersistentTraceable {

    private static final long serialVersionUID = 4640527800158274395L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESS_INSTANCE_GUID")
    private ProcessInstance processInstance;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "P6_PROCESS_TOKEN_PARENT",
            joinColumns = @JoinColumn(name = "TOKEN_GUID")
    )
    private Set<String> parents = new HashSet<>();

    @Column(name = "NODE_NAME")
    private String nodeName;

    @Column(name = "NODE_PREVIOUS")
    private String previousName;

    @Column(name = "TOKEN_STATUS")
    @Enumerated(EnumType.STRING)
    private ProcessTokenStatus status;

    @Column(name = "DATA", length = 5000)
    private byte[] data;
}
