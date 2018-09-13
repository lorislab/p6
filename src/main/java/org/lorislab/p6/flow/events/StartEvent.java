/*
 * Copyright 2018 lorislab.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lorislab.p6.flow.events;

import lombok.Getter;
import lombok.Setter;

/**
 * The start event.
 *
 * @author andrej
 */
@Getter
@Setter
public class StartEvent extends CatchEvent {

    /**
     * This attribute only applies to Start Events of Event Sub-Processes ; it
     * is ignored for other Start Events. This attribute denotes whether the
     * Sub-Process encompassing the Event Sub-Process should be cancelled or
     * not, If the encompassing Sub- Process is not cancelled, multiple
     * instances of the Event Sub-Process can run concurrently. This attribute
     * cannot be applied to Error Events (where it’s always true), or
     * Compensation Events (where it doesn’t apply).
     */
    private boolean isInterrupting = true;

    /**
     * The type of the start event.
     */
    private StartEventType startType;

    public StartEvent(StartEventType startType) {
        super(EventType.START);
        this.startType = startType;
    }
}
