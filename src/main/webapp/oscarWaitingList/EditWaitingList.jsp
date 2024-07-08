<%--

    Copyright (c) 2006-. OSCARservice, OpenSoft System. All Rights Reserved.
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

--%>
<%
  if(session.getValue("user") == null) response.sendRedirect("../../logout.jsp");
%>
<%@ page import="java.sql.*, java.util.*, oscar.oscarWaitingList.util.*"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<html:html lang="en">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title></title>
<%
    WLWaitingListUtil.updateWaitingListRecord(request.getParameter("listId"), request.getParameter("waitingListNote"), request.getParameter("demographicNo"), request.getParameter("onListSince"));
    response.sendRedirect("../demographic/demographiccontrol.jsp?demographic_no=" + request.getParameter("demographicNo") + "&displaymode=edit&dboperation=search_detail");    
%>


<html:base />
</head>


<link rel="stylesheet" type="text/css" href="../styles.css">
<body topmargin="0" leftmargin="0" vlink="#0000FF">
<html:errors />
<table>
	<tr>
		<td>Update waiting list</td>
	</tr>
</table>




</body>
</html:html>
