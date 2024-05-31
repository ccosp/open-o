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
<%@page import="org.oscarehr.util.LoggedInInfo"%>
<%@page import="org.oscarehr.PMmodule.web.ManageLinkedClients.LinkedDemographicHolder"%>
<%@page import="org.oscarehr.util.SpringUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.oscarehr.common.dao.DemographicDao"%>
<%@page import="org.oscarehr.common.model.Demographic"%>
<%@page import="org.oscarehr.PMmodule.web.ManageLinkedClients"%>

<%
	LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
	DemographicDao demographicDao=(DemographicDao)SpringUtils.getBean(DemographicDao.class);

	int currentDemographicId=Integer.parseInt(request.getParameter("demographicId"));
	
	Demographic demographic=demographicDao.getDemographicById(currentDemographicId);
	ArrayList<ManageLinkedClients.LinkedDemographicHolder> demographicsToDisplay=ManageLinkedClients.getDemographicsToDisplay(loggedInInfo, loggedInInfo.getCurrentFacility(), currentDemographicId);
%>

<html>
	<head>
		<title>Manage Linked Clients</title>
	</head>
	<body>

<h3>Manage Linked Clients</h3>

<h4>Demographic</h4>
<form action="manage_linked_clients_action.jsp">
	<input type="hidden" name="demographicId" value="<%=currentDemographicId%>" />
	<table style="padding-left:20px" class="genericTable">
		<tr class="genericTableRow">
			<td class="genericTableHeader">Linked</td>
			<td class="genericTableHeader">Score</td>
			<td class="genericTableHeader">Last Name</td>
			<td class="genericTableHeader">First Name</td>
			<td class="genericTableHeader">Birth Date</td>
			<td class="genericTableHeader">Gender</td>
		</tr>
		<tr class="genericTableRow" style="background-color:#ccffcc">
			<td class="genericTableData">Local</td>
			<td class="genericTableData">100</td>
			<td class="genericTableData"><%=demographic.getLastName()%></td>
			<td class="genericTableData"><%=demographic.getFirstName()%></td>
			<td class="genericTableData"><%=demographic.getFormattedDob()%></td>
			<td class="genericTableData"><%=demographic.getSex()%></td>
		</tr>
		<tr>
			<td colspan="10"></td>
		</tr>
	
		<%
			if (demographicsToDisplay!=null)
			{
				for (LinkedDemographicHolder temp : demographicsToDisplay)
				{
					%>
						<tr class="genericTableRow" style="background-color:#f3f3f3">
							<td class="genericTableData">
								<input type="checkbox" name="linked.<%=temp.linkDestination+'.'+temp.remoteLinkId%>" <%=temp.linked?"checked=\"on\"":""%> />
							</td>
							<td class="genericTableData"><%=temp.matchingScore%></td>
							<td class="genericTableData"><%=temp.lastName%></td>
							<td class="genericTableData"><%=temp.firstName%></td>
							<td class="genericTableData"><%=temp.birthDate%></td>
							<td class="genericTableData"><%=temp.gender%></td>
						</tr>
					<%
				}
			}
		%>
	</table>
	<br />
	<input type="submit" value="save" /> &nbsp;&nbsp; <input type="button" value="cancel" onclick="history.go(-1)" />
</form>

	
	</body>
</html>