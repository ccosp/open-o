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

<%
  if(session.getValue("user") == null) response.sendRedirect(request.getContextPath() + "/logout.jsp");
  String roleName$ = (String)session.getAttribute("userrole") + "," + (String)session.getAttribute("user");
%>

<%@ page import="java.util.*,oscar.oscarReport.reportByTemplate.*"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<security:oscarSec roleName="<%=roleName$%>"
	objectName="_admin,_report"	rights="r" reverse="<%=true%>">
	<%
	response.sendRedirect(request.getContextPath() + "/logout.jsp");
	%>
</security:oscarSec>
<!DOCTYPE html>
<html:html locale="true">
<head>

<title>Report by Template</title>

	<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
	<link href="${pageContext.request.contextPath}/css/DT_bootstrap.css" rel="stylesheet">
	<link href="${pageContext.request.contextPath}/css/bootstrap-responsive.css" rel="stylesheet">
	<link href="${pageContext.request.contextPath}/share/calendar/calendar.css" title="win2k-cold-1" rel="stylesheet">
	<script src="${pageContext.request.contextPath}/share/javascript/Oscar.js"></script>

	<script src="${pageContext.request.contextPath}/share/calendar/calendar.js"></script>
	<script src="${pageContext.request.contextPath}/share/calendar/lang/<bean:message key="global.javascript.calendar"/>"></script>
	<script src="${pageContext.request.contextPath}/share/calendar/calendar-setup.js"></script>
	<script src="${pageContext.servletContext.contextPath}/library/jquery/jquery-3.6.4.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/bootstrap.min.2.js"></script>
	<script >
	    function checkform(formobj) {
	        if (!validDateFieldsByClass('datefield', formobj)) {
	            alert("Invalid Date: Must be in the format YYYY/MM/DD");
	            return false;
	        }
	        return true;
	    }
	</script>
	<style>
		div#optionsDiv a {
			padding-left: 5px;
			border-left:#0088cc 2px solid;
		}
		div#optionsDiv a:first-of-type {
			border-left:none;
		}

	</style>

</head>

<%
	String templateid = request.getParameter("templateid");
	if (templateid == null) templateid = (String) request.getAttribute("templateid");
	ReportObject curreport = (new ReportManager()).getReportTemplate(templateid);
	ArrayList parameters = curreport.getParameters();
	pageContext.setAttribute("curreport", curreport);
	int step = 0;
%>

<body>

<%@ include file="rbtTopNav.jspf"%>

<%if (templateid == null) { %>
	<jsp:forward page="homePage.jsp" />
<%}%>

<h3>
	<c:out value="${ curreport.title }" /><br>
	<small><c:out value="${ curreport.description }" /></small>
</h3>

<c:if test="${ not empty errormsg }" >
	<div class="alert alert-error" >
    	<a href="#" data-dismiss="alert" class="close">&times;</a>
    	<c:out value="${ errormsg }" />
    </div>
</c:if>

	<div class="well configDiv" id=manageGroups >
		<html:form styleClass="form" action="/oscarReport/reportByTemplate/GenerateReportAction" onsubmit="return checkform(this);">
			<input type="hidden" name="templateId" value="${ curreport.templateId }">
			<input type="hidden" name="type" value="${ curreport.type }">

				<%for (int i=0; i<parameters.size(); i++) {
                             step++;
                             Parameter curparam = (Parameter) parameters.get(i); %>
					<div class="control-group">
						<label class="control-label" for="<%=curparam.getParamId()%>" ><strong>Step <%=step%>: </strong> <%=curparam.getParamDescription()%></label>

					<%-- If LIST field --%>
					<%if (curparam.getParamType().equals(curparam.LIST)) {%>
						 <div class="controls">
							<select name="<%=curparam.getParamId()%>" id="<%=curparam.getParamId()%>">
								<%ArrayList paramChoices = curparam.getParamChoices();
		                                         for (int i2=0; i2<paramChoices.size(); i2++) {
		                                         Choice curchoice = (Choice) paramChoices.get(i2);%>
								<option value="<%=curchoice.getChoiceId()%>"><%=curchoice.getChoiceText()%></option>
								<%}%>
							</select>
						</div>

					<%--If TEXT field --%>
					<% } else if (curparam.getParamType().equals(curparam.TEXT)) {%>
						<div class="controls">
							<input type="text" name="<%=curparam.getParamId()%>" id="<%=curparam.getParamId()%>" />
						</div>

					<%--If DATE field --%>
					<% } else if (curparam.getParamType().equals(curparam.DATE)) {%>
						<div class="controls">
							<div class="input-append" id="<%=curparam.getParamId()%>">
								<input type="text" class="datefield" id="datefield<%=i%>" name="<%=curparam.getParamId()%>" />
								<span class="add-on">
									<a id="obsdate<%=i%>">
										<img title="Calendar" src="${pageContext.request.contextPath}/images/cal.gif" alt="Calendar">
									</a>
								</span>
							</div>
						</div>
						<script> Calendar.setup( { inputField : "datefield<%=i%>", ifFormat : "%Y-%m-%d", showsTime :false, button : "obsdate<%=i%>", singleClick : true, step : 1 } );
                        </script>

                    <%--If CHECK field --%>
                    <% } else if (curparam.getParamType().equals(curparam.CHECK)) {%>
						<input type="hidden" name="<%=curparam.getParamId()%>:check" value="" />
						<div class="controls">

							<input type="checkbox" name="mastercheck" id="mastercheck" onclick="checkAll(this, 'enclosingCol<%=i%>', 'checkclass<%=i%>')" />

							<%ArrayList paramChoices = curparam.getParamChoices();
	                        for (int i2=0; i2<paramChoices.size(); i2++) {
	                            Choice curchoice = (Choice) paramChoices.get(i2);%>
	                            <label class="checkbox control-label" for="<%=curparam.getParamId() + curchoice.getChoiceId()%>">
									<input type="checkbox" name="<%=curparam.getParamId()%>" id="<%=curparam.getParamId() + curchoice.getChoiceId()%>" class="checkclass<%=i%>" value="<%=curchoice.getChoiceId()%>" />
									<%=curchoice.getChoiceText()%>
								</label>
							<%}%>
						</div>
					<% } else if (curparam.getParamType().equals(curparam.TEXTLIST)) {%>
						<div class="controls">
							<input type="text" placeholder="Comma Separated" name="<%=curparam.getParamId()%>:list" id="<%=curparam.getParamId()%>"/>
						</div>
					<% }%>

					</div>

				<%} %> <%--end for loop --%>

					<div class="control-group">
						<label class="control-label"><strong>Step <%=step+1%>:</strong></label>
						<div class="controls">
							<input type="submit" class="btn btn-primary" name="submitButton" value="Run Query" />
						</div>
					</div>
		</html:form>
	</div>

	<div id="optionsDiv" class="form-actions">
		<a href="viewTemplate.jsp?templateid=<%=curreport.getTemplateId()%>" class="link">View Template XML</a>
		<a href="addEditTemplate.jsp?templateid=<%=curreport.getTemplateId()%>&opentext=1" class="link">Edit Template</a>
		<a href="addEditTemplatesAction.do?templateid=<%=curreport.getTemplateId()%>&action=delete"
			onclick="return confirm('Are you sure you want to delete this report template?')" class="link">
			Delete Template
		</a>
	</div>

</html:html>