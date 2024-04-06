package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.BillingPaymentType;

public interface BillingPaymentTypeDao extends AbstractDao<BillingPaymentType> {
    List<BillingPaymentType> findAll();
    Integer findIdByName(String name);
    BillingPaymentType getPaymentTypeByName(String typeName);
}
