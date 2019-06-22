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

<%@ page import="org.oscarehr.util.LoggedInInfo"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ page import="org.w3c.dom.*"%>
<%@ page import="oscar.oscarMessenger.util.Msgxml"%>
<%@ page import="oscar.oscarDemographic.data.*"%>
<%@ page import="org.oscarehr.managers.MessagingManager" %>
<%@ page import="org.oscarehr.common.model.Groups" %>
<%@ page import="oscar.oscarMessenger.data.MsgProviderData" %>
<%@ page import="java.util.Map, java.util.List" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="org.oscarehr.util.MiscUtils"%>
<%
      String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
	  boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_msg" rights="w" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_msg");%>
</security:oscarSec>
<%
if(!authed) {
	return;
}
%>


<logic:notPresent name="msgSessionBean" scope="session">
	<logic:redirect href="index.jsp" />
</logic:notPresent>
<logic:present name="msgSessionBean" scope="session">
	<bean:define id="bean"
		type="oscar.oscarMessenger.pageUtil.MsgSessionBean"
		name="msgSessionBean" scope="session" />
	<logic:equal name="bean" property="valid" value="false">
		<logic:redirect href="index.jsp" />
	</logic:equal>
</logic:present>


<%
org.oscarehr.managers.MessengerGroupManager groupManager = SpringUtils.getBean(org.oscarehr.managers.MessengerGroupManager.class);
Map<Groups, List<MsgProviderData>> groups = groupManager.getAllGroupsWithMembers(LoggedInInfo.getLoggedInInfoFromSession(request));
Map<String, List<MsgProviderData>> remoteMembers = groupManager.getAllRemoteMembers(LoggedInInfo.getLoggedInInfoFromSession(request));
List<MsgProviderData> localMembers = groupManager.getAllLocalMembers(LoggedInInfo.getLoggedInInfoFromSession(request));
MessagingManager messagingManager = SpringUtils.getBean(MessagingManager.class);

request.setAttribute("groupManager", groups);
request.setAttribute("remoteMembers", remoteMembers);
request.setAttribute("localMembers", localMembers);

pageContext.setAttribute("messageSubject", request.getParameter("subject"));
pageContext.setAttribute("messageSubject", request.getAttribute("ReSubject"));
pageContext.setAttribute("messageBody", request.getAttribute("ReText"));

oscar.oscarMessenger.pageUtil.MsgSessionBean bean = (oscar.oscarMessenger.pageUtil.MsgSessionBean)pageContext.findAttribute("bean");

String demographic_no = (String) request.getAttribute("demographic_no");
DemographicData demoData = new DemographicData();
org.oscarehr.common.model.Demographic demo = demoData.getDemographic(LoggedInInfo.getLoggedInInfoFromSession(request), demographic_no);
String demoName = "";
if (demo != null) {
	demoName = demo.getLastName() + ", " + demo.getFirstName();
}

String delegate = "";
String delegateName = "";
boolean recall = (request.getParameter("recall") != null);

if(recall){
	String subjectText = messagingManager.getLabRecallMsgSubjectPref(LoggedInInfo.getLoggedInInfoFromSession(request));
	delegate = messagingManager.getLabRecallDelegatePref(LoggedInInfo.getLoggedInInfoFromSession(request));
	if(delegate!=null || delegate != ""){
		delegateName = messagingManager.getDelegateName(delegate);
	}
	pageContext.setAttribute("delegateName", delegateName);
	pageContext.setAttribute("messageSubject", subjectText);
}

%>

<html:html locale="true">
<head>
<title><bean:message key="oscarMessenger.CreateMessage.title" /></title>

<link rel="stylesheet" type="text/css" href="encounterStyles.css">
<link rel="stylesheet" type="text/css" media="all" href="../share/css/extractedFromPages.css"  />

<style type="text/css">

	summary {
		cursor: pointer;
	}
	.muted {
	    color:silver;
	}
	
	.group_member_contact, .remote_member_contact {
		margin-left:15px;
	}
	
	summary label {
		font-weight: bold;
	}
</style>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.1.min.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery-ui-1.8.18.custom.min.js"></script>
<script src="<%=request.getContextPath()%>/js/fg.menu.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>

<script type="text/javascript">

    var browserName=navigator.appName; 
    
    if (browserName=="Netscape"){ 
        if( document.implementation ){
            //this detects W3C DOM browsers (IE is not a W3C DOM Browser)
            if( Event.prototype && Event.prototype.__defineGetter__ ){
                //this detects Mozilla Based Browsers
                Event.prototype.__defineGetter__( "srcElement", function(){
                    var src = this.target;
                    if( src && src.nodeType == Node.TEXT_NODE )
                        src = src.parentNode;
                        return src;
                    }
                );
            }
        }
    }
    
    function checkGroup(group)
    {
    	$.each($("input." + group), function(){
    	    $(this).attr("checked", !$(this).attr("checked"));
		})
    }

	function validatefields(){
		
		// cannot send attachments to remote facilities
		$("input:checked").each(function () {
			if(this.id.split("-")[2] > 0 && $("#attachmentAlert").val())
			{
				alert("Cannot send attachments to Integrated facilities.");
				return false;
			}
		})	
		
	  if (document.forms[0].message.value.length == 0){
	    alert("<bean:message key="oscarMessenger.CreateMessage.msgEmptyMessage"/>");
	    return false;
	  }
	  val = validateCheckBoxes(document.forms[0]);
	  if (val  == 0)
	  {
	    alert("<bean:message key="oscarMessenger.CreateMessage.msgNoProvider"/>");
	    return false;
	  }
	  return true
	}
	
	function validateCheckBoxes(form)
	{
	  var retval = "0";
	  for (var i =0; i < form.provider.length;i++)
	    if  (form.provider[i].checked)
	      retval = "1";
	  return retval
	}
	
	function BackToOscar()
	{
	    if (opener.callRefreshTabAlerts) {
		opener.callRefreshTabAlerts("oscar_new_msg");
	        setTimeout("window.close()", 100);
	    } else {
	        window.close();
	    }
	}
	
	function XMLHttpRequestSendnArch() {
		var oRequest = new XMLHttpRequest();
		var theLink=document.referrer;
		var theLinkComponents=theLink.split('?');
		var theQueryComponents=theLinkComponents[1].split('&');
	
		for (index = 0; index < theQueryComponents.length; ++index) {
	    		var theKeyValue=theQueryComponents[index].split('=');
			if(theKeyValue[0]=='messageID'){
				var theArchiveLink=theLinkComponents[0].substring(0,theLinkComponents[0].lastIndexOf('/'))+'/DisplayMessages.do?btnDelete=archive&messageNo='+theKeyValue[1];
			}
		}
	
		oRequest.open('GET', theArchiveLink, false);
		oRequest.send();
		document.forms[0].submit();
	}

	function popupSearchDemo(keyword){ // open a new popup window
	    var vheight = 700;
	    var vwidth = 980;  
	    windowprops = "height="+vheight+",width="+vwidth+",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=0,screenY=0,top=0,left=0";    
	    var page = 'msgSearchDemo.jsp?keyword=' +keyword +'&firstSearch='+true;
	    var popUp=window.open(page, "msgSearchDemo", windowprops);
	    if (popUp != null) {
	        if (popUp.opener == null) {
	          popUp.opener = self; 
	        }
	        popUp.focus();
	    }
	}
	
	function popupAttachDemo(demographic){ // open a new popup window
	    var subject = document.forms[0].subject.value;
	    var message = document.forms[0].message.value;
	    var formData = "subject=" + subject + "&message=" + message;
	
	    $.ajax({
	    	type: "post",
	    	data : formData,
	    	success: function(data){
	    		console.log(data);
	    	},
	    	error: function (jqXHR, textStatus, errorThrown){
	 			alert("Error: " + textStatus);
	    	}
		});
	
	    var vheight = 700;
	    var vwidth = 900;  
	    windowprops = "height="+vheight+",width="+vwidth+",location=0,scrollbars=1,menubar=0,toolbar=1,resizable=1,screenX=0,screenY=0,top=0,left=0";    
	    var page = 'attachmentFrameset.jsp?demographic_no=' +demographic;
	    var demo_no  = demographic;
	    
	   
	    if ( demographic == "" || !demographic || demographic == null || demographic == "null") {
	        alert("Please select a demographic.");
	    }
	    else { 
	        var popUp=window.open(page, "msgAttachDemo", windowprops);
	        if (popUp != null) {
	            if (popUp.opener == null) {
	              popUp.opener = self; 
	            }
	            popUp.focus();
	        }
	    }
	
	}
	
	/* 
	 * Check off all the contact members that this message will be replied to. 
	 */
    var replyList = '${ replyList }';
    var replyListJson = JSON.parse(replyList);    

	$(document).ready(function(){
	    for (i in replyListJson) {
	    	var checkedElement = $("input#local-" + replyListJson[i].compositeId);
	    	if(checkedElement.length == 0)
	    	{
	    		checkedElement = $("input#remote-" + replyListJson[i].compositeId);
	    	}
	    	checkedElement.attr("checked", "checked");
	    	checkedElement.parent().parent().attr('open', '').parent().attr('open', '');
	    }
	})
</script>
</head>
<body class="BodyStyle" vlink="#0000FF">
<table class="MainTable" id="scrollNumber1">
	<tr class="MainTableTopRow">
		<td class="MainTableTopRowLeftColumn">
		<bean:message key="oscarMessenger.CreateMessage.msgMessenger" />
		</td>
		<td class="MainTableTopRowRightColumn">
		<table class="TopStatusBar">
			<tr>
				<td><h2><bean:message key="oscarMessenger.CreateMessage.msgCreate" /></h2>
				</td>
				<td>&nbsp;</td>
				<td style="text-align: right">
				<oscar:help keywords="message" key="app.top1"/> | 
				<a href="javascript:void(0)" onclick="javascript:popupPage(600,700,'../oscarEncounter/About.jsp')"><bean:message key="global.about" /></a>
			   </td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td class="MainTableLeftColumn">&nbsp;</td>
		<td class="MainTableRightColumn">
		<table>

			<tr>
				<td>
				<table cellspacing=3>
					<tr>
						<td>
						<table class=messButtonsA cellspacing=0 cellpadding=3>
							<tr>
								<td class="messengerButtonsA"><html:link
									page="/oscarMessenger/DisplayMessages.jsp"
									styleClass="messengerButtons">
									<bean:message key="oscarMessenger.ViewMessage.btnInbox" />
								</html:link></td>
							</tr>
						</table>
						</td>
						<td>
						<table class=messButtonsA cellspacing=0 cellpadding=3>
							<tr>
								<td class="messengerButtonsA"><html:link
									page="/oscarMessenger/ClearMessage.do"
									styleClass="messengerButtons">
									<bean:message key="oscarMessenger.CreateMessage.btnClear" />
								</html:link></td>
							</tr>
						</table>
						</td>
						<td>
						<table class=messButtonsA cellspacing=0 cellpadding=3>
							<tr>
								<td class="messengerButtonsA"><a
									href="javascript:BackToOscar()" class="messengerButtons"><bean:message
									key="oscarMessenger.CreateMessage.btnExit" /></a></td>
							</tr>
						</table>
						</td>
					</tr>
				</table>
				</td>
			</tr>

			<tr>
				<td>
				<html:form action="/oscarMessenger/CreateMessage" onsubmit="return validatefields()">
				<table>
						<tr>
							<th bgcolor="#DDDDFF" width="75"><bean:message
								key="oscarMessenger.CreateMessage.msgRecipients" /></th>
							<th colspan="2" align="left" bgcolor="#DDDDFF"><bean:message
								key="oscarMessenger.CreateMessage.msgMessage" /></th>
						</tr>
						<tr>
						
						<td bgcolor="#EEEEFF" valign=top>
							<table>
								<tr>
									<td><input type="submit" class="ControlPushButton"
										value="<bean:message key="oscarMessenger.CreateMessage.btnSendMessage"/>">
									</td>
									<td><input type="button" class="ControlPushButton"
										value="<bean:message key="oscarMessenger.CreateMessage.btnSendnArchiveMessage"/>" onClick="XMLHttpRequestSendnArch()">
									</td>
								</tr>
							</table>
							<div class="ChooseRecipientsBox">
							<table>                                                     
                                <tr>
									<td><!--list of the providers cell Start-->														
									<%if(recall){ %>
										<div>
											<input name="provider" value="<%=delegate%>" type="checkbox" checked> 
											<strong><a title="default recall delegate: <%=delegateName%>">default: <%=delegateName%></a></strong>								
										</div>
									<%} %>
									
										<!-- Display all local members -->
										<c:forEach items="${ localMembers }" var="member">
											<div class="member_contact">								
												<input type="checkbox" name="provider" id="local-${ member.id.compositeId }" value="${ member.id.compositeId }"  />
												<label for="local-${ member.id.compositeId }" >
													<c:out value="${ member.lastName }" />, <c:out value="${ member.firstName }" />															
												</label>												
											</div>
										</c:forEach>
									
										<!-- Display Member Groups -->
										<div id="member-groups">
										<details>
											<summary>
												<strong>Member Groups</strong>
											</summary>
											<c:forEach items="${ groupManager }" var="group" varStatus="count">
											<details>										
												<summary>			
													<input type="checkbox" name="tableDFR" id="${ group.key.id }" 
															value="${ group.key.id }" onclick="javascript:checkGroup('member_group_${ count.index }');" />
													<label for="${ group.key.id }" >${ group.key.groupDesc }</label>
												</summary>
																																			
												<c:forEach items="${ group.value }" var="member">
													<div class="group_member_contact">										
														<input type="checkbox" name="provider" class="member_group_${ count.index }" 
															id="group-${ member.id.compositeId }" value="${ member.id.compositeId }"  />
														<label for="group-${ member.id.compositeId }" >
															<c:out value="${ member.lastName }" />, <c:out value="${ member.firstName }" />															
														</label>
													</div>
												</c:forEach>
												
											</details>
											</c:forEach>
										</details>
										</div>
										
										<!-- Display Members by remote locations -->
										<c:if test="${ not empty remoteMembers }" >
										<div id="remote-locations">
										<details>
											<summary>
												<strong>Integrated Members</strong>
											</summary>
											<c:forEach items="${ remoteMembers }" var="location" varStatus="count">
												<details>										
													<summary>			
														<input type="checkbox" name="tableDFR" id="${ location.key }" 
																value="${ location.key }" onchange="javascript:checkGroup('remote_group_${ count.index }');" />
														<label for="${ location.key }" >${ location.key }</label>
													</summary>

													<c:forEach items="${ location.value }" var="member">
														<div class="remote_member_contact">												
															<input type="checkbox" name="provider" class="remote_group_${ count.index }" 
																id="remote-${ member.id.compositeId }" value="${ member.id.compositeId }"  />
															<label for="remote-${ member.id.compositeId }" >
																<c:out value="${ member.lastName }" />, <c:out value="${ member.firstName }" />															
															</label>
														</div>
													</c:forEach>
													
												</details>
											</c:forEach>
										</details>
										</div>
										</c:if>
									</td><!--list of the providers cell end-->
								</tr>
							</table>
						</div> <!-- end ChooseRecipientsBox -->
					</td>
					<td bgcolor="#EEEEFF" valign=top colspan="2"><!--Message and Subject Cell-->
					<bean:message key="oscarMessenger.CreateMessage.formSubject" /> :
					<html:text name="msgCreateMessageForm" property="subject" size="67" value="${messageSubject}"/> <br>
					<br>
					<html:textarea name="msgCreateMessageForm" property="message" cols="60" rows="18" value="${messageBody}"/> 
					<%
                       String att = bean.getAttachment();
                       String pdfAtt = bean.getPDFAttachment();
                       if (att != null || pdfAtt != null){ 
                    %>
							<br>
							<bean:message key="oscarMessenger.CreateMessage.msgAttachments" />
							<input type="hidden" id="attachmentAlert" name="attachmentAlert" value="true" />
							<% 
							bean.setSubject(null);
							bean.setMessage(null);
						}%>
					</td>
				</tr>

				<tr>
					<td bgcolor="#B8B8FF"></td>
					<td bgcolor="#B8B8FF" colspan="2"><font style="font-weight: bold"><bean:message key="oscarMessenger.CreateMessage.msgLinkThisMessage" /></font></td>
				</tr>
                                                      
				<tr>
					<td bgcolor="#EEEEFF"></td>
					<td bgcolor="#EEEEFF">
                      <input type="text" name="keyword" size="30" /> <input type="hidden" name="demographic_no" value="<%=demographic_no%>" /> 
                    </td>
	                <td bgcolor="#EEEEFF"> 
                      <input type="button" class="ControlPushButton" name="searchDemo" value="<bean:message key="oscarMessenger.CreateMessage.msgSearchDemographic" />" onclick="popupSearchDemo(document.forms[0].keyword.value)" />
                  	</td>
				</tr>
				<tr>
					<td bgcolor="#EEEEFF"></td>
					<td bgcolor="#EEEEFF" colspan="2"><font style="font-weight: bold"><bean:message key="oscarMessenger.CreateMessage.msgSelectedDemographic" /></font></td>
				</tr>
				<tr>
					<td bgcolor="#EEEEFF"></td>
					
					<td bgcolor="#EEEEFF"><input type="text"
						name="selectedDemo" size="30" readonly
						style="background: #EEEEFF; border: none" value="none" /> 
							<script type="text/javascript">
	                          if ( "<%=demoName%>" != "null" && "<%=demoName%>" != "") {
	                              document.forms[0].selectedDemo.value = "<%=demoName%>";
	                              document.forms[0].demographic_no.value = "<%=demographic_no%>";
	                          }
	                      </script>
	                </td>
	                <td bgcolor="#EEEEFF"> 
                    <input type="button"
						class="ControlPushButton" name="clearDemographic"
						value="<bean:message key="oscarMessenger.CreateMessage.msgClearSelectedDemographic" />"
						onclick='document.forms[0].demographic_no.value = ""; document.forms[0].selectedDemo.value = "none"' />
					<input type="button" class="ControlPushButton" name="attachDemo"
						value="<bean:message key="oscarMessenger.CreateMessage.msgAttachDemographic" />"
						onclick="popupAttachDemo(document.forms[0].demographic_no.value)"
						style="display: " />
					</td>

				</tr>

		</table>
		</html:form>
			</td>
		</tr>
		<tr>
			<td>
			<script language="JavaScript">
                 document.forms[0].message.focus();
            </script>
            </td>
		</tr>

	</table>
	</td>
	</tr>
	<tr>
		<td class="MainTableBottomRowLeftColumn">&nbsp;</td>
		<td class="MainTableBottomRowRightColumn">&nbsp;</td>
	</tr>
</table>
</body>
</html:html>
