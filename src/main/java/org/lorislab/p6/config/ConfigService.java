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
package org.lorislab.p6.config;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;

@JMSDestinationDefinitions(
        value = {
                @JMSDestinationDefinition(
                        name = "java:/queue/" + ConfigService.QUEUE_EXEC,
                        interfaceName = "javax.jms.Queue",
                        destinationName = ConfigService.QUEUE_EXEC,
                        properties= {"redelivery-limit=1"}
                ),
                @JMSDestinationDefinition(
                        name = "java:/queue/" + ConfigService.QUEUE_DEPLOY,
                        interfaceName = "javax.jms.Queue",
                        destinationName = ConfigService.QUEUE_DEPLOY,
                        properties= {"redelivery-limit=1"}
                ),
                @JMSDestinationDefinition(
                        name = "java:/queue/" + ConfigService.QUEUE_REQUEST,
                        interfaceName = "javax.jms.Queue",
                        destinationName = ConfigService.QUEUE_REQUEST,
                        properties= {"redelivery-limit=1"}
                ),
                @JMSDestinationDefinition(
                        name = "java:/queue/" + ConfigService.QUEUE_RESPONSE,
                        interfaceName = "javax.jms.Queue",
                        destinationName = ConfigService.QUEUE_RESPONSE,
                        properties= {"redelivery-limit=1"}
                ),
                @JMSDestinationDefinition(
                        name = "java:/queue/" + ConfigService.QUEUE_CMD,
                        interfaceName = "javax.jms.Queue",
                        destinationName = ConfigService.QUEUE_CMD,
                        properties= {"redelivery-limit=1"}
                ),
                @JMSDestinationDefinition(
                        name = "java:/queue/" + ConfigService.QUEUE_TOKEN,
                        interfaceName = "javax.jms.Queue",
                        destinationName = ConfigService.QUEUE_TOKEN,
                        properties= {"max-delivery-attempts=1"}
                )
        }
)
@Startup
@Singleton
public class ConfigService {

    public static final String JMS_RETRY_COUNT = "JMSXDeliveryCount";

    public static final String QUEUE_DEPLOY = "p6.deploy";

    public static final String QUEUE_EXEC = "p6.exec";

    public static final String QUEUE_REQUEST = "p6.request";

    public static final String QUEUE_TOKEN = "p6.token";

    public static final String QUEUE_RESPONSE = "p6.response";

    public static final String QUEUE_CMD = "p6.cmd";

    public static final String MSG_APP_NAME = "P6_APP_NAME";

    public static final String MSG_MODULE_NAME = "P6_MODULE_NAME";

    public static final String DEPLOYMENT_DESCRIPTOR = "p6.yml";

    public static final String MSG_CMD = "P6_CMD";

    public static final String MSG_PROCESS_TOKEN_SERVICE_TASK = "P6_PROCESS_TOKEN_SERVICE_TASK";

    public static final String MSG_PROCESS_TOKEN_ID = "P6_PROCESS_TOKEN_ID";

    public static final String MSG_PROCESS_ID = "P6_PROCESS_ID";

    public static final String MSG_PROCESS_INSTANCE_ID = "P6_PROCESS_INSTANCE_ID";

    public static final String MSG_PROCESS_VERSION = "P6_PROCESS_VERSION";

    public static final String MSG_PROCESS_DEF_GUID = "MSG_PROCESS_DEF_GUID";

    public static final String MSG_PROCESS_EVENT_ID = "MSG_PROCESS_EVENT_ID";

    public static final String MSG_PROCESS_MESSAGE_ID = "MSG_PROCESS_MESSAGE_ID";

    public static final String CMD_DEPLOY = "CMD_DEPLOY";

    public static final String CMD_START_PROCESS = "CMD_START_PROCESS";

    public static final String CMD_SEND_EVENT = "CMD_SEND_EVENT";

    public static final String CMD_SEND_MESSAGE = "CMD_SEND_MESSAGE";

    public static final String DATA_KEY_EVENT_DATA = "P6_EVENT_DATA";


    public static final int MAX_REDELIVERY_COUNT = 2;
}
