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
package ca.openosp.openo.sharingcenter.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import ca.openosp.openo.managers.SecurityInfoManager;
import ca.openosp.openo.sharingcenter.dao.ClinicInfoDao;
import ca.openosp.openo.sharingcenter.model.ClinicInfoDataObject;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.SpringUtils;

public class ClinicInfoServlet extends Action {

    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_admin", "r", null)) {
            throw new SecurityException("missing required security object (_admin)");
        }

        ClinicInfoDao clinicDao = SpringUtils.getBean(ClinicInfoDao.class);

        // update vs insert
        ClinicInfoDataObject existing = clinicDao.getClinic();
        if (request.getParameter("form_id") != null && !request.getParameter("form_id").isEmpty() && existing != null) {
            existing.setOid(request.getParameter("form_oid"));
            existing.setName(request.getParameter("form_name"));
            existing.setLocalAppName(request.getParameter("form_appname"));
            existing.setFacilityName(request.getParameter("form_facilityname"));
            existing.setUniversalId(request.getParameter("form_universal"));
            existing.setNamespaceId(request.getParameter("form_namespace"));
            existing.setSourceId(request.getParameter("form_source"));
            clinicDao.merge(existing);

        } else {
            ClinicInfoDataObject newInfo = new ClinicInfoDataObject();
            newInfo.setOid(request.getParameter("form_oid"));
            newInfo.setName(request.getParameter("form_name"));
            newInfo.setLocalAppName(request.getParameter("form_appname"));
            newInfo.setFacilityName(request.getParameter("form_facilityname"));
            newInfo.setUniversalId(request.getParameter("form_universal"));
            newInfo.setNamespaceId(request.getParameter("form_namespace"));
            newInfo.setSourceId(request.getParameter("form_source"));
            clinicDao.persist(newInfo);
        }

        response.sendRedirect(request.getHeader("referer"));

        return null;
    }

}
