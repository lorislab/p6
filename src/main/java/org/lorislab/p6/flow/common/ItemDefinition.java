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
package org.lorislab.p6.flow.common;

import lombok.Getter;
import lombok.Setter;

/**
 * The item definition.
 *
 * Reference to the definition on the page 91 item definition.
 *
 * @author andrej
 */
@Getter
@Setter
public class ItemDefinition {

    /**
     * This defines the nature of the Item. Possible values are physical or
     * information. The default value is information.
     */
    private ItemType type = ItemType.INFORMATION;

    /**
     * Setting this flag to true indicates that the actual data type is a
     * collection.
     */
    private boolean isCollection = false;
    
    /**
     * The concrete data structure to be used.
     */
    private String structureRef;
}
