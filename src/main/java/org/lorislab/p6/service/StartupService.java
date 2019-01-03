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
package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.jpa.model.ProcessDeployment;
import org.lorislab.p6.jpa.service.ProcessDeploymentService;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.List;

@Slf4j
@Startup
@Singleton
public class StartupService {

    @EJB
    private ProcessDeploymentService processDeploymentService;

    @EJB
    private CommandService commandService;

    @PostConstruct
    public void init() {
        try {
            List<ProcessDeployment> deployments = processDeploymentService.findAll(null, null);
            commandService.cmdStart(deployments);
        } catch (Exception se) {
            log.error("Error find the latest process definitions from the database.", se);
            throw new RuntimeException("Error find the latest process definitions!");
        }
    }
}
