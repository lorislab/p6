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
import org.lorislab.p6.flow.common.Message;
import org.lorislab.p6.flow.service.Operation;

/**
 * The message event definition.
 *
 * Reference to the definition on the page 270 Message Event Definition
 *
 * @author andrej
 */
@Getter
@Setter
public class MessageEventDefinition extends EventDefinition {

    /**
     * The Message MUST be supplied (if the isExecutable attribute of the
     * Process is set to true).
     */
    private Message message;

    /**
     * This attribute specifies the Operation that is used by the Message Event.
     * It MUST be specified for executable Processes.
     */
    private Operation operation;
}
