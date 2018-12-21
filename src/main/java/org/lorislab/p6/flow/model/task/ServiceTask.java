package org.lorislab.p6.flow.model.task;

import lombok.Data;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.NodeType;

@Data
public class ServiceTask extends Task {

    public ServiceTask() {
        super(TaskType.SERVICE_TASK);
    }
}
