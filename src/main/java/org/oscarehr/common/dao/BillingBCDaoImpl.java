package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.NativeSql;
import org.springframework.stereotype.Repository;

@Repository
public class BillingBCDaoImpl extends BillingDao implements BillingBCDao {

    @NativeSql({"ctl_billingservice", "billingservice"})
    @SuppressWarnings("unchecked")
    public List<Object[]> findBillingServices(String billRegion, String serviceGroup, String serviceType) {
        // ... implementation goes here ...
    }

    @NativeSql({"ctl_billingservice"})
    @SuppressWarnings("unchecked")
    public List<Object[]> findBillingServicesByType(String serviceType) {
        // ... implementation goes here ...
    }

    @NativeSql({"ctl_billingservice", "billingservice"})
    @SuppressWarnings("unchecked")
    public List<Object[]> findBillingServices(String billRegion, String serviceGroup, String serviceType, String billReferenceDate) {
        // ... implementation goes here ...
    }

    @SuppressWarnings("unchecked")
    @NativeSql("billinglocation")
    public List<Object[]> findBillingLocations(String billRegion) {
        // ... implementation goes here ...
    }

    @SuppressWarnings("unchecked")
    @NativeSql("billingvisit")
    public List<Object[]> findBillingVisits(String billRegion) {
        // ... implementation goes here ...
    }

    @SuppressWarnings("unchecked")
    @NativeSql("wcb_side")
    public List<Object[]> findInjuryLocations() {
        // ... implementation goes here ...
    }
}
