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

<%@page contentType="text/html"%>
<%@ include file="/casemgmt/taglibs.jsp"%>
<%@page import="java.util.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
	"http://www.w3.org/TR/html4/loose.dtd">

<html:html>
	<head>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
		<html:base />
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><bean-el:message key="${providertitle}" /></title>
		<link rel="stylesheet" type="text/css" href="../oscarEncounter/encounterStyles.css">
	</head>

	<body class="BodyStyle" vlink="#0000FF">
		<table class="MainTable" id="scrollNumber1" name="encounterTable">
			<tr class="MainTableTopRow">
				<td class="MainTableTopRowLeftColumn">
					<bean-el:message key="${providermsgPrefs}" />
			   	</td>
				<td style="color: white" class="MainTableTopRowRightColumn"></td>
			</tr>
			<tr>
				<td class="MainTableLeftColumn"></td>
				<td class="MainTableRightColumn">
			<%if( request.getAttribute("status") == null ){%>
					<html:form action="/setProviderStaleDate.do">
						<input type="hidden" name="method" value="<c:out value="${method}"/>">
						
						<html:textarea property="labMacroJSON.value" style="width:80%;height:80%" rows="25"></html:textarea>
						
						<br/>
						
						<input type="submit" value="<bean-el:message key="${providerbtnSubmit}"/>"/>
						<input type="button" value="<bean-el:message key="${providerbtnCancel}"/>" onclick="window.close();"/>
					</html:form>
			<%}else {%>
					<bean-el:message key="${providermsgSuccess}" />
					<br/><br/>
					<input type="button" value="<bean-el:message key="${providerbtnClose}"/>" onclick="window.close();"/>
			<%}%>
				</td>
			</tr>
			<tr>
				<td class="MainTableBottomRowLeftColumn"></td>
				<td class="MainTableBottomRowRightColumn"></td>
			</tr>
		</table>
	</body>
</html:html>
