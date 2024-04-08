package org.oscarehr.common.dao;

import java.math.BigDecimal;
import java.util.List;
import org.oscarehr.common.model.BillingOnItemPayment;

public interface BillingOnItemPaymentDao extends AbstractDao<BillingOnItemPayment> {
    BillingOnItemPayment findByPaymentIdAndItemId(int paymentId, int itemId);
    List<BillingOnItemPayment> getAllByItemId(int itemId);
    List<BillingOnItemPayment> getItemsByPaymentId(int paymentId);
    BigDecimal getAmountPaidByItemId(int itemId);
    List<BillingOnItemPayment> getItemPaymentByInvoiceNoItemId(Integer ch1_id, Integer item_id);
    List<BillingOnItemPayment> findByBillingNo(int billingNo);
    //BigDecimal calculateItemPaymentTotal(List<BillingOnItemPayment> paymentRecords);
    //BigDecimal calculateItemRefundTotal(List<BillingOnItemPayment> paymentRecords);
}
