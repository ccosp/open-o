package org.oscarehr.common.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.BillingONEAReport;
import org.springframework.stereotype.Repository;
import oscar.oscarBilling.ca.on.data.BillingProviderData;
import oscar.util.ParamAppender;

@Repository
@SuppressWarnings("unchecked")
public class BillingONEAReportDaoImpl extends AbstractDaoImpl<BillingONEAReport> implements BillingONEAReportDao {
    
    public BillingONEAReportDaoImpl() {
        super(BillingONEAReport.class);	
    }
    
    @Override 
    public List<BillingONEAReport> findByProviderOhipNoAndGroupNoAndSpecialtyAndProcessDate(String providerOhipNo, String groupNo, String specialty, Date processDate) {
        String sql = "select b from BillingONEAReport b where b.providerOHIPNo=:providerOHIPNo and b.groupNo=:groupNo and b.specialty=:specialty and b.processDate = :processDate";
        Query query = entityManager.createQuery(sql);
        query.setParameter("providerOHIPNo", providerOhipNo);
        query.setParameter("groupNo", groupNo);
        query.setParameter("specialty", specialty);
        query.setParameter("processDate", processDate);

        List<BillingONEAReport> results = query.getResultList();
        return results;
    }

    @Override 
    public List<BillingONEAReport> findByProviderOhipNoAndGroupNoAndSpecialtyAndProcessDateAndBillingNo(String providerOhipNo, String groupNo, String specialty, Date processDate, Integer billingNo) {
    	String sql = "select b from BillingONEAReport b where b.providerOHIPNo=:providerOHIPNo and b.groupNo=:groupNo and b.specialty=:specialty and b.processDate = :processDate  and b.billingNo=:billingNo";
    	Query query = entityManager.createQuery(sql);
        query.setParameter("providerOHIPNo", providerOhipNo);
        query.setParameter("groupNo", groupNo);
        query.setParameter("specialty", specialty);
        query.setParameter("processDate", processDate);
  	query.setParameter("billingNo", billingNo);
        
        List<BillingONEAReport> results = query.getResultList();       
        return results;
    }
    
    @Override 
    public List<BillingONEAReport> findByBillingNo(Integer billingNo) {
    	String sql = "select b from BillingONEAReport b where b.billingNo=:billingNo order by b.processDate DESC";
    	Query query = entityManager.createQuery(sql);
        query.setParameter("billingNo", billingNo);

        
        List<BillingONEAReport> results = query.getResultList();
        
        return results;
    }
    
    @Override 
    public List<String> getBillingErrorList(Integer billingNo) {
        List<String> errors = new ArrayList<String>();
        
        Query query = entityManager.createQuery("select eaRpt from BillingONEAReport eaRpt where eaRpt.billingNo = (:billingNo) order by processDate desc");
        query.setParameter("billingNo", billingNo);

        
        List<BillingONEAReport> eaReports = query.getResultList();
        
        for (BillingONEAReport eaReport : eaReports) {
            String[] claimErrors = eaReport.getClaimError().split("\\s");
            for (String claimError : claimErrors) {
                if (!claimError.trim().isEmpty())
                    errors.add(claimError);
            }
            
            String[] codeErrors = eaReport.getCodeError().split("\\s");
            for (String codeError : codeErrors) {
                if (!codeError.trim().isEmpty())
                    errors.add(codeError);
            }                         
        }
		
        return errors;
    }

	@Override 
    public List<BillingONEAReport> findByMagic(String ohipNo, String billingGroupNo, String specialtyCode, Date fromDate, Date toDate, String reportName) {
		ParamAppender appender = getAppender("b");
		appender.and("b.providerOHIPNo = :ohipNo", "ohipNo", ohipNo);
		appender.and("b.groupNo = :billingGroupNo", "billingGroupNo", billingGroupNo);
		appender.and("b.specialty = :specialtyCode", "specialtyCode", specialtyCode);
		appender.and("b.codeDate >= :fromDate", "fromDate", fromDate);
		appender.and("b.codeDate <= :toDate", "toDate", toDate);
		
		if( reportName != null && !"".equals(reportName)) {
			appender.and("b.reportName = :reportName", "reportName", reportName);
		}
		
		appender.addOrder("b.codeDate");
		
		Query query = entityManager.createQuery(appender.toString());
		appender.setParams(query);
		return query.getResultList();
    }

	@Override 
    public List<BillingONEAReport> findByMagic(List<BillingProviderData> list, Date fromDate, Date toDate, String reportName) {
		ParamAppender appender = getAppender("b");
		
		boolean hasProviderData = !list.isEmpty();
		if (hasProviderData) {
			ParamAppender providerSubclauseAppender = new ParamAppender();
			for (int i = 0; i < list.size(); i++) {
				ParamAppender providerAppender = new ParamAppender();
				
				BillingProviderData d  = list.get(i);
				ParamAppender pa = new ParamAppender();
				pa.and("b.providerOHIPNo = :ohipNo" + i, "ohipNo" + i, d.getOhipNo());
				pa.and("b.groupNo = :billingGroupNo" + i, "billingGroupNo" + i, d.getBillingGroupNo());
				pa.and("b.specialty = :specialtyCode" + i, "specialtyCode" + i, d.getSpecialtyCode());
				
				providerSubclauseAppender.or(providerAppender);
			}
			appender.and(providerSubclauseAppender);
		}
		
		appender.and("b.codeDate >= :fromDate", "fromDate", fromDate);
		appender.and("b.codeDate <= :toDate", "toDate", toDate);
		if( !"".equals(reportName) ) {
			appender.and("b.reportName = :reportName", "reportName", reportName);
		}
		appender.addOrder("b.codeDate");

		Query query = entityManager.createQuery(appender.toString());
		appender.setParams(query);
		return query.getResultList();

    }
}
