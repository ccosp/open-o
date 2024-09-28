<%--


    Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for
    Centre for Research on Inner City Health, St. Michael's Hospital,
    Toronto, Ontario, Canada

--%>
<%@page import="ca.openosp.openo.ehrutil.LoggedInInfo" %>
<%@page import="ca.openosp.openo.common.model.OcanStaffForm" %>
<%@page import="ca.openosp.openo.PMmodule.web.OcanForm" %>
<%@page import="ca.openosp.openo.ehrutil.WebUtils" %>
<%@page import="java.util.Arrays" %>
<%@page import="java.util.Map" %>
<%

    String reasonForAssessment = request.getParameter("reasonForAssessment1");
    Integer clientId = Integer.valueOf(request.getParameter("demographicId1"));
    LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);


    //When can we make Initial OCAN
	/* We can make reassessment without having initial ocan, so comment out the following coding lines.
	if("IA".equals(reasonForAssessment)) {
		if(OcanForm.canCreateInitialAssessment(clientId)) { 
			out.print("ia_true");
		} else {
			out.print("ia_false");
		}
	} else if("RA".equals(reasonForAssessment)) { 
		if(OcanForm.canCreateInitialAssessment(clientId)) { 
			out.print("ra_false");
		} else {
			out.print("ra_true");
		}
	} else if("DIS".equals(reasonForAssessment) || "OTHR".equals(reasonForAssessment) || 
			"SC".equals(reasonForAssessment) || "REV".equals(reasonForAssessment) || 
			"REK".equals(reasonForAssessment)) {
		//Firstly must have an intial ocan
		if(OcanForm.haveInitialAssessment(clientId)) { 
			out.print("ia_exists_true");
		} else {
			
				if(OcanForm.haveReassessment(clientId)) {
					out.print("ia_exists_true");
				} else {
					out.print("ia_exists_false");
				}
		}
	}
	*/

    out.print("ia_true");
    out.print("ra_true");
    if ("DIS".equals(reasonForAssessment) || "OTHR".equals(reasonForAssessment) ||
            "SC".equals(reasonForAssessment) || "REV".equals(reasonForAssessment) ||
            "REK".equals(reasonForAssessment)) {
        //Firstly must have an intial ocan
        if (OcanForm.haveInitialAssessment(loggedInInfo.getCurrentFacility().getId(), clientId)) {
            out.print("ia_exists_true");
        } else {

            if (OcanForm.haveReassessment(loggedInInfo.getCurrentFacility().getId(), clientId)) {
                out.print("ia_exists_true");
            } else {
                out.print("ia_exists_false");
            }
        }
    }

%>
