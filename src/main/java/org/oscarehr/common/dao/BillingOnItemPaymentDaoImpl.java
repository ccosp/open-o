package org.oscarehr.common.dao;

import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.BillingOnItemPayment;
import org.springframework.stereotype.Repository;

@Repository
public class BillingOnItemPaymentDaoImpl extends AbstractDaoImpl<BillingOnItemPayment> implements BillingOnItemPaymentDao {
    public BillingOnItemPaymentDaoImpl() {
        super(BillingOnItemPayment.class);
    }

    @Override
    public BillingOnItemPayment findByPaymentIdAndItemId(int paymentId, int itemId) {
        Query query = entityManager.createQuery("select boip from BillingOnItemPayment boip where boip.billingOnPaymentId = ?1 and boip.billingOnItemId = ?2");
		query.setParameter(1, paymentId);
		query.setParameter(2, itemId);
		return getSingleResultOrNull(query);
    }

    @Override
    public List<BillingOnItemPayment> getAllByItemId(int itemId) {
        Query query = entityManager.createQuery("select boip from BillingOnItemPayment boip where boip.billingOnItemId =?1");
		query.setParameter(1, itemId);
		return query.getResultList();
    }

    @Override
    public List<BillingOnItemPayment> getItemsByPaymentId(int paymentId) {
        Query query = entityManager.createQuery("select boip from BillingOnItemPayment boip where boip.billingOnPaymentId = ?1");
		query.setParameter(1, paymentId);
		return query.getResultList();
    }

    @Override
    public BigDecimal getAmountPaidByItemId(int itemId) {
        Query query = entityManager.createQuery("select sum(boip.paid) from BillingOnItemPayment boip where boip.billingOnItemId = ?1");
		query.setParameter(1, itemId);
		BigDecimal paid = null;
		try {
			paid = (BigDecimal) query.getSingleResult();
		} catch (Exception e) {}
		
		if (paid == null) {
			paid = new BigDecimal("0.00");
		}
		
		return paid;
    }

    @Override
    public List<BillingOnItemPayment> getItemPaymentByInvoiceNoItemId(Integer ch1_id, Integer item_id) {
        String sql = "select bPay from BillingOnItemPayment bPay where bPay.ch1Id= ?1 and bPay.billingOnItemId = ?2 ";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, ch1_id);    
        query.setParameter(2, item_id); 
        
        @SuppressWarnings("unchecked")
        List<BillingOnItemPayment> results = query.getResultList();
                              
        return results;
    }

    @Override
    public List<BillingOnItemPayment> findByBillingNo(int billingNo) {
        BigDecimal paidTotal = new BigDecimal("0.00");	        
	     for (BillingOnItemPayment bPay : paymentRecords) {
	         BigDecimal amtPaid = bPay.getPaid();
	         paidTotal = paidTotal.add(amtPaid);                                   
	     }
	         
	     return paidTotal;
    }

    public static BigDecimal calculateItemPaymentTotal(List<BillingOnItemPayment> paymentRecords) {
        BigDecimal refundTotal = new BigDecimal("0.00");
        for (BillingOnItemPayment bPay : paymentRecords) {
       	 	BigDecimal amtRefunded = bPay.getRefund();
            refundTotal = refundTotal.add(amtRefunded);                                   
        }
        
        return refundTotal;
    }

    public static BigDecimal calculateItemRefundTotal(List<BillingOnItemPayment> paymentRecords) {
        Query query = entityManager.createQuery("select boip from BillingOnItemPayment boip where boip.ch1Id = ?1");
		query.setParameter(1, billingNo);
		return query.getResultList();
    }
}
