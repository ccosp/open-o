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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import ca.openosp.openo.common.dao.MeasurementGroupStyleDao;
import ca.openosp.openo.common.model.MeasurementGroupStyle;
import ca.openosp.openo.managers.SecurityInfoManager;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;


public class EctEditMeasurementStyleAction extends Action {

    private MeasurementGroupStyleDao dao = SpringUtils.getBean(MeasurementGroupStyleDao.class);
    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_admin", "w", null) || securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_admin.measurements", "w", null)) {

            EctEditMeasurementStyleForm frm = (EctEditMeasurementStyleForm) form;
            request.getSession().setAttribute("EctEditMeasurementStyleForm", frm);

            String groupName = frm.getGroupName();
            String styleSheet = frm.getStyleSheet();

            changeCSS(groupName, styleSheet);

            MiscUtils.getLogger().debug("The selected style sheet is: " + styleSheet);
            HttpSession session = request.getSession();
            session.setAttribute("groupName", groupName);

            return mapping.findForward("continue");

        } else {
            throw new SecurityException("Access Denied!"); //missing required security object (_admin)
        }

    }

    /*****************************************************************************************
     * change CSSID to the associated group
     *
     * @return
     ******************************************************************************************/
    private void changeCSS(String inputGroupName, String styleSheet) {

        for (MeasurementGroupStyle m : dao.findByGroupName(inputGroupName)) {
            m.setId(Integer.parseInt(styleSheet));
            dao.merge(m);
        }

    }
}
