package org.lorislab.p6.flow.model.activity;

import lombok.Data;
import lombok.ToString;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.NodeType;

@Data
@ToString(callSuper = true)
public class CallActivity extends Node {

    private String activity;

    public CallActivity() {
        super(NodeType.CALL_ACTIVITY);
    }
}
