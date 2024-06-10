package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.CtlBillingService;

public interface CtlBillingServiceDao extends AbstractDao<CtlBillingService> {
    public static final String DEFAULT_STATUS = "A";
    List<CtlBillingService> findAll();
    List<Object[]> getUniqueServiceTypes(String serviceStatus);
    List<Object[]> getUniqueServiceTypes();
    List<CtlBillingService> findByServiceTypeId(String serviceTypeId);
    List<CtlBillingService> findByServiceGroupAndServiceTypeId(String serviceGroup, String serviceTypeId);
    List<CtlBillingService> findByServiceType(String serviceTypeId);
    List<CtlBillingService> getServiceTypeList();
    List<Object[]> getAllServiceTypes();
    List<CtlBillingService> findByServiceGroup(String serviceGroup);
    List<CtlBillingService> findByServiceGroupAndServiceType(String serviceGroup, String serviceType);
    List<Object[]> findUniqueServiceTypesByCode(String serviceCode);
    List<Object[]> findServiceTypes();
    List<Object[]> findServiceTypesByStatus(String status);
    List<Object> findServiceCodesByType(String serviceType);
}
