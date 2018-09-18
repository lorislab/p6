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

/**
 *
 * @author andrej
 */
public enum StartEventType {

    /**
     * The None Start Event does not have a defined trigger . There is no
     * specific EventDefinition subclass (see page 260) for None Start Events.
     * If the Start Event has no associated EventDefiniton , then the Event MUST
     * be displayed without a marker (see the figure on the right).
     */
    NONE,
    /**
     * A Message arrives from a Participant and triggers the start of the
     * Process. See page 93 for more details on Messages. If there is only one
     * EventDefinition associated with the Start Event and that EventDefinition
     * is of the subclass MessageEventDefinition, then the Event is a Message
     * Start Event and MUST be displayed with an envelope marker (see the figure
     * to the right). The actual Participant from which the Message is received
     * can be identified by connecting the Event to a Participant using a
     * Message Flow within the definitional Collaboration of the Process.
     */
    MESSAGE,
    /**
     * A specific time-date or a specific cycle (e.g., every Monday at 9am) can
     * be set that will trigger the start of the Process. If there is only one
     * EventDefinition associated with the Start Event and that EventDefinition
     * is of the subclass TimerEventDefini- tion, then the Event is a Timer
     * Start Event and MUST be displayed with a clock marker.
     */
    TIMER,
    /**
     * This type of event is triggered when a condition such as “SandP 500 changes
     * by more than 10% since opening”, or “Temperature above 300C” become true.
     * The condition Expression for the Event MUST become false and then true
     * before the Event can be triggered again. The Condition Expression of a
     * Conditional Start Event MUST NOT refer to the data context or instance
     * attribute of the Process (as the Process instance has not yet been
     * created). Instead, it MAY refer to static Process attributes and states
     * of entities in the environment. The specification of mechanisms to
     * access such states is out of scope of the standard. If there is only
     * one EventDefinition associated with the Start Event and that
     * EventDefinition is of the subclass ConditionalEventDefinition, then the
     * Event is a Conditional Start Event and MUST be displayed with a lined
     * paper marker.
     */
    CONDITIONAL,
    /**
     * A Signal arrives that has been broadcast from another Process and
     * triggers the start of the Process. Note that the Signal is not a Message,
     * which has a specific target for the Message. Multiple Processes can have
     * Start Events that are triggered from the same broadcasted Signal. If
     * there is only one EventDefinition associated with the Start Event and
     * that EventDefinition is of the subclass SignalEventDefinition, then the
     * Event is a Signal Start Event and MUST be displayed with a triangle
     * marker.
     */
    SIGNAL,
    /**
     * This means that there are multiple ways of triggering the Process . Only
     * one of them is REQUIRED. There is no specific EventDefinition subclass
     * for Multiple Start Events. If the Start Event has more than one
     * associated EventDefiniton, then the Event MUST be displayed with the
     * Multiple Event marker (a pentagon).
     */
    MULTIPLE,
    /**
     * This means that there are multiple triggers REQUIRED before the Process
     * can be instantiated. All of the types of triggers that are listed in the
     * Start Event MUST be triggered before the Process is instantiated. There
     * is no specific EventDefinition subclass for Parallel Multiple Start
     * Events. If the Start Event has more than one associated EventDefiniton
     * and the parallelMultiple attribute of the Start Event is true, then the
     * Event MUST be displayed with the Parallel Multiple Event marker (an open
     * plus sign).
     */
    PARALLEL_MULTIPLE;
}
