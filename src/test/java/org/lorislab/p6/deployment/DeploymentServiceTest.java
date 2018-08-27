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
package org.lorislab.p6.deployment;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import javax.ejb.EJB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.lorislab.p6.bpmn2.Definitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author andrej
 */
@RunWith(Arquillian.class)
public class DeploymentServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentServiceTest.class);

    @EJB
    private DeploymentService service;

    @Test
    public void deploymentTest() throws Exception {
        Files.walkFileTree(Paths.get("src/test/resources/bpmn"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    LOGGER.info("Start deployment for process: {}", file.toString());
                    Definitions definitions = loadDefinitions(file.toFile());
                    long process = service.deploy(definitions);
                    LOGGER.info("Process deploy: {} id: {}", file, process);
                } catch (Exception ex) {
                    LOGGER.error("Error executing the script test for the file {}", file.toString());
                    ex.printStackTrace();
                    Assertions.assertNull(ex, "Error executing the script test for the file " + file.toString());
                }
                return FileVisitResult.CONTINUE;
            }

        });
    }

    private static Definitions loadDefinitions(File file) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(Definitions.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (Definitions) jaxbUnmarshaller.unmarshal(file);
    }
}
