/**
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
 */

package oscar.oscarBilling.ca.bc.pageUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.dao.PropertyDao;
import org.oscarehr.common.model.Property;
import org.oscarehr.common.model.Provider;
import org.oscarehr.util.SpringUtils;

import oscar.oscarBilling.ca.bc.MSP.MSPReconcile;
import oscar.oscarBilling.ca.bc.data.BillingFormData;
import oscar.oscarBilling.ca.bc.data.BillingPreference;
import oscar.oscarBilling.ca.bc.data.BillingPreferencesDAO;

/**
 * Forwards flow of control to Billing Preferences Screen
 * @version 1.0
 */
public class ViewBillingPreferencesAction
    extends Action {
  public ActionForward execute(ActionMapping actionMapping,
                               ActionForm actionForm,
                               HttpServletRequest servletRequest,
                               HttpServletResponse servletResponse) {
    BillingPreferencesActionForm frm = (
        BillingPreferencesActionForm) actionForm;
    BillingPreferencesDAO dao = SpringUtils.getBean(BillingPreferencesDAO.class);
    PropertyDao propertyDao = SpringUtils.getBean(PropertyDao.class);
    ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);

    List<Property> propList = propertyDao.findByNameAndProvider("invoice_payee_info", frm.getProviderNo());
    Property invoicePayeeInfo = propList.isEmpty() ? null : propList.get(0);

    if(invoicePayeeInfo == null || invoicePayeeInfo.getValue() == null) {
      Provider provider = providerDao.getProvider(frm.getProviderNo());
      frm.setInvoicePayeeInfo("Dr. " + provider.getFirstName() + " " + provider.getLastName());
    } else {
      frm.setInvoicePayeeInfo(invoicePayeeInfo.getValue());
    }
    //Default to true when nothing is set
    frm.setInvoicePayeeDisplayClinicInfo(propertyDao.findByNameAndProvider("invoice_payee_display_clinic", frm.getProviderNo()).isEmpty() || propertyDao.isActiveBooleanProperty("invoice_payee_display_clinic", frm.getProviderNo()));

    BillingPreference pref = dao.getUserBillingPreference(frm.getProviderNo());
    //If the user doesn't have a BillingPreference record create one
    if (pref == null) {
      pref = new BillingPreference();
      pref.setProviderNo(frm.getProviderNo());
      dao.saveUserPreferences(pref);
    }
    BillingFormData billform = new BillingFormData();
    ArrayList billingFormList = new ArrayList<>();
    oscar.oscarBilling.ca.bc.data.BillingFormData.BillingForm defaultBillingForm = billform.new BillingForm("Clinic Default", "CLINICDEFAULT");
    billingFormList.add(defaultBillingForm);
    billingFormList.addAll(Arrays.asList(billform.getFormList()));
    servletRequest.setAttribute("billingFormList", billingFormList);

    List<Property> defaultBillingFormPropertyList = propertyDao.findByNameAndProvider("default_billing_form", frm.getProviderNo());
    frm.setDefaultBillingForm(defaultBillingFormPropertyList.isEmpty() ? null : defaultBillingFormPropertyList.get(0).getValue());

    List<Property> defaultBillingProviderPropertyList = propertyDao.findByNameAndProvider("default_billing_provider", frm.getProviderNo());
    frm.setDefaultBillingProvider(defaultBillingProviderPropertyList.isEmpty() ? null : defaultBillingProviderPropertyList.get(0).getValue());

    List<Property> gstNoProps = propertyDao.findByNameAndProvider("gst_number", frm.getProviderNo());

    frm.setReferral(String.valueOf(pref.getReferral()));
    frm.setPayeeProviderNo(String.valueOf(pref.getDefaultPayeeNo()));
    frm.setGstNo(gstNoProps.isEmpty() ? null : gstNoProps.get(0).getValue());

    servletRequest.setAttribute("providerList",this.getPayeeProviderList());

    servletRequest.setAttribute("billingProviderList",providerDao.getProvidersWithNonEmptyOhip());
    return actionMapping.findForward("success");
  }

  /**
   * Returns a List of Provider instances
   * @return List
   */
  public List getPayeeProviderList() {
    MSPReconcile rec = new MSPReconcile();
    return rec.getAllProviders();
  }
}
