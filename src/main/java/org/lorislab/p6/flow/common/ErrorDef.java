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
 * The error.
 *
 * Reference to the definition on the page 81 error.
 *
 * @author andrej
 */
@Getter
@Setter
public class ErrorDef {

    /**
     * The descriptive name of the Error.
     */
    protected String name;

    /**
     * For an End Event: If the result is an Error, then the errorCode MUST be
     * supplied (if the processType attribute of the Process is set to execut-
     * able) This “throws” the Error.
     *
     * For an Intermediate Event within normal flow: If the trigger is an Error,
     * then the errorCode MUST be entered (if the processType attribute of the
     * Process is set to execut- able). This “throws” the Error.
     *
     * For an Intermediate Event attached to the boundary of an Activity: If the
     * trigger is an Error, then the errorCode MAY be entered. This Event
     * “catches” the Error. If there is no errorCode, then any error SHALL
     * trigger the Event. If there is an errorCode, then only an Error that
     * matches the errorCode SHALL trigger the Event.
     */
    protected String errorCode;

    /**
     * An ItemDefinition is used to define the “payload” of the Error.
     */
    protected ItemDefinition structureRef;
}
