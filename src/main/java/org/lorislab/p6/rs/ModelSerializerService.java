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
package org.lorislab.p6.rs;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.lorislab.jee.exception.ServiceException;
import org.lorislab.p6.bpmn2.Definitions;

/**
 *
 * @author andrej
 */
@Stateless
public class ModelSerializerService {

    private static JAXBContext CONTEXT;

    static {
        try {
            CONTEXT = JAXBContext.newInstance(Definitions.class);
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
    }

    public <T> T fromByte(byte[] data, Class<T> clazz) throws ServiceException {
        try  {
            YamlReader reader = new YamlReader(new StringReader(new String(data, StandardCharsets.UTF_8)));
            T result = reader.read(clazz);
            return result;
        } catch (Exception ex) {
            throw new ServiceException(ModelSerializerErrors.ERROR_OBJECT_FROM_BYTE, ex, clazz);
        }
    }

    public byte[] toByte(Object value) throws ServiceException {
        try {
            Writer writer = new StringWriter();
            YamlWriter yaml = new YamlWriter(writer);
            yaml.write(value);
            yaml.close();
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new ServiceException(ModelSerializerErrors.ERROR_OBJECT_TO_BYTE, ex);
        }
    }

    public byte[] definitionsToByte(Definitions definitions) throws ServiceException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream())  {
            Marshaller jaxbMarshaller = CONTEXT.createMarshaller();
            jaxbMarshaller.marshal(definitions, os);
            return os.toByteArray();
        } catch (Exception ex) {
            throw new ServiceException(ModelSerializerErrors.ERROR_DEFINITIONS_TO_BYTE, ex);
        }
    }

}
