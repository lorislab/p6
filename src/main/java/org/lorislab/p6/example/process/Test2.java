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
package org.lorislab.p6.example.process;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.client.cdi.ServiceTask;
import org.lorislab.p6.client.cdi.WorkflowProcess;
import org.lorislab.p6.client.service.ServiceTaskItem;

@Slf4j
@WorkflowProcess(processId = "org.lorislab.p6.example.Test2", processVersion = "1.0.0")
public class Test2 {

    @ServiceTask(value = "service1")
    public void service1(ServiceTaskItem item) {
        String parameter = item.getParameter("parameter");
        log.info("Execute service1! {}", parameter );
        item.addResult("parameter", "SERVICE1");
    }

    @ServiceTask(value = "service2")
    public void service2(ServiceTaskItem item) {
        String parameter = item.getParameter("parameter");
        log.info("Execute service2! {}", parameter );
        item.addResult("parameter", "SERVICE2");
    }

    @ServiceTask(value = "service3")
    public void service3(ServiceTaskItem item) {
        String parameter = item.getParameter("parameter");
        log.info("Execute service3! {}", parameter );
        item.addResult("parameter", "SERVICE3");
    }

    @ServiceTask(value = "service4")
    public void service4(ServiceTaskItem item) {
        String parameter = item.getParameter("parameter");
        log.info("Execute service4! {}", parameter );
        item.addResult("parameter", "SERVICE4");
    }
}
