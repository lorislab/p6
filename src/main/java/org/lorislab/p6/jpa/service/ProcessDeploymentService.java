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
package org.lorislab.p6.jpa.service;

import org.lorislab.jee.exception.ServiceException;
import org.lorislab.jee.jpa.service.AbstractEntityService;
import org.lorislab.p6.jpa.model.ProcessDeployment_;
import org.lorislab.p6.jpa.model.ProcessDeployment;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ProcessDeploymentService extends AbstractEntityService<ProcessDeployment, String> {

    public ProcessDeployment findByProcessId(String processId) throws ServiceException {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<ProcessDeployment> cq = cb.createQuery(ProcessDeployment.class);
            Root<ProcessDeployment> root = cq.from(ProcessDeployment.class);
            cq.where(cb.equal(root.get(ProcessDeployment_.processId), processId));
            TypedQuery<ProcessDeployment> query = getEntityManager().createQuery(cq);
            try {
                return query.getSingleResult();
            } catch (NoResultException no) {
                // ignore
            }
            return null;
        } catch (Exception ex) {
            throw new ServiceException(ErrorKeys.ERROR_FIND_PROCESS_DEF_BY_ID, ex, processId);
        }
    }

}
