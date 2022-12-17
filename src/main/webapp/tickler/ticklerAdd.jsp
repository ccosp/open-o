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

<%@page import="org.oscarehr.common.dao.DemographicDao"%>
<%@page import="org.oscarehr.common.model.Demographic"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
    String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_tickler" rights="w" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_tickler");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>

<%    
String user_no = (String) session.getAttribute("user");
int  nItems=0;
String strLimit1="0";
String strLimit2="5";
if(request.getParameter("limit1")!=null) strLimit1 = request.getParameter("limit1");
if(request.getParameter("limit2")!=null) strLimit2 = request.getParameter("limit2");
String providerview = request.getParameter("providerview")==null?"all":request.getParameter("providerview") ;
boolean bFirstDisp=true; //this is the first time to display the window
if (request.getParameter("bFirstDisp")!=null) bFirstDisp= (request.getParameter("bFirstDisp")).equals("true");
String ChartNo;
String demoNo = "";
String demoMRP = "";
String demoName = request.getParameter("name");
String defaultTaskAssignee = ""; 

DemographicDao demographicDao = SpringUtils.getBean(DemographicDao.class);
Demographic demographic = demographicDao.getDemographic(request.getParameter("demographic_no"));
if(demographic != null) {
	demoName = demographic.getFormattedName();
	demoMRP = demographic.getProviderNo();
}
if ( request.getAttribute("demographic_no") != null){
    demoNo = (String) request.getAttribute("demographic_no");
    demoName = (String) request.getAttribute("demoName");
    bFirstDisp = false;
}
if(demoName == null){demoName ="";}

Boolean writeToEncounter = false;
    LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
    Boolean caisiEnabled = OscarProperties.getInstance().isPropertyActive("caisi");
    Integer defaultProgramId = null;
    List<ProgramProvider> programProviders = new ArrayList<ProgramProvider>();

    if (caisiEnabled) {
        ProgramProviderDAO programProviderDao = SpringUtils.getBean(ProgramProviderDAO.class);
        programProviders = programProviderDao.getProgramProviderByProviderNo(loggedInInfo.getLoggedInProviderNo());
        if (programProviders.size() == 1) {
            defaultProgramId = programProviders.get(0).getProgram().getId();
        }
    }

//Retrieve encounter id for updating encounter navbar if info this page changes anything
String parentAjaxId;
if( request.getParameter("parentAjaxId") != null )
    parentAjaxId = request.getParameter("parentAjaxId");
else
    parentAjaxId = "";
    
String updateParent;
if( request.getParameter("updateParent") != null )
    updateParent = request.getParameter("updateParent");
else
    updateParent = "true";  

boolean recall = false;
String taskTo = user_no; //default current user
String priority = "Normal";
if(request.getParameter("taskTo")!=null) taskTo = request.getParameter("taskTo");
if(request.getParameter("priority")!=null) priority = request.getParameter("priority");
if(request.getParameter("recall")!=null) recall = true;

UserPropertyDAO propertyDao = (UserPropertyDAO)SpringUtils.getBean("UserPropertyDAO");
UserProperty prop = propertyDao.getProp(user_no,"tickler_task_assignee");

//don't over ride taskTo query param
if(request.getParameter("taskTo")==null){

if(prop!=null) {
	defaultTaskAssignee = prop.getValue();
	if(!defaultTaskAssignee.equals("mrp")){
	  taskTo = defaultTaskAssignee;	
	}else if(defaultTaskAssignee.equals("mrp")){
	  taskTo = demoMRP;
	}
}

}   


%>
<%@ page import="java.util.*, java.sql.*, oscar.*, java.net.*, oscar.oscarEncounter.pageUtil.EctSessionBean" %>
<%@page import="org.oscarehr.util.SpringUtils" %>
<%@page import="org.oscarehr.common.model.Appointment" %>
<%@page import="org.oscarehr.common.dao.OscarAppointmentDao" %>
<%@page import="org.oscarehr.common.model.Provider" %>
<%@page import="org.oscarehr.PMmodule.dao.ProviderDao" %>

<%
	ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
	OscarAppointmentDao appointmentDao = SpringUtils.getBean(OscarAppointmentDao.class);
%>

<%
GregorianCalendar now=new GregorianCalendar();
  int curYear = now.get(Calendar.YEAR);
  int curMonth = (now.get(Calendar.MONTH)+1);
  int curDay = now.get(Calendar.DAY_OF_MONTH);   
  
  %><% //String providerview=request.getParameter("provider")==null?"":request.getParameter("provider");
   String xml_vdate=request.getParameter("xml_vdate") == null?"":request.getParameter("xml_vdate");
   String xml_appointment_date = request.getParameter("xml_appointment_date")==null?MyDateFormat.getMysqlStandardDate(curYear, curMonth, curDay):request.getParameter("xml_appointment_date");
%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@page import="org.oscarehr.common.dao.SiteDao"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="org.oscarehr.common.model.Site"%>
<%@ page import="org.oscarehr.util.LoggedInInfo" %>
<%@page import="org.oscarehr.common.model.Provider"%>
<%@ page import="org.oscarehr.common.dao.UserPropertyDAO"%>
<%@ page import="org.oscarehr.common.model.UserProperty"%>
<%@ page import="org.oscarehr.PMmodule.dao.ProgramProviderDAO" %>
<%@ page import="org.oscarehr.PMmodule.model.ProgramProvider" %>

<html:html locale="true">
<head>
<title><bean:message key="tickler.ticklerAdd.title"/></title>

      <script language="JavaScript">

      function addMonths(months) {
    	  var d = new Date();
    	  d = new Date(d.setMonth(d.getMonth() + months));
    	  var mth = ((d.getMonth()+1)<10)? ("0"+(d.getMonth()+1)) : (d.getMonth()+1);
    	  var day =  d.getDate() > 10 ? d.getDate() : ("0" + d.getDate());
    	  var newD = d.getFullYear() + "-" + mth + "-" + day;
    	  document.serviceform.xml_appointment_date.value = newD;
      }
	
      function addDays(numDays) {
    	  var d = new Date();
    	  d = new Date(d.setDate(d.getDate() + numDays));  
    	  var mth = ((d.getMonth()+1)<10)? ("0"+(d.getMonth()+1)) : (d.getMonth()+1);
    	  var day =  d.getDate() > 10 ? d.getDate() : ("0" + d.getDate());
    	  var newD = d.getFullYear() + "-" + mth + "-" + day;
          document.serviceform.xml_appointment_date.value = newD;
      }

      function toggleQuickPickDateDisplay(linkToggle)
      {
          var options = document.getElementById("quickPickDateOptions");
          if (options.style.display === "none")
          {
                options.style.display = "block";
                linkToggle.innerHTML = "<bean:message key="tickler.ticklerAdd.btnHideQuickpick"/>";
          }
          else
          {
                options.style.display = "none";
                linkToggle.innerHTML = "<bean:message key="tickler.ticklerAdd.btnShowQuickpick"/>";
          }
      }

      
      <!--
function popupPage(vheight,vwidth,varpage) { //open a new popup window
  var page = "" + varpage;
  windowprops = "height="+vheight+",width="+vwidth+",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes";
  var popup=window.open(page, "attachment", windowprops);
  if (popup != null) {
    if (popup.opener == null) {
      popup.opener = self; 
    }
  }
}
function selectprovider(s) {
  if(self.location.href.lastIndexOf("&providerview=") > 0 ) a = self.location.href.substring(0,self.location.href.lastIndexOf("&providerview="));
  else a = self.location.href;
	self.location.href = a + "&providerview=" +s.options[s.selectedIndex].value ;
}
function openBrWindow(theURL,winName,features) { 
  window.open(theURL,winName,features);
}
function setfocus() {
  this.focus();
  document.ADDAPPT.keyword.focus();
  document.ADDAPPT.keyword.select();
}

function validate(form){
validate(form, false);
}
function validate(form, writeToEncounter){
    if (validateDemoNo(form) <%=caisiEnabled?"&& validateSelectedProgram()":""%>){
        if(writeToEncounter){
            form.action = "dbTicklerAdd.jsp?writeToEncounter=true";
        }
        else{
            form.action = "dbTicklerAdd.jsp";
        }
        form.submit();
    }
}
	function validateSelectedProgram() {
		if (document.serviceform.program_assigned_to.value === "none") {
            document.getElementById("error").insertAdjacentText("beforeend","<bean:message key="tickler.ticklerAdd.msgNoProgramSelected"/>");
            document.getElementById("error").style.display='block';
			return false;
		}
		return true;
	}	

    function IsDate(value) {
      let dateWrapper=new Date(value);
        return !isNaN(dateWrapper.getDate());
    }	
	
    function validateDemoNo() {
        if (document.serviceform.demographic_no.value == "") {
            document.getElementById("error").insertAdjacentText("beforeend","<bean:message key="tickler.ticklerAdd.msgInvalidDemographic"/>");
            document.getElementById("error").style.display='block';
    	    return false;
        }
            else{  if (document.serviceform.xml_appointment_date.value == "" || !IsDate(document.serviceform.xml_appointment_date.value)) {
                document.getElementById("error").insertAdjacentText("beforeend","<bean:message key="tickler.ticklerAdd.msgMissingDate"/>");
                document.getElementById("error").style.display='block';
            	return false;
            }
    <% if (org.oscarehr.common.IsPropertiesOn.isMultisitesEnable()) { %>
             else if (document.serviceform.site.value=="none"||document.serviceform.site.value=="0"){
                document.getElementById("error").insertAdjacentText("beforeend","Must assign task to a provider.");
                document.getElementById("error").style.display='block';
	            return false;
             } 
    <% } %>
             else{
                return true;
            }
        }
    }

function refresh() {
  var u = self.location.href;
  if(u.lastIndexOf("view=1") > 0) {
    self.location.href = u.substring(0,u.lastIndexOf("view=1")) + "view=0" + u.substring(eval(u.lastIndexOf("view=1")+6));
  } else {
    history.go(0);
  }
}

//-->
</script>

<link href="<%=request.getContextPath() %>/css/bootstrap.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" rightmargin="0" topmargin="10" onLoad="setfocus()">
    <table width="100%">                       
        <tr style="background-color: black">   
            <td colspan="4" style="text-align:left; font-weight: 900; height:40px;font-size:large;font-family:arial,sans-serif;color:white"><bean:message key="tickler.ticklerAdd.msgTickler"/></td>
        </tr>
    </table>

<div class="container-fluid well" > 
<table width="100%" border="0" cellspacing="0" cellpadding="0"bgcolor="#EEEEFF">
 <form name="ADDAPPT" method="post" action="../appointment/appointmentcontrol.jsp">
<tr><td colspan="4"> <p><div id="error" class="alert alert-error" style="display:none;"></div> </td></tr>
<tr> 
      <td width="35%"><font color="#003366"><font face="Verdana, Arial, Helvetica, sans-serif" size="2"><b><bean:message key="tickler.ticklerAdd.formDemoName"/>: </b></font></font></td>
      <td colspan="2" width="65%">
<div align="left"><INPUT TYPE="TEXT" NAME="keyword" size="25" VALUE="<%=bFirstDisp?"":demoName.equals("")?session.getAttribute("appointmentname"):demoName%>">
   	 <input type="submit" name="Submit" class="btn"  value="<bean:message key="tickler.ticklerAdd.btnSearch"/>">
  </div>
</td>
    </tr>
  <INPUT TYPE="hidden" NAME="orderby" VALUE="last_name" >
  <%
    String searchMode = request.getParameter("search_mode");
    if (searchMode == null || searchMode.isEmpty()) {
        searchMode = OscarProperties.getInstance().getProperty("default_search_mode","search_name");
    }
%>
				      <INPUT TYPE="hidden" NAME="search_mode" VALUE="<%=searchMode%>" >
				      <INPUT TYPE="hidden" NAME="originalpage" VALUE="../tickler/ticklerAdd.jsp" >
				      <INPUT TYPE="hidden" NAME="limit1" VALUE="0" >
				      <INPUT TYPE="hidden" NAME="limit2" VALUE="5" >
              <!--input type="hidden" name="displaymode" value="TicklerSearch" -->
              <INPUT TYPE="hidden" NAME="displaymode" VALUE="Search "> 

<% ChartNo = bFirstDisp?"":request.getParameter("chart_no")==null?"":request.getParameter("chart_no"); %>
   <INPUT TYPE="hidden" NAME="appointment_date" VALUE="2002-10-01" WIDTH="25" HEIGHT="20" border="0" hspace="2">
       <INPUT TYPE="hidden" NAME="status" VALUE="t"  WIDTH="25" HEIGHT="20" border="0" hspace="2">
              <INPUT TYPE="hidden" NAME="start_time" VALUE="10:45" WIDTH="25" HEIGHT="20" border="0"  onChange="checkTimeTypeIn(this)">
              <INPUT TYPE="hidden" NAME="type" VALUE="" WIDTH="25" HEIGHT="20" border="0" hspace="2">
              <INPUT TYPE="hidden" NAME="duration" VALUE="15" WIDTH="25" HEIGHT="20" border="0" hspace="2" >
              <INPUT TYPE="hidden" NAME="end_time" VALUE="10:59" WIDTH="25" HEIGHT="20" border="0" hspace="2"  onChange="checkTimeTypeIn(this)">
       

 <input type="hidden" name="demographic_no"  readonly value="" width="25" height="20" border="0" hspace="2">
         <input type="hidden" name="location"  tabindex="4" value="" width="25" height="20" border="0" hspace="2">
              <input type="hidden" name="resources"  tabindex="5" value="" width="25" height="20" border="0" hspace="2">
              <INPUT TYPE="hidden" NAME="user_id" readonly VALUE='oscardoc, doctor' WIDTH="25" HEIGHT="20" border="0" hspace="2">
     	        <INPUT TYPE="hidden" NAME="dboperation" VALUE="search_demorecord">
              <INPUT TYPE="hidden" NAME="createdatetime" readonly VALUE="2002-10-1 17:53:50" WIDTH="25" HEIGHT="20" border="0" hspace="2">
              <INPUT TYPE="hidden" NAME="provider_no" VALUE="115">
              <INPUT TYPE="hidden" NAME="creator" VALUE="oscardoc, doctor">
              <INPUT TYPE="hidden" NAME="remarks" VALUE="">
              <input type="hidden" name="parentAjaxId" value="<%=parentAjaxId%>"/>
              <input type="hidden" name="updateParent" value="<%=updateParent%>"/> 
 </form>
</table>
<table width="100%" border="0" bgcolor="#EEEEFF">
  <form name="serviceform" method="post" >
      <input type="hidden" name="parentAjaxId" value="<%=parentAjaxId%>"/>
      <input type="hidden" name="updateParent" value="<%=updateParent%>"/>
 
     <tr> 
      <td width="35%"> <div align="left"><font color="#003366"><font face="Verdana, Arial, Helvetica, sans-serif" size="2"><strong><bean:message key="tickler.ticklerAdd.formChartNo"/>:</strong> </font></font></div></td>
      <td colspan="2"> <div align="left"><INPUT TYPE="hidden" NAME="demographic_no" VALUE="<%=bFirstDisp?"":request.getParameter("demographic_no").equals("")?"":request.getParameter("demographic_no")%>"><%=ChartNo%></div></td>
    </tr>

    <tr> 
      <td style="text-align:left; font-weight: 900; height:40px;font-size:large;font-family:arial,sans-serif;color:white"><bean:message key="tickler.ticklerAdd.formServiceDate"/></td>
      <td><input type="date" style="height:26px;" name="xml_appointment_date" value="<%=xml_appointment_date%>"> 
        <font color="#003366" size="1" face="Verdana, Arial, Helvetica, sans-serif">
        
        <a href="#" onClick="toggleQuickPickDateDisplay(this)" title="<bean:message key="tickler.ticklerAdd.btnToggleQuickpickDates"/>" style="padding-left:5px; vertical-align: middle;"><bean:message key="tickler.ticklerAdd.btnHideQuickpick"/></a>
        <div id="quickPickDateOptions"  style="display:block;">
         <a href="#" onClick="addDays(3)">3d</a>&nbsp; &nbsp;
         <a href="#" onClick="addDays(7)">1w</a>&nbsp; &nbsp;
         <a href="#" onClick="addDays(14)">2w</a>&nbsp; &nbsp;
         <a href="#" onClick="addDays(21)">3w</a>&nbsp; &nbsp;
         <a href="#" onClick="addDays(28)">4w</a>&nbsp; &nbsp;<br>
         <a href="#" onClick="addMonths(1)">1m</a>&nbsp; &nbsp;
         <a href="#" onClick="addMonths(2)">2m</a>&nbsp; &nbsp;
         <a href="#" onClick="addMonths(3)">3m</a>&nbsp; &nbsp;
         <a href="#" onClick="addMonths(4)">4m</a>&nbsp; &nbsp;
         <a href="#" onClick="addMonths(6)">6m</a>&nbsp; &nbsp;
         <a href="#" onClick="addMonths(9)">9m</a>&nbsp; &nbsp;<br>
         <a href="#" onClick="addMonths(12)">1yr</a>&nbsp; &nbsp;
         <a href="#" onClick="addMonths(24)">2yr</a>&nbsp; &nbsp;
         <a href="#" onClick="addMonths(36)">3yr</a>&nbsp; &nbsp;
         <a href="#" onClick="addMonths(60)">5yr</a>&nbsp; &nbsp;
         <a href="#" onClick="addMonths(96)">8yr</a>&nbsp; &nbsp;
         <a href="#" onClick="addMonths(120)">10yr</a>
        </div>
        </font> </td>
        
        
      <td>&nbsp;</td>
    </tr>
    <tr> 
      <td height="21" valign="top"><font color="#003366" size="2" face="Verdana, Arial, Helvetica, sans-serif"><strong><bean:message key="tickler.ticklerMain.Priority"/>:</strong></font></td>
      <td valign="top"> 
	<select name="priority" style="font-face:Verdana, Arial, Helvetica, sans-serif">
 	<option value="<bean:message key="tickler.ticklerMain.priority.high"/>" <%=priority.equals("High")?"selected":""%>><bean:message key="tickler.ticklerMain.priority.high"/>
	<option value="<bean:message key="tickler.ticklerMain.priority.normal"/>" <%=priority.equals("Normal")?"selected":""%>><bean:message key="tickler.ticklerMain.priority.normal"/>
	<option value="<bean:message key="tickler.ticklerMain.priority.low"/>" <%=priority.equals("Low")?"selected":""%>><bean:message key="tickler.ticklerMain.priority.low"/>	
     	</select>
      </td>
      <td>&nbsp;</td>
    </tr>

    <tr> 
      <td height="21" valign="top"><font color="#003366" size="2" face="Verdana, Arial, Helvetica, sans-serif"><strong><bean:message key="tickler.ticklerAdd.assignTaskTo"/>:</strong></font></td>
      <td valign="top"> <font face="Verdana, Arial, Helvetica, sans-serif" size="2" color="#333333">
<% if (org.oscarehr.common.IsPropertiesOn.isMultisitesEnable()) 
{ // multisite start ==========================================
        	SiteDao siteDao = (SiteDao)WebApplicationContextUtils.getWebApplicationContext(application).getBean("siteDao");
          	List<Site> sites = siteDao.getActiveSitesByProviderNo(user_no);
          	String appNo = (String) session.getAttribute("cur_appointment_no");
          	String location = null;
          	if (appNo != null) {
          		Appointment a  = appointmentDao.find(Integer.parseInt(appNo));
          		if(a != null) {
          			location = a.getLocation();
          		}
          		
          	}
      %> 
      <script>
var _providers = [];

<%	
String taskToName = "";

if(defaultTaskAssignee.equals("mrp"))
taskToName = "Preference set to MRP, attach a patient.";

if(!taskTo.isEmpty())
taskToName = providerDao.getProviderNameLastFirst(taskTo);

Site site = null;
for (int i=0; i<sites.size(); i++) { 
    Set<Provider> siteProviders = sites.get(i).getProviders();
	List<Provider>  siteProvidersList = new ArrayList<Provider> (siteProviders);
     Collections.sort(siteProvidersList,(new Provider()).ComparatorName());%>
	_providers["<%= sites.get(i).getName() %>"]="<% Iterator<Provider> iter = siteProvidersList.iterator();
	while (iter.hasNext()) {
		Provider p=iter.next();
		if ("1".equals(p.getStatus())) {
	%><option value='<%= p.getProviderNo() %>'><%= p.getLastName() %>, <%= p.getFirstName() %></option><% }} %>";
<%
	} %>
function changeSite(sel) {
	sel.form.task_assigned_to.innerHTML=sel.value=="none"?"":_providers[sel.value];
	sel.style.backgroundColor=sel.options[sel.selectedIndex].style.backgroundColor;
}
      </script>
 
<div id="selectWrapper">
      	<select id="site" name="site" onchange="changeSite(this)">
      		<option value="none" style="background-color: white">---select clinic---</option>
      	<%
      	for (int i=0; i<sites.size(); i++) {
      	%>
      		<option value="<%=sites.get(i).getName()%>"
													style="background-color:<%=sites.get(i).getBgColor()%>"><%=sites.get(i).getName()%></option>
      	<% } %>
      	</select>

      	<select name="task_assigned_to" id="task_assigned_to" style="width:140px"></select>

	<h4 id="preferenceLink" style="display:none"><small><a href="#" onClick="toggleWrappers()">[preference]</a></small></h4>
</div>

<div id="nameWrapper" style="display:none">
	<h4><%=taskToName%> <small><a href="#" onClick="toggleWrappers()">[change]</a></small></h4>
	<input type="hidden" id="taskToBin" value="<%=taskTo%>">
	<input type="hidden" id="taskToNameBin" value="<%=taskToName%>">
</div>
      	<script>
      		document.getElementById("site").value = '<%= site==null?"none":site.getSiteId() %>';
      		changeSite(document.getElementById("site"));
      	</script>

<% if(prop!=null) {%>
<script>
//prop exists so hide selectWrapper
document.getElementById("selectWrapper").style.display="none";
document.getElementById("nameWrapper").style.display="block";
document.getElementById("preferenceLink").style.display="inline-block";

var taskToValue = document.getElementById("taskToBin").value;
var taskToName = document.getElementById('taskToNameBin').value;

function toggleWrappers(){
if(document.getElementById("selectWrapper").style.display=="none"){
document.getElementById("selectWrapper").style.display="block";
document.getElementById("nameWrapper").style.display="none";
}else{
document.getElementById("selectWrapper").style.display="none";
document.getElementById("nameWrapper").style.display="block";
}
}

_providers.push("<option value=\""+taskToValue+"\" selected>"+taskToName+"</option>");

var newItemKey = _providers.length-1;

var selSite = document.getElementById('site');
var optSite = document.createElement('option');
optSite.appendChild( document.createTextNode("** preference **") );
optSite.value = newItemKey; 
optSite.setAttribute('selected', 'selected');
selSite.appendChild(optSite); 
changeSite(selSite);
</script>
<%}%>

<% // multisite end ==========================================
} else {
%>

      <select name="task_assigned_to">        
            <%  String proFirst="";
                String proLast="";
                String proOHIP="";
				
                for(Provider p : providerDao.getActiveProviders()) {
               
                    proFirst =p.getFirstName();
                    proLast = p.getLastName();
                    proOHIP = p.getProviderNo();

            %> 
            <option value="<%=proOHIP%>" <%=taskTo.equals(proOHIP)?"selected":""%>><%=proLast%>, <%=proFirst%></option>
            <%
                }
            %>
      </select>
<% } %>
          
           <input type="hidden" name="docType" value="<%=request.getParameter("docType")%>"/>
           <input type="hidden" name="docId" value="<%=request.getParameter("docId")%>"/>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr> 
      <td height="21" valign="top"><font color="#003366" size="2" face="Verdana, Arial, Helvetica, sans-serif"><strong><bean:message key="tickler.ticklerAdd.formReminder"/>:</strong></font></td>
          <td valign="top"> <textarea style="font-face:Verdana, Arial, Helvetica, sans-serif"name="textarea" cols="50" rows="5"></textarea></td>
          <td>&nbsp;</td>
      </tr>
     <INPUT TYPE="hidden" NAME="user_no" VALUE="<%=user_no%>">
      <input type="hidden" name="writeToEncounter" value="<%=writeToEncounter%>"/>
    <tr>
      <td><input type="button" name="Button" class="btn" value="<bean:message key="tickler.ticklerAdd.btnCancel"/>" onClick="window.close()"></td>
      <td><input type="button" name="Button" class="btn" value="<bean:message key="tickler.ticklerAdd.btnSubmit"/>" onClick="event.preventDefault();validate(this.form);">
          <input type="button" name="Button" class="btn btn-primary" value="<bean:message key="tickler.ticklerAdd.btnWriteSubmit"/>" onClick="validate(this.form, true)">
      </td>
      <td></td>
          </tr>
  </form>
</table>
<p><font face="Arial, Helvetica, sans-serif" size="2"> </font></p>
  <p>&nbsp; </p>

</div>
</body>
</html:html>