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

package org.lorislab.p6.flow.script;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ScriptEngineTest {

    @Test
    public void expresionTest() throws Exception {
        Map<String, Object> data = new HashMap<>();
        String tmp = loadFile("Expression.el");
        boolean result = ScriptEngine.expression(tmp, data);
        Assert.assertFalse(result);
        Assert.assertFalse(data.isEmpty());
        Assert.assertEquals(1L, data.get("x"));
        Assert.assertEquals(2L, data.get("y"));
    }

    @Test
    public void expresion2Test() throws Exception {
        Map<String, Object> data = new HashMap<>();
        String tmp = loadFile("Expression2.el");
        boolean result = ScriptEngine.expression(tmp, data);
        Assert.assertTrue(result);
        Assert.assertFalse(data.isEmpty());
        Assert.assertEquals(3L, data.get("x"));
        Assert.assertEquals(3L, data.get("y"));
    }

    @Test
    public void simpleTest() throws Exception {
        Map<String, Object> data = new HashMap<>();
        String tmp = loadFile("Simple.el");
        ScriptEngine.runScript(tmp, data);

        Assert.assertFalse(data.isEmpty());
        Assert.assertEquals("text", data.get("a"));
        Assert.assertEquals(1L, data.get("x"));
    }

    private String loadFile(String name) throws Exception {
        Path path = Paths.get(getClass().getResource(name).toURI());
        String tmp = Files.readString(path);
        log.info("\n--------------- Test script:{}\n{}\n---------------", name, tmp);
        return tmp;
    }
}
