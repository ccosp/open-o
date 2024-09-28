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
<%@page import="ca.openosp.openo.common.model.OcanStaffForm" %>
<%@page import="ca.openosp.openo.common.model.Admission" %>
<%@page import="ca.openosp.openo.common.model.Demographic" %>
<%@page import="ca.openosp.openo.PMmodule.web.OcanForm" %>
<%@page import="ca.openosp.openo.ehrutil.LoggedInInfo" %>

<%
    LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
    int currentDemographicId = Integer.parseInt(request.getParameter("demographicId"));
    int prepopulationLevel = OcanForm.PRE_POPULATION_LEVEL_ALL;
    String ocanType = request.getParameter("ocanType");
    int ocanStaffFormId = 0;
    if (request.getParameter("ocanStaffFormId") != null && request.getParameter("ocanStaffFormId") != "") {
        ocanStaffFormId = Integer.parseInt(request.getParameter("ocanStaffFormId"));
    }
    int centerNumber = Integer.parseInt(request.getParameter("center_num"));
    String LHIN_code = request.getParameter("LHIN_code");
    String orgName = request.getParameter("orgName");
    String programName = request.getParameter("programName");

    int prepopulate = 0;
    prepopulate = Integer.parseInt(request.getParameter("prepopulate") == null ? "0" : request.getParameter("prepopulate"));

    OcanStaffForm ocanStaffForm = null;
    if (ocanStaffFormId != 0) {
        ocanStaffForm = OcanForm.getOcanStaffForm(Integer.valueOf(request.getParameter("ocanStaffFormId")));
    } else {
        ocanStaffForm = OcanForm.getOcanStaffForm(loggedInInfo.getCurrentFacility().getId(), currentDemographicId, prepopulationLevel, ocanType);

        if (ocanStaffForm.getAssessmentId() == null) {

            OcanStaffForm lastCompletedForm = OcanForm.getLastCompletedOcanFormByOcanType(loggedInInfo.getCurrentFacility().getId(), currentDemographicId, ocanType);
            if (lastCompletedForm != null) {

                // prepopulate the form from last completed assessment
                if (prepopulate == 1) {

                    lastCompletedForm.setAssessmentId(null);
                    lastCompletedForm.setAssessmentStatus("In Progress");

                    ocanStaffForm = lastCompletedForm;

                }
            }
        }
        if (ocanStaffForm != null) {
            ocanStaffFormId = ocanStaffForm.getId() == null ? 0 : ocanStaffForm.getId().intValue();
        }
    }
%>

<div id="center_programNumber<%=centerNumber%>">
    <table>
        <tr>
            <td class="genericTableHeader">Program Number</td>
            <td class="genericTableData">
                <%=OcanForm.renderAsOrgProgramNumberTextField(ocanStaffForm.getId(), "serviceUseRecord_programNumber" + centerNumber, OcanForm.getOcanConnexProgramNumber(LHIN_code, orgName), 10, prepopulationLevel)%>
            </td>
        </tr>


    </table>
</div>
