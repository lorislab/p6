/*
 * Copyright 2018 lorislab.org
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
package org.lorislab.p6.rs;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.lorislab.jee.exception.ServiceException;
import org.lorislab.p6.deployment.DeploymentService;
import org.lorislab.p6.bpmn2.Definitions;

/**
 *
 * @author andrej
 */
@Path("deployment")
public class DeploymentRsService {

    @EJB
    private DeploymentService service;

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public long deploy(Definitions definitions) throws ServiceException {
        return service.deploy(definitions);
    }
}
