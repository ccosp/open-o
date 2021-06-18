<%--

    Copyright (c) 2008-2012 Indivica Inc.

    This software is made available under the terms of the
    GNU General Public License, Version 2, 1991 (GPLv2).
    License details are available via "indivica.ca/gplv2"
    and "gnu.org/licenses/gpl-2.0.html".

--%>

<%@ page import="java.util.*" %>
<%@ page import="oscar.oscarMDS.data.*,oscar.OscarProperties" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%
      String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
	  boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_lab" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect(request.getContextPath() + "/securityError.jsp?type=_lab");%>
</security:oscarSec>
<%
if(!authed) {
	return;
}
%>

<%
//TODO all of this below needs to be removed. It is unsafe. Consider using proper JSTL.  
@SuppressWarnings("unchecked")
ArrayList<PatientInfo> patients = (ArrayList<PatientInfo>) request.getAttribute("patients");
if (patients!=null) {
	Collections.sort(patients);
}
Integer unmatchedDocs   = (Integer) request.getAttribute("unmatchedDocs");
Integer unmatchedLabs   = (Integer) request.getAttribute("unmatchedLabs");
Integer totalDocs		= (Integer) request.getAttribute("totalDocs");
Integer totalLabs 		= (Integer) request.getAttribute("totalLabs");
if (totalLabs==null) { totalLabs = 0;}
Integer abnormalCount 	= (Integer) request.getAttribute("abnormalCount");
Integer normalCount 	= (Integer) request.getAttribute("normalCount");
Integer totalNumDocs    = (Integer) request.getAttribute("");
Long categoryHash       = (Long) request.getAttribute("categoryHash");
String  providerNo		= (String) request.getAttribute("providerNo");
String searchProviderNo = (String) request.getAttribute("searchProviderNo");
String demographicNo	= (String) request.getAttribute("demographicNo");
String ackStatus 		= (String) request.getAttribute("ackStatus");
String abnormalStatus   = (String) request.getAttribute("abnormalStatus");

String selectedCategory        = request.getParameter("selectedCategory");
if ("normalOnly".equals(abnormalStatus)) {
    selectedCategory = "4";
} else if ("abnormalOnly".equals(abnormalStatus)) {
    selectedCategory = "5";
}
String selectedCategoryPatient = request.getParameter("selectedCategoryPatient");
String selectedCategoryType    = request.getParameter("selectedCategoryType");
String isListView			   = request.getParameter("isListView");
String currentProviderNo 	   = request.getParameter("providerNo");

String patientFirstName    = (String) request.getAttribute("patientFirstName");
String patientLastName     = (String) request.getAttribute("patientLastName");
String patientHealthNumber = (String) request.getAttribute("patientHealthNumber");

String startDate = (String) request.getAttribute("startDate");
String endDate = (String) request.getAttribute("endDate");

%>

<!DOCTYPE html>
<html>
<head>
<title>
<bean:message key="oscarMDS.index.title"/>
</title>
<html:base/>
	<link rel="stylesheet" type="text/css" media="all" href="${pageContext.servletContext.contextPath}/library/jquery/jquery-ui.theme-1.12.1.min.css" />
	<link rel="stylesheet" type="text/css" media="all" href="${pageContext.servletContext.contextPath}/library/jquery/jquery-ui-1.12.1.min.css" />
	<link rel="stylesheet" type="text/css" media="all" href="${pageContext.servletContext.contextPath}/library/jquery/jquery-ui.structure-1.12.1.min.css" />
	<link rel="stylesheet" type="text/css" media="all" href="${pageContext.servletContext.contextPath}/css/showDocument.css" />
	<link rel="stylesheet" type="text/css" media="all" href="${pageContext.servletContext.contextPath}/share/css/oscarMDSIndex.css"  />
	<link rel="stylesheet" type="text/css" media="all" href="${pageContext.servletContext.contextPath}/oscarMDS/encounterStyles.css">
	<link rel="stylesheet" type="text/css" media="all" href="${pageContext.servletContext.contextPath}/share/calendar/calendar.css" title="win2k-cold-1" />
    	
	<style type="text/css">
		.multiPage {
			background-color: RED;
			color: WHITE;
			font-weight:bold;
			padding: 0px 5px;
			font-size: medium;
		}

		#ticklerWrap{
			position:relative;
			top:0px;
			background-color:#FF6600;
			width:100%;
		}  
		
		.completedTickler{
		    opacity: 0.8;
		    filter: alpha(opacity=80); /* For IE8 and earlier */
		}
		
		@media print { 
		.DoNotPrint{display:none;}
		}  	
	
		.TDISRes {
			font-weight: bold; 
			font-size: 10pt; 
			color: black; 
			font-family: Verdana, Arial, Helvetica
		}
	</style>
</head>

<body vlink="#0000FF" >
<div id="inbox_wrapper">
    <form name="reassignForm" method="post" action="ReportReassign.do" id="lab_form">
        <table>
            <tr>
                <td class="MainTableTopRowRightColumn" align="left">
                 <table>
                        <tr>
                            <td align="left" valign="top" >
                                <input type="hidden" name="providerNo" value="<%= providerNo %>" />
                                <input type="hidden" name="searchProviderNo" value="<%= searchProviderNo %>" />
                                <%= (request.getParameter("lname") == null ? "" : "<input type=\"hidden\" name=\"lname\" value=\""+request.getParameter("lname")+"\">") %>
                                <%= (request.getParameter("fname") == null ? "" : "<input type=\"hidden\" name=\"fname\" value=\""+request.getParameter("fname")+"\">") %>
                                <%= (request.getParameter("hnum") == null ? "" : "<input type=\"hidden\" name=\"hnum\" value=\""+request.getParameter("hnum")+"\">") %>
                                <input type="hidden" name="status" value="<%= ackStatus %>" />
                                <input type="hidden" name="selectedProviders" />
                                <input type="hidden" name="favorites" value="" />
                                <input type="hidden" name="isListView" value="" />
                                <input id="listSwitcher" type="button" style="display:none;" class="smallButton" value="<bean:message key="inboxmanager.document.listView"/>" onClick="switchView();" />
                                <input id="readerSwitcher" type="button" class="smallButton" value="<bean:message key="inboxmanager.document.readerView"/>" onClick="switchView();" />
                                <% if (demographicNo == null) { %>
                                    <input type="button" class="smallButton" value="<bean:message key="oscarMDS.index.btnSearch"/>" onClick="window.location='${pageContext.servletContext.contextPath}/oscarMDS/Search.jsp?providerNo=<%= providerNo %>'" />
                                <% } %>
                                <input type="button" class="smallButton" value="<bean:message key="oscarMDS.index.btnClose"/>" onClick="wrapUp()" />
                      		</td>

                            <td align="right" valign="top">
                                <a href="javascript:parent.reportWindow('${pageContext.servletContext.contextPath}/oscarMDS/ForwardingRules.jsp?providerNo=<%= providerNo %>');" style="color: #FFFFFF;" >Forwarding Rules</a>
                                <a href="javascript:popupStart(800,1000,'${pageContext.servletContext.contextPath}/lab/CA/ALL/testUploader.jsp')" style="color: #FFFFFF; "><bean:message key="admin.admin.hl7LabUpload"/></a>
                                <% if (OscarProperties.getInstance().getBooleanProperty("legacy_document_upload_enabled", "true")) { %>
                                	<a href="javascript:popupStart(600,500,'${pageContext.servletContext.contextPath}/dms/html5AddDocuments.jsp')" style="color: #FFFFFF; "><bean:message key="inboxmanager.document.uploadDoc"/></a>
                                <% } else { %>
                                	<a href="javascript:popupStart(800,1000,'${pageContext.servletContext.contextPath}/dms/documentUploader.jsp')" style="color: #FFFFFF; "><bean:message key="inboxmanager.document.uploadDoc"/></a>
                                	
                    <%--    Soon:         	<a href="javascript:void(0)" style="color:white;" class="dialog-link" id="/dms/documentUploader.jsp" >
                                		<bean:message key="inboxmanager.document.uploadDoc" />
                                	</a> --%>
                                <% } %>
								
								<a href="javascript:popupStart(700,1100,'../dms/inboxManage.do?method=getDocumentsInQueues')" style="color: #FFFFFF;"><bean:message key="inboxmanager.document.pendingDocs"/></a>
								
								<a href="javascript:popupStart(800,1200,'${pageContext.servletContext.contextPath}/dms/incomingDocs.jsp')" style="color: #FFFFFF;" ><bean:message key="inboxmanager.document.incomingDocs"/></a>
									                                
								<% if (! OscarProperties.getInstance().isBritishColumbiaBillingRegion()) { %>
									<a href="javascript:popupStart(800,1000, '${pageContext.servletContext.contextPath}/oscarMDS/CreateLab.jsp')" style="color: #FFFFFF;"><bean:message key="global.createLab" /></a>
	                                <a href="javascript:popupStart(800,1000, '${pageContext.servletContext.contextPath}/olis/Search.jsp')" style="color: #FFFFFF;"><bean:message key="olis.olisSearch" /></a>
	                                <a href="javascript:popupPage(400, 400,'<html:rewrite page="/hospitalReportManager/hospitalReportManager.jsp"/>')" style="color: #FFFFFF;">HRM Status/Upload</a>
								<% } %>
								
								<span class="HelpAboutLogout">
									<oscar:help keywords="&Title=Inbox&portal_type%3Alist=Document" key="app.top1" style="color: #FFFFFF"/>
                                	<a href="javascript:popupStart(300,400,'${pageContext.servletContext.contextPath}/oscarEncounter/About.jsp')" style="color: #FFFFFF;" ><bean:message key="global.about"/></a>
								</span>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>

        <table id="readerViewTable" style="table-layout: fixed;background-color: #E0E1FF" >
                                                     <col width="175">
                                                     <col width="100%">
          <tr>
              <td id="categoryList" valign="top" style="background-color: #E0E1FF" >

			<input type="hidden" id="categoryHash" value="<%=categoryHash%>" />
               <div class="documentSummaryList">
               	<c:if test="${ totalNumDocs gt 0}" >
               		<details id="masterDocumentSummary" ${ param.providerNo gt 0 ? 'open' : ''}>    
                        <summary>
                        	<a id="totalAll" href="javascript:void(0);" onclick="un_bold(this);changeView(CATEGORY_ALL);">
                        		All (<span id="totalNumDocs"><c:out value="${totalNumDocs}" /></span>)
                        	</a>
                        </summary>
                        <ul>
    
						<c:if test="${ totalDocs gt 0 }">
							<li>
								<a id="totalDocs" href="javascript:void(0);" onclick="un_bold(this);changeView(CATEGORY_DOCUMENTS);"
								   title="Documents">Documents (<span id="totalDocsNum"><c:out value="${totalDocs}" /></span>)
							   </a>
						   </li>
					   </c:if>

                     <c:if test="${ totalLabs  gt 0 }">
                       <li>
                            <a id="totalHL7s" href="javascript:void(0);" onclick="un_bold(this);changeView(CATEGORY_HL7);" title="HL7">
                           		HL7 (<span id="totalHL7Num"><c:out value="${totalLabs}" /></span>)
                           	</a>
                       </li>
					</c:if>
		
					    <li>
					    	<a id="totalNormals" href="javascript:void(0);" onclick="un_bold(this);changeView(CATEGORY_NORMAL);" title="Normal">
					    		Normal
				    		</a>
		    			</li>

						<li>
    						<a id="totalAbnormals" href="javascript:void(0);" onclick="un_bold(this);changeView(CATEGORY_ABNORMAL);" title="Abnormal">
    							Abnormal
   							</a>
						</li>
					</ul>
					</details>
				</c:if>	
	
				<c:if test="${ unmatchedDocs gt 0 or unmatchedLabs gt 0 }">	
					<details class="unmatchedList" ${ param.providerNo eq 0 ? 'open' : '' }>
						<summary>
       						<a id="patient0all" href="javascript:void(0);"  
       							onclick="un_bold(this);changeView(CATEGORY_PATIENT,0)" 
       							title="Unmatched">Unmatched (<span id="patientNumDocs0"><c:out value="${unmatchedDocs+unmatchedLabs}" /></span>)
       						</a>
                    	</summary> 
                    		
                    		<ul id="labdoc0showSublist" >
                    				<c:if test="${ unmatchedDocs gt 0}" >   
			                        		<li>
			                        			<a id="patient0docs" href="javascript:void(0);" onclick="un_bold(this);changeView(CATEGORY_PATIENT_SUB,0,CATEGORY_TYPE_DOC);" title="Documents">
			                        				Documents (<span id="pDocNum_0"><c:out value="${unmatchedDocs}" /></span>)
			                       				</a>
			                        		</li>
			                        </c:if>
			                        <c:if test="${ unmatchedLabs gt 0 }" >
			                     			<li>
			                     				<a id="patient0hl7s" href="javascript:void(0);" onclick="un_bold(this);changeView(CATEGORY_PATIENT_SUB,0,CATEGORY_TYPE_HL7);" title="HL7">
			                     					HL7 (<span id="pLabNum_0"><c:out value="${unmatchedLabs}" /></span>)
			                   					</a>
			                        		</li>
			                        </c:if>
			                   
			                 </ul>
			            </details>
				</c:if>
			
				<% if(patients.size() > 0) { %>	
									
				<div id="patientsdoclabs">
				<details id="patientsdoclabsDetails" open>
					<summary id="patientsdoclabsSummary" >Matched</summary>
				
				    <%
				         for (PatientInfo info : patients) {
				                        String patientId= info.id + "";
				                        String patientName= info.toString();
				                        int numDocs= info.getDocCount() + info.getLabCount();
				   %>
					<details id="patientsdoclabsDetailList" >
   					   <summary>
       						<a id="patient<%=patientId%>all" href="javascript:void(0);"  onclick="un_bold(this);changeView(CATEGORY_PATIENT,<%=patientId%>);" 
       							title="<%=patientName%>" >
       								<%=patientName%> (<span id="patientNumDocs<%=patientId%>"><%=numDocs%></span>)
       						</a>
       					</summary>
                    		<ul id="labdoc<%=patientId%>showSublist" >
				                   <%if (info.getDocCount() > 0) {%>
				                        		<li>
				                        			<a id="patient<%=patientId%>docs" href="javascript:void(0);" onclick="un_bold(this);changeView(CATEGORY_PATIENT_SUB,<%=patientId%>,CATEGORY_TYPE_DOC);" title="Documents">
				                        				Documents (<span id="pDocNum_<%=patientId%>"><%=info.getDocCount()%></span>)
				                       				</a>
				                        		</li>
				                   <%} if (info.getLabCount() > 0) {%>
				                     			<li>
				                     				<a id="patient<%=patientId%>hl7s" href="javascript:void(0);" onclick="un_bold(this);changeView(CATEGORY_PATIENT_SUB,<%=patientId%>,CATEGORY_TYPE_HL7);" title="HL7">
				                     					HL7 (<span id="pLabNum_<%=patientId%>"><%=info.getLabCount()%></span>)
				                   					</a>
				                        		</li>
				                   <%}%>
                    		</ul>
                  </details>

					<% if (selectedCategoryPatient != null) { 
						if (selectedCategoryPatient.equals(Integer.toString(info.id))) { %>
							<script>
								un_bold($('patient<%=info.id%><%=(selectedCategoryType.equals("CATEGORY_TYPE_HL7"))?"hl7s":(selectedCategoryType.equals("CATEGORY_TYPE_DOC")?"docs":"all")%>'));
							</script>
					<% }} %>
            	<%}  // end of for loop for patient list %>
				</details>
			<%} %>
        </div>       
	</div>
</td>
	
             <td style="background-color: #E0E1FF; height:600px;" valign="top">
                 <div id="docViews" style="overflow:scroll; height:600px; width:100%;" onscroll="handleScroll(this)">

                 </div>
             </td>
          </tr>
     </table>
     </form>

	
   	<!-- main calendar program -->
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/share/calendar/calendar.js"></script>
	<!-- language for the calendar -->
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/share/calendar/lang/<bean:message key='global.javascript.calendar'/>"></script>
	<!-- the following script defines the Calendar.setup helper function, which makes
	       adding a calendar a matter of 1 or 2 lines of code. -->
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/share/calendar/calendar-setup.js"></script>
	<!-- calendar style sheet -->
			
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/share/javascript/prototype.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/share/javascript/scriptaculous.js"></script>

	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/library/jquery/jquery-1.12.0.min.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/library/jquery/jquery-ui-1.12.1.min.js"></script>  
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/js/jquery.tablesorter.min.js"></script>    
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/js/jquery.tablesorter.widgets.js"></script> 
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/js/demographicProviderAutocomplete.js"></script>
	
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/share/javascript/jquery/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/js/global.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/share/javascript/Oscar.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/share/yui/js/yahoo-dom-event.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/share/yui/js/connection-min.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/share/yui/js/animation-min.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/share/yui/js/datasource-min.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/dms/showDocument.js"></script>
	
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/hospitalReportManager/hrmActions.js"></script>
	<script type="text/javascript" src="${pageContext.servletContext.contextPath}/js/documentDescriptionTypeahead.js"></script>        

	<script type="text/javascript" >
		jQuery.noConflict();
		
		var ctx = "${pageContext.servletContext.contextPath}";
		var contextpath = "${pageContext.servletContext.contextPath}";
		
	 	jQuery(window).on("scroll",handleScroll());
		 
		function renderCalendar(id,inputFieldId){
	    	Calendar.setup({ inputField : inputFieldId, ifFormat : "%Y-%m-%d", showsTime :false, button : id });
	        
		}
		
		function split(id) {
			var loc = ctx + "/oscarMDS/Split.jsp?document=" + id;
			popupStart(1100, 1100, loc, "Splitter");
		}
	
		var page = 1;
		var pageSize = 20;
		var selected_category = <%=(selectedCategory == null ? "1" : selectedCategory)%>;
		var selected_category_patient = <%=(selectedCategoryPatient == null ? "\"\"" : selectedCategoryPatient)%>;
		var selected_category_type = <%=(selectedCategoryType == null ? "\"\"" : selectedCategoryType)%>;
		var searchProviderNo = "<%=(searchProviderNo == null ? "" : searchProviderNo)%>";
		var firstName = "<%=(patientFirstName == null ? "" : patientFirstName)%>";
		var lastName = "<%=(patientLastName == null ? "" : patientLastName)%>";
		var hin = "<%=(patientHealthNumber == null ? "" : patientHealthNumber)%>";
		var providerNo = "<%=(providerNo == null ? "" : providerNo)%>";
		var searchStatus = "<%=(ackStatus == null ? "": ackStatus)%>";
		var url = ctx + "/dms/inboxManage.do?";
		var startDate = "<%= startDate %>";
		var endDate = "<%= endDate %>";
		var request = null;
		var canLoad = true;
		console.log("<%= isListView == null %>");
		var isListView = <%= isListView %>;
		var loadingDocs = false;
		var currentBold = false;
		var oldestDate = null;
	
		window.changePage = function (p) {
			if (p == "Next") { page++; }
			else if (p == "Previous") { page--; }
			else { page = p; }
			if (request != null) { 
				request.transport.onreadystatechange = Prototype.emptyFunction;
				request.transport.abort();
	    	}
			request = updateListView();
		};
	
		function handleScroll(e) {
			if (!canLoad || loadingDocs) { return false; }
			var evt = e || window.event;
			var loadMore = false;
		    if (isListView && evt.scrollHeight > $("listViewDocs").clientHeight && evt.scrollTop > 0 && evt.scrollTop + evt.offsetHeight >= evt.scrollHeight) {
		    	loadMore = true;
		    }
		    else if (isListView && evt.scrollHeight <= $("listViewDocs").clientHeight) {
		    	loadMore = true;
		    }
		    else if (!isListView && evt.scrollTop + evt.offsetHeight >= evt.scrollHeight) {
		    	loadMore = true;
		    }
		    if (loadMore) {
		    	loadingDocs = true;
	        	changePage("Next");
		    }
		}
	
		function fakeScroll() {
			var scroller;
			if (isListView) {
				scroller = document.getElementById("summaryView");
	            jQuery("#summaryView").trigger("updateRows");
			}
			else {
				scroller = document.getElementById("docViews");
			}
	 		handleScroll(scroller);
		}
	
		function updateListView() {
			var query = getQuery();
			if (page == 1) {
				document.getElementById("docViews").innerHTML = "";
				canLoad = true;
			}
	
			if (isListView && page == 1) {
				document.getElementById("docViews").innerHTML =	"<div id='tempLoader'><img src='" + ctx + "/images/DMSLoader.gif'> Loading reports...</div>"
			}
			else if (isListView) {
				document.getElementById("loader").style.display = "block";
			}
			else {
				jQuery("#docViews").append(jQuery("<div id='tempLoader'><img src='" + ctx + "/images/DMSLoader.gif'> Loading reports...</div>"));
			}
			var div;
			if (!isListView || page == 1) {
				div = document.getElementById("docViews");
			}
			else {
				div = document.getElementById("summaryBody");
			}	
			jQuery("#readerSwitcher").prop("disabled",true);
			jQuery("#listSwitcher").prop("disabled",true);
	
			return new Ajax.Updater(div,url,{
				method:'get',
				parameters:query,
				insertion:Insertion.Bottom,
				evalScripts:true,
				onSuccess:function(transport){
					loadingDocs = false;
					var tmp = jQuery("#tempLoader");
					if (tmp != null) { tmp.remove(); }
					if (isListView) {
						if (page == 1) { jQuery("#tempLoader").remove(); }
						else { document.getElementById("loader").style.display = "none"; }
					}
		
					if (transport.responseText.indexOf("<input type=\"hidden\" name=\"NoMoreItems\" value=\"true\" />") >= 0) {
						canLoad = false;
		                var div = document.getElementById("summaryBody");
		                var newDiv = "<tbody id=\"newBody\"></tbody>";
		                div.insertAdjacentHTML("beforeBegin", newDiv);
		                newDiv = document.getElementById("newBody");
		                newDiv.innerHTML = div.innerHTML;
		                div.innerHTML = "";
		                newDiv.id = "summaryBody";
		                div.id = "";
		                jQuery("#summaryView").trigger("updateRows");
					}
					else {
						// It is possible that the current amount of loaded items has not filled up the page enough
						// to create a scroll bar. So we fake a scroll (since no scroll bar is equivalent to reaching the bottom).
						setTimeout("fakeScroll()", 500);
					}
					
					jQuery("#readerSwitcher").prop("disabled",false);
					jQuery("#listSwitcher").prop("disabled",false);
				}
			});
		}
	
		function getQuery() {
			var CATEGORY_ALL = 1,CATEGORY_DOCUMENTS = 2,CATEGORY_HL7 = 3,CATEGORY_NORMAL = 4,CATEGORY_ABNORMAL = 5,CATEGORY_PATIENT = 6,CATEGORY_PATIENT_SUB = 7,CATEGORY_TYPE_DOC = 'DOC',CATEGORY_TYPE_HL7 = 'HL7';
			var query = "method=prepareForContentPage";
			query +="&searchProviderNo="+searchProviderNo+"&providerNo="+providerNo+"&status="+searchStatus+"&page="+page
				   +"&pageSize="+pageSize+"&isListView="+(isListView?"true":"false") 
				   +"&startDate=" + startDate + "&endDate=" + endDate;
			switch (selected_category) {
			case CATEGORY_ALL:
				query  += "&view=all";
				query  += "&fname=" + firstName + "&lname=" + lastName + "&hnum=" + hin;
				break;
			case CATEGORY_DOCUMENTS:
				query  += "&view=documents";
				query  += "&fname=" + firstName + "&lname=" + lastName + "&hnum=" + hin;
				break;
			case CATEGORY_HL7:
				query  += "&view=labs";
				query  += "&fname=" + firstName + "&lname=" + lastName + "&hnum=" + hin;
				break;
			case CATEGORY_NORMAL:
				query  += "&view=normal";
				query  += "&fname=" + firstName + "&lname=" + lastName + "&hnum=" + hin;
				break;
			case CATEGORY_ABNORMAL:
				query  += "&view=abnormal";
				query  += "&fname=" + firstName + "&lname=" + lastName + "&hnum=" + hin;
				break;
			case CATEGORY_PATIENT:
				query  += "&view=all&demographicNo=" + selected_category_patient;
				break;
		    case CATEGORY_PATIENT_SUB:
		    	query  += "&demographicNo=" + selected_category_patient;
		    	switch (selected_category_type) {
			    	case CATEGORY_TYPE_DOC:
			    		query  += "&view=documents";
						break;
			    	case CATEGORY_TYPE_HL7:
			    		query  += "&view=labs";
						break;
		    	}
		    	break;
		    }
	
			if (oldestLab != null) {
				query += "&newestDate=" + encodeURIComponent(oldestLab);
			}
			
			return query;
		}
	
		function changeView(type,patientId,subtype) {
			loadingDocs = true;
			selected_category = type;
			selected_category_patient = patientId;
			selected_category_type = subtype;
			document.getElementById("docViews").innerHTML = "";
			
			changePage(1);
		}
	
		function switchView() {
			var newUrl;
			if(location.href.includes("isListView")){
				newUrl = location.href.replace("isListView="+ isListView, "isListView="+ !isListView);
			}else{
				newUrl = location.href + "&isListView="+ !isListView
			}
			window.location.href = newUrl;
		}
	
		jQuery(document).ready(function() {
			if(isListView == null){
				isListView = <%= (selectedCategoryPatient == null) %>;
			}
			jQuery('input[name=isListView]').val(isListView);
			
			loadingDocs = true;
			document.getElementById("docViews").innerHTML = "";
			var list = document.getElementById("listSwitcher");
			var view = document.getElementById("readerSwitcher");
			var active, passive;
			if (isListView) {
				pageSize = 20;
				active = view;
				passive = list;
			}
			else {
				pageSize = 5;
				active = list;
				passive = view;
			}
			active.style.display = "inline";
			passive.style.display = "none";
			
			changePage(1);
			//un_bold($("totalAll"));
			currentBold = "totalAll";
			refreshCategoryList();
		});
		/* function ForwardSelectedRows() {
			var query = jQuery(document.reassignForm).formSerialize();
			var labs = jQuery("input[name='flaggedLabs']:checked");
			for (var i = 0; i < labs.length; i++) {
				query += "&flaggedLabs=" + labs[i].value;
				query += "&" + jQuery(labs[i]).next().name + "=" + jQuery(labs[i]).next().value;
			}
			jQuery.ajax({
				type: "POST",
				url:  ctx + "/oscarMDS/ReportReassign.do",
				data: query,
				success: function (data) {
					jQuery("input[name='flaggedLabs']:checked").each(function () {
						this.checked = false;
					});
				}
			});
		}*/

		function refreshCategoryList() {
			jQuery("#categoryHash").val("-1");
			updateCategoryList();
		}
	
		function updateCategoryList() {
			jQuery.ajax({
				type: "GET",
				url: ctx + "/dms/inboxManage.do",
				data: window.location.search.substr(1) + "&ajax=true",
				success: function (data) {
					if (jQuery("#categoryHash").length == 0 || jQuery(data)[2].value != jQuery("#categoryHash").val()) {
						var categoryList = jQuery("#categoryList");
					
						if(categoryList !== undefined)
						{
							categoryList.replaceWith(jQuery(data).find("#categoryList"));
							re_bold(currentBold);
						}
					}
				}
			});
		}

	
 		window.removeReport = function (reportId) {
			var el = jQuery("#labdoc_" + reportId);
			if (el != null) {
				el.remove();
			}
			refreshCategoryList();
			fakeScroll();
		}
		
		// Jquery modal windows
		jQuery(document).ready( function(){
	
			jQuery( function() {
					
				jQuery( "#dialog" ).dialog({
			      autoOpen: false,
			      modal: true
			    });
			 
				jQuery(".dialog-link").on( "click", function(event) {
					event.preventDefault();
			    	var url = ctx + this.id;
	 		    	jQuery("#dialog").load(url).dialog({modal:true}); 
			    });
			});
		});
	</script>
	
</div> <!--  end wrapper  -->  
<input type="hidden" id="ctx" value="${pageContext.servletContext.contextPath}" />
<script type="text/javascript" src="${pageContext.servletContext.contextPath}/share/javascript/oscarMDSIndex.js"></script>
<div id="dialog" ></div> 

</body>
</html>



