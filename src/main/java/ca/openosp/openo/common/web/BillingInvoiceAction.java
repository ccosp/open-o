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
package ca.openosp.openo.common.web;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.openosp.openo.OscarProperties;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import ca.openosp.openo.common.service.PdfRecordPrinter;
import ca.openosp.openo.managers.BillingONManager;
import ca.openosp.openo.managers.SecurityInfoManager;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

import ca.openosp.openo.util.ConcatPDF;
import ca.openosp.openo.util.UtilDateUtilities;

/**
 * @author mweston4
 */
public class BillingInvoiceAction extends DispatchAction {

    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);


    public ActionForward getPrintPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String invoiceNo = request.getParameter("invoiceNo");
        String actionResult = "failure";

        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_billing", "r", null)) {
            throw new SecurityException("missing required security object (_billing)");
        }


        if (invoiceNo != null) {
            response.setContentType("application/pdf"); // octet-stream
            response.setHeader("Content-Disposition", "attachment; filename=\"BillingInvoice" + invoiceNo + "_" + UtilDateUtilities.getToday("yyyy-MM-dd.hh.mm.ss") + ".pdf\"");
            boolean bResult = processPrintPDF(Integer.parseInt(invoiceNo), request.getLocale(), response.getOutputStream());
            if (bResult) {
                actionResult = "success";
            }
        }
        return mapping.findForward(actionResult);
    }

    public ActionForward getListPrintPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_billing", "r", null)) {
            throw new SecurityException("missing required security object (_billing)");
        }

        String actionResult = "failure";
        String[] invoiceNos = request.getParameterValues("invoiceAction");

        ArrayList<Object> fileList = new ArrayList<Object>();
        OutputStream fos = null;
        if (invoiceNos != null) {
            for (String invoiceNoStr : invoiceNos) {
                try {
                    Integer invoiceNo = Integer.parseInt(invoiceNoStr);
                    String filename = "BillingInvoice" + invoiceNo + "_" + UtilDateUtilities.getToday("yyyy-MM-dd.hh.mm.ss") + ".pdf";
                    String savePath = OscarProperties.getInstance().getProperty("INVOICE_DIR") + "/" + filename;
                    fos = new FileOutputStream(savePath);
                    processPrintPDF(invoiceNo, request.getLocale(), fos);
                    fileList.add(savePath);
                } catch (Exception e) {
                    MiscUtils.getLogger().error("Error", e);
                } finally {
                    if (fos != null) fos.close();
                }
            }
        }
        if (!fileList.isEmpty()) {
            response.setContentType("application/pdf"); // octet-stream
            response.setHeader("Content-Disposition", "attachment; filename=\"BillingInvoices" + "_" + UtilDateUtilities.getToday("yyyy-MM-dd.hh.mm.ss") + ".pdf\"");
            ConcatPDF.concat(fileList, response.getOutputStream());
            actionResult = "listSuccess";
        }

        return mapping.findForward(actionResult);
    }

    /*
     * The sendInvoiceEmailNotification method in BillingManager is no longer supported.
     * For more details, please refer to the sendInvoiceEmailNotification method.
     */
    @Deprecated
    public ActionForward sendEmail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        throw new UnsupportedOperationException("This method is no longer supported.");
        //  if(!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_billing", "w", null)) {
        //  	throw new SecurityException("missing required security object (_billing)");
        //  }

        // String invoiceNoStr = request.getParameter("invoiceNo");
        // Integer invoiceNo = Integer.parseInt(invoiceNoStr);
        // Locale locale = request.getLocale();
        // String actionResult = "failure";

        // if (invoiceNo != null) {
        //     BillingONManager billingManager = (BillingONManager) SpringUtils.getBean(BillingONManager.class);
        //     billingManager.sendInvoiceEmailNotification(invoiceNo, locale);
        //     billingManager.addEmailedBillingComment(invoiceNo, locale); 
        //     actionResult = "success";
        // }

        // ActionRedirect redirect = new ActionRedirect(mapping.findForward(actionResult));
        // redirect.addParameter("billing_no", invoiceNo);
        // return redirect;
    }

    /*
     * The sendInvoiceEmailNotification method in BillingManager is no longer supported.
     * For more details, please refer to the sendInvoiceEmailNotification method.
     */
    @Deprecated
    public ActionForward sendListEmail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        throw new UnsupportedOperationException("This method is no longer supported.");
        //  if(!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_billing", "w", null)) {
        //  	throw new SecurityException("missing required security object (_billing)");
        //  }

        // String actionResult = "failure";       
        // String[] invoiceNos = request.getParameterValues("invoiceAction");
        // Locale locale = request.getLocale();

        // if (invoiceNos != null) {
        //     for (String invoiceNoStr : invoiceNos) {
        //         Integer invoiceNo = Integer.parseInt(invoiceNoStr);
        //         BillingONManager billingManager = (BillingONManager) SpringUtils.getBean(BillingONManager.class);
        //         billingManager.sendInvoiceEmailNotification(invoiceNo, locale);
        //         billingManager.addEmailedBillingComment(invoiceNo, locale);               
        //     }
        //     actionResult = "listSuccess";
        // }

        // return mapping.findForward(actionResult);
    }

    private boolean processPrintPDF(Integer invoiceNo, Locale locale, OutputStream os) {

        boolean bResult = false;

        if (invoiceNo != null) {
            //Create PDF of the invoice
            PdfRecordPrinter printer = new PdfRecordPrinter(os);
            printer.printBillingInvoice(invoiceNo, locale);

            BillingONManager billingManager = (BillingONManager) SpringUtils.getBean(BillingONManager.class);
            billingManager.addPrintedBillingComment(invoiceNo, locale);
            bResult = true;
        }

        return bResult;
    }

}
