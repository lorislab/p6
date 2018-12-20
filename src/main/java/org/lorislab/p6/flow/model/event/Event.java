package org.lorislab.p6.flow.model.event;

import lombok.Data;
import org.lorislab.p6.flow.model.Node;

@Data
public class Event extends Node {

    private EventType type;

}
