package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ActivityExecutionService {
    
}
