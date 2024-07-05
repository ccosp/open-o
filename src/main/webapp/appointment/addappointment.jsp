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
<!DOCTYPE HTML>

<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
    String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_appointment" rights="w" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_appointment");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>


<%@ page import="java.util.Set"%>
<%@ page import="java.util.HashSet"%>

<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.format.FormatStyle" %>
<%@ page import="java.time.ZoneId" %>

<%@ page import="java.util.*, java.lang.*, oscar.appt.*"%>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="oscar.appt.status.service.AppointmentStatusMgr"%>
<%@ page import="oscar.appt.status.service.impl.AppointmentStatusMgrImpl"%>
<%@ page import="oscar.oscarBilling.ca.bc.decisionSupport.BillingGuidelines"%>
<%@ page import="oscar.oscarEncounter.data.EctFormData"%>
<%@ page import="oscar.util.ConversionUtils" %>
<%@ page import="oscar.OscarProperties" %>

<%@ page import="org.oscarehr.common.model.AppointmentStatus"%>
<%@ page import="org.oscarehr.common.model.DemographicCust" %>
<%@ page import="org.oscarehr.common.dao.DemographicCustDao" %>
<%@ page import="org.oscarehr.PMmodule.dao.ProviderDao" %>
<%@ page import="org.oscarehr.common.model.Provider" %>
<%@ page import="org.oscarehr.common.model.Demographic" %>
<%@ page import="org.oscarehr.common.dao.DemographicDao" %>
<%@ page import="org.oscarehr.common.model.EncounterForm" %>
<%@ page import="org.oscarehr.common.dao.EncounterFormDao" %>
<%@ page import="org.oscarehr.common.model.Appointment" %>
<%@ page import="org.oscarehr.common.dao.OscarAppointmentDao" %>
<%@ page import="org.oscarehr.PMmodule.model.Program" %>
<%@ page import="org.oscarehr.PMmodule.model.ProgramProvider" %>
<%@ page import="org.oscarehr.common.model.Facility" %>
<%@ page import="org.oscarehr.PMmodule.service.ProviderManager" %>
<%@ page import="org.oscarehr.PMmodule.service.ProgramManager" %>
<%@ page import="org.oscarehr.managers.ProgramManager2"%>
<%@ page import="org.oscarehr.decisionSupport.model.DSConsequence"%>

<%@ page import="org.oscarehr.util.MiscUtils"%>
<%@ page import="org.oscarehr.util.SessionConstants"%>
<%@ page import="org.oscarehr.common.model.ProviderPreference"%>

<%@ page import="org.oscarehr.managers.LookupListManager"%>
<%@ page import="org.oscarehr.common.model.LookupList"%>
<%@ page import="org.oscarehr.common.dao.SiteDao"%>
<%@ page import="org.oscarehr.common.model.Site"%>
<%@ page import="org.oscarehr.common.dao.AppointmentTypeDao" %>
<%@ page import="org.oscarehr.common.model.AppointmentType" %>
<%@ page import="org.oscarehr.util.LoggedInInfo"%>
<%@ page import="org.oscarehr.util.SpringUtils" %>

<%@ page import="org.owasp.encoder.Encode" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>

<jsp:useBean id="providerBean" class="java.util.Properties" scope="session" />

<%
 
  String DONOTBOOK = "Do_Not_Book";
  String curProvider_no = request.getParameter("provider_no");
  String curDoctor_no = request.getParameter("doctor_no") != null ? request.getParameter("doctor_no") : "";
  String curUser_no = (String) session.getAttribute("user");
  String userfirstname = (String) session.getAttribute("userfirstname");
  String userlastname = (String) session.getAttribute("userlastname");
  
  LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);

  ProviderPreference providerPreference=(ProviderPreference)session.getAttribute(SessionConstants.LOGGED_IN_PROVIDER_PREFERENCE);
  int everyMin=providerPreference.getEveryMin();

  boolean bFirstDisp=true; //this is the first time to display the window
  boolean bFromWL=false; //this is from waiting list page

  if (request.getParameter("bFirstDisp")!=null) bFirstDisp= (request.getParameter("bFirstDisp")).equals("true");
  if (request.getParameter("demographic_no")!=null) bFromWL=true;

  String duration = request.getParameter("duration")!=null?(request.getParameter("duration").equals(" ")||request.getParameter("duration").equals("")||request.getParameter("duration").equals("null")?(""+everyMin) :request.getParameter("duration")):(""+everyMin) ;

  //check for management fee code eligibility
  Set<String> billingRecommendations = new HashSet<String>();
  try{
    List<DSConsequence> list = BillingGuidelines.getInstance().evaluateAndGetConsequences(loggedInInfo, request.getParameter("demographic_no"), curProvider_no);

    for (DSConsequence dscon : list){
        if (dscon.getConsequenceStrength().equals(DSConsequence.ConsequenceStrength.recommendation)) {
            String recommendation = new String(dscon.getText());
            billingRecommendations.add(recommendation);
        }
    }
  }catch(Exception e){
    MiscUtils.getLogger().error("Error", e);
  }
%>


<%
	DemographicCustDao demographicCustDao = (DemographicCustDao)SpringUtils.getBean("demographicCustDao");
	ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
	DemographicDao demographicDao = SpringUtils.getBean(DemographicDao.class);
	EncounterFormDao encounterFormDao = SpringUtils.getBean(EncounterFormDao.class);
	OscarAppointmentDao appointmentDao = SpringUtils.getBean(OscarAppointmentDao.class);
	
	ProviderManager providerManager = SpringUtils.getBean(ProviderManager.class);
	ProgramManager programManager = SpringUtils.getBean(ProgramManager.class);
	
	String providerNo = loggedInInfo.getLoggedInProviderNo();
	Facility facility = loggedInInfo.getCurrentFacility();
	
    List<Program> programs = programManager.getActiveProgramByFacility(providerNo, facility.getId());

	LookupListManager lookupListManager = SpringUtils.getBean(LookupListManager.class);
	LookupList reasonCodes = lookupListManager.findLookupListByName(loggedInInfo, "reasonCode");
	pageContext.setAttribute("reasonCodes", reasonCodes);
	
    int iPageSize=5;

    ApptData apptObj = ApptUtil.getAppointmentFromSession(request);

    oscar.OscarProperties pros = oscar.OscarProperties.getInstance();
    String strEditable = pros.getProperty("ENABLE_EDIT_APPT_STATUS");
    Boolean isMobileOptimized = session.getAttribute("mobileOptimized") != null;

    AppointmentStatusMgr apptStatusMgr = new AppointmentStatusMgrImpl();
    List<AppointmentStatus> allStatus = apptStatusMgr.getAllActiveStatus();
    
    String useProgramLocation = OscarProperties.getInstance().getProperty("useProgramLocation");
    String moduleNames = OscarProperties.getInstance().getProperty("ModuleNames");
    boolean caisiEnabled = moduleNames != null && org.apache.commons.lang.StringUtils.containsIgnoreCase(moduleNames, "Caisi");
    boolean locationEnabled = caisiEnabled && (useProgramLocation != null && useProgramLocation.equals("true"));
    
    ProgramManager2 programManager2 = SpringUtils.getBean(ProgramManager2.class);
%>

<html:html lang="en">
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><bean:message key="appointment.addappointment.title" /></title>



<link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet" type="text/css"> <!-- Bootstrap 2.3.1 -->
<link href="${pageContext.request.contextPath}/css/bootstrap-responsive.css" rel="stylesheet" type="text/css">
<link href="${pageContext.request.contextPath}/css/font-awesome.min.css" rel="stylesheet">


<link href="${pageContext.request.contextPath}/library/jquery/jquery-ui.theme-1.12.1.min.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/library/jquery/jquery-ui.structure-1.12.1.min.css" rel="stylesheet">

<script src="${pageContext.request.contextPath}/library/jquery/jquery-3.6.4.min.js"></script>
<script src="${pageContext.request.contextPath}/library/jquery/jquery-migrate-3.4.0.js"></script>

<script src="${pageContext.request.contextPath}/library/jquery/jquery-ui-1.12.1.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/bootstrap.min.js" ></script>

<script src="<%= request.getContextPath() %>/js/global.js"></script>
<script src="<%= request.getContextPath() %>/js/checkDate.js"></script>
<script src="<%= request.getContextPath() %>/share/javascript/Oscar.js"></script>

<style>
body, html {
  --color: #945;
  --size: 2rem;
  --border: calc(var(--size) * 0.125);
  --borderRadius: calc(var(--size) * 0.5);
  --labelSize: calc(var(--size) * 0.75);
  --margin: calc(var(--size) * 0.25);
  --marginLeft: calc(var(--size) + calc(var(--size) * 0.5));
}
.time {
  background-image: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='40' height='40'><circle cx='20' cy='20' r='18.5' fill='none' stroke='%23222' stroke-width='3' /><path d='M20,4 20,8 M4,20 8,20 M36,20 32,20 M20,36 20,32' stroke='%23bbb' stroke-width='1' /><circle cx='20' cy='20' r='2' fill='%23222' stroke='%23222' stroke-width='2' /></svg>");
  background-position: var(--margin) 50%;
  background-repeat: no-repeat;
  background-size: var(--size) var(--size);
  border: var(--border) ;
  border-radius: var(--borderRadius);
  color: #222;
  font-size: var(--size);
  padding: var(--margin) var(--margin) var(--margin) var(--marginLeft);
  transition: backgroundImage 0.25s;
}
</style>
<!-- override styles for ui select menu -->
<style>
.ui-selectmenu-button.ui-button {
    background-color: white;
    width: 190px;
    margin-bottom: 10px;

}
.ui-icon-triangle-1-s {
	border-style: solid;
	border-width: 0.142em 0.142em 0 0;
	content: '';
	display: inline-block;
	height: 0.33em;
	left: 0.6em;
	position: relative;
	top: 0.17em;
	transform: rotate(135deg);
	vertical-align: top;
	width: 0.34em;

}

</style>
<%
    // multisites start ==================
    SiteDao siteDao = (SiteDao)SpringUtils.getBean("siteDao");
    List<Site> sites = siteDao.getActiveSitesByProviderNo((String) session.getAttribute("user"));
    boolean bMultisites = org.oscarehr.common.IsPropertiesOn.isMultisitesEnable();
    // multisites end ==================
    if (bMultisites) { %>
<style>
	        <% for (Site s:sites) { %>
.<%=s.getShortName()%> {
    background-color:<%=s.getBgColor()%>;
}
	        <% } %>
</style>
    <% } %>
<style>
	        <% for (int i = 0; i < allStatus.size(); i++) {%>
.<%=(allStatus.get(i)).getStatus()%> {
    background-color:<%=(allStatus.get(i)).getColor()%>;
}
	        <% } %>
</style>

<script>

function updateTime(){
    const reTime = /^([0-1][0-9]|2[0-3]):[0-5][0-9]$/;
      const time = document.ADDAPPT.start_time.value;
      if (reTime.exec(time)) {
        const minute = Number(time.substring(3,5));
        const minuteDeg = Number(time.substring(3,5)) * 360/60;
        const hourDeg = (Number(time.substring(0,2)) % 12 + (minute / 60)) * 360/12;
console.log("minute="+minute+" minDeg ="+minuteDeg);
        document.getElementById("header").style.backgroundImage = `url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='40' height='40'><circle cx='20' cy='20' r='18.5' fill='none' stroke='%23222' stroke-width='3' /><path d='M20,4 20,8 M4,20 8,20 M36,20 32,20 M20,36 20,32' stroke='%23bbb' stroke-width='1' /><circle cx='20' cy='20' r='2' fill='%23222' stroke='%23222' stroke-width='2' /></svg>"), url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='40' height='40'><path d='M18.5,24.5 19.5,4 20.5,4 21.5,24.5 Z' fill='%23222' style='transform:rotate(`+minuteDeg+`deg); transform-origin: 50% 50%;' /></svg>"), url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='40' height='40'><path d='M18.5,24.5 19.5,8.5 20.5,8.5 21.5,24.5 Z' style='transform:rotate(`+hourDeg+`deg); transform-origin: 50% 50%;' /></svg>")`;
      }
}

</script>


<oscar:customInterface section="addappt"/>
<script>

function onAdd() {
    return calculateEndTime() ;
}

function setfocus() {
	this.focus();
  document.ADDAPPT.keyword.focus();
  document.ADDAPPT.keyword.select();
}

function moveAppt() {
	var determinator = 0;
	determinator = localStorage.getItem('copyPaste');
	if (determinator == 1) {  //This means we are moving an appt
    pasteAppt(false) ;
    document.forms['ADDAPPT'].displaymode.value='Add Appointment';
	//$("#pasteButton").trigger( "click" );
	//$("#addButton").trigger( "click" );
	localStorage.setItem('copyPaste','0');  //reset
	}
}

function upCaseCtrl(ctrl) {
	ctrl.value = ctrl.value.toUpperCase();
}

function onBlockFieldFocus(obj) {
  obj.blur();
  document.ADDAPPT.keyword.focus();
  document.ADDAPPT.keyword.select();
  window.alert("<bean:message key="Appointment.msgFillNameField"/>");
}

function checkTypeNum(typeIn) {
	var typeInOK = true;
	var i = 0;
	var length = typeIn.length;
	var ch;

	// walk through a string and find a number
	if (length>=1) {
	  while (i <  length) {
		  ch = typeIn.substring(i, i+1);
		  if (ch == ":") { i++; continue; }
		  if ((ch < "0") || (ch > "9") ) {
			  typeInOK = false;
			  break;
		  }
	    i++;
      }
	} else typeInOK = false;
	return typeInOK;
}

function checkTimeTypeIn(obj) {
  var colonIdx;
  if(!checkTypeNum(obj.value) ) {
	  alert ("<bean:message key="Appointment.msgFillTimeField"/>");
  } else {
      colonIdx = obj.value.indexOf(':');
      if(colonIdx==-1) {
        if(obj.value.length < 3) alert("<bean:message key="Appointment.msgFillValidTimeField"/>");
        obj.value = obj.value.substring(0, obj.value.length-2 )+":"+obj.value.substring( obj.value.length-2 );
  }
}
          
  var hours = "";
  var minutes = "";  

  colonIdx = obj.value.indexOf(':');  
  if (colonIdx < 1)
      hours = "00";     
  else if (colonIdx == 1)
      hours = "0" + obj.value.substring(0,1);
  else
      hours = obj.value.substring(0,2);
  
  minutes = obj.value.substring(colonIdx+1,colonIdx+3);
  if (minutes.length == 0)
	    minutes = "00";
  else if (minutes.length == 1)
    minutes = "0" + minutes;
  else if (minutes > 59)
    minutes = "00";

  obj.value = hours + ":" + minutes;    
}

var readOnly=false;
function checkDateTypeIn(obj) {
    if (obj.value == '') {
        alert("Date cannot be empty");
        return false;
    } else { 
        obj.value = obj.value.replace(/\//g,"-");
        if (!check_date(obj.name))
          return false;
    } 
}

function calculateEndTime() {
  var stime = document.ADDAPPT.start_time.value;
  var vlen = stime.indexOf(':')==-1?1:2;
  var shour = stime.substring(0,2) ;
  var smin = stime.substring(stime.length-vlen) ;
  var duration = document.ADDAPPT.duration.value ;
  
  if(isNaN(duration)) {
	  alert("<bean:message key="Appointment.msgFillTimeField"/>");
	  return false;
  }

  if(eval(duration) == 0) { duration =1; }
  if(eval(duration) < 0) { duration = Math.abs(duration) ; }

  var lmin = eval(smin)+eval(duration)-1 ;
  var lhour = parseInt(lmin/60);

  if((lmin) > 59) {
    shour = eval(shour) + eval(lhour);
    shour = shour<10?("0"+shour):shour;
    smin = lmin - 60*lhour;
  } else {
    smin = lmin;
  }

  smin = smin<10?("0"+ smin):smin;
  document.ADDAPPT.end_time.value = shour +":"+ smin;

  if(shour > 23) {
    alert("<bean:message key="Appointment.msgCheckDuration"/>");
    return false;
  }

  //no show
  if(document.ADDAPPT.keyword.value.substring(0,1) === "." && document.ADDAPPT.demographic_no.value === "" ) {
    document.ADDAPPT.status.value = 'N' ;
  }

  return true;
}

function onNotBook() {
	document.forms[0].keyword.value = "<%=DONOTBOOK%>" ;
}

function onButRepeat() {
	document.forms[0].action = "appointmentrepeatbooking.jsp" ;
	if (calculateEndTime()) { document.forms[0].submit(); }
}
<% if(apptObj!=null) { %>
function pasteAppt(multipleSameDayGroupAppt) {

        var warnMsgId = document.getElementById("tooManySameDayGroupApptWarning");

        if (multipleSameDayGroupAppt) {
           warnMsgId.style.display = "block";
           if (document.forms[0].groupButton) {
              document.forms[0].groupButton.style.display = "none";
           }
           if (document.forms[0].addPrintPreviewButton){
              document.forms[0].addPrintPreviewButton.style.display = "none";
           }
           document.forms[0].addButton.style.display = "none";
           document.forms[0].printButton.style.display = "none";

           if (document.forms[0].pasteButton) {
                document.forms[0].pasteButton.style.display = "none";
           }

           if (document.forms[0].apptRepeatButton) {
                document.forms[0].apptRepeatButton.style.display = "none";
           }
        }
        else {
           warnMsgId.style.display = "none";
        }

        document.forms[0].duration.value = "<%=Encode.forJavaScriptBlock(apptObj.getDuration())%>";
        //document.forms[0].chart_no.value = "<%=Encode.forJavaScriptBlock(apptObj.getChart_no())%>";
        document.forms[0].keyword.value = "<%=Encode.forJavaScriptBlock(apptObj.getName())%>";
        document.forms[0].demographic_no.value = "<%=Encode.forJavaScriptBlock(apptObj.getDemographic_no())%>";
        document.forms[0].reason.value = "<%= Encode.forJavaScriptBlock(apptObj.getReason()) %>";
        document.forms[0].reasonCode.value = "<%= Encode.forJavaScriptBlock(apptObj.getReasonCode()) %>";
        document.forms[0].notes.value = "<%= Encode.forJavaScriptBlock(apptObj.getNotes()) %>";
        document.forms[0].resources.value = "<%=Encode.forJavaScriptBlock(apptObj.getResources())%>";
        document.forms[0].type.value = "<%=Encode.forJavaScriptBlock(apptObj.getType())%>";
        document.forms[0].location.value = "<%=Encode.forJavaScriptBlock(apptObj.getLocation())%>";
        if('<%=apptObj.getUrgency()%>' == 'critical') {
                document.forms[0].urgency.checked = "checked";
        }
		document.forms[0].reasonCode.value = "<%=apptObj.getReasonCode() %>";
		
		<%if("true".equals(pros.getProperty("appointment.paste.status","false"))) {%>
            var statusCode = "<%=Encode.forJavaScriptBlock(apptObj.getStatus())%>";
            statusCode = statusCode.substring(0,1); //the selector only supports setting the first status
		    document.forms[0].status.value = statusCode;
		<%}%>
		<%if("true".equals(pros.getProperty("appointment.paste.location","false"))) {%>
			document.forms[0].location.value = "<%=Encode.forJavaScriptBlock(apptObj.getLocation())%>";
		<%}%>


}
<% } %>


	function openTypePopup () {
		windowprops = "height=230,width=500,location=no,scrollbars=no,menubars=no,toolbars=no,resizable=yes,screenX=0,screenY=0,top=100,left=100";
		var popup=window.open("appointmentType.jsp?type="+document.forms['ADDAPPT'].type.value, "Appointment Type", windowprops);
		if (popup != null) {
			if (popup.opener == null) {
				popup.opener = self;
			}
			popup.focus();
		}
	}

	function setType(typeSel,reasonSel,locSel,durSel,notesSel,resSel) {
		  document.forms['ADDAPPT'].type.value = typeSel;
		  document.forms['ADDAPPT'].reason.value = reasonSel;
		  document.forms['ADDAPPT'].duration.value = durSel;
		  document.forms['ADDAPPT'].notes.value = notesSel;
		  document.forms['ADDAPPT'].duration.value = durSel;
		  document.forms['ADDAPPT'].resources.value = resSel;
		  var loc = document.forms['ADDAPPT'].location;
		  if(loc.nodeName === 'SELECT') {
		          for(c = 0;c < loc.length;c++) {
		                  if(loc.options[c].innerHTML == locSel) {
		                          loc.selectedIndex = c;
		                          loc.style.backgroundColor=loc.options[loc.selectedIndex].style.backgroundColor;
		                          break;
		                  }
		          }
		  } else if (loc.nodeName === "INPUT") {
			  document.forms['ADDAPPT'].location.value = locSel;
		  }
	}



	$(document).ready(function() {
		$( document ).tooltip();

		var url = "<%= request.getContextPath() %>/demographic/SearchDemographic.do?jqueryJSON=true&activeOnly=true";

		$("#keyword").autocomplete( {
			source: url,
			minLength: 2,

			focus: function( event, ui ) {
				$("#keyword").val( ui.item.formattedName );
				return false;
			},
			select: function( event, ui ) {
				$("#demographic_no").val( ui.item.value );
				$("#mrp").val( ui.item.provider );
				$("#keyword").val( ui.item.formattedName );
				return false;
			}
		})
        .autocomplete( "instance" )._renderItem = function( ul, item ) {
          return $( "<li>" )
            .append( "<div><b>" + item.label + "</b>" + "<br>" + item.provider + "</div>" )
            .appendTo( ul );
        };


        $.widget('custom.myselectmenu', $.ui.selectmenu, {

              /**
               * @see {@link https://api.jqueryui.com/selectmenu/#method-_renderItem}
               */
              _renderItem: function(ul, item) {
                    var string = "<div><b>" + item.label + "</b> "
                    if (item.element.attr("data-dur") && item.element.attr("data-dur").length > 0){
                        string = string + item.element.attr("data-dur")+ "&nbsp;<bean:message key='provider.preference.min' />";
                    }
                    if (item.element.attr("data-notes") && item.element.attr("data-notes").length > 0){
                        string = string + "&nbsp;&nbsp;" + "<span style='color:gray'> <i class='icon-pencil' title='" + "<bean:message key="Appointment.formNotes" />:&nbsp;" +
                        item.element.attr("data-notes") + "'></i></span>";
                    }
                    string = string + "<br>";
                    if (item.element.attr("data-reason") && item.element.attr("data-reason").length > 0){
                        string = string + "<span style='color:gray'><i class='icon-tags' title='" + "<bean:message key="Appointment.formReason" />" + "'></i></span>&nbsp;&nbsp;" +
                        item.element.attr("data-reason");
                        }
                    if (item.element.attr("data-resources") && item.element.attr("data-resources").length > 0){
                        string = string + "<br>" + "<span style='color:gray'><i class='icon-cog' title='" + "<bean:message key="Appointment.formResources" />" + "'></i></span>&nbsp;&nbsp;" +
                        item.element.attr("data-resources");
                    }
                    if (item.element.attr("data-loc") && item.element.attr("data-loc").length > 1){
                        string = string + "<br>" + "<span style='color:gray'><i class='icon-home' title='" + "<bean:message key="Appointment.formLocation" />" + "'></i></span>&nbsp;&nbsp;" +
                        item.element.attr("data-loc");
                    }
                    string = string + "</div>";
                    return $( "<li>" )
                        .append( string )
                        .appendTo( ul );

                    }
        });

        // render custom selectmenu
        $('#type').myselectmenu({
            change: function( event, data ) {
                label=data.item.value;
                origReason = $("textarea[name='reason']").val();
                reason=data.item.element.attr("data-reason");
                if (origReason.length > 0 ) {
                    reason = reason.concat(" -- ".concat(origReason));
                }
                loc=data.item.element.attr("data-loc");
                dur=data.item.element.attr("data-dur");
                notes=data.item.element.attr("data-notes");
                resources=data.item.element.attr("data-resources");
                setType(label,reason,loc,dur,notes,resources);
            }

            });


    });

// stop javascript -->

</script>

<%

  SimpleDateFormat fullform = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
  SimpleDateFormat inform = new SimpleDateFormat ("yyyy-MM-dd");
  SimpleDateFormat outform = new SimpleDateFormat ("EEE");

  java.util.Date apptDate;

  if(request.getParameter("year")==null || request.getParameter("month")==null || request.getParameter("day")==null){
    Calendar cal = Calendar.getInstance();
    String sDay = String.valueOf(cal.get(Calendar.DATE));
    String sMonth = String.valueOf(cal.get(Calendar.MONTH) + 1);
    String sYear = String.valueOf(cal.get(Calendar.YEAR));
    String sTime=(request.getParameter("start_time")==null)?"00:00AM":request.getParameter("start_time");
    apptDate = fullform.parse(bFirstDisp?(sYear + "-" + sMonth + "-" + sDay + " "+ sTime):
        (request.getParameter("appointment_date") + " " + sTime)) ;
  }else if(request.getParameter("start_time")==null){
    apptDate = fullform.parse(bFirstDisp?(request.getParameter("year") + "-" + request.getParameter("month") + "-" + request.getParameter("day")+" "+ "00:00 AM"):
        (request.getParameter("appointment_date") + " " + "00:00AM")) ;
  }else
  {
    apptDate = fullform.parse(bFirstDisp?(request.getParameter("year") + "-" + request.getParameter("month") + "-" + request.getParameter("day")+" "+ request.getParameter("start_time")):
        (request.getParameter("appointment_date") + " " + request.getParameter("start_time"))) ;
  }


// Get localized pattern for UI
DateTimeFormatter pattern2 = DateTimeFormatter.ofPattern("EEE").withLocale(request.getLocale());
// Convert Java Date to Java LocalDateTime
LocalDateTime apptd=apptDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

  //String dateString1 = outform.format(apptDate );
  String dateString1 = pattern2.format(apptd);
  String dateString2 = inform.format(apptDate );

  GregorianCalendar caltime =new GregorianCalendar( );
  caltime.setTime(apptDate);
  caltime.add(GregorianCalendar.MINUTE, Integer.parseInt(duration)-1 );

  java.util.Date startTime = ConversionUtils.fromDateString(request.getParameter("start_time"),"HH:mm");
  java.util.Date endTime = ConversionUtils.fromDateString(caltime.get(Calendar.HOUR_OF_DAY) +":"+ caltime.get(Calendar.MINUTE),"HH:mm");
  
  List<Appointment> appts = appointmentDao.search_appt(apptDate, curProvider_no, startTime, endTime, startTime, endTime, startTime, endTime, Integer.parseInt((String)request.getSession().getAttribute("programId_oscarView")));
  
  long apptnum = appts.size() > 0 ? new Long(appts.size()) : 0;
  
  OscarProperties props = OscarProperties.getInstance();
  
  String timeoutSeconds = props.getProperty("appointment_locking_timeout","0");
  int timeoutSecs = 0; 
  try { 
    timeoutSecs = Integer.parseInt(timeoutSeconds);
  }catch (NumberFormatException e) {/*empty*/}
  
  int hourInt = caltime.get(Calendar.HOUR_OF_DAY); 
  String hour = String.valueOf(hourInt);
  if (hour.length() == 0)
      hour = "00";
   else if (hour.length() == 1)
      hour = "0" + hour;
  
  int minuteInt = caltime.get(Calendar.MINUTE); 
  String minute = String.valueOf(minuteInt);
   if (minute.length() == 0)
      minute = "00";
   else if (minute.length() == 1) 
      minute = "0" + minute;  
  
   if (timeoutSecs > 0) {
%>

<script>
        var timers = new Array();

	$(document).ready(function(){

		$(window).on('beforeunload',function(){cancelPageLock();
		});
		//cancel any page view/locks held by provider on clicking 'X'
		$("form#addappt").on( "submit",function() {$(window).off('beforeunload');
		});

		calculateEndTime();
		var endTime = document.forms[0].end_time.value;
		var startTime = document.forms[0].start_time.value;
		var apptDate = document.forms[0].appointment_date.value;
		updatePageLock(100,apptDate,startTime,endTime);
	});

        function checkPageLock() {
           $("#searchBtn").attr("disabled","disabled");
           calculateEndTime();
           var endTime = document.forms[0].end_time.value;
           var startTime = document.forms[0].start_time.value;
           var apptDate = document.forms[0].appointment_date.value;
           updatePageLock(100,apptDate,startTime,endTime);
           
        }
        
        function updatePageLock(timeout, apptDate, startTime, endTime) {

           for (var i = 0; i < timers.length; i++) {
                clearTimeout(timers[i]);               
           }
                      
	   haveLock=false;
           $.ajax({
               type: "POST",
               url: "<%=request.getContextPath()%>/PageMonitoringService.do",
               data: { method: "update", page: "addappointment", pageId: "<%=curProvider_no%>|" + apptDate + "|" + startTime + "|" + endTime, lock: true, timeout: <%=timeoutSeconds%>, cleanupExisting: true},
               dataType: 'json',
               async: false,
               success: function(data,textStatus) {
                       lockData=data;
                            var locked=false;
                            var lockedProviderName='';
                            var providerNames='';
                            haveLock=false;
                       $.each(data, function(key, val) {				
                         if(val.locked) {
                             locked=true;
                             lockedProviderName=val.providerName;
                         }
                         if(val.locked==true && val.self==true) {
                                       haveLock=true;
                               }
                         if(providerNames.length > 0)
                             providerNames += ",";
                         providerNames += val.providerName;

                       });

                       var lockedMsg = locked?'<span style="color:red" title="'+lockedProviderName+'">&nbsp(locked)</span>':'';
                       $("#lock_notification").html(
                            '<span title="'+providerNames+'">Viewers:'+data.length+lockedMsg+'</span>'	   
                       );


                       if(haveLock==true) { //i have the lock
                            $("#addButton").show(); 
                            $("#printButton").show();
                            $("#addPrintPreviewButton").show(); 
                            $("#pasteButton").show(); 
                            $("#apptRepeatButton").show(); 
                       } else if(locked && !haveLock) { //someone else has lock.
                            $("#addButton").hide(); 
                            $("#printButton").hide();
                            $("#addPrintPreviewButton").hide(); 
                            $("#pasteButton").hide(); 
                            $("#apptRepeatButton").hide();                          
                       } else { //no lock
                            $("#addButton").show(); 
                            $("#printButton").show();
                            $("#addPrintPreviewButton").show(); 
                            $("#pasteButton").show(); 
                            $("#apptRepeatButton").show();                                 
                       }
                       $("#searchBtn").removeAttr("disabled");
               }
             }
            );
            
            timers.push(setTimeout(function(){updatePageLock(5000, apptDate, startTime, endTime)},timeout));      
        }
        
        function cancelPageLock() {
           calculateEndTime();
           var endTime = document.forms[0].end_time.value;
           var startTime = document.forms[0].start_time.value;
           var apptDate = document.forms[0].appointment_date.value;
           
           for (var i = 0; i < timers.length; i++) {
               clearTimeout(timers[i]);               
           }
           
           $.ajax({
               type: "POST",
               url: "<%=request.getContextPath()%>/PageMonitoringService.do",
               data: { method: "cancel", page: "addappointment", pageId: "<%=curProvider_no%>|" + apptDate + "|" + startTime + "|" + endTime},
               dataType: 'json',
               async: false,
               success: function(data,textStatus) {}
           });
        }

</script>

<%
 } else {        
%>
<script>
    function checkPageLock() { //don't do anything unless timeout/locking is enabled.
    }
    function cancelPageLock() { //don't do anything unless timeout/locking is enabled.
    }
</script>

<%
  }
  String deepcolor = apptnum==0?"#E8E8E8":"gold", weakcolor = apptnum==0?"#f3f6f9":"ivory";

  boolean bDnb = false;
  for(Appointment a : appts) {
	  String apptName = a.getName();
	  if (apptName.equalsIgnoreCase(DONOTBOOK)) bDnb = true;
  }


  // select provider lastname & firstname
  String pLastname = "";
  String pFirstname = "";
  Provider p = providerDao.getProvider(curProvider_no);
  if(p != null) {
	  pLastname = p.getLastName();
      pFirstname = p.getFirstName();
  }
%>

<script>
function parseSearch() {
    // sane defaults
    document.forms['ADDAPPT'].displaymode.value='Search ';
    document.getElementById("search_mode").value='search_name';

    var keyObj = document.forms['ADDAPPT'].keyword;
    var keyVal = keyObj.value;
    console.log(keyVal);

    // start with the loosest pattern
    // address pattern 293 Meridian
    const reAddr = /^\d{1,9}[\s]\w*/;
    if (reAddr.exec(keyVal)) {
        document.getElementById("search_mode").value="search_address";
    }

    // hin OHIP 10 didgits  MSP 9 didgits Regie 4 alpha + 8 digits
    const reHIN = /^\d{9,10}$/;
    if (reHIN.exec(keyVal)) {
        document.getElementById("search_mode").value="search_hin";
    }
    const reRegie = /^[A-Z]{4}\d{8}$/;
    if (reRegie.exec(keyVal)) {
        document.getElementById("search_mode").value="search_hin";
    }

    //phone xxx-xxx-xxxx with varying delimiters
    const rePhone = /^\d{3}[-\s.]\d{3}[-\s.]\d{4}$/;
    if (rePhone.exec(keyVal)) {
        const area =  keyVal.substring(0,3);
        const p1 = keyVal.substring(4,7);
        const p2 = keyVal.substring(8);
        const phone = area +"-"+p1+"-"+p2;
        keyObj.value = phone;
        document.getElementById("search_mode").value="search_phone";
    }

    // DOB yyyy-mm-dd with varying delimiters
    const reDOB=/^(19|20)\d\d([\/.-\s])(0[1-9]|1[012])[\/.-\s](0[1-9]|[12]\d|3[01])$/;
    if (reDOB.exec(keyVal)) {
        const yyyy = keyVal.substring(0,4);
        const mm = keyVal.substring(5,7);
        const dd = keyVal.substring(8);
        const dob = yyyy+"-"+mm+"-"+dd;
        keyObj.value = dob;
        document.getElementById("search_mode").value="search_dob";
    }

    //swipe pattern
    if (keyVal.indexOf('%b610054') == 0 && keyVal.length > 18){
         keyObj.value = keyVal.substring(8,18);
         document.getElementById("search_mode").value="search_hin";
    }
}

function locale(){
    // add style for multisites location
    var loc = document.forms['ADDAPPT'].location;
    if(loc.nodeName.toUpperCase() == 'SELECT') loc.style.backgroundColor=loc.options[loc.selectedIndex].style.backgroundColor;
}

</script>
</head>
<body onLoad="setfocus(); moveAppt(); updateTime(); locale(); " >
 <% if (timeoutSecs >0) { %>
    <div id="lock_notification">
        <span title="">Viewers: N/A</span>
    </div>
 <% } %>
<%
  String patientStatus = "";
  String disabled="";
  String address ="";
  String province = "";
  String city ="";
  String postal ="";
  String phone = "";
  String phone2 = "";
  String email  = "";
  String hin = "";
  String dob = "";
  String sex = "";

  //to show Alert msg

  boolean bMultipleSameDayGroupAppt = false;
  String displayStyle = "display:none";
  String myGroupNo = providerPreference.getMyGroupNo();

  if (props.getProperty("allowMultipleSameDayGroupAppt", "").equalsIgnoreCase("no")) {

        String demographicNo = request.getParameter("demographic_no");

        if (!bFirstDisp && (demographicNo != null) && (!demographicNo.equals(""))) {


        	appts = appointmentDao.search_group_day_appt(myGroupNo, Integer.parseInt(demographicNo), apptDate);

            long numSameDayGroupAppts = appts.size() > 0 ? new Long(appts.size()) : 0;
            bMultipleSameDayGroupAppt = (numSameDayGroupAppts > 0);
        }

        if (bMultipleSameDayGroupAppt){
            displayStyle="display:block";
        }
  }
  %>
  <div id="tooManySameDayGroupApptWarning" style="<%=displayStyle%>">
    <div class="alert alert-error" >
        <h4><bean:message key='appointment.addappointment.titleMultipleGroupDayBooking'/></h4>
        <bean:message key='appointment.addappointment.MultipleGroupDayBooking'/>
    </div>
</div>
<%
  if (!bFirstDisp && request.getParameter("demographic_no") != null && !request.getParameter("demographic_no").equals("")) {
	  Demographic d = demographicDao.getDemographic(request.getParameter("demographic_no"));
	  if(d != null) {
		  patientStatus = d.getPatientStatus();
		  address = d.getAddress();
		  city = d.getCity();
		  province = d.getProvince();
		  postal = d.getPostal();
		  phone = d.getPhone();
		  phone2 = d.getPhone2();
		  email = d.getEmail();
		  String year_of_birth   = d.getYearOfBirth();
	      String month_of_birth  = d.getMonthOfBirth();
	      String date_of_birth   = d.getDateOfBirth();
	      dob = "("+year_of_birth+"-"+month_of_birth+"-"+date_of_birth+")";
	      sex = d.getSex();
	      hin = d.getHin();
	      String ver = d.getVer();
	      hin = hin +" "+ ver;

	      if (patientStatus == null || patientStatus.equalsIgnoreCase("AC")) {
	        patientStatus = "";
	      } else if (patientStatus.equalsIgnoreCase("FI")||patientStatus.equalsIgnoreCase("DE")||patientStatus.equalsIgnoreCase("IN")) {
	      	disabled = "disabled";
	      }

	      String rosterStatus = d.getRosterStatus();
	      if (rosterStatus == null || rosterStatus.equalsIgnoreCase("RO")) {
	      	rosterStatus = "";
	      }

	      if(!patientStatus.equals("") || !rosterStatus.equals("") ) {
	        String exp = " null-undefined\n IN-inactive ID-deceased OP-out patient\n NR-not signed\n FS-fee for service\n TE-terminated\n SP-self pay\n TP-third party";

        %>
<div class="alert alert-info" title='<%=exp%>'>
    <h4><bean:message key="Appointment.msgPatientStatus" />:</h4>
    <%=patientStatus%>&nbsp;<bean:message key="Appointment.msgRosterStatus" />:&nbsp;<%=rosterStatus%>
</div>
        <%

        }
	}
	if( request.getParameter("demographic_no")!=null && !"".equals(request.getParameter("demographic_no")) ) {
	DemographicCust demographicCust = demographicCustDao.find(Integer.parseInt(request.getParameter("demographic_no")));

		if (demographicCust != null && demographicCust.getAlert() != null && !demographicCust.getAlert().equals("") ) {

%>
<div class="alert alert-error">
	<h4><bean:message key="Appointment.formAlert" />:</h4> <%=demographicCust.getAlert()%>
</div>

<%

		}
	}
  }


  if(apptnum!=0) {

%>
<div class="alert alert-error" >
    <h4><bean:message key='appointment.addappointment.msgDoubleBooking' /></h4>
    <%
			if(bDnb) out.println("<br/>You CANNOT book an appointment on this time slot.");
    %>
</div>


<% } %>

<% if (billingRecommendations.size() > 0) { %>
        <table width="100%" class="alert alert-info" >
            <% for (String recommendation : billingRecommendations) { %>
                <tr>
                    <td><%=recommendation%></td>
                </tr>
            <% } %>
        </table>
<% } %>

<form name="ADDAPPT" id="addappt" method="post" action="<%=request.getContextPath()%>/appointment/appointmentcontrol.jsp"
	onsubmit="return(onAdd())"><input type="hidden"
	name="displaymode" value="">
	<input type="hidden" name="year" value="<%=request.getParameter("year") %>" >
    <input type="hidden" name="month" value="<%=request.getParameter("month") %>" >
    <input type="hidden" name="day" value="<%=request.getParameter("day") %>" >
    <input type="hidden" name="fromAppt" value="1" >

<div class="span12">
    <div class="time" id="header"><h4>
        <!-- We display a shortened title for the mobile version -->
        <% if (isMobileOptimized) { %><bean:message key="appointment.addappointment.msgMainLabelMobile" />
        <% } else { %><bean:message key="appointment.addappointment.msgMainLabel" />
        <%          out.println("("+pFirstname+" "+pLastname+")"); %>
        <% } %></h4>
    </div>
</div>
<!-- /div -->
<div>

<div class="container-fluid well" >
    <div class ="span6">
    <table>
        <tr>
            <td style="width: 100px;">
                <bean:message key="Appointment.formDate" />&nbsp;<span style="color:brown;">(<%=dateString1%>)</span>:
            </td>
            <td>
                <input type="date" name="appointment_date"
                    value="<%=dateString2%>"
                    onChange="checkDateTypeIn(this);checkPageLock()">
            </td>
        </tr>
        <tr>
            <td>
                <bean:message key="Appointment.formStartTime" />:
            </td>
            <td>
                <input type="time" name="start_time"
                    value='<%=request.getParameter("start_time")%>' onChange="checkTimeTypeIn(this);updateTime();checkPageLock()">
            </td>
        </tr>
        <tr>
            <td style="font-size:8pt;">
                <bean:message key="Appointment.formDuration" />:
            </td>
            <td>
                <input type="number" name="duration" id="duration"
                        value="<%=duration%>" onChange="checkPageLock()" onblur="calculateEndTime();">
                <input type="hidden" name="end_time"
                        value='<%=request.getParameter("end_time")%>'
                         onChange="checkTimeTypeIn(this)">
            </td>
        </tr>
        <tr>
            <td>
                 <input type="submit" name="searchBtn" id="searchBtn" class="btn" style="margin-bottom:10px;"
                    onclick="parseSearch(); document.forms['ADDAPPT'].displaymode.value='Search ';"
                    value="<bean:message key="appointment.addappointment.btnSearch"/>">
            </td>
            <td>
            	<%
            		String name="";
            		name = String.valueOf((bFirstDisp && !bFromWL)?"":request.getParameter("name")==null?session.getAttribute("appointmentname")==null?"":session.getAttribute("appointmentname"):request.getParameter("name"));
            	%>
                <input type="text" name="keyword" id="keyword"
                        value="<%=Encode.forHtmlAttribute(name)%>"
                        placeholder="<bean:message key="Appointment.formNamePlaceholder" />">
            </td>
        </tr>
        <tr>
            <td>
                <bean:message key="Appointment.formReason" />:
            </td>
            <td>
                <select name="reasonCode">
	                <c:choose>
	                	<c:when test="${ not empty reasonCodes  }">
	                		<c:forEach items="${ reasonCodes.items }" var="reason" >
	                		<c:if test="${ reason.active }">
	                			<option value="${ reason.id }" id="${ reason.value }" >
	                				<c:out value="${ reason.label }" />
	                			</option>
	                		</c:if>
	                		</c:forEach>     
	                	</c:when>
	                	<c:otherwise>
	                		<option value="-1">Other</option>
	                	</c:otherwise>
	                </c:choose>
				</select>
            </td>
        </tr>
        <tr>
            <td></td><td>
		    <textarea id="reason" name="reason" tabindex="2" rows="2" style="resize:none;" placeholder="<bean:message key="Appointment.formReason" />" cols="18" maxlength="80"><%=bFirstDisp?"":request.getParameter("reason").equals("")?"":Encode.forHtmlContent(request.getParameter("reason"))%></textarea>
            </td>
        </tr>
            <%
				    boolean bMoreAddr = bMultisites? true : props.getProperty("scheduleSiteID", "").equals("") ? false : true;
				    String tempLoc = "";
				    if(bFirstDisp && bMoreAddr) {
				            tempLoc = (new JdbcApptImpl()).getLocationFromSchedule(dateString2, curProvider_no);
				    }
				    String loc = bFirstDisp?tempLoc:request.getParameter("location");
				    String colo = bMultisites
				                                        ? ApptUtil.getColorFromLocation(sites, loc)
				                                        : bMoreAddr? ApptUtil.getColorFromLocation(props.getProperty("scheduleSiteID", ""), props.getProperty("scheduleSiteColor", ""),loc) : "white";
			%>

        <tr>
            <td>
                <bean:message key="Appointment.formLocation" />:
            </td>
            <td>
		<% // multisites start ==================
		if (bMultisites) { %>
	        <select tabindex="4" name="location" style="background-color: <%=colo%>" onchange='this.style.backgroundColor=this.options[this.selectedIndex].style.backgroundColor'>
	        <% for (Site s:sites) { %>
	                <option value="<%=Encode.forHtmlAttribute(s.getName())%>" class="<%=s.getShortName()%>" style="background-color: <%=s.getBgColor()%>" <%=s.getName().equals(loc)?"selected":"" %>><%=Encode.forHtmlContent(s.getName())%></option>
	        <% } %>
	        </select>
		<% } else {
			// multisites end ==================
			if (locationEnabled) {
		%>
			<select name="location" >
                <%
                String sessionLocation = "";
                ProgramProvider programProvider = programManager2.getCurrentProgramInDomain(loggedInInfo, loggedInInfo.getLoggedInProviderNo());
                if(programProvider!=null && programProvider.getProgram() != null) {
                	sessionLocation = programProvider.getProgram().getId().toString();
                }
                if (programs != null && !programs.isEmpty()) {
			       	for (Program program : programs) {
			       	    String description = StringUtils.isBlank(program.getLocation()) ? program.getName() : program.getLocation();
			   	%>
			        <option value="<%=program.getId()%>" <%=program.getId().toString().equals(sessionLocation) ? "selected='selected'" : ""%>><%=Encode.forHtmlAttribute(description)%></option>
			    <%	}
                }
			  	%>
            </select>
        	<% } else { %>
        	<input type="TEXT" name="location" tabindex="4" value="<%=loc%>" width="25" height="20" border="0" hspace="2">
        	<% } %>
		<% } %>
            </td>
        </tr>
        <tr>
            <td>
                <bean:message key="Appointment.formCreator" />:
            </td>
            <td>
                <input type="TEXT" name="user_id" readonly
                    value='<%=bFirstDisp?(Encode.forHtmlAttribute(userlastname)+", "+Encode.forHtmlAttribute(userfirstname)):request.getParameter("user_id").equals("")?"Unknown":Encode.forHtmlAttribute(request.getParameter("user_id"))%>'
                    >
            </td>
        </tr>

        <% if (pros.isPropertyActive("mc_number")) { %>
        <tr>
            <td>
                <bean:message key="Appointment.formMC" />:
            </td>
            <td>
                <input type="text" name="appt_mc_number" tabindex="4" />
            </td>
        </tr>

        <% } %>

    </table>
    </div>


    <div class ="span6">
    <table>
        <tr>
            <td style="width: 100px;">
                <bean:message key="Appointment.formStatus" />:
            </td>
            <td>
				<%
            if (strEditable!=null&&strEditable.equalsIgnoreCase("yes")){
            %>

<select name="status" style="background-color:<%=(allStatus.get(0)).getColor()%>" onchange='this.style.backgroundColor=this.options[this.selectedIndex].style.backgroundColor' >
                    <% for (int i = 0; i < allStatus.size(); i++) { %>
                    <option class="<%=(allStatus.get(i)).getStatus()%>" style="background-color:<%=(allStatus.get(i)).getColor()%>"
                            value="<%=(allStatus.get(i)).getStatus()%>"
                            <%=(allStatus.get(i)).getStatus().equals(request.getParameter("status"))?"SELECTED":""%>><%=(allStatus.get(i)).getDescription()%></option>
                    <% } %>
            </select> <%
            }
            if (strEditable==null || !strEditable.equalsIgnoreCase("yes")){
            %> <input type="text" name="status"
					value='<%=bFirstDisp?"t":request.getParameter("status")==null?"":request.getParameter("status").equals("")?"":request.getParameter("status")%>'
					> <%}%>
            </td>
        </tr>
        <tr>
            <td>
                <bean:message key="Appointment.formType"/>:
                <!--<input type="button" class="btn" name="typeButton" value="<bean:message key="Appointment.formType"/>" style="margin-bottom:10px;" onClick="openTypePopup();"> -->
             </td>
             <td>
                <!-- <input type="text" name="type" id="type" value='<%=bFirstDisp?"":request.getParameter("type").equals("")?"":request.getParameter("type")%>' > -->
                <select name="type" id="type" title="<bean:message key="billing.billingCorrection.msgSelectVisitType"/>"
                    >
                <option data-dur="" data-reason=""></option><!-- important leave a blank top entry  -->

        <% AppointmentTypeDao appDao = (AppointmentTypeDao) SpringUtils.getBean("appointmentTypeDao");
           List<AppointmentType> types = appDao.listAll();
                for(int j = 0;j < types.size(); j++) {
%>
                    <option data-dur="<%= types.get(j).getDuration() %>"
                            data-reason="<%= Encode.forHtmlAttribute(types.get(j).getReason()) %>"
                            data-loc="<%= Encode.forHtmlAttribute(types.get(j).getLocation()) %>"
                            data-notes="<%= Encode.forHtmlAttribute(types.get(j).getNotes()) %>"
                            data-resources="<%= Encode.forHtmlAttribute(types.get(j).getResources()) %>">
                        <%=Encode.forHtml(types.get(j).getName()) %>
                    </option>
                <% } %>
                </select>

            </td>
        </tr>
        <tr>
            <td>
                <bean:message key="Appointment.formDoctor" />:
            </td>
            <td>
                <input type="text" id="mrp" readonly
                       value="<%=bFirstDisp ? "" : StringEscapeUtils.escapeHtml(providerBean.getProperty(curDoctor_no,""))%>">
            </td>
        </tr>
        <tr>
            <td><input type="button" value="<bean:message key="Appointment.doNotBook" />" class="btn btn-link" style="padding-left:0px;" onclick="onNotBook();">

            </td>
            <td>

                <input type="text" name="demographic_no" id="demographic_no"
                    ONFOCUS="onBlockFieldFocus(this)" readonly
                    value='<%=(bFirstDisp && !bFromWL)?"":request.getParameter("demographic_no").equals("")?"":request.getParameter("demographic_no")%>' >
            </td>
        </tr>
        <tr>
            <td>
                <bean:message key="Appointment.formNotes" />:
            </td>
            <td>
                <textarea name="notes" tabindex="3" rows="2" style="resize:none;" placeholder="<bean:message key="Appointment.formNotes" />" cols="18" maxlength="255"><%=bFirstDisp?"":request.getParameter("notes").equals("")?"":request.getParameter("notes")%></textarea>
            </td>
        </tr>
        <tr>
            <td>
                <bean:message key="Appointment.formResources" />:
            </td>
            <td>
                <input type="text" name="resources"
                    tabindex="5"
                    value='<%=bFirstDisp?"":request.getParameter("resources").equals("")?"": Encode.forHtmlAttribute(request.getParameter("resources"))%>'
                    >
            </td>
        </tr>
        <tr>
            <td>
                <bean:message key="Appointment.formDateTime" />:
            </td>
            <td>
<%
            GregorianCalendar now=new GregorianCalendar();
            GregorianCalendar cal = (GregorianCalendar) now.clone();
            cal.add(GregorianCalendar.DATE, 1);
            String strDateTime=now.get(Calendar.YEAR)+"-"+(now.get(Calendar.MONTH)+1)+"-"+now.get(Calendar.DAY_OF_MONTH)+" "
                + now.get(Calendar.HOUR_OF_DAY)+":"+now.get(Calendar.MINUTE)+":"+now.get(Calendar.SECOND);

            LocalDateTime create=now.toZonedDateTime().toLocalDateTime();
            DateTimeFormatter pattern = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(request.getLocale()).withZone(ZoneId.systemDefault());

%>
                <input type="hidden" name="createdatetime" value="<%=strDateTime%>">
                <%=create.format(pattern)%>
                <input type="hidden" name="provider_no" value="<%=curProvider_no%>">
                <input type="hidden" name="dboperation" value="search_titlename">
                <input type="hidden" name="creator" value='<%=Encode.forHtmlAttribute(userlastname)+", "+Encode.forHtmlAttribute(userfirstname)%>'>
                <input type="hidden" name="remarks" value="">
            </td>
        </tr>
        <tr>
            <td>
                <bean:message key="Appointment.formCritical" /> <i class="icon-warning-sign"></i>:
            </td>
            <td>
            	<input type="checkbox" name="urgency" value="critical"><span class="checkmark"></span>
            </td>
        </tr>
            <% String emailReminder = pros.getProperty("emailApptReminder");
               if ((emailReminder != null) && emailReminder.equalsIgnoreCase("yes")) { %>
        <tr>
            <td>
                <bean:message key="Appointment.formEmailReminder" />:
            </td>
            <td>
                <input type="checkbox" name="emailPt" value="email reminder">
            </td>
        </tr>
             <%  }else { %>
        <tr><td></td>
            <td></td>
        </tr>
	     <%  }%>

    </table>
    </div>






            <input type="hidden" name="orderby" value="last_name, first_name">
<%
    String searchMode = request.getParameter("search_mode");
    if (searchMode == null || searchMode.isEmpty()) {
        searchMode = OscarProperties.getInstance().getProperty("default_search_mode","search_name");
    }
%>
            <input type="hidden" name="search_mode" id="search_mode" value="<%=searchMode%>">
            <input type="hidden" name="originalpage" value="<%=request.getContextPath() %>/appointment/addappointment.jsp">
            <input type="hidden" name="limit1" value="0">
            <input type="hidden" name="limit2" value="5">
            <input type="hidden" name="ptstatus" value="active">
			<input type="hidden" name="outofdomain" value="<%=OscarProperties.getInstance().getProperty("pmm.client.search.outside.of.domain.enabled","true")%>" >
            <!--input type="hidden" name="displaymode" value="Search " -->






<%String demoNo = request.getParameter("demographic_no");%>
<div class ="span12">


        <% if(!(bDnb || bMultipleSameDayGroupAppt)) { %>

        <%    if (!props.getProperty("allowMultipleSameDayGroupAppt", "").equalsIgnoreCase("no")) {%>
        <input type="submit" id="addButton" class="btn btn-primary"
            onclick="document.forms['ADDAPPT'].displaymode.value='Add Appointment'"
            tabindex="6"
            value="<% if (isMobileOptimized) { %><bean:message key="appointment.addappointment.btnAddAppointmentMobile" />
                   <% } else { %><bean:message key="appointment.addappointment.btnAddAppointment"/><% } %>"
            <%=disabled%>>
      <input type="submit" id="groupButton" class="btn"
            onclick="document.forms['ADDAPPT'].displaymode.value='Group Appt'"
            value="<bean:message key="appointment.addappointment.btnGroupAppt"/>"
            <%=disabled%>>
        <% }

  if(dateString2.equals( inform.format(inform.parse(now.get(Calendar.YEAR)+"-"+(now.get(Calendar.MONTH)+1)+"-"+now.get(Calendar.DAY_OF_MONTH))) )

    || dateString2.equals( inform.format(inform.parse(cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH))) ) ) {

        org.apache.struts.util.MessageResources resources = org.apache.struts.util.MessageResources.getMessageResources("oscarResources");

        %> <input type="submit" id="addPrintPreviewButton" class="btn"
            onclick="document.forms['ADDAPPT'].displaymode.value='Add Appt & PrintPreview'"
            value="<bean:message key='appointment.addappointment.btnAddApptPrintPreview'/>"
            <%=disabled%>>


<%
  }

%>

        <input type="submit" id="printReceiptButton" class="btn"
            onclick="document.forms['ADDAPPT'].displaymode.value='Add Appointment';document.forms['ADDAPPT'].printReceipt.value='1';"
            value="<bean:message key='appointment.addappointment.btnPrintReceipt'/>"
            <%=disabled%>>
        <input type="hidden" name="printReceipt" value="">
<input type="submit" id="printButton"
            onclick="document.forms['ADDAPPT'].displaymode.value='Add Appt & PrintCard'" class="btn"
            value="<bean:message key='global.btnPrint'/>"
            <%=disabled%>>



        <% } %>



        <%
           if(bFirstDisp && apptObj!=null) {

               long numSameDayGroupApptsPaste = 0;

               if (props.getProperty("allowMultipleSameDayGroupAppt", "").equalsIgnoreCase("no")) {
                    String [] sqlParam = new String[3] ;
                    sqlParam[0] = myGroupNo; //schedule group
                    //convert empty string to placeholder demographic number "0" to prevent NumberFormatException when cutting/copying an empty appointmnet.
                    if(apptObj.getDemographic_no().trim().equals(""))
                    {
                        apptObj.setDemographic_no("0");//demographic numbers start at 1
                    }
                    sqlParam[1] = apptObj.getDemographic_no();
                    sqlParam[2] = dateString2;
		    appts = appointmentDao.search_group_day_appt(myGroupNo, Integer.parseInt(apptObj.getDemographic_no()), apptDate);
                    numSameDayGroupApptsPaste = appts.size() > 0 ? new Long(appts.size()) : 0;
                }
          %>
          <input type="button" id="pasteButton" value="Paste" class="btn" onclick="pasteAppt(<%=(numSameDayGroupApptsPaste > 0)%>);">
        <% }%>

       <% if (!props.getProperty("allowMultipleSameDayGroupAppt", "").equalsIgnoreCase("no")) {%>
          <input type="button" id="apptRepeatButton" class="btn" value="<bean:message key="appointment.addappointment.btnRepeat"/>" onclick="onButRepeat()" <%=disabled%>>
      <%  } %>
<input type="RESET" id="backButton" class="btn btn-link" value="<bean:message key="global.btnCancel"/>" onClick="cancelPageLock();window.close();">

</div>
</div>
</div>
</FORM>

<div class ="span12">
<table style="margin-left:auto;">
<tr>
    <td style="vertical-align: top;">
        <%if( bFromWL && demoNo != null && demoNo.length() > 0 ) {%>
        <table style="font-size: 9pt; background-color:#e8e8e8; text-align:center; vertical-align: top; padding:3px;">
            <tr style="background-color:#e8e8e8;">
                <th colspan="2">
                    <bean:message key="appointment.addappointment.msgDemgraphics"/>
                    <a title="Master File" onclick="popup(700,1000,'<%=request.getContextPath() %>/demographic/demographiccontrol.jsp?demographic_no=<%=demoNo%>&amp;displaymode=edit&amp;dboperation=search_detail','master')" href="javascript: function myFunction() {return false; }"><bean:message key="appointment.addappointment.btnEdit"/></a>

                    <bean:message key="appointment.addappointment.msgSex"/>: <%=sex%> &nbsp; <bean:message key="appointment.addappointment.msgDOB"/>: <%=dob%>
                </th>
            </tr>
             <tr style="background-color:#fdfdfd">
                <th style="padding-right: 20px; text-align: left"><bean:message key="appointment.addappointment.msgHin"/>:</th>
                <td><%=hin.replace("null", "")%> </td>
            </tr>
            <tr style="background-color:#f3f6f9">
                <th style="padding-right: 20px; text-align: left"><bean:message key="appointment.addappointment.msgAddress"/>:</th>
                <td><%=StringUtils.trimToEmpty(address)%>, <%=StringUtils.trimToEmpty(city)%>, <%=StringUtils.trimToEmpty(province)%>, <%=StringUtils.trimToEmpty(postal)%></td>
            </tr>
            <tr style="background-color:#fdfdfd">
                <th style="padding-right: 20px; text-align: left"><bean:message key="appointment.addappointment.msgPhone"/>:</th>
                <td><b><bean:message key="appointment.addappointment.msgH"/></b>:<%=StringUtils.trimToEmpty(phone)%> <b><bean:message key="appointment.addappointment.msgW"/></b>:<%=StringUtils.trimToEmpty(phone2)%> </td>
            </tr>
            <tr style="background-color:#f3f6f9; text-align:left">
                <th style="padding-right: 20px"><bean:message key="appointment.addappointment.msgEmail"/>:</th>
                <td><%=StringUtils.trimToEmpty(email)%></td>
            </tr>

        </table>
        <%}%>
    </td>
    <td style="vertical-align: top;">
    <%
        String formTblProp = props.getProperty("appt_formTbl","");
        String[] formTblNames = formTblProp.split(";");

        int numForms = 0;
        for (String formTblName : formTblNames){
            if ((formTblName != null) && !formTblName.equals("")) {
                //form table name defined
                for(EncounterForm ef:encounterFormDao.findByFormTable(formTblName)) {
                    String formName = ef.getFormName();
                    pageContext.setAttribute("formName", formName);
                    boolean formComplete = false;
                    EctFormData.PatientForm[] ptForms = EctFormData.getPatientFormsFromLocalAndRemote(loggedInInfo, demoNo, formTblName);

                    if (ptForms.length > 0) {
                        formComplete = true;
                    }
                    numForms++;
                    if (numForms == 1) {

    %>
            <table style="background-color: #e8e8e8; margin-left:auto; vertical-align: top;padding:3px">
                <tr style="background-color:#f3f6f9">
                    <th colspan="2">
                        <bean:message key="appointment.addappointment.msgFormsSaved"/>
                    </th>
                </tr>
    <%              }%>

                <tr style="background-color:#e8e8e8; text-align:left">
                    <th style="padding-right: 20px"><c:out value="${formName}:"/></th>
    <%              if (formComplete){  %>
                        <td><bean:message key="appointment.addappointment.msgFormCompleted"/></td>
    <%              } else {            %>
                        <td><bean:message key="appointment.addappointment.msgFormNotCompleted"/></td>
    <%              } %>
                </tr>
    <%
                }
            }
        }

        if (numForms > 0) {
    %>
         </table>
    <%  }   %>
    </td>
    <td style="vertical-align: top;">
<table style="font-size: 8pt; background-color:#e9e9e9; margin-left:auto; vertical-align: top;">
	<tr style="background-color:#e8e8e8">
		<th colspan="4"><bean:message key="appointment.addappointment.msgOverview" /></th>
	</tr>
	<tr style="background-color:#fdfdfd">
		<th style="padding-right: 25px"><bean:message key="Appointment.formDate" /></th>
 		<th style="padding-right: 25px"><bean:message key="Appointment.formStartTime" /></th>
		<th style="padding-right: 25px"><bean:message key="appointment.addappointment.msgProvider" /></th>
		<th><bean:message key="appointment.addappointment.msgComments" /></th>
	</tr>
	<%

        int iRow=0;
        if( bFromWL && demoNo != null && demoNo.length() > 0 ) {

            Object [] param2 = new Object[3];
            param2[0] = demoNo;
            Calendar cal2 = Calendar.getInstance();
            param2[1] = new java.sql.Date(cal2.getTime().getTime());
            java.util.Date start = cal2.getTime();
            cal2.add(Calendar.YEAR, 1);
            java.util.Date end = cal2.getTime();
            param2[2] = new java.sql.Date(cal2.getTime().getTime());

            for(Object[] result : appointmentDao.search_appt_future(Integer.parseInt(demoNo), start, end)) {
            	Appointment a = (Appointment)result[0];
            	p = (Provider)result[1];

                iRow ++;
                if (iRow > iPageSize) break;
    %>
	<tr style="background-color:#e8e8e8">
		<td style="background-color: #e8e8e8; padding-right: 25px"><%=ConversionUtils.toDateString(a.getAppointmentDate())%></td>
		<td style="background-color: #e8e8e8; padding-right: 25px"><%=ConversionUtils.toTimeString(a.getStartTime())%></td>
		<td style="background-color: #e8e8e8; padding-right: 25px"><%=p.getFormattedName()%></td>
		<td style="background-color: #e8e8e8;"><%=a.getStatus()==null?"":(a.getStatus().equals("N")?"No Show":(a.getStatus().equals("C")?"Cancelled":"") )%></td>
	</tr>
	<%
            }

            iRow=0;
            cal2 = Calendar.getInstance();
            cal2.add(Calendar.YEAR, -1);

            for(Object[] result : appointmentDao.search_appt_past(Integer.parseInt(demoNo), start, cal2.getTime())) {
            	Appointment a = (Appointment)result[0];
            	p = (Provider)result[1];
                iRow ++;
                if (iRow > iPageSize) break;
    %>
	<tr style="background-color:#e8e8e8">
		<td style="background-color: #e8e8e8; padding-right: 25px"><%=ConversionUtils.toDateString(a.getAppointmentDate())%></td>
		<td style="background-color: #e8e8e8; padding-right: 25px"><%=ConversionUtils.toTimeString(a.getStartTime())%></td>
		<td style="background-color: #e8e8e8; padding-right: 25px"><%=p.getFormattedName()%></td>
		<td style="background-color: #e8e8e8;"><%=a.getStatus()==null?"":(a.getStatus().equals("N")?"No Show":(a.getStatus().equals("C")?"Cancelled":"") )%></td>
	</tr>
	<%
            }
        }
    %>
</table>
</td>
</tr>
</table>
</div>

</body>

</html:html>