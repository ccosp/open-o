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
package ca.openosp.openo.PMmodule.notification;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import ca.openosp.openo.PMmodule.dao.ProgramDao;
import ca.openosp.openo.ehrutil.MiscUtils;


public class EmailTriggerServlet extends HttpServlet {

    private static final Logger logger = MiscUtils.getLogger();

    private ProgramDao programDao;

    @Override
    public final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String app_path = "";
        String domainName = request.getServerName();
        String contextPath = request.getContextPath();
        int serverPort = request.getServerPort();
        if (80 != serverPort) {
            app_path = String.format("%s:%d", domainName, serverPort);
        } else {
            app_path = domainName;
        }
        if (null != contextPath && !"".equalsIgnoreCase(contextPath)) {
            app_path = app_path + contextPath;
        }
        String fid = request.getParameter("fid");
        String demographic_no = request.getParameter("demographic_no");
        String appointment = request.getParameter("appointment");
        String program_id = request.getParameter("program.id");

        int sc = 200;
        try {
            trigerEmail(app_path, fid, demographic_no, appointment, program_id);
        } catch (Exception e) {
            sc = 500;
        }
        response.setStatus(sc);
    }

    @Override
    public final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    /*
     * The sendProxyEformNotification method in WaitListManager is no longer supported.
     * For more details, please refer to the sendProxyEformNotification method.
     *
     * trigger email notification when proxy email is sent to oscar
     */
    @Deprecated
    public void trigerEmail(String app_ctx_path, String fid, String demographic_no, String appointment, String program_id) throws Exception {
        // String programIdsString=StringUtils.trimToNull(OscarProperties.getInstance().getProperty("wait_list_email_notification_program_ids"));

        throw new UnsupportedOperationException("This method is no longer supported.");
        // 	String programIdsString = program_id;

        // 	if (programIdsString==null) return;

        // 	String[] programIdsStringSplit=programIdsString.split(",");

        // 	WaitListManager waitListManager=(WaitListManager) SpringUtils.getBean(WaitListManager.class);
        // 	programDao = (ProgramDao) SpringUtils.getBean(ProgramDao.class);

        // 	for (String programIdString : programIdsStringSplit)
        // 	{
        // 		try {
        //             Integer programId=new Integer(programIdString);
        //             Program program=programDao.getProgram(programId);
        //             waitListManager.sendProxyEformNotification(program, app_ctx_path, fid);
        //         } catch (Exception e) {
        //         	logger.error("Unexpected error processing programId="+programIdString, e);
        //         	throw new Exception("Failed to trigger email notification!");
        //         }
        // 	}
    }
}
