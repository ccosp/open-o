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


package ca.openosp.openo.PMmodule.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import ca.openosp.openo.common.dao.AdmissionDao;
import ca.openosp.openo.common.dao.OcanStaffFormDao;
import ca.openosp.openo.common.dao.OcanStaffFormDataDao;
import ca.openosp.openo.common.model.Admission;
import ca.openosp.openo.common.model.OcanStaffForm;
import ca.openosp.openo.common.model.OcanStaffFormData;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

public class OcanWorkloadAction extends DispatchAction {

    Logger logger = MiscUtils.getLogger();

    private OcanStaffFormDao ocanStaffFormDao = (OcanStaffFormDao) SpringUtils.getBean(OcanStaffFormDao.class);
    private OcanStaffFormDataDao ocanStaffFormDataDao = (OcanStaffFormDataDao) SpringUtils.getBean(OcanStaffFormDataDao.class);
    private AdmissionDao admissionDao = (AdmissionDao) SpringUtils.getBean(AdmissionDao.class);

    protected ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return view(mapping, form, request, response);
    }

    public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        String providerNo = loggedInInfo.getLoggedInProviderNo();
        Integer facilityId = loggedInInfo.getCurrentFacility().getId();

        List<OcanStaffForm> ocanForms = ocanStaffFormDao.findLatestOcanFormsByStaff(facilityId, providerNo);

        //filter out discharged ones. ie. discharged longer than 3 months.
        //how do we define discharged?
        List<OcanStaffForm> filteredOcanForms = new ArrayList<OcanStaffForm>();
        for (OcanStaffForm ocan : ocanForms) {
            Integer demographicNo = ocan.getClientId();
            List<Admission> currentAdmissions = admissionDao.getCurrentAdmissions(demographicNo);
            boolean admit = true;
            for (Admission adm : currentAdmissions) {
				/*if(adm.getProgramType().equals("community")) {  //Being in community program does not mean the clients are discharged.
					//discharged
					Calendar now = Calendar.getInstance();
					Calendar then = Calendar.getInstance();
					then.setTime(adm.getAdmissionDate());

					now.add(Calendar.MONTH, -3);
					if(then.before(now)) {
						admit=false;
					}
				}
				*/
                //If the client is not in any bed or service program, means he's discharged. Then don't list this client's OCAN form.
                if (adm.getProgramType().equalsIgnoreCase("bed") || adm.getProgramType().equalsIgnoreCase("service"))
                    admit = true;
            }
            if (admit)
                filteredOcanForms.add(ocan);
        }

        request.setAttribute("ocans", filteredOcanForms);

        return mapping.findForward("view");

    }

    /*
     * We have to reassign ALL OCANs for this client over to the new staff member.
     *
     */
    public ActionForward reassign(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MiscUtils.getLogger().info("Reassigning OCAN Workload");

        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

        String assessmentId = request.getParameter("assessmentId");
        String consumerId = request.getParameter("consumerId");
        String newProviderNo = request.getParameter("reassign_new_provider");
        logger.info("assessmentId=" + assessmentId);
        logger.info("newProviderNo=" + newProviderNo);
        logger.info("consumerId=" + consumerId);

        //get the latest of each assessment for the client.
        //update the providerNo, and persist as new form,
        //same with data.
        List<OcanStaffForm> ocans = ocanStaffFormDao.findLatestByConsumer(loggedInInfo.getCurrentFacility().getId(), Integer.valueOf(consumerId));
        for (OcanStaffForm ocan : ocans) {
            List<OcanStaffFormData> ocanData = ocanStaffFormDataDao.findByForm(ocan.getId());

            ocan.setId(null);
            ocan.setProviderNo(newProviderNo);
            ocan.setCreated(new Date());
            ocanStaffFormDao.persist(ocan);

            for (OcanStaffFormData data : ocanData) {
                data.setId(null);
                data.setOcanStaffFormId(ocan.getId());
                ocanStaffFormDataDao.persist(data);
            }
        }

        return view(mapping, form, request, response);
    }
}
