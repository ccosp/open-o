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
package ca.openosp.openo.dashboard.admin;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import ca.openosp.openo.common.model.Provider;
import ca.openosp.openo.common.model.Tickler;
import ca.openosp.openo.common.model.TicklerCategory;
import ca.openosp.openo.common.model.TicklerTextSuggest;
import ca.openosp.openo.dashboard.handler.TicklerHandler;
import ca.openosp.openo.managers.ProviderManager2;
import ca.openosp.openo.managers.SecurityInfoManager;
import ca.openosp.openo.managers.TicklerManager;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

import net.sf.json.JSONObject;

public class AssignTicklerAction extends DispatchAction {

    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
    private TicklerManager ticklerManager = SpringUtils.getBean(TicklerManager.class);
    private ProviderManager2 providerManager = SpringUtils.getBean(ProviderManager2.class);

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) {

        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

        if (!securityInfoManager.hasPrivilege(loggedInInfo, "_tickler", SecurityInfoManager.WRITE, null)) {
            return mapping.findForward("unauthorized");
        }

        String demographics = request.getParameter("demographics");
        Tickler.PRIORITY[] priorities = Tickler.PRIORITY.values();
        List<TicklerTextSuggest> textSuggestions = ticklerManager.getActiveTextSuggestions(loggedInInfo);
        List<Provider> providers = providerManager.getProviders(loggedInInfo, Boolean.TRUE);
        List<TicklerCategory> ticklerCategories = ticklerManager.getActiveTicklerCategories(loggedInInfo);

        request.setAttribute("priorities", priorities);
        request.setAttribute("textSuggestions", textSuggestions);
        request.setAttribute("providers", providers);
        request.setAttribute("ticklerCategories", ticklerCategories);
        request.setAttribute("demographics", demographics);

        return mapping.findForward("success");
    }


    @SuppressWarnings({"unchecked", "unused"})
    public ActionForward saveTickler(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) {

        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

        if (!securityInfoManager.hasPrivilege(loggedInInfo, "_tickler", SecurityInfoManager.WRITE, null)) {
            return mapping.findForward("unauthorized");
        }

        TicklerHandler ticklerHandler = new TicklerHandler(loggedInInfo, ticklerManager);
        ticklerHandler.createMasterTickler(request.getParameterMap());
        JSONObject jsonObject = new JSONObject();

        if (ticklerHandler.addTickler(request.getParameter("demographics"))) {
            jsonObject.put("success", "true");
        } else {
            jsonObject.put("success", "false");
        }

        try {
            jsonObject.write(response.getWriter());
        } catch (IOException e) {
            MiscUtils.getLogger().error("JSON response failed", e);
            return mapping.findForward("error");
        }

        return null;
    }
}
