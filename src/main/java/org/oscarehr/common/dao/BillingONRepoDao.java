package org.oscarehr.common.dao;

import org.oscarehr.common.model.BillingONItem;
import org.oscarehr.common.model.BillingONCHeader1;
import java.util.Locale;

public interface BillingONRepoDao extends AbstractDao<BillingONRepo> {
    void createBillingONItemEntry(BillingONItem bItem, Locale locale);
    void createBillingONCHeader1Entry(BillingONCHeader1 bCh1, Locale locale);
}
