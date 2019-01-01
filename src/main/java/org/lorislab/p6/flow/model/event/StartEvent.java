package org.lorislab.p6.flow.model.event;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class StartEvent extends Event {

    public StartEvent() {
        super(EventType.START);
    }
}
