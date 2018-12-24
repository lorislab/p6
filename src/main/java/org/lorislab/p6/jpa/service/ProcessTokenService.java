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
package org.lorislab.p6.jpa.service;

import org.lorislab.jee.exception.ServiceException;
import org.lorislab.jee.jpa.model.AbstractPersistent_;
import org.lorislab.jee.jpa.service.AbstractEntityService;
import org.lorislab.p6.jpa.model.ProcessInstance;
import org.lorislab.p6.jpa.model.ProcessInstance_;
import org.lorislab.p6.jpa.model.ProcessToken;
import org.lorislab.p6.jpa.model.ProcessToken_;
import org.lorislab.p6.jpa.model.enums.ProcessTokenStatus;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.*;
import java.util.HashMap;
import java.util.Map;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ProcessTokenService extends AbstractEntityService<ProcessToken, String> {

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ProcessToken loadByGuid(String guid) throws ServiceException {
        return super.loadByGuid(guid);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateTokenStatus(String guid, ProcessTokenStatus status) throws ServiceException {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaUpdate<ProcessToken> cq = cb.createCriteriaUpdate(ProcessToken.class);
            Root<ProcessToken> root = cq.from(ProcessToken.class);
            cq.set(root.get(ProcessToken_.status), status)
                    .where(cb.equal(root.get(ProcessToken_.guid), guid));
            int count = getEntityManager().createQuery(cq).executeUpdate();
            flush();
        } catch (Exception ex) {
            throw new ServiceException(ErrorKeys.ERROR_UPDATE_TOKEN_STATUS, ex, guid, status);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ProcessToken findByNodeNameAndProcessInstance(String nodeName, String processInstanceId) throws ServiceException {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<ProcessToken> cq = cb.createQuery(ProcessToken.class);
            Root<ProcessToken> root = cq.from(ProcessToken.class);
            root.fetch(ProcessToken_.parents);
            cq.where(
                    cb.and(
                            cb.equal(root.get(ProcessToken_.startNodeName), nodeName),
                            cb.equal(root.get(ProcessToken_.processInstance).get(ProcessInstance_.guid), processInstanceId)
                    )
            );

            try {
                return getEntityManager().createQuery(cq).getSingleResult();
            } catch (NoResultException no) {
                // ignore
            }
        } catch (Exception ex) {
            throw new ServiceException(ErrorKeys.ERROR_LOAD_BY_NODE_NAME_PROCESS_INSTANCE_ID, ex, nodeName, processInstanceId);
        }
        return null;
    }
}
