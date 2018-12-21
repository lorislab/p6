package org.lorislab.p6.flow.model.event;

import lombok.Data;
import org.lorislab.p6.flow.model.Node;
import org.lorislab.p6.flow.model.NodeType;

@Data
public class Event extends Node {

    private EventType eventType;

    public Event(EventType eventType) {
        super(NodeType.EVENT);
        this.eventType = eventType;
    }
}
