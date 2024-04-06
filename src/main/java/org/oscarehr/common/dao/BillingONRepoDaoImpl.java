package org.oscarehr.common.dao;

import org.oscarehr.common.model.BillingONRepo;
import org.oscarehr.common.model.BillingONItem;
import org.springframework.stereotype.Repository;

import org.oscarehr.common.model.BillingONCHeader1;
import org.oscarehr.util.MiscUtils;
import oscar.util.DateUtils;
import java.util.Locale;
import java.util.Date;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.model.Provider;
import org.oscarehr.util.SpringUtils;

@Repository
public class BillingONRepoDaoImpl extends AbstractDaoImpl<BillingONRepo> implements BillingONRepoDao {
    
    public BillingONRepoDaoImpl() {
        super(BillingONRepo.class);        
    }
    
    @Override
    public void createBillingONItemEntry(BillingONItem bItem, Locale locale) {
        // ... rest of the code ...
    }
    
    @Override
    public void createBillingONCHeader1Entry(BillingONCHeader1 bCh1, Locale locale) {
        // ... rest of the code ...
    } 
}
