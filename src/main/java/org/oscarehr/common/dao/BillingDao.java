package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import org.oscarehr.common.model.Billing;
import org.oscarehr.util.DateRange;

public interface BillingDao extends AbstractDao<Billing> {
    List<Billing> findActive(int billingNo);
    List<Billing> findByBillingType(String type);
    List<Billing> findByAppointmentNo(int apptNo);
    List<Billing> findSet(List<String> list);
    List<Object[]> findBillings(Integer demoNo, List<String> serviceCodes);
    List<Billing> findBillings(Integer demoNo, String statusType, String providerNo, Date startDate, Date endDate);
    List<Object[]> findBillings(Integer billing_no);
    List<Object[]> findProviderBillingsWithGst(String[] providerNos, DateRange dateRange);
    List<Billing> findByProviderStatusAndDates(String providerNo, List<String> statusList, DateRange dateRange);
    List<Billing> getMyMagicBillings();
    List<Object[]> findByManyThings(String statusType, String providerNo, String startDate, String endDate, String demoNo, boolean excludeWCB, boolean excludeMSP, boolean excludePrivate, boolean exludeICBC);
    List<Object[]> findBillingsByStatus(String statusType);
    List<Object[]> findOutstandingBills(Integer demographicNo, String billingType, List<String> statuses);
    List<Object[]> findByBillingMasterNo(Integer billingmasterNo);
    List<Object[]> findBillingsByManyThings(Integer billing, Date billingDate, String ohipNo, String serviceCode);
    Integer countBillings(String diagCode, String creator, Date sdate, Date edate);
    List<Object[]> countBillingVisitsByCreator(String providerNo, Date dateBegin, Date dateEnd);
    List<Object[]> countBillingVisitsByProvider(String providerNo, Date dateBegin, Date dateEnd);
    Integer search_billing_no_by_appt(int demographicNo, int appointmentNo);
    Integer search_billing_no(int demographicNo);
    List<Object[]> search_billob(String providerNo, Date startDate, Date endDate);
    List<Object[]> search_billflu(String creator, Date startDate, Date endDate);
    List<Billing> search_unsettled_history_daterange(String providerNo, Date startDate, Date endDate);
    List<Billing> findActiveBillingsByDemoNo(Integer demoNo, int limit);
    List<Billing> findBillingsByDemoNoServiceCodeAndDate(Integer demoNo, Date date, List<String> serviceCodes);
    List<Billing> search_bill_history_daterange(String providerNo, Date startBillingDate, Date endBillingDate);
    List<Billing> findByProviderStatusForTeleplanFileWriter(String hin);
    List<Object[]> search_bill_generic(int billingNo);
}
