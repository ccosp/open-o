<%--

    Copyright (c) 2008-2012 Indivica Inc.

    This software is made available under the terms of the
    GNU General Public License, Version 2, 1991 (GPLv2).
    License details are available via "indivica.ca/gplv2"
    and "gnu.org/licenses/gpl-2.0.html".

--%>
<!DOCTYPE html>

<%@page import="org.oscarehr.util.LoggedInInfo"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
      String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
      boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_admin,_admin.misc" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_admin&type=_admin.misc");%>
</security:oscarSec>
<%
if(!authed) {
	return;
}
%>

<%
   LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
%>
<%@ page import="org.oscarehr.util.SpringUtils"%>
<%@ page import="java.util.*"%>
<%@ page import="org.oscarehr.hospitalReportManager.SFTPConnector, org.oscarehr.hospitalReportManager.dao.HRMProviderConfidentialityStatementDao" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>

<html:html locale="true" >
<head>
<html:base />
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Hospital Report Manager</title>

    <script src="<%= request.getContextPath() %>/js/global.js"></script>

	<link href="<%=request.getContextPath() %>/css/bootstrap.css" rel="stylesheet" type="text/css">
	<link rel="stylesheet" type="text/css" href="${ pageContext.request.contextPath }/hospitalReportManager/inbox.css" >


	<script>
	function runFetch() {
		window.location = "<%=request.getContextPath() %>/hospitalReportManager/hospitalReportManager.jsp?fetch=true";
	}
	</script>


</head>
<body>
<div class="container">
<h4>Hospital Report Manager</h4>
<% if (request.getParameter("fetch") != null && request.getParameter("fetch").equalsIgnoreCase("true"))
		new SFTPConnector(loggedInInfo).startAutoFetch(loggedInInfo);
%>
<p>
	HRM Status: <%=SFTPConnector.isFetchRunning() ? "Fetching data from HRM" : "Idle" %><br>
	<% if (!SFTPConnector.isFetchRunning()) { %>
		<input type="button" class="btn" onClick="runFetch()" value="Fetch New Data from HRM" >
	<% } else { %>
		Please wait until the current fetch task completes before requesting another data fetch.
	<% } %>
</p>
<form enctype="multipart/form-data" action="<%=request.getContextPath() %>/hospitalReportManager/UploadLab.do" method="post">
    Upload an HRM report from your computer: <input type="file" name="importFile" />
    <span title="<bean:message key="global.uploadWarningBody"/>" style="vertical-align:middle;font-family:arial;font-size:20px;font-weight:bold;color:#ABABAB;cursor:pointer"><img alt="alert" src="../images/icon_alertsml.gif"></span>

     <input type="submit" class="btn" name="submit" value="Upload" >
</form>
<%
	HRMProviderConfidentialityStatementDao hrmProviderConfidentialityStatementDao = (HRMProviderConfidentialityStatementDao) SpringUtils.getBean("HRMProviderConfidentialityStatementDao");
	String statement = hrmProviderConfidentialityStatementDao.getConfidentialityStatementForProvider(loggedInInfo.getLoggedInProviderNo());
%>
<form action="<%=request.getContextPath() %>/hospitalReportManager/Statement.do" method="post">
	<div class="control-group">
		<label class="control-label">Provider Confidentiality Statement</label>
		<div class="controls">
			<textarea name="statement"><%=statement %></textarea>
		</div>
	</div>
	<div>
		<input type="submit" class="btn btn-primary" name="submit" value="Save Statement" >
		<% if (request.getAttribute("statementSuccess") != null && (Boolean) request.getAttribute("statementSuccess")) { %>
			Success
		<% } else if (request.getAttribute("statementSuccess") != null && !((Boolean) request.getAttribute("statementSuccess")))  { %>
			Error
		<% } %>
	</div>
</form>
<input type="button" class="btn" value="I don't want to receive any more HRM outtage messages for this outtage instance" onclick="window.location='disable_msg_action.jsp'" >
</div>
</body>
</html:html>