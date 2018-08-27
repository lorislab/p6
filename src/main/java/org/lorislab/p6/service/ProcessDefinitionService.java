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
package org.lorislab.p6.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.lorislab.jee.exception.ServiceException;
import org.lorislab.jee.jpa.service.AbstractEntityService;
import org.lorislab.p6.model.ProcessDefinition;
import org.lorislab.p6.model.ProcessDefinition_;

/**
 *
 * @author andrej
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ProcessDefinitionService extends AbstractEntityService<ProcessDefinition, Long> {

    public ProcessDefinition loadById(String id) throws ServiceException {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<ProcessDefinition> cq = cb.createQuery(ProcessDefinition.class);
            Root<ProcessDefinition> root = cq.from(ProcessDefinition.class);
            cq.distinct(true);
            root.fetch(ProcessDefinition_.contents);
            cq.where(cb.equal(root.get(ProcessDefinition_.id), id));
            TypedQuery<ProcessDefinition> query = getEntityManager().createQuery(cq);
            try {
                return query.getSingleResult();
            } catch (NoResultException nr) {
                // ignore
            }
            return null;
        } catch (Exception ex) {
            throw new ServiceException(ErrorKeys.ERROR_LOAD_PROCESS_DEF_BY_ID, ex, id);
        }
    }
    
    public ProcessDefinition findById(String id) throws ServiceException {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<ProcessDefinition> cq = cb.createQuery(ProcessDefinition.class);
            Root<ProcessDefinition> root = cq.from(ProcessDefinition.class);
            cq.where(cb.equal(root.get(ProcessDefinition_.id), id));
            TypedQuery<ProcessDefinition> query = getEntityManager().createQuery(cq);
            try {
                return query.getSingleResult();
            } catch (NoResultException nr) {
                // ignore
            }

            return null;
        } catch (Exception ex) {
            throw new ServiceException(ErrorKeys.ERROR_FIND_PROCESS_DEF_BY_ID, ex, id);
        }
    }
}
