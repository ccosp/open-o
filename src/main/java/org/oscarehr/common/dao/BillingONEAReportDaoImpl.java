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
    
    // ... rest of the methods implementation ...
}
