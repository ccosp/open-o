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

package openo.oscarResearch.oscarDxResearch.pageUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.common.dao.DxresearchDAO;
import org.oscarehr.common.dao.PartialDateDao;
import org.oscarehr.common.model.Dxresearch;
import org.oscarehr.common.model.PartialDate;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.util.ConversionUtils;
import oscar.util.ParameterActionForward;

public class dxResearchUpdateAction extends Action {
    private static final PartialDateDao partialDateDao = (PartialDateDao) SpringUtils.getBean(PartialDateDao.class);
    private static SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_dxresearch", "u", null)) {
            throw new RuntimeException("missing required security object (_dxresearch)");
        }

        String status = request.getParameter("status");
        String did = request.getParameter("did");
        String demographicNo = request.getParameter("demographicNo");
        String providerNo = request.getParameter("providerNo");
        String startDate = request.getParameter("startdate");


        partialDateDao.setPartialDate(startDate, PartialDate.DXRESEARCH, Integer.valueOf(did), PartialDate.DXRESEARCH_STARTDATE);
        startDate = partialDateDao.getFullDate(startDate);


        DxresearchDAO dao = SpringUtils.getBean(DxresearchDAO.class);
        Dxresearch research = dao.find(ConversionUtils.fromIntString(did));
        if (research != null) {
            if (status != null && (status.equals("C") || status.equals("D"))) {
                research.setStatus(status.charAt(0));
            }
            if (startDate != null) {
                if (research.getStatus() == 'A') {
                    try {
                        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                        research.setStartDate(fmt.parse(startDate));
                    } catch (ParseException e) {

                    }
                }
            }
            research.setUpdateDate(new Date());

            dao.merge(research);
        }

        ParameterActionForward forward = new ParameterActionForward(mapping.findForward("success"));
        forward.addParameter("demographicNo", demographicNo);
        forward.addParameter("providerNo", providerNo);
        forward.addParameter("quickList", "");

        String ip = request.getRemoteAddr();
        LogAction.addLog((String) request.getSession().getAttribute("user"), LogConst.UPDATE, "DX", "" + research.getId(), ip, "");


        return forward;
    }

}
