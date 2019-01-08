/*
 * Copyright 2019 lorislab.org.
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
package org.lorislab.p6.client.cdi;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;
import java.lang.annotation.*;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface ServiceTaskEvent {

    String value();

    String processId();

    String processVersion();

    final class Literal extends AnnotationLiteral<ServiceTaskEvent> implements ServiceTaskEvent {

        private static final long serialVersionUID = 1L;

        private String value;

        private String processId;

        private String processVersion;

        public Literal(String processId, String processVersion, String value) {
            this.value = value;
            this.processId = processId;
            this.processVersion = processVersion;
        }

        public String value() {
            return value;
        }

        public String processId() {
            return processId;
        }

        public String processVersion() {
            return processVersion;
        }
    }
}
