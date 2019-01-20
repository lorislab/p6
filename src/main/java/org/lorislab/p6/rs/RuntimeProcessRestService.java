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

package org.lorislab.p6.rs;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.runtime.RuntimeProcessService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Slf4j
@Path("runtime")
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class RuntimeProcessRestService {

    @EJB
    private RuntimeProcessService runtimeProcessService;

    @GET
    @Path("process/count")
    public String getSize() {
        return "" + runtimeProcessService.getSize();
    }

}
