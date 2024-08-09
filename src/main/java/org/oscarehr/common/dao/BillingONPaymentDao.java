/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version. 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 *
 * Modifications made by Magenta Health in 2024.
 */
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
    public static BigDecimal calculatePaymentTotal(List<BillingONPayment> paymentRecords) {

        BigDecimal paidTotal = new BigDecimal("0.00");
        //BillingONExtDao bExtDao = (BillingONExtDao) SpringUtils.getBean(BillingONExtDao.class);
        for (BillingONPayment bPay : paymentRecords) {

            //BigDecimal amtPaid = bExtDao.getPayment(bPay);
            BigDecimal amtPaid = bPay.getTotal_payment();
            paidTotal = paidTotal.add(amtPaid);
        }

        return paidTotal;
    }

    public static BigDecimal calculateRefundTotal(List<BillingONPayment> paymentRecords) {

        BigDecimal refundTotal = new BigDecimal("0.00");
        //BillingONExtDao bExtDao = (BillingONExtDao) SpringUtils.getBean(BillingONExtDao.class);
        for (BillingONPayment bPay : paymentRecords) {

            //BigDecimal amtRefunded = bExtDao.getRefund(bPay);
            BigDecimal amtRefunded = bPay.getTotal_refund();
            refundTotal = refundTotal.add(amtRefunded);
        }

        return refundTotal;
    }
}
