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


package ca.openosp.openo.eform.actions;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.WebUtils;

import ca.openosp.openo.eform.EFormUtil;
import ca.openosp.openo.eform.data.EFormBase;
import ca.openosp.openo.eform.data.HtmlEditForm;


public class HtmlEditAction extends Action {

    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        HtmlEditForm fm = (HtmlEditForm) form;

        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_eform", "w", null)) {
            throw new SecurityException("missing required security object (_eform)");
        }

        try {
            String fid = fm.getFid();
            String formName = fm.getFormName();
            String formSubject = fm.getFormSubject();
            String formFileName = fm.getFormFileName();
            String formHtml = fm.getFormHtml();
            boolean showLatestFormOnly = WebUtils.isChecked(request, "showLatestFormOnly");
            boolean patientIndependent = WebUtils.isChecked(request, "patientIndependent");
            String roleType = fm.getRoleType();

            HashMap<String, String> errors = new HashMap<String, String>();
            EFormBase updatedform = new EFormBase(fid, formName, formSubject, formFileName, formHtml, showLatestFormOnly, patientIndependent, roleType); //property container (bean)
            //validation...
            if ((formName == null) || (formName.length() == 0)) {
                errors.put("formNameMissing", "eform.errors.form_name.missing.regular");
            }
            if ((fid.length() > 0) && (EFormUtil.formExistsInDBn(formName, fid) > 0)) {
                errors.put("formNameExists", "eform.errors.form_name.exists.regular");
            }
            if ((fid.length() == 0) && (errors.size() == 0)) {
                fid = EFormUtil.saveEForm(formName, formSubject, formFileName, formHtml, showLatestFormOnly, patientIndependent, roleType);
                request.setAttribute("success", "true");
            } else if (errors.size() == 0) {
                EFormUtil.updateEForm(updatedform);
                request.setAttribute("success", "true");
            }

            HashMap<String, Object> curht = createHashMap(fid, formName, formSubject, formFileName, formHtml, showLatestFormOnly, patientIndependent, roleType);
            request.setAttribute("submitted", curht);

            request.setAttribute("errors", errors);
        } catch (Exception e) {
            MiscUtils.getLogger().error("Error", e);
        }

        return (mapping.findForward("success"));
    }

    private HashMap<String, Object> createHashMap(String fid, String formName, String formSubject, String formFileName, String formHtml, boolean showLatestFormOnly, boolean patientIndependent, String roleType) {
        HashMap<String, Object> curht = new HashMap<String, Object>();
        curht.put("fid", fid);
        curht.put("formName", formName);
        curht.put("formSubject", formSubject);
        curht.put("formFileName", formFileName);
        curht.put("formHtml", formHtml);
        curht.put("showLatestFormOnly", showLatestFormOnly);
        curht.put("patientIndependent", patientIndependent);
        curht.put("roleType", roleType);

        if (fid.length() == 0) {
            curht.put("formDate", "--");
            curht.put("formTime", "--");
        } else {
            curht.put("formDate", EFormUtil.getEFormParameter(fid, "formDate"));
            curht.put("formTime", EFormUtil.getEFormParameter(fid, "formTime"));
        }
        return curht;
    }

}
