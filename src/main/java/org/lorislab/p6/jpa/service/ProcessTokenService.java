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
import org.lorislab.p6.jpa.model.ProcessToken;
import org.lorislab.p6.jpa.model.ProcessToken_;
import org.lorislab.p6.jpa.model.enums.ProcessTokenStatus;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ProcessTokenService extends AbstractEntityService<ProcessToken, String> {

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ProcessToken loadByGuid(String guid) throws ServiceException {
        return super.loadByGuid(guid);
    }

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
}
