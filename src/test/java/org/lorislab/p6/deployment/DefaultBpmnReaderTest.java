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
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.lorislab.p6.bpmn2.Definitions;
import org.lorislab.p6.bpmn2.TProcess;
import org.lorislab.p6.bpmn2.TRootElement;
import org.lorislab.p6.runtime.model.Definition;
import org.lorislab.p6.runtime.model.RuntimeProcess;
import static org.lorislab.p6.test.DeploymentConfig.BPMN_RESOURCE_DIR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author andrej
 */
//@RunWith(JUnitPlatform.class)
public class DefaultBpmnReaderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBpmnReaderTest.class);

    @TestFactory
    public Iterable<DynamicTest> createDefinitionTest() throws Exception {
        List<DynamicTest> tests = new ArrayList<>();
        Files.walkFileTree(Paths.get(BPMN_RESOURCE_DIR), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                tests.add(DynamicTest.dynamicTest(file.getFileName().toString(),
                        () -> {
                            try {
                                LOGGER.info("Load definitions: {}", file.toString());
                                Definitions definitions = loadDefinitions(file.toFile());
                                DefaultBpmnReader reader = new DefaultBpmnReader();
                                Definition def = reader.create(definitions);

                                Assertions.assertEquals(def.getId(), definitions.getId());
                                Assertions.assertEquals(def.getName(), definitions.getName());
                                // FIXME: Write the test case for the definition runtime model.
                            } catch (Exception ex) {
                                LOGGER.error("Error executing the script test for the file {}", file.toString());
                                ex.printStackTrace();
                                Assertions.assertNull(ex, "Error executing the script test for the file " + file.toString());
                            }
                        })
                );
                return FileVisitResult.CONTINUE;
            }

        });
        return tests;
    }

    @TestFactory
    public Iterable<DynamicTest> createProcessTest() throws Exception {

        List<DynamicTest> tests = new ArrayList<>();
        Files.walkFileTree(Paths.get(BPMN_RESOURCE_DIR), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    Definitions definitions = loadDefinitions(file.toFile());
                    for (JAXBElement<? extends TRootElement> e : definitions.getRootElements()) {
                        if (e.getValue() instanceof TProcess) {

                            tests.add(DynamicTest.dynamicTest(file.getFileName().toString(),
                                    () -> {
                                        try {
                                            LOGGER.info("Load process: {}", file.toString());
                                            DefaultBpmnReader reader = new DefaultBpmnReader();
                                            TProcess p = (TProcess) e.getValue();
                                            RuntimeProcess rp = reader.create(p);

                                            Assertions.assertEquals(rp.getId(), p.getId());
                                            Assertions.assertEquals(rp.getName(), p.getName());
                                            // FIXME: Write the test case for the process runtime model.
                                        } catch (Exception ex) {
                                            LOGGER.error("Error executing the script test for the file {}", file.toString());
                                            ex.printStackTrace();
                                            Assertions.assertNull(ex, "Error executing the script test for the file " + file.toString());
                                        }
                                    })
                            );
                        }
                    }
                } catch (Exception ex) {
                    Assertions.assertNull(ex, "Error reading the definition file " + file.toString());
                }
                return FileVisitResult.CONTINUE;
            }

        });
        return tests;
    }

    private static Definitions loadDefinitions(File file) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(Definitions.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (Definitions) jaxbUnmarshaller.unmarshal(file);
    }
}
