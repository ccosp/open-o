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

import javax.persistence.Query;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.oscarehr.common.model.BillingONCHeader1;
import org.oscarehr.common.model.BillingONExt;
import org.oscarehr.common.model.BillingONPayment;
import org.oscarehr.common.model.BillingPaymentType;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mweston4
 */
public interface BillingONExtDao extends AbstractDao<BillingONExt> {

    public final static String KEY_PAYMENT = "payment";
    public final static String KEY_REFUND = "refund";
    public final static String KEY_DISCOUNT = "discount";
    public final static String KEY_CREDIT = "credit";
    public final static String KEY_PAY_DATE = "payDate";
    public final static String KEY_PAY_METHOD = "payMethod";
    public final static String KEY_TOTAL = "total";
    public final static String KEY_GST = "gst";

    public List<BillingONExt> find(String key, String value);

    public List<BillingONExt> findByBillingNoAndKey(Integer billingNo, String key);

    public List<BillingONExt> findByBillingNoAndPaymentIdAndKey(Integer billingNo, Integer paymentId, String key);

    public String getPayMethodDesc(BillingONExt bExt);

    public BigDecimal getPayment(BillingONPayment paymentRecord);

    public BigDecimal getRefund(BillingONPayment paymentRecord);

    public BillingONExt getRemitTo(BillingONCHeader1 bCh1);

    public BillingONExt getBillTo(BillingONCHeader1 bCh1);

    public BillingONExt getBillToInactive(BillingONCHeader1 bCh1);

    public BillingONExt getDueDate(BillingONCHeader1 bCh1);

    public BillingONExt getUseBillTo(BillingONCHeader1 bCh1);

    public List<BillingONExt> find(Integer billingNo, String key, Date start, Date end);

    public List<BillingONExt> findByBillingNoAndPaymentNo(int billingNo, int paymentId);

    public List<BillingONExt> getClaimExtItems(int billingNo);

    public List<BillingONExt> getBillingExtItems(String billingNo);

    public List<BillingONExt> getInactiveBillingExtItems(String billingNo);

    public BigDecimal getAccountVal(int billingNo, String key);

    public BillingONExt getClaimExtItem(Integer billingNo, Integer demographicNo, String keyVal);

    public void setExtItem(int billingNo, int demographicNo, String keyVal, String value, Date dateTime, char status);

    // public static boolean isNumberKey(String key);
    public boolean isNumberKey(String key);
}
