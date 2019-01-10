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
import org.lorislab.jee.annotation.LoggerService;
import org.lorislab.p6.config.ConfigService;
import org.lorislab.p6.flow.model.task.ScriptTask;
import org.lorislab.p6.flow.model.task.ServiceTask;
import org.lorislab.p6.flow.script.ScriptEngine;
import org.lorislab.p6.jpa.model.ProcessInstance;
import org.lorislab.p6.jpa.model.ProcessToken;
import org.lorislab.p6.jpa.service.ProcessTokenService;
import org.lorislab.p6.json.ServerJsonService;
import org.lorislab.p6.runtime.RuntimeProcess;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TaskExecutorService {

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    @EJB
    private ProcessTokenService processTokenService;

    @EJB
    private TokenService tokenService;

    public void completeServiceTask(ProcessToken token, RuntimeProcess runtimeProcess, ServiceTask task, @LoggerService.Exclude String response) throws Exception {

        token = ServerJsonService.mergeData(token, response);
        ProcessInstance processInstance = token.getProcessInstance();
        String next = runtimeProcess.getNextNodeName(task.getName());

        // create token
        token.setPreviousName(token.getNodeName());
        token.setNodeName(next);
        processTokenService.update(token);

        // send token message
        tokenService.sendTokenMessage(processInstance, token);
    }

    public void serviceTask(ProcessToken token, RuntimeProcess runtimeProcess, ServiceTask task) throws Exception {

        ProcessInstance processInstance = token.getProcessInstance();

        String content = null;
        if (token.getData() != null) {
            content = new String(token.getData(), StandardCharsets.UTF_8);
        }
        TextMessage request = context.createTextMessage(content);
        request.setStringProperty(ConfigService.MSG_PROCESS_ID, processInstance.getProcessId());
        request.setStringProperty(ConfigService.MSG_PROCESS_VERSION, processInstance.getProcessVersion());
        request.setStringProperty(ConfigService.MSG_PROCESS_INSTANCE_ID, processInstance.getGuid());
        request.setStringProperty(ConfigService.MSG_PROCESS_TOKEN_ID, token.getGuid());
        request.setStringProperty(ConfigService.MSG_PROCESS_TOKEN_SERVICE_TASK, token.getNodeName());

        // send request to the application.
        Queue queue = context.createQueue(ConfigService.QUEUE_REQUEST);
        context.createProducer().send(queue, request);
    }

    public void scriptTask(ProcessToken token, RuntimeProcess runtimeProcess, ScriptTask task) throws Exception {

        Map<String, Object> data = ServerJsonService.loadData(token);
        Map<String, Object> result = ScriptEngine.runScript(task.getScript(), data);
        token = ServerJsonService.saveData(token, result);

        // create token
        token.setPreviousName(token.getNodeName());
        token.setNodeName(runtimeProcess.getNextNodeName(task.getName()));
        processTokenService.update(token);

        // send token message
        tokenService.sendTokenMessage(token.getProcessInstance(), token);

    }
}
