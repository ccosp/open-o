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
package ca.openosp.openo.fax.admin;

import net.sf.json.JSONObject;
import ca.openosp.openo.form.JSONAction;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import ca.openosp.openo.common.dao.FaxConfigDao;
import ca.openosp.openo.common.model.FaxConfig;
import ca.openosp.openo.managers.FaxManager;
import ca.openosp.openo.managers.SecurityInfoManager;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigureFaxAction extends JSONAction {

    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
    private final FaxManager faxManager = SpringUtils.getBean(FaxManager.class);
    private static final String PASSWORD_BLANKET = "**********";

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return configure(mapping, form, request, response);
    }

    public ActionForward configure(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject;

        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_admin", "r", null)) {
            throw new SecurityException("missing required security object (_admin)");
        }

        try {
            FaxConfigDao faxConfigDao = SpringUtils.getBean(FaxConfigDao.class);
            List<FaxConfig> savedFaxConfigList = faxConfigDao.findAll(null, null);
            List<FaxConfig> faxConfigList = new ArrayList<FaxConfig>();

            String faxUrl = request.getParameter("faxUrl");
            String siteUser = request.getParameter("siteUser");
            String sitePasswd = request.getParameter("sitePasswd");

            String[] faxConfigIds = request.getParameterValues("id");
            String[] faxUsers = request.getParameterValues("faxUser");
            String[] faxPasswds = request.getParameterValues("faxPassword");
            String[] inboxQueues = request.getParameterValues("inboxQueue");
            String[] activeState = request.getParameterValues("activeState");
            String[] faxNumbers = request.getParameterValues("faxNumber");
            String[] senderEmails = request.getParameterValues("senderEmail");
            String[] accountNames = request.getParameterValues("accountName");
            String[] downloadState = request.getParameterValues("downloadState");

            Integer id;
            int savedidx;
            FaxConfig faxConfig;
            FaxConfig savedFaxConfig;
            FaxConfig masterFaxConfig;

            if (faxConfigIds == null) {
                for (FaxConfig sfaxConfig : savedFaxConfigList) {
                    faxConfigDao.remove(sfaxConfig.getId());
                }
            } else {
                for (int idx = 0; idx < faxConfigIds.length; ++idx) {
                    if (StringUtils.trimToNull(faxConfigIds[idx]) == null) {
                        continue;
                    }
                    id = Integer.parseInt(faxConfigIds[idx]);
                    faxConfig = new FaxConfig();
                    faxConfig.setId(id);

                    savedidx = savedFaxConfigList.indexOf(faxConfig);
                    if (savedidx > -1) {
                        savedFaxConfig = savedFaxConfigList.get(savedidx);
                        savedFaxConfig.setUrl(faxUrl);
                        savedFaxConfig.setSiteUser(siteUser);

                        if (!PASSWORD_BLANKET.equals(sitePasswd)) {
                            savedFaxConfig.setPasswd(sitePasswd);
                        }

                        savedFaxConfig.setFaxUser(faxUsers[idx]);

                        if (!PASSWORD_BLANKET.equals(faxPasswds[idx])) {
                            savedFaxConfig.setFaxPasswd(faxPasswds[idx]);
                        }

                        String faxNumber = faxNumbers[idx];
                        if (faxNumber != null) {
                            faxNumber = faxNumber.trim().replaceAll("\\D", "");

                        }
                        savedFaxConfig.setFaxNumber(faxNumber);
                        savedFaxConfig.setSenderEmail(senderEmails[idx]);
                        savedFaxConfig.setQueue(Integer.parseInt(inboxQueues[idx]));
                        savedFaxConfig.setAccountName(accountNames[idx]);
                        savedFaxConfig.setActive(Boolean.parseBoolean(activeState[idx]));
                        savedFaxConfig.setDownload(Boolean.parseBoolean(downloadState[idx]));
                        faxConfigList.add(savedFaxConfig);
                    } else {
                        faxConfig.setId(null);
                        faxConfig.setSiteUser(siteUser);

                        if (!PASSWORD_BLANKET.equals(sitePasswd)) {
                            faxConfig.setPasswd(sitePasswd);
                        }
                        // the password carries over from the last configuration. Usually the first entry
                        else if ((masterFaxConfig = savedFaxConfigList.get(0)) != null) {
                            faxConfig.setPasswd(masterFaxConfig.getPasswd());
                        }

                        faxConfig.setUrl(faxUrl);
                        faxConfig.setFaxUser(faxUsers[idx]);

                        if (!PASSWORD_BLANKET.equals(faxPasswds[idx])) {
                            faxConfig.setFaxPasswd(faxPasswds[idx]);
                        }

                        faxConfig.setFaxNumber(faxNumbers[idx]);
                        faxConfig.setSenderEmail(senderEmails[idx]);
                        faxConfig.setQueue(Integer.parseInt(inboxQueues[idx]));
                        faxConfig.setAccountName(accountNames[idx]);
                        faxConfig.setActive(Boolean.parseBoolean(activeState[idx]));
                        faxConfig.setDownload(Boolean.parseBoolean(downloadState[idx]));
                        faxConfigList.add(faxConfig);
                    }
                }


                for (FaxConfig faxConfig1 : faxConfigList) {
                    faxConfigDao.saveEntity(faxConfig1);
                }


                for (FaxConfig faxConfig2 : savedFaxConfigList) {
                    if (!faxConfigList.contains(faxConfig2)) {
                        faxConfigDao.remove(faxConfig2.getId());
                    }
                }
            }

            /*
             * Ensure that the fax server information remains intact
             * whenever all the gateway accounts are wiped out.
             */
            int auditList = faxConfigDao.getCountAll();
            if (auditList == 0) {
                faxConfig = new FaxConfig();
                faxConfig.setUrl(faxUrl);
                faxConfig.setSiteUser(siteUser);

                if (!PASSWORD_BLANKET.equals(sitePasswd)) {
                    faxConfig.setPasswd(sitePasswd);
                }
                // the password carries over from the last configuration. Usually the first entry
                else if ((masterFaxConfig = savedFaxConfigList.get(0)) != null) {
                    faxConfig.setPasswd(masterFaxConfig.getPasswd());
                }
                faxConfigDao.saveEntity(faxConfig);
            }

            jsonObject = JSONObject.fromObject("{success:true}");
        } catch (Exception ex) {
            jsonObject = JSONObject.fromObject("{success:false}");
            MiscUtils.getLogger().error("COULD NOT SAVE FAX CONFIGURATION", ex);
        }


        try {
            MiscUtils.getLogger().info("JSON: " + jsonObject);
            jsonObject.write(response.getWriter());
        } catch (IOException e) {
            MiscUtils.getLogger().error("JSON WRITER ERROR", e);
        }
        return null;

    }

    public void restartFaxScheduler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_admin.fax.restart", "w", null)) {
            throw new SecurityException("missing required security object (_admin.fax.restart)");
        }
        faxManager.restartFaxScheduler(LoggedInInfo.getLoggedInInfoFromSession(request));
    }

    public void getFaxSchedularStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        if (!securityInfoManager.hasPrivilege(loggedInInfo, "_admin.fax.restart", "r", null)) {
            throw new SecurityException("missing required security object (_admin.fax.restart)");
        }
        jsonResponse(response, faxManager.getFaxSchedularStatus(loggedInInfo));
    }

}
