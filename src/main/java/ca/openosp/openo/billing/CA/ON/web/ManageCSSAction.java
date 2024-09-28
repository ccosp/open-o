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


package ca.openosp.openo.billing.CA.ON.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.validator.DynaValidatorForm;
import ca.openosp.openo.common.dao.BillingServiceDao;
import ca.openosp.openo.common.dao.CSSStylesDAO;
import ca.openosp.openo.common.model.BillingService;
import ca.openosp.openo.common.model.CssStyle;
import ca.openosp.openo.managers.SecurityInfoManager;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.SpringUtils;

public class ManageCSSAction extends DispatchAction {
    private CSSStylesDAO cssStylesDao = (CSSStylesDAO) SpringUtils.getBean(CSSStylesDAO.class);
    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<CssStyle> styles = cssStylesDao.findAll();

        DynaValidatorForm frm = (DynaValidatorForm) form;
        frm.set("styles", styles);
        frm.set("styleText", "");
        frm.set("selectedStyle", "-1");
        frm.set("editStyle", "-1");
        return mapping.findForward("init");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {


        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_admin", "w", null)) {
            throw new SecurityException("missing required security object (_admin)");
        }

        DynaValidatorForm frm = (DynaValidatorForm) form;
        String style = (String) frm.get("editStyle");
        String selectedStyle = (String) frm.get("selectedStyle");
        CssStyle cssStyle = null;
        boolean newStyle = false;
        List<CssStyle> styles = cssStylesDao.findAll();
        ;

        if (selectedStyle.equals("-1")) {
            cssStyle = new CssStyle();
            cssStyle.setStatus(CssStyle.ACTIVE);
            newStyle = true;
        } else {
            for (CssStyle cssStylecurrent : styles) {
                if (cssStylecurrent.getStyle().equalsIgnoreCase(style)) {
                    cssStyle = cssStylecurrent;
                    break;
                }
            }

        }

        cssStyle.setName(frm.getString("styleName"));
        cssStyle.setStyle(frm.getString("styleText"));

        if (newStyle) {
            cssStylesDao.persist(cssStyle);
            styles.add(cssStyle);
        } else {
            cssStylesDao.merge(cssStyle);
        }

        frm.set("styles", styles);
        request.setAttribute("success", "true");

        return mapping.findForward("init");
    }


    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_admin", "w", null)) {
            throw new SecurityException("missing required security object (_admin)");
        }

        DynaValidatorForm frm = (DynaValidatorForm) form;
        String style = (String) frm.get("editStyle");
        List<CssStyle> styles = cssStylesDao.findAll();
        ;
        int idx = 0;
        for (CssStyle cssStylecurrent : styles) {
            if (cssStylecurrent.getStyle().equalsIgnoreCase(style)) {
                cssStylecurrent.setStatus(CssStyle.DELETED);
                cssStylesDao.merge(cssStylecurrent);
                styles.remove(idx);

                BillingServiceDao billingServiceDao = (BillingServiceDao) SpringUtils.getBean(BillingServiceDao.class);
                List<BillingService> serviceCodes = billingServiceDao.findBillingCodesByFontStyle(cssStylecurrent.getId());
                for (BillingService servicecode : serviceCodes) {
                    servicecode.setDisplayStyle(null);
                    billingServiceDao.merge(servicecode);
                }
                break;
            }
            ++idx;
        }

        frm.set("styles", styles);
        request.setAttribute("success", "true");

        return mapping.findForward("init");
    }
}
