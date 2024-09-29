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

package ca.openosp.openo.phr.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;
import org.apache.struts.actions.DispatchAction;
import ca.openosp.openo.common.dao.ProviderPreferenceDao;
import ca.openosp.openo.common.model.ProviderPreference;
import ca.openosp.openo.myoscar.utils.MyOscarLoggedInInfo;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

public class PHRLogoutAction extends DispatchAction {
    private static Logger log = MiscUtils.getLogger();

    public PHRLogoutAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        String providerNo = loggedInInfo.getLoggedInProviderNo();

        HttpSession session = request.getSession();
        MyOscarLoggedInInfo.setLoggedInInfo(session, null);

        clearSavedMyOscarPassword(providerNo);

        String forwardTo = request.getParameter("forwardto");
        ActionRedirect ar = new ActionRedirect(forwardTo);
        return ar;
    }

    private void clearSavedMyOscarPassword(String providerNo) {
        try {
            ProviderPreferenceDao providerPreferenceDao = (ProviderPreferenceDao) SpringUtils.getBean(ProviderPreferenceDao.class);
            ProviderPreference providerPreference = providerPreferenceDao.find(providerNo);
            if (providerPreference.getEncryptedMyOscarPassword() != null) {
                providerPreference.setEncryptedMyOscarPassword(null);
                providerPreferenceDao.merge(providerPreference);
            }
        } catch (Exception e) {
            log.error("Error clearing myoscarPassword.", e);
        }
    }
}
