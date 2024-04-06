package org.oscarehr.common.dao;

import org.oscarehr.common.model.BillingONCHeader1;
import org.oscarehr.common.model.BillingONItem;
import org.oscarehr.common.model.BillingONPayment;
import org.oscarehr.common.model.BillingOnTransaction;
import oscar.oscarBilling.ca.on.data.BillingClaimHeader1Data;

public interface BillingOnTransactionDao extends AbstractDao<BillingOnTransaction> {
    BillingOnTransaction getTransTemplate(BillingONCHeader1 cheader1, BillingONItem billItem, BillingONPayment billPayment, String curProviderNo,int itempaymentId);
    BillingOnTransaction getUpdateCheader1TransTemplate(BillingClaimHeader1Data cheader1, String curProviderNo);
}
