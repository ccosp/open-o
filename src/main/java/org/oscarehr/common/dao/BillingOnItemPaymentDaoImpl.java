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
        // ... implementation code ...
    }

    @Override
    public List<BillingOnItemPayment> getAllByItemId(int itemId) {
        // ... implementation code ...
    }

    @Override
    public List<BillingOnItemPayment> getItemsByPaymentId(int paymentId) {
        // ... implementation code ...
    }

    @Override
    public BigDecimal getAmountPaidByItemId(int itemId) {
        // ... implementation code ...
    }

    @Override
    public List<BillingOnItemPayment> getItemPaymentByInvoiceNoItemId(Integer ch1_id, Integer item_id) {
        // ... implementation code ...
    }

    @Override
    public List<BillingOnItemPayment> findByBillingNo(int billingNo) {
        // ... implementation code ...
    }

    @Override
    public static BigDecimal calculateItemPaymentTotal(List<BillingOnItemPayment> paymentRecords) {
        // ... implementation code ...
    }

    @Override
    public static BigDecimal calculateItemRefundTotal(List<BillingOnItemPayment> paymentRecords) {
        // ... implementation code ...
    }
}
