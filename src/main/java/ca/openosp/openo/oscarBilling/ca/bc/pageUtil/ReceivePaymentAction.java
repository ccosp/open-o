//CHECKSTYLE:OFF
/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * <p>
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

package ca.openosp.openo.oscarBilling.ca.bc.pageUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ca.openosp.openo.oscarBilling.ca.bc.MSP.MSPReconcile;
import ca.openosp.openo.oscarBilling.ca.bc.data.BillingHistoryDAO;


/**
 * <p>Responible for executing logic for receiving a private payment</p>
 * <p>When a payment is recieved the method of payment is updated and the staus is set to paidprivate
 * <p>if the entire balance owing is recovered</p>
 *
 * @version 1.0
 */
public class ReceivePaymentAction
        extends Action {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm actionForm,
                                 HttpServletRequest servletRequest,
                                 HttpServletResponse servletResponse) {
        ReceivePaymentActionForm frm = (
                ReceivePaymentActionForm) actionForm;
        double dblAmount = new Double(frm.getAmountReceived()).doubleValue();
        if ("true".equals(frm.getIsRefund())) {

            frm.setAmountReceived(String.valueOf(dblAmount * -1.0));
        }
        this.receivePayment(frm.getBillingmasterNo(), dblAmount, frm.getPaymentMethod());
        frm.setPaymentReceived(true);
        return actionMapping.findForward("success");
    }

    public void receivePayment(String billingMasterNo, double amount, String paymentType) {
        BillingHistoryDAO dao = new BillingHistoryDAO();
        MSPReconcile msp = new MSPReconcile();
        dao.createBillingHistoryArchive(billingMasterNo, amount, paymentType);
        msp.settleIfBalanced(billingMasterNo);
    }
}
