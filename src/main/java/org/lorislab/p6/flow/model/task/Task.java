package org.lorislab.p6.flow.model.task;

import lombok.Data;
import lombok.ToString;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.NodeType;

@Data
@ToString(callSuper = true)
public class Task extends Node {

    private TaskType taskType;

    public Task(TaskType taskType) {
        super(NodeType.TASK);
        this.taskType = taskType;
    }
}