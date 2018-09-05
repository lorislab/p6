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
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.lorislab.p6.bpmn2.Definitions;
import org.lorislab.p6.bpmn2.TProcess;
import org.lorislab.p6.bpmn2.TRootElement;
import org.lorislab.p6.runtime.model.Definition;
import org.lorislab.p6.runtime.model.RuntimeProcess;
import static org.lorislab.p6.test.Deployments.BPMN_RESOURCE_DIR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author andrej
 */
@RunWith(Parameterized.class)
public class DefaultBpmnReaderDefinitionTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBpmnReaderDefinitionTest.class);

    @Parameters
    public static Iterable<? extends Object> data() throws Exception {
        List<Path> result = new ArrayList<>();
        Files.walkFileTree(Paths.get(BPMN_RESOURCE_DIR), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                result.add(file);
                return FileVisitResult.CONTINUE;
            }
        });
        return result;
    }

    @Parameter
    public Path file;

    @Test
    public void createDefinitionTest() throws Exception {
        try {
            LOGGER.info("Load definitions: {}", file.toString());
            Definitions definitions = loadDefinitions(file.toFile());
            DefaultBpmnReader reader = new DefaultBpmnReader();
            Definition def = reader.create(definitions);

            Assert.assertEquals(def.getId(), definitions.getId());
            Assert.assertEquals(def.getName(), definitions.getName());
            // FIXME: Write the test case for the definition runtime model.
        } catch (Exception ex) {
            LOGGER.error("Error executing the script test for the file {}", file.toString());
            ex.printStackTrace();
            Assert.assertNull("Error executing the script test for the file " + file.toString(), ex);
        }
    }

    private static Definitions loadDefinitions(File file) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(Definitions.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (Definitions) jaxbUnmarshaller.unmarshal(file);
    }
}
