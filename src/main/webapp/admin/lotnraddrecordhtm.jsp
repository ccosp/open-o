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
<%@page import="java.text.SimpleDateFormat, java.util.*,oscar.oscarPrevention.*,oscar.util.*"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/caisi-tag.tld" prefix="caisi"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ page import="org.oscarehr.common.model.PreventionsLotNrs"%>
<%@ page import="org.oscarehr.common.dao.PreventionsLotNrsDao"%>
<%@ page import="org.oscarehr.util.SpringUtils"%>
<%@ page import="java.util.*"%>
<%@ page import="oscar.OscarProperties"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%
  String curProvider_no,userfirstname,userlastname;
  curProvider_no = (String) session.getAttribute("user");
  userfirstname = (String) session.getAttribute("userfirstname");
  userlastname = (String) session.getAttribute("userlastname"); 
%>

<%
    String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean isSiteAccessPrivacy=false;
    boolean authed=true;
%>

<security:oscarSec roleName="<%=roleName$%>" objectName="_admin" rights="w" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_admin");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>


<security:oscarSec objectName="_site_access_privacy" roleName="<%=roleName$%>" rights="r" reverse="false">
	<%
		isSiteAccessPrivacy=true;
	%>
</security:oscarSec>

<%
ArrayList<String> inject_prev_list = new ArrayList<String>();
ArrayList<HashMap<String,String>> prevList = PreventionDisplayConfig.getInstance().getPreventions();
        for (int i=0; i<prevList.size(); i++){
            HashMap<String,Object> h = new HashMap<String,Object>();
            h.putAll(prevList.get(i));
            if (h!= null && h.get("layout")!= null && h.get("layout").equals("injection")){
            	inject_prev_list.add((String) h.get("name"));
            }
        }
PreventionsLotNrsDao PreventionsLotNrsDao = (PreventionsLotNrsDao)SpringUtils.getBean(PreventionsLotNrsDao.class);

String selectedPrevention = request.getParameter("prevention");
%>
<html:html locale="true">
<head>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/global.js"></script>
<title><bean:message key="admin.admin.add_lot_nr.title" /></title>
<link rel="stylesheet" href="../web.css">
<script type="text/javascript">

function setfocus() {
	document.addlotnr.prevention.focus();
}

function onsub() {
  if(document.addlotnr.prevention.value =="" ||
		  document.addlotnr.lotnr.value ==""   ) {
     alert("<bean:message key="admin.admin.adddelete_lot_nr.msgMissingParams"/>");
     return false;
  }
}

</script>
</head>

<body onLoad="setfocus()" topmargin="0" leftmargin="0" rightmargin="0">
<center>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr bgcolor="#486ebd">
		<th align="CENTER"><font face="Helvetica" color="#FFFFFF"><bean:message
			key="admin.admin.add_lot_nr.description" /></font></th>
	</tr>
</table>
<form method="post" action="lotnraddrecord.jsp" name="addlotnr"
	onsubmit="return onsub();">
<table cellspacing="0" cellpadding="2" width="90%" border="0">
	<tr>
		<td width="50%" align="right"><bean:message
			key="admin.admin.add_lot_nr.prevention" /><font color="red">:</font></td>
		<td>
		 <select id="prevention" name="prevention">
                             <% for (String s:inject_prev_list) {
							 %>
                               <option value="<%=s%>" <%=(s.equals(selectedPrevention)) ? " selected=\"selected\" ":"" %>><%=s%> </option>
                             <%}%>
                         </select>
		</td>
	</tr>
	
	<tr>
			<td align="right"><bean:message
				key="admin.admin.add_lot_nr.lotnr" />:</td>
			<td><input type="text" name="lotnr" size="20"
				maxlength="20"></td>
	</tr>		

	<tr>
		<td colspan="2">
		<div align="center">
		<input type="submit" name="submitbtn"
			value="<bean:message key="admin.lotaddrecordhtm.btnlotAddRecord"/>">
		</div>
		</td>
	</tr>
</table>
</form>


</center>
</body>
</html:html>
