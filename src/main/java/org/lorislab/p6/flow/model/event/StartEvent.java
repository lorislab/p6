package org.lorislab.p6.flow.model.event;

import lombok.Data;

@Data
public class StartEvent extends Event {

    public StartEvent() {
        super(EventType.START);
    }
}
