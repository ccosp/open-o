package org.oscarehr.common.dao;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.oscarehr.common.model.BillingONCHeader1;
import org.oscarehr.common.model.BillingONPayment;
import org.oscarehr.common.model.BillingOnTransaction;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

import oscar.oscarBilling.ca.on.data.BillingClaimHeader1Data;
import oscar.oscarBilling.ca.on.data.BillingDataHlp;
import org.oscarehr.common.model.BillingONItem;

@Repository
public class BillingOnTransactionDaoImpl extends AbstractDaoImpl<BillingOnTransaction> implements BillingOnTransactionDao {
	
	public BillingOnTransactionDaoImpl() {
        super(BillingOnTransaction.class);
    }
	
	public BillingOnTransaction getTransTemplate(BillingONCHeader1 cheader1, BillingONItem billItem, BillingONPayment billPayment, String curProviderNo,int itempaymentId) {
		// ... existing implementation ...
	}
	
	public BillingOnTransaction getUpdateCheader1TransTemplate(BillingClaimHeader1Data cheader1, String curProviderNo) {
		// ... existing implementation ...
	}
}
