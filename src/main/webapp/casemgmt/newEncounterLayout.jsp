
<%--


    Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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

    This software was written for
    Centre for Research on Inner City Health, St. Michael's Hospital,
    Toronto, Ontario, Canada

--%>

<%@ taglib uri="/WEB-INF/caisi-tag.tld" prefix="caisi"%>

<%@ include file="/casemgmt/taglibs.jsp" %>

<%@page import="java.util.Enumeration, org.apache.commons.lang.StringEscapeUtils" %>
<%@page import="org.oscarehr.casemgmt.web.formbeans.*, org.oscarehr.casemgmt.model.CaseManagementNote"%>
<%@page import="org.oscarehr.common.dao.UserPropertyDAO, oscar.OscarProperties" %>
<%@page import="org.oscarehr.common.model.UserProperty" %>
<%@page import="org.oscarehr.util.SpringUtils" %>
<%@page import="org.oscarehr.util.LoggedInInfo" %>
<%@page import="org.oscarehr.casemgmt.common.Colour" %>
<%@page import="org.oscarehr.common.dao.ProviderDataDao" %>
<%@page import="org.oscarehr.common.model.ProviderData"%>
<%@page import="java.util.List"%>


<%
    String roleName = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");

    oscar.oscarEncounter.pageUtil.EctSessionBean bean = null;
    String beanName = "casemgmt_oscar_bean" + (String) request.getAttribute("demographicNo");

    pageContext.setAttribute("providerNo",request.getParameter("providerNo"), PageContext.PAGE_SCOPE);
    pageContext.setAttribute("demographicNo",request.getParameter("demographicNo"), PageContext.PAGE_SCOPE);
    
    org.oscarehr.casemgmt.model.CaseManagementNoteExt cme = new org.oscarehr.casemgmt.model.CaseManagementNoteExt();

    String frmName = "caseManagementEntryForm" + request.getParameter("demographicNo");
	CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean)session.getAttribute(frmName);

    String encTimeMandatoryValue = OscarProperties.getInstance().getProperty("ENCOUNTER_TIME_MANDATORY","false");

%>
<html:html locale="true">
<head>
	<title>
		Encounter
	</title>
<c:set var="ctx" value="${pageContext.request.contextPath}"	scope="request" />
<link rel="stylesheet" href="<c:out value="${ctx}"/>/css/encounterStyles.css" type="text/css">
	<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}"/>/css/print.css" media="print">

<!-- 
<script src="<c:out value="${ctx}/js/jquery.js"/>"></script>
 -->
 <script src="<c:out value="${ctx}/js/jquery-1.7.1.min.js"/>"></script>
<script language="javascript">
     jQuery.noConflict();
</script>

<script src="<c:out value="${ctx}"/>/share/javascript/prototype.js" type="text/javascript"></script>
<script src="<c:out value="${ctx}"/>/share/javascript/scriptaculous.js" type="text/javascript"></script>

<script type="text/javascript" src="<c:out value="${ctx}"/>/js/messenger/messenger.js"> </script>
<script type="text/javascript" src="<c:out value="${ctx}"/>/js/messenger/messenger-theme-future.js"> </script>
<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}"/>/js/messenger/messenger.css"> </link>
<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}"/>/js/messenger/messenger-theme-future.css"> </link>

<script type="text/javascript" src="newEncounterLayout.js.jsp"> </script>
	
<%-- for popup menu of forms --%>
<script src="<c:out value="${ctx}"/>/share/javascript/popupmenu.js" type="text/javascript"></script>
<script src="<c:out value="${ctx}"/>/share/javascript/menutility.js" type="text/javascript"></script>

<!-- library for rounded elements -->
<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}/share/css/niftyCorners.css" />">
<script type="text/javascript" src="<c:out value="${ctx}/share/javascript/nifty.js"/>"></script>

<!-- calendar stylesheet -->
<link rel="stylesheet" type="text/css" media="all" href="<c:out value="${ctx}"/>/share/calendar/calendar.css" title="win2k-cold-1">

<!-- main calendar program -->
<script type="text/javascript" src="<c:out value="${ctx}"/>/share/calendar/calendar.js"></script>

<!-- language for the calendar -->
<script type="text/javascript" src="<c:out value="${ctx}"/>/share/calendar/lang/<bean:message key="global.javascript.calendar"/>"></script>

<!-- the following script defines the Calendar.setup helper function, which makes adding a calendar a matter of 1 or 2 lines of code. -->
<script type="text/javascript" src="<c:out value="${ctx}"/>/share/calendar/calendar-setup.js"></script>

<!-- js window size utility funcs since prototype's funcs are buggy in ie6 -->
<script type="text/javascript" src="<c:out value="${ctx}/share/javascript/screen.js"/>"></script>

<!-- scriptaculous based select box -->
<script type="text/javascript" src="<c:out value="${ctx}/share/javascript/select.js"/>"></script>

<!-- phr popups -->
<%--<script type="text/javascript" src="<c:out value="${ctx}/phr/phr.js"/>"></script>--%>


<link rel="stylesheet" type="text/css" href="<c:out value="${ctx}/css/oscarRx.css" />">


<script type="text/javascript">
var Colour = {
	prevention: '<%=Colour.getInstance().prevention%>',
	tickler: '<%=Colour.getInstance().tickler%>',
	disease: '<%=Colour.getInstance().disease%>',
	forms: '<%=Colour.getInstance().forms%>',
	eForms: '<%=Colour.getInstance().eForms%>',
	documents: '<%=Colour.getInstance().documents%>',
	labs: '<%=Colour.getInstance().labs%>',
	messages: '<%=Colour.getInstance().messages%>',
	measurements: '<%=Colour.getInstance().measurements%>',
	consultation: '<%=Colour.getInstance().consultation%>',
	allergy: '<%=Colour.getInstance().allergy%>',
	rx: '<%=Colour.getInstance().rx%>',
	omed: '<%=Colour.getInstance().omed%>',
	riskFactors: '<%=Colour.getInstance().riskFactors%>',
	familyHistory: '<%=Colour.getInstance().familyHistory%>',
	unresolvedIssues: '<%=Colour.getInstance().unresolvedIssues%>',
	resolvedIssues: '<%=Colour.getInstance().resolvedIssues%>',
	episode: '<%=Colour.getInstance().episode%>',
	pregancies: '<%=Colour.getInstance().episode%>'
};
</script>


<!--js code for newCaseManagementView.jsp -->
<script type="text/javascript" src="<c:out value="${ctx}/js/newCaseManagementView.js.jsp"/>"></script>

		<%-- Javascripts for the BC Care Connect Button --%>
<oscar:oscarPropertiesCheck value="BC" property="billregion">
	<security:oscarSec roleName="<%=roleName%>" objectName="_careconnect" rights="r" >
		<c:set value="${ OscarProperties.getInstance()['BC_CARECONNECT_URL'] }" var="careconnecturl" scope="application" />
		<c:if test="${ not empty careconnecturl }">
			<script type="text/javascript" src="${pageContext.servletContext.contextPath}/careconnect/careconnect.js"></script>
		</c:if>
	</security:oscarSec>
</oscar:oscarPropertiesCheck>

<% if (OscarProperties.getInstance().getBooleanProperty("note_program_ui_enabled", "true")) { %>
	<link rel="stylesheet" href="<c:out value="${ctx}/casemgmt/noteProgram.css" />" />
	<script type="text/javascript" src="<c:out value="${ctx}/casemgmt/noteProgram.js" />"></script>
<% } 

LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
%>

<script type="text/javascript">

    jQuery(document).ready(function() {
           <%
  if( loggedInInfo.getLoggedInProvider().getProviderType().equals("resident"))  {
%>    


    jQuery("input[name='reviewed']").change(function() {
            
            if( jQuery("input[name='reviewed']:checked").val() == "true") {                
                if( jQuery(".supervisor").is(":visible") ) {
                    jQuery(".supervisor").slideUp(300);                    
                }
                jQuery(".reviewer").slideDown(600);
                jQuery("#reviewer").focus();
               
            }
            else {
                if( jQuery(".reviewer").is(":visible") ) {
                    jQuery(".reviewer").slideUp(300);
                }
                jQuery(".supervisor").slideDown(600);
                jQuery("#supervisor").focus();
            }
        }
    
        );
<%}%> 
    });



   function assembleMainChartParams(displayFullChart) {

	   var params = "method=edit&ajaxview=ajaxView&fullChart=" + displayFullChart;
	   <%
		 Enumeration<String>enumerator = request.getParameterNames();
		 String paramName, paramValue;
		 while( enumerator.hasMoreElements() ) {
			paramName = enumerator.nextElement();
			if( paramName.equals("method") || paramName.equals("fullChart") ) {
				continue;
			}

			paramValue = request.getParameter(paramName);

		%>
			params += "&<%=paramName%>=<%=StringEscapeUtils.escapeJavaScript(paramValue)%>";
		<%

		 }
	   %>

	   return params;
   }

   function reorderNavBarElements(idToMove, afterThisId) {
	   var clone = jQuery("#"+idToMove).clone();
       jQuery("#"+idToMove).remove();
       clone.insertAfter(jQuery("#"+afterThisId));
   }

   function reorderNavBarElementsBefore(idToMove, beforeThisId) {
	   var clone = jQuery("#"+idToMove).clone();
       jQuery("#"+idToMove).remove();
       clone.insertBefore(jQuery("#"+beforeThisId));
   }

   function makeElement(type, attributes) {
	   var element = document.createElement(type);
	   if (attributes != null) {
	     for (var i in attributes) {
	       element.setAttribute(i, attributes[i]);
	     }
	   }
	   return element;
	 }

   function insertAfter( referenceNode, newNode )
   {
       referenceNode.parentNode.insertBefore( newNode, referenceNode.nextSibling );
   }


   function addCppRow(rowNumber) {
		if(!document.getElementById("divR" + rowNumber)) {
			var newDiv = makeElement('div',{'id':'divR'+rowNumber});

			var i1 = makeElement('div',{'id':'divR' + rowNumber + 'I1','class':'topBox'});
			var i2 = makeElement('div',{'id':'divR' + rowNumber + 'I2','class':'topBox'});
			newDiv.appendChild(i1);
			newDiv.appendChild(i2);
			var prevRow = document.getElementById("divR"+(rowNumber-1));
			insertAfter(prevRow,newDiv);
		}
   }

   function removeCppRow(rowNumber) {
	   jQuery("#divR"+rowNumber).remove();
   }
   
   function hideCpp(title) {
	   jQuery("#"+title).remove();
   }

   function popColumn(url,div,params, navBar, navBarObj) {
	   params = "reloadURL=" + url + "&numToDisplay=6&cmd=" + params;

       var objAjax = new Ajax.Request (
                           url,
                           {
                               method: 'post',
                               postBody: params,
                               evalScripts: true,
                               onSuccess: function(request) {
                                               $(div).update(request.responseText);

                                               if( $("leftColLoader") != null )
                                                   Element.remove("leftColLoader");

                                               if( $("rightColLoader") != null )
                                                   Element.remove("rightColLoader");
                                          },
                               onFailure: function(request) {
                                               $(div).innerHTML = "<h3>" + div + "</h3>Error: " + request.status;
                                           }
                           }

                     );
       };

       function addLeftNavDiv(name) {
    	   var div = document.createElement("div");
           div.className = "leftBox";
           div.style.display = "block";
           div.style.visiblity = "hidden";
           div.id = name;
           $("leftNavBar").appendChild(div);

       }

       function addRightNavDiv(name) {
    	   var div = document.createElement("div");
           div.className = "leftBox";
           div.style.display = "block";
           div.style.visiblity = "hidden";
           div.id = name;
           $("rightNavBar").appendChild(div);

       }

       function removeNavDiv(name) {
    	   var tmpEl = document.getElementById(name);
           tmpEl.parentNode.removeChild(tmpEl);
       }

       function reloadNav(name) {
    	   var url = jQuery("#" + name + " input[name='reloadUrl']").val();
    	   popColumn(url,name,name,null,null);
       }

       function addPrintOption(name,bean) {
    	   var test1Str = "<img style=\"cursor: pointer;\" title=\"Print "+name+"\" id=\"img"+name+"\" alt=\"Print "+name+"\" onclick=\"return printInfo(this, 'extPrint"+name+"');\" src=\"" + ctx + "/oscarEncounter/graphics/printer.png\">&nbsp;"+name;
           jQuery("#printDateRow").before("<tr><td></td><td>" + test1Str + "</tr></tr>");
           jQuery("form[name='caseManagementEntryForm']").append("<input name=\"extPrint"+name+"\" id=\"extPrint"+name+"\" value=\"false\" type=\"hidden\"/>");
           jQuery.ajax({ url: ctx+"/casemgmt/ExtPrintRegistry.do?method=register&name="+name+"&bean="+bean, async:false, success: function(data){
           }});
       }


       <%if(request.getParameter("appointmentNo") != null && request.getParameter("appointmentNo").length()>0) { %>
       var appointmentNo = <%=request.getParameter("appointmentNo")%>;
        <% } else { %>
        var appointmentNo = 0;
        <%}%>

        var savedNoteId=0;
   </script>

<script type="text/javascript" src=" ${ctx}/jspspellcheck/spellcheck-caller.js" ></script>
<script>
	function spellCheck()
    {
		// Build an array of form elements (not there values)
        var elements = new Array(0);

        // Your form elements that you want to have spell checked
        elements[elements.length] = document.getElementById(caseNote);

        // Start the spell checker
        startSpellCheck(ctx+'/jspspellcheck/',elements);

    }
</script>

<!-- set size of window if customized in preferences -->
<%
	UserPropertyDAO uPropDao = (UserPropertyDAO)SpringUtils.getBean("UserPropertyDAO");
	
	String providerNo=loggedInInfo.getLoggedInProviderNo();
	UserProperty widthP = uPropDao.getProp(providerNo, "encounterWindowWidth");
	UserProperty heightP = uPropDao.getProp(providerNo, "encounterWindowHeight");
	UserProperty maximizeP = uPropDao.getProp(providerNo, "encounterWindowMaximize");

	if(maximizeP != null && maximizeP.getValue().equals("yes")) {
		%><script> jQuery(document).ready(function(){window.resizeTo(screen.width,screen.height);});</script>
<%
	} else if(widthP != null && widthP.getValue().length()>0 && heightP != null && heightP.getValue().length()>0) {
		String width = widthP.getValue();
		String height = heightP.getValue();
		%>
<script> jQuery(document).ready(function(){
    
    window.resizeTo(<%=width%>,<%=height%>);

        
   }   
);

    		

</script>
<%
	}
%>
<oscar:customInterface section="cme" />

<html:base />
<title><bean:message key="oscarEncounter.Index.title" /> -
	<oscar:nameage demographicNo="<%=request.getParameter(\"demographicNo\")%>" /></title>
<script type="text/javascript">
    	ctx = "<c:out value="${ctx}"/>";
    	demographicNo = "<c:out value="${demographicNo}"/>";
    	providerNo = "<c:out value="${providerNo}"/>";

        socHistoryLabel = "oscarEncounter.socHistory.title";
        medHistoryLabel = "oscarEncounter.medHistory.title";
        onGoingLabel = "oscarEncounter.onGoing.title";;
        remindersLabel = "oscarEncounter.reminders.title";
        oMedsLabel = "oscarEncounter.oMeds.title";
        famHistoryLabel = "oscarEncounter.famHistory.title";
        riskFactorsLabel = "oscarEncounter.riskFactors.title";

        diagnosticNotesLabel = "oscarEncounter.eyeform.diagnosticNotes.title";
        pastOcularHistoryLabel = "oscarEncounter.eyeform.pastOcularHistory.title";
        patientLogLabel = "oscarEncounter.eyeform.patientLog.title";
        ocularMedicationsLabel = "oscarEncounter.eyeform.ocularMedications.title";
		currentHistoryLabel = "oscarEncounter.eyeform.currentHistory.title";

		quickChartMsg = "<bean:message key="oscarEncounter.quickChart.msg"/>";
		fullChartMsg = "<bean:message key="oscarEncounter.fullChart.msg"/>";
        insertTemplateError = "<bean:message key="oscarEncounter.templateError.msg"/>";
        unsavedNoteWarning = "<bean:message key="oscarEncounter.unsavedNoteWarning.msg"/>";
        sessionExpiredError = "<bean:message key="oscarEncounter.sessionExpiredError.msg"/>";
        unlockNoteError = "<bean:message key="oscarEncounter.unlockNoteError.msg"/>";
        filterError = "<bean:message key="oscarEncounter.filterError.msg"/>";
        pastObservationDateError = "<bean:message key="oscarEncounter.pastObservationDateError.msg"/>";
        encTimeError = "<bean:message key="oscarEncounter.encounterTimeError.msg"/>";
        encMinError = "<bean:message key="oscarEncounter.encounterMinuteError.msg"/>";
        assignIssueError = "<bean:message key="oscarEncounter.assignIssueError.msg"/>";
        assignObservationDateError = "<bean:message key="oscarEncounter.assignObservationDateError.msg"/>";
      
        encTimeMandatoryMsg = "<bean:message key="oscarEncounter.encounterTimeMandatory.msg"/>";
		encTimeMandatory = <%=encTimeMandatoryValue%>;

        assignEncTypeError = "<bean:message key="oscarEncounter.assignEncTypeError.msg"/>";
        savingNoteError = "<bean:message key="oscarEncounter.savingNoteError.msg"/>";
        changeIssueMsg = "<bean:message key="oscarEncounter.change.title"/>";
        closeWithoutSaveMsg = "<bean:message key="oscarEncounter.closeWithoutSave.msg"/>";
        pickIssueMsg = "<bean:message key="oscarEncounter.pickIssue.msg"/>";
        assignIssueMsg = "<bean:message key="oscarEncounter.assign.title"/>";
        updateIssueError = "<bean:message key="oscarEncounter.updateIssueError.msg"/>";
        unsavedNoteMsg = "<bean:message key="oscarEncounter.unsavedNote.msg"/>";
        printDateMsg = "<bean:message key="oscarEncounter.printDate.msg"/>";
        printDateOrderMsg = "<bean:message key="oscarEncounter.printDateOrder.msg"/>";
        nothing2PrintMsg = "<bean:message key="oscarEncounter.nothingToPrint.msg"/>";
        editUnsignedMsg = "<bean:message key="oscarEncounter.editUnsignedNote.msg"/>";
        msgDraftSaved = "<bean:message key="oscarEncounter.draftSaved.msg"/>";
        msgPasswd = "<bean:message key="Logon.passWord"/>";
        btnMsgUnlock = "<bean:message key="oscarEncounter.Index.btnUnLock"/>";
        editLabel = "<bean:message key="oscarEncounter.edit.msgEdit"/>";
        annotationLabel = "<bean:message key="oscarEncounter.Index.btnAnnotation"/>";
        month[0] = "<bean:message key="share.CalendarPopUp.msgJan"/>";
        month[1] = "<bean:message key="share.CalendarPopUp.msgFeb"/>";
        month[2] = "<bean:message key="share.CalendarPopUp.msgMar"/>";
        month[3] = "<bean:message key="share.CalendarPopUp.msgApr"/>";
        month[4] = "<bean:message key="share.CalendarPopUp.msgMay"/>";
        month[5] = "<bean:message key="share.CalendarPopUp.msgJun"/>";
        month[6] = "<bean:message key="share.CalendarPopUp.msgJul"/>";
        month[7] = "<bean:message key="share.CalendarPopUp.msgAug"/>";
        month[8] = "<bean:message key="share.CalendarPopUp.msgSep"/>";
        month[9] = "<bean:message key="share.CalendarPopUp.msgOct"/>";
        month[10] = "<bean:message key="share.CalendarPopUp.msgNov"/>";
        month[11] = "<bean:message key="share.CalendarPopUp.msgDec"/>";

function init() {
	//scrollDownInnerBar();
	viewFullChart(false);
    showIssueNotes();

    var navBars = new navBarLoader();
    navBars.load();

    monitorNavBars(null);

    Element.observe(window, "resize", monitorNavBars);
    
    if(!NiftyCheck()) {
        return;
    }

    Rounded("div.showEdContent","all","transparent","#CCCCCC","big border #000000");
    Rounded("div.printOps","all","transparent","#CCCCCC","big border #000000");
    Calendar.setup({ inputField : "printStartDate", ifFormat : "%d-%b-%Y", showsTime :false, button : "printStartDate_cal", singleClick : true, step : 1 });
    Calendar.setup({ inputField : "printEndDate", ifFormat : "%d-%b-%Y", showsTime :false, button : "printEndDate_cal", singleClick : true, step : 1 });

    <c:url value="/CaseManagementEntry.do" var="issueURLCPP">
        <c:param name="method" value="issueList"/>
        <c:param name="demographicNo" value="${demographicNo}" />
        <c:param name="providerNo" value="${providerNo}" />
        <c:param name="all" value="true" />
    </c:url>
    var issueAutoCompleterCPP = new Ajax.Autocompleter("issueAutocompleteCPP", "issueAutocompleteListCPP", "<c:out value="${issueURLCPP}" />", {minChars: 3, indicator: 'busy2', afterUpdateElement: addIssue2CPP, onShow: autoCompleteShowMenuCPP, onHide: autoCompleteHideMenuCPP});    
    
    <nested:notEmpty name="DateError">
        alert("<nested:write name="DateError"/>");
    </nested:notEmpty>
}

function doscroll(){
	x=document.body.scrollHeight;
	x=x+99999
	window.scrollTo(0,x);
}
	
window.onbeforeunload = onClosing;


</script>
</head>
<body id="body">
<div id="newEncounterLayoutWrapper">
	<%--
	<caisi:isModuleLoad moduleName="eaaps.enabled">
		<div id="eaaps" style="display: none;">
			  <div id="basic-template">
		      <a class="ui-notify-cross ui-notify-close" href="#">x</a>
		      <h1>TITLE</h1>
		      <p>text</p>
	   </div>

			<!-- jsp : include page="/eaaps/status.jsp">< / jsp : include -->
		</div>
	</caisi:isModuleLoad>
	--%>
	 
	<div id="header" class="row">
		<tiles:insert attribute="header" />
	</div>

	<div id="navigation-layout" class="row">
		<div id="rightNavBar">
			<tiles:insert attribute="rightNavigation" />
		</div>

		<div id="leftNavBar" >
			<tiles:insert attribute="leftNavigation" />
		</div>

		<div id="content">
			<tiles:insert attribute="body" />
		</div>
	</div>
	<!-- hovering divs -->
	<div id="showEditNote" class="showEdContent">
		<form id="frmIssueNotes" action="" method="post"
			onsubmit="return updateCPPNote();">
			<input type="hidden" id="reloadUrl" name="reloadUrl" value="">
			<input type="hidden" id="containerDiv" name="containerDiv" value="">
			<input type="hidden" id="issueChange" name="issueChange" value="">
			<input type="hidden" id="archived" name="archived" value="false">
			<input type="hidden" id="annotation_attrib" name="annotation_attrib">
			<div id="winTitle"></div>
			<textarea style="margin: 10px;" cols="50" rows="15" id="noteEditTxt"
				name="value"></textarea>
			<br>

			<table>
				<tr id="Itemproblemdescription">
					<td><bean:message
							key="oscarEncounter.problemdescription.title" />:</td>
					<td><input type="text" id="problemdescription"
						name="problemdescription" value=""></td>
				</tr>
				<tr id="Itemstartdate">
					<td><bean:message key="oscarEncounter.startdate.title" />:</td>
					<td><input type="text" id="startdate" name="startdate"
						value="" size="12"> (YYYY-MM-DD)</td>
				</tr>
				<tr id="Itemresolutiondate">
					<td><bean:message key="oscarEncounter.resolutionDate.title" />:
					</td>
					<td><input type="text" id="resolutiondate"
						name="resolutiondate" value="" size="12"> (YYYY-MM-DD)</td>
				</tr>
				<tr id="Itemprocedure">
					<td><bean:message
							key="oscarEncounter.procedure.title" />:</td>
					<td><input type="text" id="procedure"
						name="procedure" value=""></td>
				</tr>
				<tr id="Itemageatonset">
					<td><bean:message key="oscarEncounter.ageAtOnset.title" />:</td>
					<td><input type="text" id="ageatonset" name="ageatonset"
						value="" size="2"></td>
				</tr>
				
				<tr id="Itemproceduredate">
					<td><bean:message key="oscarEncounter.procedureDate.title" />:
					</td>
					<td><input type="text" id="proceduredate" name="proceduredate"
						value="" size="12"> (YYYY-MM-DD)</td>
				</tr>
				<tr id="Itemtreatment">
					<td><bean:message key="oscarEncounter.treatment.title" />:</td>
					<td><input type="text" id="treatment" name="treatment"
						value=""></td>
				</tr>
				<tr id="Itemproblemstatus">
					<td><bean:message key="oscarEncounter.problemStatus.title" />:
					</td>
					<td><input type="text" id="problemstatus" name="problemstatus"
						value="" size="8"> <bean:message
							key="oscarEncounter.problemStatusExample.msg" /></td>
				</tr>
				<tr id="Itemexposuredetail">
					<td><bean:message key="oscarEncounter.exposureDetail.title" />:
					</td>
					<td><input type="text" id="exposuredetail"
						name="exposuredetail" value=""></td>
				</tr>
				<tr id="Itemrelationship">
					<td><bean:message key="oscarEncounter.relationship.title" />:
					</td>
					<td><input type="text" id="relationship" name="relationship"
						value=""></td>
				</tr>
				<tr id="Itemlifestage">
					<td><bean:message key="oscarEncounter.lifestage.title" />:</td>
					<td><select name="lifestage" id="lifestage">
							<option value="">
								<bean:message key="oscarEncounter.lifestage.opt.notset" />
							</option>
							<option value="N">
								<bean:message key="oscarEncounter.lifestage.opt.newborn" />
							</option>
							<option value="I">
								<bean:message key="oscarEncounter.lifestage.opt.infant" />
							</option>
							<option value="C">
								<bean:message key="oscarEncounter.lifestage.opt.child" />
							</option>
							<option value="T">
								<bean:message key="oscarEncounter.lifestage.opt.adolescent" />
							</option>
							<option value="A">
								<bean:message key="oscarEncounter.lifestage.opt.adult" />
							</option>
					</select></td>
				</tr>
				<tr id="Itemhidecpp">
					<td><bean:message key="oscarEncounter.hidecpp.title" />:</td>
					<td><select id="hidecpp" name="hidecpp">
							<option value="0">No</option>
							<option value="1">Yes</option>
					</select></td>
				</tr>
			</table>
			<input type="hidden" id="startTag" value='<bean:message key="oscarEncounter.Index.startTime"/>'>
			<input type="hidden" id="endTag" value='<bean:message key="oscarEncounter.Index.endTime"/>'>
			<br> <span style="float: right; margin-right: 10px;"> <input
				type="image"
				src="<c:out value="${ctx}/oscarEncounter/graphics/copy.png"/>"
				title='<bean:message key="oscarEncounter.Index.btnCopy"/>'
				onclick="copyCppToCurrentNote(); return false;"> <input
				type="image"
				src="<c:out value="${ctx}/oscarEncounter/graphics/annotation.png"/>"
				title='<bean:message key="oscarEncounter.Index.btnAnnotation"/>'
				id="anno" style="padding-right: 10px;"> <input type="image"
				src="<c:out value="${ctx}/oscarEncounter/graphics/edit-cut.png"/>"
				title='<bean:message key="oscarEncounter.Index.btnArchive"/>'
				onclick="$('archived').value='true';" style="padding-right: 10px;">
				<input type="image"
				src="<c:out value="${ctx}/oscarEncounter/graphics/note-save.png"/>"
				title='<bean:message key="oscarEncounter.Index.btnSignSave"/>'
				onclick="$('archived').value='false';" style="padding-right: 10px;">
				<input type="image"
				src="<c:out value="${ctx}/oscarEncounter/graphics/system-log-out.png"/>"
				title='<bean:message key="global.btnExit"/>'
				onclick="this.focus();$('channel').style.visibility ='visible';$('showEditNote').style.display='none';return false;">
			</span>
			<bean:message key="oscarEncounter.Index.btnPosition" />
			<select id="position" name="position"><option id="popt0"
					value="0">1</option>
			</select>
			<div id="issueNoteInfo" style="clear: both; text-align: left;"></div>
			<div id="issueListCPP"
				style="background-color: #FFFFFF; height: 200px; width: 350px; position: absolute; z-index: 1; display: none; overflow: auto;">
				<div class="enTemplate_name_auto_complete"
					id="issueAutocompleteListCPP"
					style="position: relative; left: 0px; display: none;"></div>
			</div>
			<bean:message key="oscarEncounter.Index.assnIssue" />
			&nbsp;<input tabindex="100" type="text" id="issueAutocompleteCPP"
				name="issueSearch" style="z-index: 2;" size="25">&nbsp; <span
				id="busy2" style="display: none"><img
				style="position: absolute;"
				src="<c:out value="${ctx}/oscarEncounter/graphics/busy.gif"/>"
				alt="<bean:message key="oscarEncounter.Index.btnWorking"/>"></span>

		</form>
	</div>
	<div id="printOps" class="printOps">
		<h3 style="margin-bottom: 5px; text-align: center;">
			<bean:message key="oscarEncounter.Index.PrintDialog" />
		</h3>
		<form id="frmPrintOps" action="" onsubmit="return false;">
			<table id="printElementsTable">
				<tr>
					<td><input type="radio" id="printopSelected" name="printop"
						value="selected">
					<bean:message key="oscarEncounter.Index.PrintSelect" /></td>
					<td>
						<security:oscarSec roleName="<%=roleName%>"
							objectName="_newCasemgmt.cpp" rights="r" reverse="false">
							<img style="cursor: pointer;"
								title="<bean:message key="oscarEncounter.print.title"/>"
								id='imgPrintCPP'
								alt="<bean:message key="oscarEncounter.togglePrintCPP.title"/>"
								onclick="return printInfo(this,'printCPP');"
								src='<c:out value="${ctx}"/>/oscarEncounter/graphics/printer.png'>&nbsp;<bean:message
								key="oscarEncounter.cpp.title" />
						</security:oscarSec>
					</td>
				</tr>
				<tr>
					<td><input type="radio" id="printopAll" name="printop"
						value="all">
					<bean:message key="oscarEncounter.Index.PrintAll" /></td>
					<td><img style="cursor: pointer;"
						title="<bean:message key="oscarEncounter.print.title"/>"
						id='imgPrintRx'
						alt="<bean:message key="oscarEncounter.togglePrintRx.title"/>"
						onclick="return printInfo(this, 'printRx');"
						src='<c:out value="${ctx}"/>/oscarEncounter/graphics/printer.png'>&nbsp;<bean:message
							key="oscarEncounter.Rx.title" /></td>
				</tr>
				<tr>
					<td></td>
					<td><img style="cursor: pointer;"
						title="<bean:message key="oscarEncounter.print.title"/>"
						id='imgPrintLabs'
						alt="<bean:message key="oscarEncounter.togglePrintLabs.title"/>"
						onclick="return printInfo(this, 'printLabs');"
						src='<c:out value="${ctx}"/>/oscarEncounter/graphics/printer.png'>&nbsp;<bean:message
							key="oscarEncounter.Labs.title" /></td>
				</tr>
				<tr>
					<td></td>
					<td><img style="cursor: pointer;"
						title="<bean:message key="oscarEncounter.print.title"/>"
						id='imgPrintPreventions'
						alt="<bean:message key="oscarEncounter.togglePrintPreventions.title"/>"
						onclick="return printInfo(this, 'printPreventions');"
						src='<c:out value="${ctx}"/>/oscarEncounter/graphics/printer.png'>&nbsp;<bean:message
							key="oscarEncounter.Preventions.title" /></td>
				</tr>
				<!--  extension point -->
				<tr id="printDateRow">
					<td><input type="radio" id="printopDates" name="printop"
						value="dates">
					<bean:message key="oscarEncounter.Index.PrintDates" />&nbsp;<a
						style="font-variant: small-caps;" href="#"
						onclick="return printToday(event);"><bean:message
								key="oscarEncounter.Index.PrintToday" /></a></td>
					<td></td>
				</tr>
			</table>

			<div style="float: left; margin-left: 5px; width: 30px;">
				<bean:message key="oscarEncounter.Index.PrintFrom" />
				:
			</div>
			<img src="<c:out value="${ctx}/images/cal.gif" />"
				id="printStartDate_cal" alt="calendar">&nbsp;<input
				type="text" id="printStartDate" name="printStartDate"
				ondblclick="this.value='';"
				style="font-style: italic; border: 1px solid #7682b1; width: 125px; background-color: #FFFFFF;"
				readonly value=""><br>
			<div style="float: left; margin-left: 5px; width: 30px;">
				<bean:message key="oscarEncounter.Index.PrintTo" />
				:
			</div>
			<img src="<c:out value="${ctx}/images/cal.gif" />"
				id="printEndDate_cal" alt="calendar">&nbsp;<input type="text"
				id="printEndDate" name="printEndDate" ondblclick="this.value='';"
				style="font-style: italic; border: 1px solid #7682b1; width: 125px; background-color: #FFFFFF;"
				readonly value=""><br>
			<div style="margin-top: 5px; text-align: center">
				<input type="submit" id="printOp" style="border: 1px solid #7682b1;"
					value="Print" onclick="return printNotes();">
				
					<indivo:indivoRegistered
						demographic="<%=(String) request.getAttribute(\"demographicNo\")%>"
						provider="<%=(String) request.getSession().getAttribute(\"user\")%>">
						<input type="submit" id="sendToPhr"
							style="border: 1px solid #7682b1;" value="Send To Phr"
							onclick="return sendToPhrr();">
					</indivo:indivoRegistered>
				<input type="submit" id="cancelprintOp"
					style="border: 1px solid #7682b1;" value="Cancel"
					onclick="$('printOps').style.display='none';"> <input
					type="submit" id="clearprintOp" style="border: 1px solid #7682b1;"
					value="Clear"
					onclick="$('printOps').style.display='none'; return clearAll(event);">
			</div>

			<%
if (OscarProperties.getInstance().getBooleanProperty("note_program_ui_enabled", "true")) {
%>
			<span class="popup" style="display: none;" id="_program_popup">
				<div class="arrow"></div>
				<div class="contents">
					<div class="selects">
						<select class="selectProgram"></select> <select class="role"></select>
					</div>
					<div class="under">
						<div class="errorMessage"></div>
						<input type="button" class="scopeBtn" value="View Note Scope" />
						<input type="button" class="closeBtn" value="Close" /> <input
							type="button" class="saveBtn" value="Save" />
					</div>
				</div>
			</span>

			<div id="_program_scope" class="_program_screen"
				style="display: none;">
				<div class="_scopeBox">
					<div class="boxTitle">
						<span class="text">Note Permission Summary</span><span
							class="uiBigBarBtn"><span class="text">x</span></span>
					</div>
					<table class="details">
						<tr>
							<th>Program Name (of this note)</th>
							<td class="programName">...</td>
						</tr>
						<tr>
							<th>Role Name (of this note)</th>
							<td class="roleName">...</td>
						</tr>
					</table>
					<div class="explanation">The following is a summary of what
						kind of access providers in the above program have to this note.</div>
					<div class="loading">Loading...</div>
					<table class="permissions"></table>
				</div>
			</div>
			<%
}
%>
		</form>
	</div>
</div>
<div id="encounterModal" ></div>
<%
    String apptNo = request.getParameter("appointmentNo");
    if( OscarProperties.getInstance().getProperty("resident_review", "false").equalsIgnoreCase("true") && 
            loggedInInfo.getLoggedInProvider().getProviderType().equals("resident") && !"null".equalsIgnoreCase(apptNo) && !"".equalsIgnoreCase(apptNo)) {
        ProviderDataDao providerDao = SpringUtils.getBean(ProviderDataDao.class);
        List<ProviderData> providerList = providerDao.findAllBilling("1");
%>
                <div id="showResident" class="showResident">
                    
                        <div class="showResidentBorder residentText">
                            Resident Check List
                        
                        <form action="" id="resident" name="resident" onsubmit="return false;">
                            <input type="hidden" name="residentMethod" id="residentMethod" value="">
                            <input type="hidden" name="residentChain" id="residentChain" value="">
                            <table class="showResidentContent">
                            <tr>
                                <td>
                                    Was this encounter reviewed?
                                </td>
                                <td>
                                    Yes <input type="radio" value="true" name="reviewed">&nbsp;No <input type="radio" value="false" name="reviewed">
                                </td>
                            </tr>
                            <tr class="reviewer" style="display:none">
                                <td class="residentText">
                                    Who did you review the encounter with?
                                </td>
                                <td>
                                    <select id="reviewer" name="reviewer">
                                        <option value="">Choose Reviewer</option>
                                        <%
                                            for( ProviderData p : providerList ) {
                                        %>
                                        <option value="<%=p.getId()%>"><%=p.getLastName() + ", " + p.getFirstName()%></option>
                                        <%
                                            }
                                        %>
                                    </select>
                                </td>
                            </tr>
                            <tr class="supervisor" style="display:none">
                                <td class="residentText">
                                    Who is your Supervisor/Monitor for this encounter?
                                </td>
                                <td>
                                    <select id="supervisor" name="supervisor">
                                        <option value="">Choose Supervisor</option>
                                        <%
                                            for( ProviderData p : providerList ) {
                                        %>
                                        <option value="<%=p.getId()%>"><%=p.getLastName() + ", " + p.getFirstName()%></option>
                                        <%
                                            }
                                        %>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2">
                                    <input id="submitResident" value="Continue" name="submitResident" type="submit" onclick="return subResident();"/>
                                    <input id="submitResident" value="Return to Chart" name="submitResident" type="submit" onclick="return cancelResident();"/>                                
                                </td>                                
                            </tr>
                        </table>
                        </form>
                        </div>
                    
                </div>
 <%}%>

</body>
</html:html>
