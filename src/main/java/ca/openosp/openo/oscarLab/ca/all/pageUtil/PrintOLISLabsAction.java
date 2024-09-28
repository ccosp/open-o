//CHECKSTYLE:OFF
/**
 * Copyright (c) 2008-2012 Indivica Inc.
 * <p>
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 * <p>
 * PrintLabsAction.java
 * <p>
 * Created on November 27, 2007, 9:42 AM
 * Author: Adam Balanga
 * <p>
 * PrintLabsAction.java
 * <p>
 * Created on November 27, 2007, 9:42 AM
 * Author: Adam Balanga
 */

/**
 * PrintLabsAction.java
 *
 * Created on November 27, 2007, 9:42 AM
 * Author: Adam Balanga
 */

package ca.openosp.openo.oscarLab.ca.all.pageUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.olis.OLISResultsAction;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import ca.openosp.openo.oscarLab.ca.all.Hl7textResultsData;
import ca.openosp.openo.oscarLab.ca.all.parsers.Factory;
import ca.openosp.openo.oscarLab.ca.all.parsers.OLISHL7Handler;

/**
 *
 * @author wrighd
 */
public class PrintOLISLabsAction extends Action {

    Logger logger = org.oscarehr.util.MiscUtils.getLogger();
    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

    /** Creates a new instance of PrintLabsAction */
    public PrintOLISLabsAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_lab", "r", null)) {
            throw new SecurityException("missing required security object (_lab)");
        }

        try {
            String segmentID = request.getParameter("segmentID");
            String resultUuid = request.getParameter("uuid");


            int obr = Integer.valueOf(request.getParameter("obr"));
            int obx = Integer.valueOf(request.getParameter("obx"));

            if ("true".equals(request.getParameter("showLatest"))) {
                String multiLabId = Hl7textResultsData.getMatchingLabs(segmentID);
                segmentID = multiLabId.split(",")[multiLabId.split(",").length - 1];
            }

            OLISHL7Handler handler = null;

            if (resultUuid != null && !"".equals(resultUuid)) {
                handler = OLISResultsAction.searchResultsMap.get(resultUuid);

            } else {
                handler = (OLISHL7Handler) Factory.getHandler(segmentID);
            }

            if (handler != null) {
                handler.processEncapsulatedData(request, response, obr, obx);
            }
        } catch (Exception e) {
            MiscUtils.getLogger().error("error", e);
        }
        return null;
    }
}
