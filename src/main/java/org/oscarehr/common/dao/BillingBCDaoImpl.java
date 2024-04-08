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
        Query query = entityManager.createNativeQuery("SELECT DISTINCT b.service_code, b.description, b.value, b.percentage "
        + "FROM ctl_billingservice c left outer join billingservice b on b.service_code="
        + "c.service_code where b.region = ? and c.service_group= ? and c.servicetype = ? order by c.service_order");
        
        query.setParameter(1, billRegion);
        query.setParameter(2, serviceGroup);
        query.setParameter(3, serviceType);
        
        return query.getResultList();
    }

    @NativeSql({"ctl_billingservice"})
    @SuppressWarnings("unchecked")
    public List<Object[]> findBillingServicesByType(String serviceType) {
        Query query = entityManager.createNativeQuery("SELECT DISTINCT service_code, servicetype, service_group, status, service_order "
				+ "FROM ctl_billingservice where servicetype = ? order by service_order");

		query.setParameter(1, serviceType);

		return query.getResultList();
    }

    @NativeSql({"ctl_billingservice", "billingservice"})
    @SuppressWarnings("unchecked")
    public List<Object[]> findBillingServices(String billRegion, String serviceGroup, String serviceType, String billReferenceDate) {
        Query query = entityManager.createNativeQuery(
	              "SELECT DISTINCT b.service_code, b.description , b.value, b.percentage " +
	              "FROM ctl_billingservice c left outer join billingservice b on b.service_code="
	              + "c.service_code where b.region = ? and c.service_group = ? and c.servicetype = ?" +
	              " and b.billingservice_date in (select max(b2.billingservice_date) from billingservice b2 where b2.billingservice_date <= ? and b2.service_code = b.service_code) order by c.service_order");
		
	    query.setParameter(1, billRegion);
		query.setParameter(2, serviceGroup);
		query.setParameter(3, serviceType);
		query.setParameter(4, billReferenceDate);

	    return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @NativeSql("billinglocation")
    public List<Object[]> findBillingLocations(String billRegion) {
        Query query = entityManager.createNativeQuery("SELECT billinglocation,billinglocation_desc FROM billinglocation WHERE region = ?");
	    query.setParameter(1, billRegion);
	    return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @NativeSql("billingvisit")
    public List<Object[]> findBillingVisits(String billRegion) {
        Query query = entityManager.createNativeQuery("SELECT visittype, visit_desc FROM billingvisit WHERE region = ? ORDER BY visittype ASC");
	    query.setParameter(1, billRegion);
	    return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @NativeSql("wcb_side")
    public List<Object[]> findInjuryLocations() {
        Query query = entityManager.createNativeQuery("SELECT sidetype, sidedesc FROM wcb_side");
	    return query.getResultList();
    }
}
