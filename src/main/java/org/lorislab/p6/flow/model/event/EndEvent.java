package org.lorislab.p6.flow.model.event;

import lombok.Data;
import lombok.ToString;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.NodeType;

@Data
@ToString(callSuper = true)
public class EndEvent extends Node {

    public EndEvent() {
        super(NodeType.END_EVENT);
    }
}
