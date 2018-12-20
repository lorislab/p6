package org.lorislab.p6.flow.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Sequence {

    private List<String> from;

    private List<String> to;

    public void addDirectionTo(Node ... nodes) {
        if (nodes != null && nodes.length > 0) {
            if (to == null) {
                to = new ArrayList<>();
            }
            for (Node node : nodes) {
                to.add(node.getName());
            }
        }
    }

    public void addDirectionFrom(Node ... nodes) {
        if (nodes != null && nodes.length > 0) {
            if (from == null) {
                from = new ArrayList<>();
            }
            for (Node node : nodes) {
                from.add(node.getName());
            }
        }
    }

}
