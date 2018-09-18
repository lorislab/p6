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

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * The timer event definition.
 * 
 * Reference to the definition on the page 274 Timer Event
 * 
 * @author andrej
 */
@Getter
@Setter
public class TimerEventDefinition extends EventDefinition {

    /**
     * If the trigger is a Timer, then a timeDate MAY be entered. Timer
     * attributes are mutually exclusive and if any of the other Timer
     * attributes is set, timeDate MUST NOT be set (if the isExecutable
     * attribute of the Process is set to true). The return type of the
     * attribute timeDate MUST conform to the ISO-8601 format for date and time
     * representations.
     */
    private Date timeDate;

    /**
     * If the trigger is a Timer, then a timeDuration MAY be entered. Timer
     * attributes are mutually exclusive and if any of the other Timer
     * attributes is set, timeDuration MUST NOT be set (if the isExecutable
     * attribute of the Process is set to true). The return type of the
     * attribute timeDuration MUST conform to the ISO-8601 format for time
     * interval representations.
     */
    private Date timeDuration;

    /**
     * If the trigger is a Timer, then a timeCycle MAY be entered. Timer
     * attributes are mutually exclusive and if any of the other Timer
     * attributes is set, timeCycle MUST NOT be set (if the isExecutable
     * attribute of the Process is set to true). The return type of the
     * attribute timeCycle MUST conform to the ISO-8601 format for recurring
     * time interval representations.
     */
    private Date timeCycle;

    public TimerEventDefinition() {
        super(EventDefinitionType.TIMER);
    }
    
}
