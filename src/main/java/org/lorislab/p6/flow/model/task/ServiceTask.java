package org.lorislab.p6.flow.model.task;

import lombok.Data;
import lombok.ToString;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.NodeType;

@Data
@ToString(callSuper = true)
public class ServiceTask extends Node {

    public ServiceTask() {
        super(NodeType.SERVICE_TASK);
    }
}
