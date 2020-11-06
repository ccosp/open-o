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
<%@ page import="org.oscarehr.util.SessionConstants"%>
<%@ page import="org.oscarehr.common.model.ProviderPreference"%>
<%@ include file="/taglibs.jsp"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
      String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
      boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_report,_admin.reporting" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect(request.getContextPath() + "/securityError.jsp?type=_report&type=_admin.reporting");%>
</security:oscarSec>
<%
if(!authed) {
	return;
}
%>

<%@ page import="org.oscarehr.common.dao.DxresearchDAO"%>
<%@ page import="org.oscarehr.common.model.Dxresearch"%>
<%@ page import="oscar.oscarResearch.oscarDxResearch.util.*"%>
<%@ page import="java.util.*, java.sql.*"%>

<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.PMmodule.dao.ProviderDao" %>
<%@ page import="org.oscarehr.common.model.Provider" %>
<%@ page import="org.oscarehr.common.dao.MyGroupDao" %>
<%@ page import="org.oscarehr.common.model.MyGroup" %>

<%
	ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
	MyGroupDao myGroupDao = SpringUtils.getBean(MyGroupDao.class);
%>

<!DOCTYPE html>
<html:html locale="true">
    <head>
        <title><bean:message key="admin.admin.DiseaseRegistry"/></title>
    
	<link rel="stylesheet" type="text/css" media="all" href="${pageContext.servletContext.contextPath}/library/jquery/jquery-ui.theme-1.12.1.min.css" />
	<link rel="stylesheet" type="text/css" media="all" href="${pageContext.servletContext.contextPath}/library/jquery/jquery-ui.structure-1.12.1.min.css" />
	<link href="${pageContext.servletContext.contextPath}/css/DT_bootstrap.css" rel="stylesheet" type="text/css" />
	<link type="text/css" media="all" href="${pageContext.servletContext.contextPath}/css/bootstrap.min.css" rel="stylesheet">
	<link href="${pageContext.servletContext.contextPath}/library/DataTables-1.10.12/media/css/jquery.dataTables.min.css" rel="stylesheet" type="text/css" />	
	<link href="${pageContext.servletContext.contextPath}/library/DataTables-1.10.12/extensions/Responsive/css/responsive.dataTables.min.css" rel="stylesheet" type="text/css" />		
   	<link href="${pageContext.servletContext.contextPath}/library/DataTables-1.10.12/extensions/Responsive/css/responsive.jqueryui.min.css" rel="stylesheet" type="text/css" />

    <script type="text/javascript" src="${pageContext.servletContext.contextPath}/library/jquery/jquery-1.12.0.min.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/library/jquery/jquery-ui-1.12.1.min.js"></script> 	
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/js/bootstrap.min.2.js"></script>
    <script type="text/javascript" src="${pageContext.servletContext.contextPath}/js/dxJSONCodeSearch.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/library/DataTables-1.10.12/media/js/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/library/DataTables-1.10.12/extensions/Responsive/js/dataTables.responsive.min.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/library/DataTables-1.10.12/extensions/Responsive/js/responsive.jqueryui.min.js"></script>

	<script type="text/javascript">
		var ctx = "${pageContext.servletContext.contextPath}";
    	function setAction(target)
    	{
     		document.forms[0].action.value=target;
		};
		
		$(document).ready(function() {
			$('#listview').DataTable({
				responsive: true
			});
		});

	</script>
	<style>
		.ui-autocomplete {
			max-height: 200px;
			overflow-y: auto;
			/* prevent horizontal scrollbar */
			overflow-x: hidden;
			width: 200px;
		}
		/* IE 6 doesn't support max-height
			   * we use height instead, but this forces the menu to always be this tall
			   */
		* html .ui-autocomplete {
			height: 100px;
		}
	</style>

    </head>
    <%
			    ProviderPreference providerPreference=(ProviderPreference)session.getAttribute(SessionConstants.LOGGED_IN_PROVIDER_PREFERENCE);
                String curUser_no = (String) session.getAttribute("user");
                String mygroupno = providerPreference.getMyGroupNo();
                pageContext.setAttribute("mygroupno", mygroupno);
                String radiostatus = (String) session.getAttribute("radiovaluestatus");
                if (radiostatus==null || radiostatus=="")
                    radiostatus="patientRegistedAll";
                String formAction =  "/report/DxresearchReport?method=" + radiostatus;
                request.setAttribute("listview", request.getSession().getAttribute("listview"));
                request.setAttribute("codeSearch", request.getSession().getAttribute("codeSearch"));
                //request.setAttribute("editingCode", request.getSession().getAttribute("editingCode"));
                String editingCodeType = (String) session.getAttribute("editingCodeType");
                String editingCodeCode = (String) session.getAttribute("editingCodeCode");
                String editingCodeDesc = (String) session.getAttribute("editingCodeDesc");

    %>
<body>

<div class="container-fluid">
<div class="navbar">
    <div class="navbar-inner">
    	<a class="brand" href="#"><bean:message key="admin.admin.DiseaseRegistry"/></a>
	</div>
</div>

<div class="well well-small">
	<html:form action="/report/DxresearchReport?method=addSearchCode">
	<div class="row-fluid">
		<input type="hidden" name="action" value="NA"/>
		<html:select property="quicklistname" styleClass="sel">
			<option  value="">Add Dx QuickList</option>
			<logic:iterate id="quickLists" name="allQuickLists" property="dxQuickListBeanVector">
				<option value="<bean:write name="quickLists" property="quickListName"/>" <bean:write name="quickLists" property="lastUsed" />>
					<bean:write name="quickLists" property="quickListName"/>
				</option>
			</logic:iterate>
		</html:select>
        OR
        <html:select property="codesystem" styleClass="sel" styleId="codingSystem">
        	<option value="">Select Coding System</option>
			<logic:iterate id="codingSys" name="codingSystem" property="codingSystems">
				<option value="<bean:write name="codingSys"/>"><bean:write name="codingSys"/></option>
			</logic:iterate>
		</html:select>
        <input type="text" id="codesearch" placeholder="search description" name="codesearch" class="span4 jsonDxSearch"/>
    </div>
    <div class="row-fluid">    
        <nested:submit styleClass="btn btn-primary">Add</nested:submit>
        <input type="button" class="btn btn-danger" value="Clear" onclick="javascript:this.form.action='${pageContext.servletContext.contextPath}/report/DxresearchReport.do?method=clearSearchCode';this.form.submit()" />          
	</div>
	</html:form>

</div>
<div class="row-fluid">
	<strong>Search all patients with disease codes:</strong>
</div>

    <nested:form action='<%=formAction%>' styleClass="form-inline">

	<div class="row-fluid">
		<display:table name="codeSearch" id="codeSearch" class="table table-condensed table-striped">
		    <display:column property="type" title="Code System" />
		    <display:column property="dxSearchCode" title="Code" />
		    <display:column property="description" title="Description" />
		</display:table>
	</div>
	<div class="row-fluid">
        <label class="radio">
            <input type="radio" name="SearchBy" value="radio" id="SearchBy_0" <%="patientRegistedDistincted".equals(radiostatus)?"checked":""%> onclick="javascript:this.form.action='<%= request.getContextPath()%>/report/DxresearchReport.do?method=patientRegistedDistincted'">
            ALL(distincted)</label>
        <label class="radio">
            <input type="radio" name="SearchBy" value="radio" id="SearchBy_1" <%="patientRegistedAll".equals(radiostatus)?"checked":""%> onclick="javascript:this.form.action='<%= request.getContextPath()%>/report/DxresearchReport.do?method=patientRegistedAll'">
            ALL</label>
        <label class="radio">
            <input type="radio" name="SearchBy" value="radio" id="SearchBy_0" <%="patientRegistedActive".equals(radiostatus)?"checked":""%> onclick="javascript:this.form.action='<%= request.getContextPath()%>/report/DxresearchReport.do?method=patientRegistedActive'">
            Active</label>
        <label class="radio">
            <input type="radio" name="SearchBy" value="radio" id="SearchBy_0" <%="patientRegistedDeleted".equals(radiostatus)?"checked":""%> onclick="javascript:this.form.action='<%= request.getContextPath()%>/report/DxresearchReport.do?method=patientRegistedDeleted'">
            Deleted</label>
        <label class="radio">
            <input type="radio" name="SearchBy" value="radio" id="SearchBy_1" <%="patientRegistedResolve".equals(radiostatus)?"checked":""%> onclick="javascript:this.form.action='<%= request.getContextPath()%>/report/DxresearchReport.do?method=patientRegistedResolve'">
            Resolved</label>


        <select id="provider_no" name="provider_no" class="sel">
            <option value="*"><bean:message key="report.reportindex.formAllProviders"/></option>
            
            <option disabled>___________</option>
      
            <security:oscarSec roleName="<%=roleName$%>" objectName="_team_schedule_only" rights="r" reverse="false">
                <%
                       for(Provider p : providerDao.getActiveProviders()) {
                %>
                <option value="<%=p.getProviderNo()%>" <%=mygroupno.equals(p.getProviderNo())?"selected":""%>>
                    <%=p.getFormattedName()%></option>
                    <%
                            }
                    %>
            </security:oscarSec>
            <security:oscarSec roleName="<%=roleName$%>" objectName="_team_schedule_only" rights="r" reverse="true">
 
                <%
                		for(MyGroup g : myGroupDao.searchmygroupno() ){
                      
                %>
                <option value="<%="_grp_"+g.getId().getMyGroupNo()%>" <%=mygroupno.equals(g.getId().getMyGroupNo())?"selected":""%>><%=g.getId().getMyGroupNo()%></option>
                <%
                        }

                        for(Provider p: providerDao.getActiveProviders()) {
                %>
                <option value="<%=p.getProviderNo()%>" <%=mygroupno.equals(p.getProviderNo())?"selected":""%>><%=p.getFormattedName()%></option>
                    <%
                            }
                    %>
            </security:oscarSec>
  
        </select>


		<nested:submit styleClass="btn btn-primary" >Search</nested:submit>
	</div>
	
		<h3>Results</h3>	
 	<div class="row-fluid">
		<display:table name="listview" id="listview" class="table table-striped table-hover table-condensed">
			<display:column property="strFirstName" title="First Name"/>
			<display:column property="strLastName" title="Last Name"/>
			<display:column property="strSex" title="Sex"/>
			<display:column property="strDOB" title="DOB"/>
			<display:column property="strPhone" title="Phone"/>
			<display:column property="strHIN" title="HIN"/>
			<display:column property="strCodeSys" title="Code System"/>
			<display:column property="strCode" title="Code"/>
			<display:column property="strStartDate" title="Start Date"/>
			<display:column property="strUpdateDate" title="Update Date"/>
			<display:column property="strStatus" title="Status"/>
		</display:table>
 	</div>


	<c:if test="${ not empty listview and not empty listview.strCode }">
		<input type="button" class="btn" value="Download Excel" onclick="javascript:this.form.action='${pageContext.servletContext.contextPath}/report/DxresearchReport.do?method=patientExcelReport';this.form.submit()">
	</c:if>

	</nested:form>
</div>
</body>
</html:html>
