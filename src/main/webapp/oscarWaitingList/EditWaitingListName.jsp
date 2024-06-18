<%--

    Copyright (c) 2007 Peter Hutten-Czapski based on OSCAR general requirements
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

<!DOCTYPE html>

<%
    if(session.getValue("user") == null) response.sendRedirect("../../logout.jsp");
%>
<%@page import="java.util.*"%>
<%@page import="org.apache.struts.action.*"%>
<%@page import="org.oscarehr.common.model.ProviderPreference"%>
<%@page import="org.oscarehr.util.SessionConstants"%>
<%@page import="org.owasp.encoder.Encode"%>
<%@page import="oscar.oscarWaitingList.bean.*"%>
<%@page import="oscar.util.*"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>

<html:html locale="true">
<head>
<script src="${pageContext.request.contextPath}/js/global.js"></script>
<!-- Bootstrap 2.3.1 -->
<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/bootstrap-responsive.css" rel="stylesheet">

<title>Change Waiting List Name</title>

<script>

function resetFields(actionType){

	if(actionType == "create"){
		document.forms[0].wlChangedName.value="";
	}else if(actionType == "change"){
		document.forms[0].wlNewName.value="";
	}else if(actionType == "remove"){
		document.forms[0].wlChangedName.value="";
		document.forms[0].wlNewName.value="";
	}
}

</script>
<%
	ProviderPreference providerPreference=(ProviderPreference)session.getAttribute(SessionConstants.LOGGED_IN_PROVIDER_PREFERENCE);

	String groupNo = "";
	if(providerPreference.getMyGroupNo() != null){
		groupNo = providerPreference.getMyGroupNo();
	}
	WLWaitingListNameBeanHandler wlNameHd = new WLWaitingListNameBeanHandler(groupNo, (String)session.getAttribute("user"));

   	List allWaitingListName = wlNameHd.getWaitingListNameList();
%>
</head>
<body>

<%
	String message = "";
	if(request.getAttribute("message") != null){
		message = (String)request.getAttribute("message");
	}
%>


<html:form
	action="/oscarWaitingList/WLEditWaitingListNameAction.do?edit=Y">
	<html:hidden property="actionChosen" />

    <h3>&nbsp;&nbsp;<bean:message key="oscarwaitinglist.displayPatientWaitingList.waitinglist" /></h3>
<%
	if(message != null  &&  !message.equals("")){
%>
<div class="alert"><bean:message key="<%=message%>" /></div>
<%
	}
%>
    <div class="container-fluid  form-horizontal span12" id="editWrapper">
        <div class="row">
            <div class="span6">
              <fieldset>
                <legend><bean:message key="marc-hi.affinityDomains.manageExisting" /></legend>
                <label class="control-label" for="selectedWL" ><bean:message key="oscarMessenger.config.MessengerAdmin.rename" /></label>
                <div class="controls">
                    <select name="selectedWL" id="selectedWL">
				        <option value=""><bean:message key="demographic.demographicaddrecordhtm.cbselectwaitinglist" /></option>
				        <%
                        for(int i=0; i<allWaitingListName.size(); i++){
                                 WLWaitingListNameBean wLBean = (WLWaitingListNameBean) allWaitingListName.get(i);
                                 String id = wLBean.getId();
                                 String name = wLBean.getWaitingListName();
                                 String selected = id.compareTo((String) request.getAttribute("WLId")==null?"0":(String) request.getAttribute("WLId"))==0?"SELECTED":"";
                         %>
				        <option value="<%=id%>" <%=selected%>><%=name%></option>
				        <%}%>
                    </select>
                    <input type="text" class="input-medium" name="wlChangedName" placeholder="" value="">
                    <input type="submit" class="btn" value="<bean:message key="global.btnSave" />"
				    onclick="resetFields('change');document.forms[0].actionChosen.value='change'">
                </div> <!-- class="controls" -->
              </fieldset>
            </div> <!-- class="span4" -->
            <div class="span6">
              <fieldset>
                <legend><bean:message key="oscarMDS.search.formReportStatusNew" /></legend>
                <label class="control-label" for="wlNewName"><bean:message key="oscarMessenger.config.MessengerAdmin.newGroup" /></label>
                <div class="controls">
                    <input type="text" class="input-medium" name="wlNewName" placeholder="" value="">
                    <input type="submit" class="btn"  value="<bean:message key="global.btnSave" />" onclick="resetFields('create');document.forms[0].actionChosen.value='create'">
                </div> <!-- class="controls" -->
              </fieldset>
            </div> <!-- class="span4" -->
            <div class="span6">
              <fieldset>
                <legend><bean:message key="global.btnDelete" /></legend>
                <label class="control-label" for="selectedWL2"><bean:message key="global.btnDeleteList" /></label>
                <div class="controls">
                    <select name="selectedWL2" id="selectedWL">
				        <option value=""><bean:message key="demographic.demographicaddrecordhtm.cbselectwaitinglist" /></option>
				        <%
                        for(int i=0; i<allWaitingListName.size(); i++){
                                 WLWaitingListNameBean wLBean = (WLWaitingListNameBean) allWaitingListName.get(i);
                                 String id = wLBean.getId();
                                 String name = wLBean.getWaitingListName();
                                 String selected = id.compareTo((String) request.getAttribute("WLId")==null?"0":(String) request.getAttribute("WLId"))==0?"SELECTED":"";
                         %>
				        <option value="<%=id%>" <%=selected%>><%=name%></option>
				        <%}%>
                    </select>
                    <input type="submit" class="btn btn-warning"  value="<bean:message key="global.btnDelete" />"
				    onclick="resetFields('remove');document.forms[0].actionChosen.value='remove'">
                </div> <!-- class="controls" -->
              </fieldset>
            </div> <!-- class="span4" -->
        </div> <!-- class="row" -->
    </div> <!-- end editWrapper -->
<div>
<input type="reset" class="btn btn-link"  value='<bean:message key="global.btnClose" />'
				onClick="window.close();">
</div>
</html:form>

</body>
</html:html>