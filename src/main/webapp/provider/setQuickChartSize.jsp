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
<%
if(session.getValue("user") == null)
    response.sendRedirect("../logout.htm");
%>

<!DOCTYPE html>
<c:set var="ctx" value="${pageContext.request.contextPath}"	scope="request" />
<html:html>
	<head>
		<html:base />
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><bean-el:message key="${providertitle}" /></title>
        <script src="<c:out value="${ctx}"/>/share/javascript/provider_form_validations.js"	></script>
        <script src="<%= request.getContextPath() %>/js/global.js"></script>
		<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet"> <!-- Bootstrap 2.3.1 -->
	</head>
<body class="BodyStyle">

<table class="MainTable" id="scrollNumber1" name="encounterTable">
	<tr class="MainTableTopRow">
		<td class="MainTableTopRowLeftColumn">
			<h4><bean-el:message key="${providermsgPrefs}" /></h4>
		</td>
		<td class="MainTableTopRowRightColumn">
			<h4>&nbsp;&nbsp;<bean-el:message key="${providermsgProvider}" /></h4>
		</td>
	</tr>
	<tr>
		<td class="MainTableLeftColumn">&nbsp;</td>
		<td class="MainTableRightColumn">
		<%if( request.getAttribute("status") == null ){%>
			<bean-el:message key="${providermsgEdit}" />

            <html:form styleId="providerForm" action="/setProviderStaleDate.do">
				<input type="hidden" name="method" value="<c:out value="${method}"/>">
				<p id="errorMessage" class="alert alert-danger" style="display: none; color: red;">
					Invalid input.
				</p>
				Number of Notes : <html:text styleId="numericFormField" property="quickChartSize.value" size="5"/>
                <br/>
                <html:submit styleClass="btn btn-primary" property="btnApply"/>
			</html:form>

		<%}else {%>
			<div class="alert alert-success" style="width:100%"><bean-el:message key="${providermsgSuccess}" /> <br></div>
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