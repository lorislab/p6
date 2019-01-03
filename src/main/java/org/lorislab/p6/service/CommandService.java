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
import org.lorislab.p6.config.ConfigService;
import org.lorislab.p6.jpa.model.ProcessDeployment;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.*;
import java.util.List;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CommandService {

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    public void cmdStart(List<ProcessDeployment> deployments) throws Exception {
        if (deployments != null && !deployments.isEmpty()) {
            Queue cmd = context.createQueue(ConfigService.QUEUE_CMD);
            JMSProducer producer = context.createProducer();
            for (int i = 0; i < deployments.size(); i++) {
                ProcessDeployment deployment = deployments.get(i);
                Message msg = createStartMessage(deployment);
                producer.send(cmd, msg);
            }
        }
    }

    public void cmdStart(ProcessDeployment deployment) throws Exception {
        Queue cmd = context.createQueue(ConfigService.QUEUE_CMD);
        JMSProducer producer = context.createProducer();
        Message msg = createStartMessage(deployment);
        producer.send(cmd, msg);
    }

    private Message createStartMessage(ProcessDeployment deployment) throws Exception {
        Message msg = context.createMessage();
        msg.setStringProperty(ConfigService.MSG_CMD, ConfigService.CMD_START);
        msg.setStringProperty(ConfigService.MSG_PROCESS_DEF_GUID, deployment.getProcessDefinitionGuid());
        msg.setStringProperty(ConfigService.MSG_PROCESS_ID, deployment.getProcessId());
        msg.setStringProperty(ConfigService.MSG_PROCESS_VERSION, deployment.getProcessVersion());
        return msg;
    }
}
