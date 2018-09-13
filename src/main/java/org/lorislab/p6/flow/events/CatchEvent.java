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

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author andrej
 */
@Getter
@Setter
public class CatchEvent extends Event {

    /**
     * This attribute is only relevant when the catch Event has more than
     * EventDefinition (Multiple). If this value is true , then all of the types
     * of triggers that are listed in the catch Event MUST be triggered before
     * the Process is instantiated.
     */
    private boolean parallelMultiple = false;

    private final Map<String, EventDefinition> definitions = new HashMap<>();       
    
    public CatchEvent(EventType type) {
        super(type);
    }

}
