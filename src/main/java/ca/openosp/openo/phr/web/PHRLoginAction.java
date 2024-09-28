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

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import ca.openosp.openo.common.dao.ProviderPreferenceDao;
import ca.openosp.openo.common.model.ProviderPreference;
import ca.openosp.openo.myoscar.client.ws_manager.AccountManager;
import ca.openosp.openo.myOscar.utils.MyOscarLoggedInInfo;
import ca.openosp.openo.myoscar_server.ws.LoginResultTransfer3;
import ca.openosp.openo.myoscar_server.ws.NotAuthorisedException_Exception;
import ca.openosp.openo.phr.util.MyOscarUtils;
import ca.openosp.openo.ehrutil.EncryptionUtils;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;
import ca.openosp.openo.ehrutil.WebUtils;

public class PHRLoginAction extends DispatchAction {
    private static Logger logger = MiscUtils.getLogger();

    public PHRLoginAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();

        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        String providerNo = loggedInInfo.getLoggedInProviderNo();

        String forwardTo = request.getParameter("forwardto");

        ActionForward ar = new ActionForward(forwardTo);
        request.setAttribute("forwardToOnSuccess", request.getParameter("forwardToOnSuccess"));

        if (MyOscarUtils.getMyOscarUserNameFromOscar(providerNo) == null) {
            request.setAttribute("phrUserLoginErrorMsg", "You have not registered for MyOSCAR");
            request.setAttribute("phrTechLoginErrorMsg", "No MyOSCAR information in the database");
            return ar;
        }

        String myoscarPassword = request.getParameter("phrPassword");
        try {
            String myOscarUserName = MyOscarUtils.getMyOscarUserNameFromOscar(providerNo);

            LoginResultTransfer3 loginResultTransfer = AccountManager.login(MyOscarLoggedInInfo.getMyOscarServerBaseUrl(), myOscarUserName, myoscarPassword);

            if (loginResultTransfer == null) {
                request.setAttribute("phrUserLoginErrorMsg", "Incorrect user/password");
                return ar;
            } else {

                if (loginResultTransfer.getPerson().getRole() != null && !loginResultTransfer.getPerson().getRole().equals("PATIENT")) {

                    MyOscarLoggedInInfo myOscarLoggedInInfo = new MyOscarLoggedInInfo(loginResultTransfer.getPerson().getId(), loginResultTransfer.getSecurityTokenKey(), request.getSession().getId(), request.getLocale());
                    MyOscarLoggedInInfo.setLoggedInInfo(request.getSession(), myOscarLoggedInInfo);

                    boolean saveMyOscarPassword = WebUtils.isChecked(request, "saveMyOscarPassword");
                    if (saveMyOscarPassword) saveMyOscarPassword(session, providerNo, myoscarPassword);
                } else {
                    logger.error("ERROR :" + loginResultTransfer.getPerson().getUserName() + " can not login with role " + loginResultTransfer.getPerson().getRole());
                    request.setAttribute("phrUserLoginErrorMsg", "Invalid Role: Patient Account can not be used");
                }
            }
        } catch (NotAuthorisedException_Exception e) {
            logger.error("Error", e);
            request.setAttribute("phrUserLoginErrorMsg", "Invalid user/password.");
            return (ar);
        } catch (Exception e) {
            logger.error("Error", e);
            request.setAttribute("phrUserLoginErrorMsg", "Error contacting MyOSCAR server, please try again later");
            request.setAttribute("phrTechLoginErrorMsg", e.getMessage());
            return ar;
        }

        logger.debug("Correct user/pass, auth success");
        return ar;
    }

    private void saveMyOscarPassword(HttpSession session, String providerNo, String myoscarPassword) {
        try {
            SecretKeySpec key = MyOscarUtils.getDeterministicallyMangledPasswordSecretKeyFromSession(session);
            byte[] encryptedMyOscarPassword = EncryptionUtils.encrypt(key, myoscarPassword.getBytes("UTF-8"));

            ProviderPreferenceDao providerPreferenceDao = (ProviderPreferenceDao) SpringUtils.getBean(ProviderPreferenceDao.class);
            ProviderPreference providerPreference = providerPreferenceDao.find(providerNo);
            providerPreference.setEncryptedMyOscarPassword(encryptedMyOscarPassword);
            providerPreferenceDao.merge(providerPreference);
        } catch (Exception e) {
            logger.error("Error saving myoscarPassword.", e);
        }
    }
}
