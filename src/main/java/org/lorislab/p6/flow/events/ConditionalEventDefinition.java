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
 *
 * @author andrej
 */
@Getter
@Setter
public class ConditionalEventDefinition extends EventDefinition {

    /**
     * The Expression might be underspecified and provided in the form of
     * natural language. For executable Processes (isExecutable = true), if the
     * trigger is Conditional, then a FormalExpression MUST be entered.
     */
    private String expression;

    public ConditionalEventDefinition() {
        super(EventDefinitionType.CONDITIONAL);
    }

}
