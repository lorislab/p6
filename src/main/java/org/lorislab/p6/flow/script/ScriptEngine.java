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

import java.io.PrintStream;
import java.util.Map;

@Slf4j
public class ScriptEngine {

    private static final String VAR_PARAMS = "params";

    private static final String VAR_LOG = "log";

    private static final String VAR_SYSTEM = "System";

    private static final SystemBean SYSTEM = new SystemBean();

    public static boolean expression(String expression, Map data) {
        ELScript el = new ELScript();
        el.addVariable(VAR_SYSTEM, SYSTEM);
        el.addVariable(VAR_LOG, log);
        el.addVariable(VAR_PARAMS, data);
        return el.evaluateExpression(expression);
    }

    public static Map runScript(String script, Map data) {
        ELScript el = new ELScript();
        el.addVariable(VAR_SYSTEM, SYSTEM);
        el.addVariable(VAR_LOG, log);
        el.addVariable(VAR_PARAMS, data);
        el.evaluateScript(script);
        return data;
    }

    public static class SystemBean {

        public PrintStream getOut() {
            return System.out;
        }

    }
}
