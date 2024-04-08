package org.oscarehr.common.dao;

import java.util.List;

public interface BillingBCDao extends BillingDao{
    List<Object[]> findBillingServices(String billRegion, String serviceGroup, String serviceType);
    List<Object[]> findBillingServicesByType(String serviceType);
    List<Object[]> findBillingServices(String billRegion, String serviceGroup, String serviceType, String billReferenceDate);
    List<Object[]> findBillingLocations(String billRegion);
    List<Object[]> findBillingVisits(String billRegion);
    List<Object[]> findInjuryLocations();
}
