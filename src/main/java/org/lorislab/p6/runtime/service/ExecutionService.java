/*
 * Copyright 2018 lorislab.
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
package org.lorislab.p6.runtime.service;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.lorislab.p6.model.ProcessToken;
import org.lorislab.p6.service.ProcessTokenService;

/**
 *
 * @author andrej
 */
@MessageDriven(name = "ExecutionService",
        activationConfig = {
            @ActivationConfigProperty(propertyName = "useJNDI", propertyValue = "false"),
            @ActivationConfigProperty(propertyName = "destination", propertyValue = "p6.execution")
        }
)
public class ExecutionService implements MessageListener {

    @EJB
    private ProcessTokenService  tokenService;
    
    @Override
    public void onMessage(Message message) {
        try {
            String guid = message.getBody(String.class);
            ProcessToken token = tokenService.findByGuid(guid);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
