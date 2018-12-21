package org.lorislab.p6.flow.model.activity;

import lombok.Data;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.NodeType;

@Data
public class CallActivity extends Node {

    private String activity;

    public CallActivity() {
        super(NodeType.ACTIVITY);
    }
}
