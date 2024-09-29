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
package ca.openosp.openo.common.web;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.openosp.openo.ehrutil.MiscUtils;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import ca.openosp.openo.common.dao.UserDSMessagePrefsDao;
import ca.openosp.openo.common.model.UserDSMessagePrefs;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.SpringUtils;

/**
 * Creates a provider notification record.
 */
public class ProviderNotificationAction extends Action {

    private static Logger logger = MiscUtils.getLogger();

    private UserDSMessagePrefsDao userDsMessagePrefsDao = SpringUtils.getBean(UserDSMessagePrefsDao.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        String providerNo = loggedInInfo.getLoggedInProviderNo();
        String resourceId = request.getParameter("id");
        String resourceType = request.getParameter("type");

        if (logger.isDebugEnabled()) {
            logger.debug("Processing notification");
        }

        List<UserDSMessagePrefs> prefs = userDsMessagePrefsDao.findMessages(providerNo, resourceType, resourceId, false);
        for (UserDSMessagePrefs p : prefs) {
            p.setArchived(true);
            userDsMessagePrefsDao.merge(p);
        }

        UserDSMessagePrefs pref = new UserDSMessagePrefs();
        pref.setProviderNo(providerNo);
        pref.setResourceId(resourceId);
        pref.setResourceType(resourceType);
        pref.setArchived(false);
        pref.setResourceUpdatedDate(new Date());

        userDsMessagePrefsDao.persist(pref);

        JSONObject json = new JSONObject();
        json.put("id", pref.getId());
        json.put("status", "success");

        Writer writer = null;
        try {
            writer = new OutputStreamWriter(response.getOutputStream());
            json.write(writer);
            writer.flush();
        } finally {
            writer.close();
        }

        return null;
    }


}
