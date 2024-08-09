<%--

    Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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

    This software was written for the
    Department of Family Medicine
    McMaster University
    Hamilton
    Ontario, Canada

--%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
    String roleName2$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName2$%>" objectName="_form" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_form");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>

<html:html lang="en">

<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title>Late Life FDI: Disability component</title>
<html:base />
<link rel="stylesheet" type="text/css" media="all" href="../share/css/extractedFromPages.css"  />
</head>

<body bgproperties="fixed" topmargin="0" leftmargin="0" rightmargin="0"
	onload="window.resizeTo(640,500)">
<!--
@oscar.formDB Table="formAdf" 
@oscar.formDB Field="ID" Type="int(10)" Null="NOT NULL" Key="PRI" Default="" Extra="auto_increment"
@oscar.formDB Field="demographic_no" Type="int(10)" Null="NOT NULL" Default="'0'" 
@oscar.formDB Field="provider_no" Type="int(10)" Null="" Default="NULL" 
@oscar.formDB Field="formCreated" Type="date" Null="" Default="NULL" 
@oscar.formDB Field="formEdited" Type="timestamp"  
-->
<table border="0" cellspacing="1" cellpadding="0" width="600px"
	height="95%">
	<tr>
		<td valign="top" colspan="2">
		<table border="0" cellspacing="0" cellpadding="0" width="600px"
			height="10%">
			<tr>
				<th class="subject">Late Life FDI: Disability Component</th>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td>
		<table border="0" cellspacing="0" cellpadding="0" height="100%"
			width="100%">
			<tr>
				<td valign="top">
				<table border="0" cellspacing="0" cellpadding="0" height="85%"
					width="100%" id="visualAid1">
					<tr>
						<td valign="top" colspan="2">
						<table width="100%" height="400px" border="0" cellspacing="1"
							cellpadding="2">
							<tr class="title">
								<th colspan="5">Disability Visual Aid #1</th>
							</tr>
							<tr>
								<td class="question" colspan="5">How often do you...? <br>
								<br>
								</td>
							</tr>
							<tr bgcolor="white" height="150px">
								<td width="34%" align="center"><img
									src="graphics/disabilityVisualAid/veryOften.jpg" align="center"
									border='0' /></td>
								<td width="30%" align="center"><img
									src="graphics/disabilityVisualAid/often.jpg" align="center"
									border='0' /></td>
								<td width="25%" align="center"><img
									src="graphics/disabilityVisualAid/onceInAWhile.jpg"
									align="center" border='0' /></td>
								<td width="20%" align="center"><img
									src="graphics/disabilityVisualAid/almostNever.jpg"
									align="center" border='0' /></td>
								<td width="11%" align="center"><img
									src="graphics/disabilityVisualAid/never.jpg" align="center"
									border='0' /></td>
							</tr>
							<tr bgcolor="white" height="80px">
								<td align="center" valign="top">Frequently<br>
								A lot of the time<br>
								A major part of your life</td>
								<td align="center" valign="top">Regularly<br>
								A regular part of your life</td>
								<td align="center" valign="top">Infrequently<br>
								From time to time<br>
								Occasionally</td>
								<td align="center" valign="top">Very infrequently<br>
								Rarely</td>
								<td align="center" valign="top"></td>
							</tr>
							<tr>
								<td></td>
							</tr>
						</table>
						</td>
					</tr>
				</table>
			<tr>
				<td valign="top">
				<table class="Head" valign="bottom" width="100%" height="15%"
					id="copyRight">
					<tr>
						<td><font style="font-size: 70%">&copy; Copyright 2002
						Trustees of Boston University, All Right Reserved</font></td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</body>
</html:html>
