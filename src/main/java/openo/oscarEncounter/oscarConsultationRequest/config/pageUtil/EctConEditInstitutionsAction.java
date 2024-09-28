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
package openo.oscarEncounter.oscarConsultationRequest.config.pageUtil;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.common.dao.InstitutionDao;
import org.oscarehr.common.model.Institution;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

public class EctConEditInstitutionsAction extends Action {
    private static SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_con", "u", null)) {
            throw new SecurityException("missing required security object (_con)");
        }

        InstitutionDao institutionDao = SpringUtils.getBean(InstitutionDao.class);

        EctConEditInstitutionsForm editSpecialistsForm = (EctConEditInstitutionsForm) form;
        String id = editSpecialistsForm.getId();
        String delete = editSpecialistsForm.getDelete();
        String specialists[] = editSpecialistsForm.getSpecialists();

        ResourceBundle oscarR = ResourceBundle.getBundle("oscarResources", request.getLocale());

        if (delete.equals(oscarR.getString("oscarEncounter.oscarConsultationRequest.config.EditSpecialists.btnDeleteSpecialist"))) {
            if (specialists.length > 0) {
                for (int i = 0; i < specialists.length; i++) {
                    institutionDao.remove(Integer.parseInt(specialists[i]));
                }
            }
            EctConConstructSpecialistsScriptsFile constructSpecialistsScriptsFile = new EctConConstructSpecialistsScriptsFile();
            constructSpecialistsScriptsFile.makeString(request.getLocale());
            return mapping.findForward("delete");
        }

        // not delete request, just update one entry
        Institution institution = institutionDao.find(Integer.parseInt(id));

        int updater = 0;
        request.setAttribute("name", institution.getName());

        request.setAttribute("address", institution.getAddress());
        request.setAttribute("city", institution.getCity());
        request.setAttribute("province", institution.getProvince());
        request.setAttribute("postal", institution.getPostal());

        request.setAttribute("phone", institution.getPhone());
        request.setAttribute("fax", institution.getFax());
        request.setAttribute("website", institution.getWebsite());
        request.setAttribute("email", institution.getEmail());

        request.setAttribute("id", id);

        request.setAttribute("annotation", institution.getAnnotation());


        request.setAttribute("upd", new Integer(updater));
        EctConConstructSpecialistsScriptsFile constructSpecialistsScriptsFile = new EctConConstructSpecialistsScriptsFile();
        request.setAttribute("verd", constructSpecialistsScriptsFile.makeFile());
        constructSpecialistsScriptsFile.makeString(request.getLocale());
        return mapping.findForward("success");
    }
}
