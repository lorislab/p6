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
 * The boundary event definition.
 *
 * Reference to the definition on the page 257 Boundary Event Definition
 *
 * @author andrej
 */
@Getter
@Setter
public class BoundaryEvent extends CatchEvent {

    /**
     * Denotes whether the Activity should be cancelled or not, i.e., whether
     * the boundary catch Event acts as an Error or an Escalation. If the
     * Activity is not cancelled, multiple instances of that handler can run
     * concurrently. This attribute cannot be applied to Error Events (where
     * it’s always true), or Compensation Events (where it doesn’t apply).
     */
    private boolean cancelActivity;
    
    public BoundaryEvent() {
        super(EventType.BOUNDARY);
    }

}
