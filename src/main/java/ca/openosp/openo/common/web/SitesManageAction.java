//CHECKSTYLE:OFF
/**
 * Copyright (c) 2006-. OSCARservice, OpenSoft System. All Rights Reserved.
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
 */

package ca.openosp.openo.common.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;
import ca.openosp.openo.common.dao.SiteDao;
import ca.openosp.openo.common.model.Site;

public class SitesManageAction extends DispatchAction {

    private SiteDao siteDao;

    @Override
    protected ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return view(mapping, form, request, response);
    }

    public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        List<Site> sites = siteDao.getAllSites();

        request.setAttribute("sites", sites);
        return mapping.findForward("list");
    }

    public ActionForward add(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        DynaBean lazyForm = (DynaBean) form;

        Site s = new Site();
        lazyForm.set("site", s);

        return mapping.findForward("details");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        DynaBean lazyForm = (DynaBean) form;

        Site s = (Site) lazyForm.get("site");

        // verify mandatories
        if (StringUtils.isBlank(s.getName()) || StringUtils.isBlank(s.getShortName())) {
            ActionMessages errors = this.getErrors(request);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.required", "Site name or short name"));
            this.saveErrors(request, errors);
        }
        if (StringUtils.isBlank(s.getBgColor())) {
            ActionMessages errors = this.getErrors(request);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.required", "Theme color"));
            this.saveErrors(request, errors);
        }

        if (this.getErrors(request).size() > 0)
            return mapping.findForward("details");


        siteDao.save(s);

        return view(mapping, form, request, response);
    }

    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DynaBean lazyForm = (DynaBean) form;

        String siteId = request.getParameter("siteId");
        Site s = siteDao.getById(new Integer(siteId));

        lazyForm.set("site", s);
        return mapping.findForward("details");
    }

    public void setSiteDao(SiteDao siteDao) {
        this.siteDao = siteDao;
    }


}
