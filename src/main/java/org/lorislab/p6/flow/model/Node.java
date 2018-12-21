package org.lorislab.p6.flow.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor()
public class Node {

    private String name;

    private NodeType nodeType;

    public Node(NodeType nodeType) {
        this.nodeType = nodeType;
    }
}
