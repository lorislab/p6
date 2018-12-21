package org.lorislab.p6.flow.model.event;

import lombok.Data;

@Data
public class EndEvent extends Event {

    public EndEvent() {
        super(EventType.END);
    }
}
