/*
 * Copyright 2018 lorislab.
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

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.lorislab.jee.jpa.service.AbstractEntityService;
import org.lorislab.p6.model.ProcessInstance;

/**
 *
 * @author andrej
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ProcessInstanceService extends AbstractEntityService<ProcessInstance, Long> {
    
}
