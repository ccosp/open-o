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


package ca.openosp.openo.eform.upload;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;

import ca.openosp.openo.eform.EFormUtil;

public class HtmlUploadForm extends ActionForm {
    private FormFile formHtml = null;
    private String formName;
    private String subject;
    private boolean showLatestFormOnly = false;
    private boolean patientIndependent = false;
    private String roleType;

    public HtmlUploadForm() {
    }

    public FormFile getFormHtml() {
        return formHtml;
    }

    public void setFormHtml(FormFile formHtml) {
        this.formHtml = formHtml;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getSubject() {
        return subject;
    }

    public void setFormSubject(String subject) {
        this.subject = subject;
    }

    public boolean isShowLatestFormOnly() {
        return showLatestFormOnly;
    }

    public void setShowLatestFormOnly(boolean showLatestFormOnly) {
        this.showLatestFormOnly = showLatestFormOnly;
    }

    public boolean isPatientIndependent() {
        return patientIndependent;
    }

    public void setPatientIndependent(boolean patientIndependent) {
        this.patientIndependent = patientIndependent;
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if ((formName == null) || (formName.length() == 0)) {
            errors.add("form", new ActionMessage("eform.errors.file_name.missing"));
        }
        if (formHtml.getFileSize() == 0) {
            errors.add("form", new ActionMessage("eform.errors.form_html.missing"));
        }
        if (EFormUtil.formExistsInDB(formName)) {
            errors.add("form", new ActionMessage("eform.errors.form_name.exists", formName));
        }
        return (errors);
    }

}
