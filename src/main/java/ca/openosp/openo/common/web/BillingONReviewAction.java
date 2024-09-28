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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsDateJsonBeanProcessor;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import ca.openosp.openo.common.dao.ClinicDAO;
import ca.openosp.openo.common.dao.DemographicDao;
import ca.openosp.openo.common.model.Clinic;
import ca.openosp.openo.common.model.Demographic;
import ca.openosp.openo.managers.SecurityInfoManager;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.SpringUtils;

/**
 * @author mweston4
 */
public class BillingONReviewAction extends DispatchAction {

    private ClinicDAO clinicDao = (ClinicDAO) SpringUtils.getBean(ClinicDAO.class);
    private DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean(DemographicDao.class);
    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);


    public ActionForward getDemographic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String demographicNo = request.getParameter("demographicNo");

        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_demographic", "r", null)) {
            throw new SecurityException("missing required security object (_demographic)");
        }

        Demographic demographic = demographicDao.getDemographic(demographicNo);


        JsonConfig config = new JsonConfig();
        config.registerJsonBeanProcessor(java.sql.Date.class, new JsDateJsonBeanProcessor());

        JSONObject json = JSONObject.fromObject(demographic, config);
        response.getOutputStream().write(json.toString().getBytes());
        return null;
    }

    public ActionForward getClinic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Clinic clinic = clinicDao.getClinic();
        JSONObject json = JSONObject.fromObject(clinic);
        response.getOutputStream().write(json.toString().getBytes());
        return null;
    }
}
