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
		int billNo = cheader1.getId();
		//Date curDate1 = billPayment.getPaymentDate();
		Date curDate=new Date();
		String staus="P";
		SimpleDateFormat admissionDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		BillingOnTransaction billTrans = new BillingOnTransaction();
		billTrans.setActionType(BillingDataHlp.ACTION_TYPE.C.name());
		try {
			billTrans.setAdmissionDate(admissionDateFormat.parse(String.valueOf(cheader1.getAdmissionDate())));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MiscUtils.getLogger().info(e.toString());
			billTrans.setAdmissionDate(null);
		}
		billTrans.setBillingDate(cheader1.getBillingDate());
		billTrans.setBillingNotes(cheader1.getComment());
		billTrans.setCh1Id(billNo);
		billTrans.setClinic(cheader1.getClinic());
		billTrans.setCreator(cheader1.getCreator());
		billTrans.setDemographicNo(cheader1.getDemographicNo());
		billTrans.setDxCode(billItem.getDx());
		billTrans.setFacilityNum(cheader1.getFaciltyNum());
		billTrans.setManReview(cheader1.getManReview());
		billTrans.setPaymentDate(curDate);
		billTrans.setPaymentId(billPayment.getId());
		billTrans.setPaymentType(billPayment.getPaymentTypeId());
		billTrans.setPayProgram(cheader1.getPayProgram());
		billTrans.setProviderNo(cheader1.getProviderNo());
		billTrans.setProvince(cheader1.getProvince());
		billTrans.setRefNum(cheader1.getRefNum());
		billTrans.setServiceCode(billItem.getServiceCode());
		billTrans.setServiceCodeInvoiced(billItem.getFee());
		billTrans.setServiceCodeNum(billItem.getServiceCount());
		billTrans.setSliCode(cheader1.getLocation());
		billTrans.setStatus(staus);
		billTrans.setUpdateDatetime(new Timestamp(curDate.getTime()));
		billTrans.setUpdateProviderNo(curProviderNo);
		billTrans.setVisittype(cheader1.getVisitType());
		billTrans.setBillingOnItemPaymentId(itempaymentId);
		
		return billTrans;
	}
	
	public BillingOnTransaction getUpdateCheader1TransTemplate(BillingClaimHeader1Data cheader1, String curProviderNo) {
		Date curDate = new Date();
		SimpleDateFormat admissionDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		BillingOnTransaction billTrans = new BillingOnTransaction();
		billTrans.setActionType(BillingDataHlp.ACTION_TYPE.UH.name());
		try {
			billTrans.setAdmissionDate(admissionDateFormat.parse(cheader1.getAdmission_date()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MiscUtils.getLogger().info(e.toString());
			billTrans.setAdmissionDate(null);
		}
		try {
			billTrans.setBillingDate(admissionDateFormat.parse(cheader1.getBilling_date()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			MiscUtils.getLogger().info(e.toString());
			billTrans.setBillingDate(null);
		}
		billTrans.setBillingNotes(cheader1.getComment());
		try {
			billTrans.setCh1Id(Integer.parseInt(cheader1.getId()));
		} catch (Exception e) {
			MiscUtils.getLogger().info(e.toString());
			billTrans.setCh1Id(-1);
		}
		billTrans.setClinic(cheader1.getClinic());
		billTrans.setCreator(cheader1.getCreator());
		try {
			billTrans.setDemographicNo(Integer.parseInt(cheader1.getDemographic_no()));
		} catch (Exception e) {
			MiscUtils.getLogger().info(e.toString());
			billTrans.setDemographicNo(-1);
		}
		billTrans.setFacilityNum(cheader1.getFacilty_num());
		billTrans.setManReview(cheader1.getMan_review());
		billTrans.setPayProgram(cheader1.getPay_program());
		billTrans.setProviderNo(cheader1.getProviderNo());
		billTrans.setProvince(cheader1.getProvince());
		billTrans.setRefNum(cheader1.getRef_num());
		billTrans.setSliCode(cheader1.getLocation());
		billTrans.setStatus(cheader1.getStatus());
		billTrans.setUpdateDatetime(new Timestamp(curDate.getTime()));
		billTrans.setUpdateProviderNo(curProviderNo);
		billTrans.setVisittype(cheader1.getVisittype());
		
		return billTrans;
	}
}
