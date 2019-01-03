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

import javax.el.ELManager;
import javax.el.ValueExpression;

import static javax.el.ELManager.getExpressionFactory;

@Slf4j
public final class ELScript {

    private final ELManager manager;

    public ELScript() {
        manager = new ELManager();
    }

    public void addVariable(final String name, Object value) {
        if (value != null) {
            manager.getELContext().getVariableMapper().setVariable(
                    name, getExpressionFactory().createValueExpression(value, value.getClass())
            );
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getVariable(final String name) {
        final Object result = manager.getELContext().getVariableMapper().resolveVariable(name).getValue(manager.getELContext());
        return (T) result;
    }

    public boolean evaluateExpression(final String expression) {
        final Object result = getExpressionFactory()
                .createValueExpression(manager.getELContext(), expression(expression), Boolean.class).getValue(manager.getELContext());
        if (result == null) {
            log.error("The expression {} returns null value. Return value muss be type of boolean.", expression);
            throw new RuntimeException("The expression returns null value!");
        }
        if (!result.getClass().isAssignableFrom(Boolean.class)) {
            log.error("The expression {} returns none boolean type {}. Return value muss be type of boolean.", expression, result.getClass().getName());
            throw new RuntimeException("The expression returns none boolean type!");
        }
        return (boolean) result;
    }

    public void evaluateScript(final String script) {
        ValueExpression expression = getExpressionFactory().createValueExpression(manager.getELContext(), expression(script), Object.class);
        Object v = expression.getValue(manager.getELContext());
    }

    /**
     * Creates the expression text.
     *
     * @param expression the input expression.
     * @return updated expression text.
     */
    private static String expression(final String expression) {
        String tmp = expression;
        if (tmp != null && !tmp.isBlank()) {
            // remove empty lines
            tmp = tmp.replaceAll("(?m)^\\s", "");
            // remove whitespaces
            tmp = tmp.strip();
            // remove last semicolon
            if (tmp.endsWith(";")) {
                tmp = tmp.substring(0, tmp.length() - 1);
            }
        }
        return "#{" + tmp + "}";
    }

}
