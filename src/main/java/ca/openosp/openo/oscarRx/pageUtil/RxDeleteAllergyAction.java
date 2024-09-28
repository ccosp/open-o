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


package ca.openosp.openo.oscarRx.pageUtil;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import ca.openosp.openo.common.model.Allergy;
import ca.openosp.openo.managers.SecurityInfoManager;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.SpringUtils;

import ca.openosp.openo.log.LogAction;
import ca.openosp.openo.log.LogConst;
import ca.openosp.openo.oscarRx.data.RxPatientData;


public final class RxDeleteAllergyAction extends Action {
    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws IOException, ServletException {

        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_allergy", "u", null)) {
            throw new RuntimeException("missing required security object (_allergy)");
        }


        // Setup variables
        // Add allergy

        int id = Integer.parseInt(request.getParameter("ID"));
        String demographicNo = request.getParameter("demographicNo");
        String action = request.getParameter("action");

        RxPatientData.Patient patient = (RxPatientData.Patient) request.getSession().getAttribute("Patient");

        Allergy allergy = patient.getAllergy(id);
        if (action != null && action.equals("activate")) {
            patient.activateAllergy(id);
            String ip = request.getRemoteAddr();
            LogAction.addLog((String) request.getSession().getAttribute("user"), "Activate", LogConst.CON_ALLERGY, "" + id, ip, "" + patient.getDemographicNo(), allergy.getAuditString());
        } else {
            patient.deleteAllergy(id);
            String ip = request.getRemoteAddr();
            LogAction.addLog((String) request.getSession().getAttribute("user"), LogConst.DELETE, LogConst.CON_ALLERGY, "" + id, ip, "" + patient.getDemographicNo(), allergy.getAuditString());
        }

        if (demographicNo != null) {
            request.setAttribute("demographicNo", demographicNo);
        }

        return (mapping.findForward("success"));
    }
}
