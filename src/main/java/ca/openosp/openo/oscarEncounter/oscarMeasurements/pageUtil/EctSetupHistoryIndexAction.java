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


package ca.openosp.openo.oscarEncounter.oscarMeasurements.pageUtil;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ca.openosp.openo.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBeanHandler;
import org.apache.logging.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import ca.openosp.openo.managers.SecurityInfoManager;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

import ca.openosp.openo.oscarEncounter.oscarMeasurements.bean.EctMeasurementsDataBean;
import ca.openosp.openo.oscarEncounter.pageUtil.EctSessionBean;

public final class EctSetupHistoryIndexAction extends Action {
    private Logger logger = MiscUtils.getLogger();
    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_measurement", "r", null)) {
            throw new SecurityException("missing required security object (_measurement)");
        }

        EctSessionBean bean = (EctSessionBean) request.getSession().getAttribute("EctSessionBean");
        request.getSession().setAttribute("EctSessionBean", bean);

        if (bean != null) {
            Integer demo = Integer.valueOf(bean.getDemographicNo());

            request.getSession().setAttribute("EctSessionBean", bean);

            EctMeasurementsDataBeanHandler hd = new EctMeasurementsDataBeanHandler(demo);
            if (loggedInInfo.getCurrentFacility().isIntegratorEnabled()) {
                List<EctMeasurementsDataBean> measureTypes = (List<EctMeasurementsDataBean>) hd.getMeasurementsDataVector();
                EctMeasurementsDataBeanHandler.addRemoteMeasurementsTypes(loggedInInfo, measureTypes, demo);
            }

            HttpSession session = request.getSession();
            session.setAttribute("measurementsData", hd);

            return (mapping.findForward("continue"));

        } else {
            logger.debug("cannot get the EctSessionBean");
        }
        return (mapping.findForward("newcontinue"));
    }

}
