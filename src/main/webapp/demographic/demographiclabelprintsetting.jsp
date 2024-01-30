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
<!DOCTYPE html>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
    String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_demographic" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect(request.getContextPath() + "/securityError.jsp?type=_demographic");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>


<%@ page import="java.util.*, java.sql.*, java.net.*, oscar.*" errorPage="../appointment/errorpage.jsp"%>

<%@page import="org.oscarehr.util.SpringUtils" %>

<%@page import="org.oscarehr.common.model.Demographic" %>
<%@page import="org.oscarehr.common.dao.DemographicDao" %>
<%@ page import="org.oscarehr.common.model.ProviderData"%>
<%@ page import="org.oscarehr.common.dao.ProviderDataDao"%>


<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<%
	if(session.getValue("user") == null)  response.sendRedirect("../logout.jsp");
	String curProvider_no = (String) session.getAttribute("user");

	java.util.Properties oscarVariables = oscar.OscarProperties.getInstance();

	DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographicDao");
 	ProviderDataDao providerDao = SpringUtils.getBean(ProviderDataDao.class);
%>

<html:html locale="true">
<head>
<title><bean:message key="demographic.demographiclabelprintsetting.title" /></title>
<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet" type="text/css"> <!-- Bootstrap 2.3.1 -->

<script src="<%= request.getContextPath() %>/js/global.js"></script>

<style>
.copytext {
    font-family:courier;
    font-size: 12px;
    user-select: all;
    cursor: pointer;
}
</style>
<script>

function onNewPatient() {
  document.labelprint.label1no.value="1";
  document.labelprint.label1checkbox.checked=true;
  document.labelprint.label2checkbox.checked=true;
  document.labelprint.label3checkbox.checked=true;
  document.labelprint.label2no.value="6";
  document.labelprint.label3no.value="0";
}
function checkTotal() {
  var total = 0+ document.labelprint.label1no.value + document.labelprint.label2no.value + document.labelprint.label3no.value + document.labelprint.label4no.value + document.labelprint.label5no.value;
  if(total>7) return false;
  return true;
}


</script>
</head>
<body onLoad="setfocus()" >
<h4><bean:message key="demographic.demographiclabelprintsetting.msgMainLabel" /></h4>

<%
	GregorianCalendar now=new GregorianCalendar();  int curYear = now.get(Calendar.YEAR);  int curMonth = (now.get(Calendar.MONTH)+1);  int curDay = now.get(Calendar.DAY_OF_MONTH);
	int age=0, dob_year=0, dob_month=0, dob_date=0;
	String first_name="",last_name="",chart_no="",address="",city="",province="",postal="",phone="",phone2="",dob="",sex="",hin="";
	String refDoc = "";
	String providername = "";
	String demoNo = request.getParameter("demographic_no");

	Demographic demo = demographicDao.getDemographic(demoNo);
	if(demo==null) {
%>
		<bean:message key="demographic.demographiclabelprintsetting.msgFailed" />
<%
	}
	else {
		ProviderData provider = providerDao.findByProviderNo(demo.getProviderNo());
		if(provider != null) {
			providername = provider.getLastName() + "," + provider.getFirstName();
		}

		first_name = Misc.JSEscape(demo.getFirstName());
		last_name = Misc.JSEscape(demo.getLastName());
		sex = demo.getSex();
		dob_year = Integer.parseInt(demo.getYearOfBirth());
		dob_month = Integer.parseInt(demo.getMonthOfBirth());
		dob_date = Integer.parseInt(demo.getDateOfBirth());
		if(dob_year!=0) age=MyDateFormat.getAge(dob_year,dob_month,dob_date);
		dob=dob_year + "/" + demo.getMonthOfBirth() + "/" + demo.getDateOfBirth();

		if (demo.getChartNo()!=null) chart_no = demo.getChartNo();
		if (demo.getAddress()!=null) address = Misc.JSEscape(demo.getAddress());
		if (demo.getCity()!=null) city = demo.getCity();
		if (demo.getProvince()!=null) province = demo.getProvince();
		if (demo.getPostal()!=null) postal = demo.getPostal();
		if (demo.getPhone()!=null) phone = demo.getPhone();
		if (demo.getPhone2()!=null) phone2 = demo.getPhone2();
		if (demo.getHin()!=null) hin = "HN "+demo.getHcType()+" "+demo.getHin()+" "+demo.getVer();
		if (demo.getFamilyDoctor()!=null) refDoc = SxmlMisc.getXmlContent(demo.getFamilyDoctor(),"rd");
	}
	phone2 = (phone2==null || phone2.equals(""))?"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;":(phone2+"&nbsp;") ;
%>

<form method="post" class="form-horizontal" name="labelprint" action="demographicprintdemographic.jsp">
<div class="well">
<table style="width:100%">
	<tr style="text-align:center">
		<th><bean:message key="demographic.demographiclabelprintsetting.msgLabel" /></th>
		<th><bean:message key="demographic.demographiclabelprintsetting.msgNumeberOfLabel" /></th>
		<th><bean:message key="demographic.demographiclabelprintsetting.msgLocation" />
			<input type="hidden" name="address" value="<%=address%>">
			<input type="hidden" name="chart_no" value="<%=chart_no%>">
			<input type="hidden" name="city" value="<%=city%>">
			<input type="hidden" name="dob" value="<%=dob%>">
			<input type="hidden" name="first_name" value="<%=first_name%>">
			<input type="hidden" name="hin" value="<%=hin%>">
			<input type="hidden" name="last_name" value="<%=last_name%>">
			<input type="hidden" name="phone" value="<%=phone%>">
			<input type="hidden" name="phone2" value="<%=phone2%>">
			<input type="hidden" name="postal" value="<%=postal%>">
			<input type="hidden" name="providername" value="<%=providername%>">
			<input type="hidden" name="province" value="<%=province%>">
			<input type="hidden" name="sex" value="<%=sex%>">
			<input type="hidden" name="age" value="<%=age%>">
		</th>
	</tr>
	<tr>
		<td style="align:center">
		<table style="width:90%">
			<tr>
				<td style="border: solid 1px; background-color: white;">
				<span id="copytext1" class="copytext" > <b><%=last_name%>,&nbsp;<%=first_name%></b><br>
				&nbsp;&nbsp;&nbsp;&nbsp;<%=hin%><br>
				&nbsp;&nbsp;&nbsp;&nbsp;<%=dob%>&nbsp;<%=sex%><br>
				<br>
				<b><%=last_name%>,&nbsp;<%=first_name%></b><br>
				&nbsp;&nbsp;&nbsp;&nbsp;<%=hin%><br>
				&nbsp;&nbsp;&nbsp;&nbsp;<%=dob%>&nbsp;<%=sex%><br>
				</span></td>
			</tr>
		</table>
		</td>
		<td style="text-align:center; background-color:#CCCCCC"><a href="#" onClick="onNewPatient()">
			<bean:message key="demographic.demographiclabelprintsetting.btnNewPatientLabel" /></a><br><br>
			<input type="checkbox" name="label1checkbox" value="checked">
			<input type="text" name="label1no" size="2" maxlength="2" value="<%= oscarVariables.getProperty("label.1no","1") %>" />
		</td>
		<td rowspan=5 style="vertical-align:middle; background-color:#999999;">
            <div class="control-group">
			<label class="control-label"><bean:message key="demographic.demographiclabelprintsetting.formLeft" />:</label>
                <div class="controls">
			        <input type="text" class="input-small" name="left" placeholder="<bean:message key="demographic.demographiclabelprintsetting.msgPx" />" maxlength="3" value="<%= oscarVariables.getProperty("label.left","200") %>" >&nbsp; <bean:message key="demographic.demographiclabelprintsetting.msgPx" />
                </div>
            </div>
            <div class="control-group">
			<label class="control-label"><bean:message key="demographic.demographiclabelprintsetting.formTop" />:</label>
                <div class="controls">
			        <input type="text" class="input-small" name="top" maxlength="3" value="<%= oscarVariables.getProperty("label.top","0")%>" placeholder="<bean:message key="demographic.demographiclabelprintsetting.msgPx" />">&nbsp; <bean:message key="demographic.demographiclabelprintsetting.msgPx" />
                </div>
            </div>
            <div class="control-group">
			<label class="control-label"><bean:message key="demographic.demographiclabelprintsetting.formHeight" />:</label>
                <div class="controls">
			        <input type="text" class="input-small" name="height" maxlength="3" value="<%= oscarVariables.getProperty("label.height","145")%>" placeholder="<bean:message key="demographic.demographiclabelprintsetting.msgPx" />">&nbsp; <bean:message key="demographic.demographiclabelprintsetting.msgPx" />
                </div>
            </div>
            <div class="control-group">
			<label class="control-label"><bean:message key="demographic.demographiclabelprintsetting.formGap" />:</label>
                <div class="controls">
			        <input type="text" class="input-small" name="gap" size="3" maxlength="3" value="<%= oscarVariables.getProperty("label.gap","0")%>" placeholder="<bean:message key="demographic.demographiclabelprintsetting.msgPx" />">&nbsp; <bean:message key="demographic.demographiclabelprintsetting.msgPx" />
                </div>
            </div>
		</td>
	</tr>
	<tr>
		<td style="align:center">
		<table style="width:90%" >
			<tr>
				<td style="border: solid 1px; background-color: white;">
				<span id="copytext2" class="copytext"> <b><%=last_name%>,&nbsp;<%=first_name%>&nbsp;<%=chart_no%></b><br><%=address%><br><%=city%>,&nbsp;<%=province%>,&nbsp;<%=postal%><br>
				<bean:message key="demographic.demographiclabelprintsetting.msgHome" />:&nbsp;<%=phone%><br><%=dob%>&nbsp;<%=sex%><br><%=hin%><br>
				<bean:message key="demographic.demographiclabelprintsetting.msgBus" />:<%=phone2%>&nbsp;
				<bean:message key="demographic.demographiclabelprintsetting.msgDr" />&nbsp;<%=providername%><br>
				</span></td>
			</tr>
		</table>
		</td>
		<td style="text-align:center; background-color:#CCCCCC">
		<input type="checkbox" name="label2checkbox" value="checked" checked>
		<input type="text" name="label2no" size="2" maxlength="2" value="<%= oscarVariables.getProperty("label.2no","1") %>"></td>
	</tr>
	<tr>
		<td style="align:center">
		<table style="width:90%" >
			<tr>
				<td style="border: solid 1px; background-color: white;">
				<span id="copytext3" class="copytext"> <%=last_name%>,&nbsp;<%=first_name%><br><%=address%><br><%=city%>,&nbsp;<%=province%>,&nbsp;<%=postal%><br>
				</span></td>
			</tr>
		</table>
		</td>
		<td style="text-align:center; background-color:#CCCCCC">
		<input type="checkbox" name="label3checkbox" value="checked">
		<input type="text" name="label3no" size="2" maxlength="2" value="<%= oscarVariables.getProperty("label.3no","1") %>"></td>
	</tr>
	<tr>
		<td style="align:center">
		<table style="width:90%">
			<tr>
				<td style="border: solid 1px; background-color: white;">
				<span id="copytext4" class="copytext"> <%=first_name%>&nbsp;<%=last_name%><br><%=address%><br><%=city%>,&nbsp;<%=province%>,&nbsp;<%=postal%><br>
				</span></td>
			</tr>
		</table>
		</td>
		<td style="text-align:center; background-color:#CCCCCC">
		<textarea id="text1" STYLE="display: none;"> </textarea>
		<input type="checkbox" name="label4checkbox" value="checked">
		<input type="text" name="label4no" size="2" maxlength="2" value="<%= oscarVariables.getProperty("label.4no","1") %>"></td>
	</tr>
	<tr>
		<td style="align:center">
		<table style="width:90%">
			<tr>
				<td style="border: solid 1px; background-color: white;">
				<span id="copytext5" class="copytext"> <%=chart_no%> &nbsp;&nbsp;<%=last_name%>, <%=first_name%><br><%=address%>, <%=city%>, <%=province%>, <%=postal%>
				<br><%=dob%> &nbsp;&nbsp;&nbsp;<%=age%> <%=sex%> &nbsp;<%=hin%><br><%=phone%>&nbsp;&nbsp;&nbsp;<%=phone2%><br><%=refDoc%>
				</span></td>
			</tr>
		</table>
		</td>
		<td style="text-align:center; background-color:#CCCCCC"><textarea id="text1" style="display: none;"></textarea>
		<input type="checkbox" name="label5checkbox" value="checked">
		<input type="text" name="label5no" size="2" maxlength="2" value="<%= oscarVariables.getProperty("label.5no","1") %>"></td>
	</tr>
	<tr>
		<td style="text-align:left" colspan="3"><br><input type="submit" name="Submit" class="btn btn-primary" value="<bean:message key='demographic.demographiclabelprintsetting.btnPrintPreviewPrint'/>">
		<input type="button" class="btn btn-link" name="button" value="<bean:message key='global.btnBack'/>" onClick="javascript:history.go(-1);return false;"></td>
	</tr>
</table>
</div>
</form>

</body>
</html:html>