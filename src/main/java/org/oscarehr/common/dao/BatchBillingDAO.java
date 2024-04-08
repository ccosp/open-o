package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.BatchBilling;

public interface BatchBillingDAO extends AbstractDao<BatchBilling> {
    List<BatchBilling> find(Integer demographicNo, String service_code);
    List<BatchBilling> findByProvider(String providerNo);
    List<BatchBilling> findByProvider(String providerNo, String service_code);
    List<BatchBilling> findByServiceCode(String service_code);
    List<BatchBilling> findAll();
    List<String> findDistinctServiceCodes();
}
