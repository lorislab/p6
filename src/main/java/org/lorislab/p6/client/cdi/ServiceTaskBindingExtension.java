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

import lombok.extern.slf4j.Slf4j;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.*;
import javax.enterprise.inject.spi.configurator.AnnotatedMethodConfigurator;
import javax.enterprise.inject.spi.configurator.AnnotatedParameterConfigurator;
import java.lang.annotation.Annotation;
import java.util.Set;

@Slf4j
public class ServiceTaskBindingExtension implements Extension {


    private static final Observes annotation = new Observes() {

        @Override
        public Class<? extends Annotation> annotationType() {
            return Observes.class;
        }

        @Override
        public Reception notifyObserver() {
            return Reception.ALWAYS;
        }

        @Override
        public TransactionPhase during() {
            return TransactionPhase.IN_PROGRESS;
        }

    };

    <T> void processServiceTaskS(@Observes @WithAnnotations({ServiceTask.class}) ProcessAnnotatedType<T> processAnnotatedType) {
        Set<AnnotatedMethodConfigurator<? super T>> mm = processAnnotatedType.configureAnnotatedType().methods();
        for (AnnotatedMethodConfigurator<? super T> n : mm) {
            for (AnnotatedParameterConfigurator p : n.params()) {
                WorkflowProcess wp = processAnnotatedType.getAnnotatedType().getAnnotation(WorkflowProcess.class);
                ServiceTask st = n.getAnnotated().getAnnotation(ServiceTask.class);
                p.add(new ServiceTaskEvent() {

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return ServiceTaskEvent.class;
                    }

                    @Override
                    public String value() {
                        return st.value();
                    }

                    @Override
                    public String processId() {
                        return wp.processId();
                    }

                    @Override
                    public String processVersion() {
                        return wp.processVersion();
                    }
                });
                log.info("Found service task: {}.{} add the Observers!", n.getAnnotated().getJavaMember().getDeclaringClass().getSimpleName(), n.getAnnotated().getJavaMember().getName());
                p.add(annotation);
            }
        }
    }
}
