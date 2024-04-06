package org.oscarehr.common.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.oscarehr.common.model.BillingONCHeader1;
import org.oscarehr.common.model.BillingONPayment;

public interface BillingONPaymentDao extends AbstractDao<BillingONPayment> {
    void setBillingONExtDao(BillingONExtDao billingONExtDao);
    void setBillingONCHeader1Dao(BillingONCHeader1Dao billingONCHeader1Dao);
    BillingONExtDao getBillingONExtDao();
    BillingONCHeader1Dao getBillingONCHeader1Dao();
    List<BillingONPayment> listPaymentsByBillingNo(Integer billingNo);
    List<BillingONPayment> listPaymentsByBillingNoDesc(Integer billingNo);
    BigDecimal getPaymentsSumByBillingNo(Integer billingNo);
    BigDecimal getPaymentsRefundByBillingNo(Integer billingNo);
    BigDecimal getPaymentsDiscountByBillingNo(Integer billingNo);
    String getTotalSumByBillingNoWeb(String billingNo);
    String getPaymentsRefundByBillingNoWeb(String billingNo);
    int getPaymentIdByBillingNo(int billingNo);
    int getCountOfPaymentByPaymentTypeId(int paymentTypeId);
    String getPaymentTypeById(int paymentTypeId);
    List<BillingONPayment> find3rdPartyPayRecordsByBill(BillingONCHeader1 bCh1);
    List<Integer> find3rdPartyPayments(Integer billingNo);
    List<BillingONPayment> find3rdPartyPaymentsByBillingNo(Integer billingNo);
    List<BillingONPayment> find3rdPartyPayRecordsByBill(BillingONCHeader1 bCh1, Date startDate, Date endDate);
    void createPayment(BillingONCHeader1 bCh1,Locale locale, String payType, BigDecimal paidAmt, String payMethod, String providerNo);
    static BigDecimal calculatePaymentTotal(List<BillingONPayment> paymentRecords);
    static BigDecimal calculateRefundTotal(List<BillingONPayment> paymentRecords);
}
