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
package ca.openosp.openo.ws.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jettison.json.JSONObject;
import ca.openosp.openo.app.AppOAuth1Config;
import ca.openosp.openo.app.OAuth1Utils;
import ca.openosp.openo.common.dao.AppDefinitionDao;
import ca.openosp.openo.common.dao.AppUserDao;
import ca.openosp.openo.common.model.AppDefinition;
import ca.openosp.openo.common.model.AppUser;
import ca.openosp.openo.managers.AppManager;
import ca.openosp.openo.managers.SecurityInfoManager;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ws.rest.to.GenericRESTResponse;
import org.springframework.beans.factory.annotation.Autowired;

import ca.openosp.openo.oscarReport.reportByTemplate.ReportManager;

@Path("/reportByTemplate")
public class ReportByTemplateService extends AbstractServiceImpl {
    @Autowired
    private SecurityInfoManager securityInfoManager;

    @Autowired
    private AppDefinitionDao appDefinitionDao;

    @Autowired
    AppManager appManager;

    @Autowired
    private AppUserDao appUserDao;

    @GET
    @Path("/K2AActive/")
    @Produces("application/json")
    public GenericRESTResponse isK2AActive() {
        if (!securityInfoManager.hasPrivilege(getLoggedInInfo(), "_admin", "r", null) && !securityInfoManager.hasPrivilege(getLoggedInInfo(), "_report", "r", null)) {
            throw new RuntimeException("Access Denied");
        }

        GenericRESTResponse response = null;
        AppDefinition appDef = appDefinitionDao.findByName("K2A");
        if (appDef == null) {
            response = new GenericRESTResponse(false, "K2A active");
        } else {
            response = new GenericRESTResponse(true, "K2A not active");
        }
        return response;
    }

    @GET
    @Path("/K2AUrl/")
    @Produces("application/json")
    public String getK2AUrl() {
        if (!securityInfoManager.hasPrivilege(getLoggedInInfo(), "_admin", "r", null) && !securityInfoManager.hasPrivilege(getLoggedInInfo(), "_report", "r", null)) {
            throw new RuntimeException("Access Denied");
        }

        String k2aUrl = null;
        AppDefinition k2aApp = appDefinitionDao.findByName("K2A");
        if (k2aApp != null) {
            try {
                k2aUrl = AppOAuth1Config.fromDocument(k2aApp.getConfig()).getBaseURL();
            } catch (Exception e) {
                MiscUtils.getLogger().error("Error getting K2A URL", e);
            }
        }
        return k2aUrl;
    }

    @GET
    @Path("/allReports")
    @Produces("application/json")
    public String getReportByTemplatesFromK2A() {
        LoggedInInfo loggedInInfo = getLoggedInInfo();
        if (!securityInfoManager.hasPrivilege(loggedInInfo, "_admin", "r", null) && !securityInfoManager.hasPrivilege(getLoggedInInfo(), "_report", "r", null)) {
            throw new RuntimeException("Access Denied");
        }

        try {
            AppDefinition k2aApp = appDefinitionDao.findByName("K2A");
            if (k2aApp != null) {
                AppUser k2aUser = appUserDao.findForProvider(k2aApp.getId(), loggedInInfo.getLoggedInProvider().getProviderNo());

                if (k2aUser != null) {
                    return OAuth1Utils.getOAuthGetResponse(loggedInInfo, k2aApp, k2aUser, "/ws/api/reportByTemplate/getReports", "/ws/api/reportByTemplate/getReports");
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @POST
    @Path("/getReportById/{id}")
    @Produces("application/json")
    public String addK2AReport(@PathParam("id") String id) {
        LoggedInInfo loggedInInfo = getLoggedInInfo();
        if (!securityInfoManager.hasPrivilege(loggedInInfo, "_admin", "r", null) && !securityInfoManager.hasPrivilege(getLoggedInInfo(), "_report", "w", null)) {
            throw new RuntimeException("Access Denied");
        }

        ReportManager reportManager = new ReportManager();

        try {
            AppDefinition k2aApp = appDefinitionDao.findByName("K2A");
            if (k2aApp != null) {
                AppUser k2aUser = appUserDao.findForProvider(k2aApp.getId(), loggedInInfo.getLoggedInProvider().getProviderNo());

                if (k2aUser != null) {
                    String uuid = null;
                    String xml = null;
                    String jsonString = OAuth1Utils.getOAuthGetResponse(loggedInInfo, k2aApp, k2aUser, "/ws/api/reportByTemplate/getReportById/" + id, "/ws/api/reportByTemplate/getReportById/" + id);

                    if (jsonString != null && !jsonString.isEmpty()) {
                        JSONObject post = new JSONObject(jsonString);

                        uuid = post.getString("uuid");
                        xml = post.getString("body");
                    }

                    return reportManager.addTemplate(uuid, StringEscapeUtils.unescapeXml(xml), loggedInInfo);
                } else {
                    return "Failed to download K2A Report By Templates, please contact an administrator";
                }
            } else {
                return "Failed to download K2A Report By Templates, please contact an administrator";
            }
        } catch (Exception e) {
            return null;
        }
    }
}
