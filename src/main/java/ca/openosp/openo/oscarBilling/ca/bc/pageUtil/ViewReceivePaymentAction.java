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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ca.openosp.openo.entities.PaymentType;

public class ViewReceivePaymentAction
        extends Action {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm actionForm,
                                 HttpServletRequest request,
                                 HttpServletResponse servletResponse) {
        ReceivePaymentActionForm frm = (
                ReceivePaymentActionForm) actionForm;
        BillingViewBean bean = new BillingViewBean();
        String billingMasterNo = request.getParameter("lineNo");
        String billNo = request.getParameter("billNo");
        List paymentTypes = bean.getPaymentTypes();
        for (int i = 0; i < paymentTypes.size(); i++) {
            PaymentType tp = (PaymentType) paymentTypes.get(i);
            if ("ELECTRONIC".equals(tp.getPaymentType())) {
                paymentTypes.remove(i);
            }
        }
        frm.setPaymentMethodList(paymentTypes);
        frm.setBillingmasterNo(billingMasterNo);
        frm.setBillNo(billNo);
        return actionMapping.findForward("success");
    }
}
