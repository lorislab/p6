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
public class StartMessageEvent extends StartEvent<MessageEventDefinition> {
    
    public StartMessageEvent() {
        super(StartEventType.MESSAGE);
    }
    
    public StartMessageEvent(MessageEventDefinition message) {
        super(StartEventType.MESSAGE, message);
    }    
}
