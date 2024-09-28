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


package openo.oscarEncounter.pageUtil;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.util.MessageResources;
import org.oscarehr.common.dao.ProfessionalSpecialistDao;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.eyeform.dao.EyeformConsultationReportDao;
import org.oscarehr.eyeform.model.EyeformConsultationReport;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import openo.util.StringUtils;

public class EctDisplayConReportAction extends EctDisplayAction {
    private static final String cmd = "conReport";

    ProfessionalSpecialistDao professionalSpecialistDao = (ProfessionalSpecialistDao) SpringUtils.getBean(ProfessionalSpecialistDao.class);
    DemographicManager demographicManager = SpringUtils.getBean(DemographicManager.class);

    public boolean getInfo(EctSessionBean bean, HttpServletRequest request, NavBarDisplayDAO Dao, MessageResources messages) {

        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        if (!securityInfoManager.hasPrivilege(loggedInInfo, "_eyeform", "r", null)) {
            throw new SecurityException("missing required security object (_eyeform)");
        }

        try {

            String appointmentNo = request.getParameter("appointment_no");
            String cpp = request.getParameter("cpp");
            if (cpp == null) {
                cpp = new String();
            }

            //Set lefthand module heading and link
            String winName = "ConReport" + bean.demographicNo;
            String pathview, pathedit;
            Demographic d = demographicManager.getDemographic(loggedInInfo, Integer.valueOf(bean.demographicNo));
            pathview = request.getContextPath() + "/eyeform/ConsultationReportList.do?method=list&cr.demographicNo=" + bean.demographicNo + "&dmname=" + d.getFormattedName();
            pathedit = request.getContextPath() + "/eyeform/Eyeform.do?method=prepareConReport&demographicNo=" + bean.demographicNo + "&appNo=" + appointmentNo + "&flag=new&cpp=" + cpp;

            String url = "popupPage(500,900,'" + winName + "','" + pathview + "')";
            Dao.setLeftHeading(messages.getMessage(request.getLocale(), "global.viewConReport"));
            Dao.setLeftURL(url);

            //set right hand heading link
            winName = "AddConReport" + bean.demographicNo;
            url = "popupPage(700,1000,'" + winName + "','" + pathedit + "'); return false;";
            Dao.setRightURL(url);
            Dao.setRightHeadingID(cmd); //no menu so set div id to unique id for this action

            EyeformConsultationReportDao crDao = (EyeformConsultationReportDao) SpringUtils.getBean(EyeformConsultationReportDao.class);

            List<EyeformConsultationReport> crs = crDao.getByDemographic(Integer.parseInt(bean.demographicNo));
            for (EyeformConsultationReport cr : crs) {
                NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
                item.setDate(cr.getDate());

                ProfessionalSpecialist specialist = professionalSpecialistDao.find(cr.getReferralId());
                String title = specialist.getFormattedName() + " - " + cr.getStatus();
                String itemHeader = StringUtils.maxLenString(title, MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);
                item.setLinkTitle(itemHeader);
                item.setTitle(itemHeader);
                int hash = Math.abs(winName.hashCode());
                url = "popupPage(700,1000,'" + hash + "','" + request.getContextPath() + "/eyeform/Eyeform.do?method=prepareConReport&conReportNo=" + cr.getId() + "&demographicNo=" + bean.demographicNo + "&cpp=" + cpp + "'); return false;";
                item.setURL(url);
                Dao.addItem(item);
            }
            // Dao.sortItems(NavBarDisplayDAO.DATESORT);

        } catch (Exception e) {
            MiscUtils.getLogger().error("Error", e);
            return false;
        }
        return true;
    }

    public String getCmd() {
        return cmd;
    }
}
