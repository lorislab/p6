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
package org.lorislab.p6.flow.service;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.lorislab.p6.flow.common.ErrorDef;
import org.lorislab.p6.flow.common.Message;

/**
 * The operation.
 *
 * Reference to the definition on the page 105 operation.
 *
 * @author andrej
 */
@Getter
@Setter
public class Operation {

    /**
     * The descriptive name of the element.
     */
    private String name;

    /**
     * This attribute specifies the input Message of the Operation. An Operation
     * has exactly one input Message.
     */
    private Message inMessageRef;

    /**
     * This attribute specifies the output Message of the Operation. An
     * Operation has at most one input Message.
     */
    private Message outMessageRef;

    /**
     * This attribute specifies errors that the Operation may return. An
     * Operation MAY refer to zero or more Error elements.
     */
    private List<ErrorDef> errorRef;
}
