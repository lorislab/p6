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
package org.lorislab.p6.client.service;

import jdk.jshell.JShell;
import jdk.jshell.Snippet;
import jdk.jshell.SnippetEvent;
import jdk.jshell.SourceCodeAnalysis;
import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.config.ConfigService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.Queue;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Startup
@Singleton(name = "ClientDeploymentService")
public class ClientDeploymentService {

    @Inject
    @JMSConnectionFactory("java:/JmsXA")
    private JMSContext context;

    @Resource(lookup = "java:app/AppName")
    private String application;

    @Resource(lookup = "java:module/ModuleName")
    private String module;

    @PostConstruct
    public void deployment() {
        try {
            log.info("Start client deployment for the {} / {}", application, module);
            test();
            Properties properties = new Properties();
            try (InputStream in = this.getClass().getResourceAsStream(ConfigService.DEPLOYMENT_DESCRIPTOR)) {
                if (in != null) {
                    properties.load(in);
                } else {
                    log.warn("No deployment descriptor {} found!", ConfigService.DEPLOYMENT_DESCRIPTOR);
                }
            }
            Set<String> resources = properties.stringPropertyNames();
            if (!resources.isEmpty()) {
                for (String resource : resources) {
                    try {
                        String process = loadProcess(resource);
                        if (process != null) {
                            Message msg = context.createTextMessage(process);
                            msg.setStringProperty(ConfigService.MSG_CMD, ConfigService.CMD_DEPLOY);
                            msg.setStringProperty(ConfigService.MSG_APP_NAME, application);
                            msg.setStringProperty(ConfigService.MSG_MODULE_NAME, module);
                            msg.setStringProperty(ConfigService.MSG_RESOURCE_PATH, resource);
                            Queue queue = context.createQueue(ConfigService.QUEUE_CMD);
                            context.createProducer().send(queue, msg);
                        }
                    } catch (Exception ex) {
                        log.error("Error start the deployment for the resource " + resource, ex);
                    }
                }
            } else {
                log.warn("The deployment descriptor {} is empty.", ConfigService.DEPLOYMENT_DESCRIPTOR);
            }
        } catch (Exception ex) {
            log.error("Error start the deployment for the module!", ex);
        }
    }

    private String loadProcess(String path) {
        try (InputStream in = this.getClass().getResourceAsStream(path);
             InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception ex) {
            log.error("Error reading the " + ConfigService.DEPLOYMENT_DESCRIPTOR + " process descriptor", ex);
        }
        return null;
    }

    private void test() {
        log.info("START TEST");
        try(JShell jshell = JShell.create()){
            jshell.onSnippetEvent(snippetEvent -> snippetEventHandler(snippetEvent));

            String s = "System.out.println(\"#########################SCRIPT\")";
            while (true) {
                // Read source line by line till semicolon (;)
                SourceCodeAnalysis.CompletionInfo an = jshell.sourceCodeAnalysis().analyzeCompletion(s);
                if (!an.completeness().isComplete()) {
                    break;
                }
                // If there are any method declaration or class declaration in new lines, resolve it
                // otherwise execution errors will be thrown
                jshell.eval(trimNewlines(an.source()));
                // Exit if there are no more expressions to evaluate. EOF
                if (an.remaining().isEmpty()) {
                    break;
                }
                // If there is semicolon, execute next seq
                s = an.remaining();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void snippetEventHandler(SnippetEvent snippetEvent){
        String value = snippetEvent.value();
        if(!Objects.isNull(value) && value.trim().length() > 0) {
            // Prints output of code evaluation
            System.out.println(value);
        }

        // If there are any erros print and exit
        if(Snippet.Status.REJECTED.equals(snippetEvent.status())){
            System.out.println("Evaluation failed : "+snippetEvent.snippet().toString()+"\nIgnoring execution of above script");
        }
    }

    private String trimNewlines(String s) {
        int b = 0;
        while (b < s.length() && s.charAt(b) == '\n') {
            ++b;
        }
        int e = s.length() -1;
        while (e >= 0 && s.charAt(e) == '\n') {
            --e;
        }
        return s.substring(b, e + 1);
    }
}
