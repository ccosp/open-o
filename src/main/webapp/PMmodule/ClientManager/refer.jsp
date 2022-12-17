
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



<%@page import="org.oscarehr.util.WebUtils"%>
<%@page import="org.oscarehr.common.model.Provider"%>
<%@page import="org.oscarehr.util.SpringUtils"%>
<%@page import="org.oscarehr.PMmodule.dao.ProviderDao"%>
<%@page import="org.oscarehr.common.model.RemoteReferral"%>
<%@ include file="/taglibs.jsp"%>
<%@ page import="org.oscarehr.PMmodule.model.*"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/WEB-INF/caisi-tag.tld" prefix="caisi"%>

<%@page import="org.apache.commons.lang.time.DateFormatUtils"%>
<%@page import="org.oscarehr.util.WebUtils"%><script>
	function resetClientFields() {
		var form = document.clientManagerForm;
		form.elements['program.name'].value='';
	}

	function search_programs() {
		var form = document.clientManagerForm;

		form.method.value='refer_select_program';
		var programName = form.elements['program.name'].value;
		var typeEl = form.elements['program.type'];
		var programType = typeEl.options[typeEl.selectedIndex].value;
		var manOrWomanEl = form.elements['program.manOrWoman'];
		var manOrWoman = manOrWomanEl.options[manOrWomanEl.selectedIndex].value;
 		var transgender = form.elements['program.transgender'].checked;
 		var firstNation = form.elements['program.firstNation'].checked;
 		var bedProgramAffiliated = form.elements['program.bedProgramAffiliated'].checked;
 		var alcohol = form.elements['program.alcohol'].checked;
 		var abstinenceSupportEl = form.elements['program.abstinenceSupport'];
 		var abstinenceSupport = abstinenceSupportEl.options[abstinenceSupportEl.selectedIndex].value;
 		var physicalHealth = form.elements['program.physicalHealth'].checked;
 		var mentalHealth = form.elements['program.mentalHealth'].checked;
 		var housing = form.elements['program.housing'].checked;

		var url = '<html:rewrite action="/PMmodule/ClientManager.do"/>';
			url += '?method=search_programs&program.name=' + programName + '&program.type=' + programType;
			url += '&program.manOrWoman='+manOrWoman+'&program.transgender='+transgender+'&program.firstNation='+firstNation+'&program.bedProgramAffiliated='+bedProgramAffiliated+'&program.alcohol='+alcohol+'&program.abstinenceSupport='+abstinenceSupport+'&program.physicalHealth='+physicalHealth+'&program.mentalHealth='+mentalHealth+'&program.housing='+housing;
			//url += '&program.manOrWoman='+manOrWoman;
			url += '&formName=clientManagerForm&formElementName=program.name&formElementId=program.id&formElementType=program.type&submit=true';

		window.open(url, "program_search", "width=800, height=600, scrollbars=1,location=1,status=1");
	}

	function do_referral() {
		var referralDate = document.getElementById('referralDate').value;
	    if(!referralDate || typeof referralDate == 'undefined') {
	    		alert("Please choose referral date");
	    		return false;
	    }   
	    var today = new Date();

	    var referralDateString = referralDate.split('-') ;
	    var referralDateYear = referralDateString[0];
	    var referralDateMonth = referralDateString[1];
	    var referralDateDate = referralDateString[2];
	    var enterDate = new Date(referralDateYear, parseInt(referralDateMonth)-1, referralDateDate);
	    if (enterDate > today)
	    {
	        alert("Please don't enter future date");
	        return false;
	    }
	    
		var form = document.clientManagerForm;
		form.method.value='refer';
		form.submit();
	}
</script>

<%=WebUtils.popErrorMessagesAsHtml(session)%>

<div class="tabs" id="tabs">
<table cellpadding="3" cellspacing="0" border="0">
	<tr>
		<th title="Programs">Referrals</th>
	</tr>
</table>
</div>
<display:table class="simple" cellspacing="2" cellpadding="3"
	id="referral" name="referrals" export="false" pagesize="0"
	requestURI="/PMmodule/ClientManager.do">
	<display:setProperty name="paging.banner.placement" value="bottom" />
	<display:column property="programName" sortable="true"
		title="Program Name" />
	<display:column property="referralDate" sortable="true"
		title="Referral Date" />
	<display:column property="providerFormattedName" sortable="true"
		title="Referring Provider" />
	<display:column property="status" sortable="true" title="Status" />
	<display:column sortable="true" title="Days in Queue">
		<%
		ClientReferral temp = (ClientReferral) pageContext.getAttribute("referral");
		Date referralDate = temp.getReferralDate();
		Date currentDate = new Date();
		String numDays = "";

		if (!temp.getStatus().equals("pending")) {
			long diff = currentDate.getTime() - referralDate.getTime();
			diff = diff / 1000; //seconds;
			diff = diff / 60; //minutes;
			diff = diff / 60; //hours
			diff = diff / 24; //days
			numDays = String.valueOf(diff);
		} else {
			numDays = "0";
		}
		%>
		<%=numDays%>
	</display:column>
	<!--  display:column property="notes" sortable="true" title="Notes" /> -->
	<display:column property="notes" sortable="true"
		title="Reason for referral" />
	<display:column property="presentProblems" sortable="true"
		title="Present Problems" />
</display:table>

<c:if test="${remoteReferrals!=null}">
	<br />
	<br />
	<div class="tabs" id="tabs">
	<table cellpadding="3" cellspacing="0" border="0">
		<tr>
			<th title="Programs">Remote Referrals</th>
		</tr>
	</table>
	</div>
	<display:table class="simple" cellspacing="2" cellpadding="3"
		id="remoteReferral" name="remoteReferrals" export="false" pagesize="0"
		requestURI="/PMmodule/ClientManager.do">
		<%
			RemoteReferral temp = (RemoteReferral) pageContext.getAttribute("remoteReferral");
		%>
		<display:setProperty name="paging.banner.placement" value="bottom" />
		<display:column	title="Facility / Program">
			<%=temp.getReferredToFacilityName()%> / <%=temp.getReferredToProgramName()%>
		</display:column>
		<display:column title="Referral Date">
			<%
				String date=DateFormatUtils.ISO_DATETIME_FORMAT.format(temp.getReferalDate());
				date=date.replace("T", " ");
			%>
			<%=date%>
		</display:column>
		<display:column title="Referring Provider">
			<%
				String providerName=temp.getReferringProviderNo();
				ProviderDao providerDao=(ProviderDao)SpringUtils.getBean("providerDao");
				Provider provider=providerDao.getProvider(temp.getReferringProviderNo());
				if (provider!=null) providerName=provider.getFormattedName();
			%>
			<%=providerName%>
		</display:column>
		<display:column property="reasonForReferral" title="Reason for referral" />
		<display:column property="presentingProblem" title="Present Problems" />
	</display:table>
</c:if>

<br />
<br />
<div class="tabs" id="tabs">
<table cellpadding="3" cellspacing="0" border="0">
	<tr>
		<th title="Programs">Search Programs</th>
	</tr>
</table>
</div>
<html:hidden property="program.id" />
<html:hidden property="referral.remoteFacilityId" />
<html:hidden property="referral.remoteProgramId" />
<table width="100%" border="1" cellspacing="2" cellpadding="3">
	<tr class="b">
		<td width="20%">Program Name</td>
		<td><html:text property="program.name" /></td>
	</tr>
	<tr class="b">
		<td width="20%">Program Type</td>
		<td><html:select property="program.type">
			<html:option value="">&nbsp;</html:option>
			<html:option value="Bed">Bed</html:option>
			<html:option value="Service">Service</html:option>
		</html:select></td>
	</tr>

	<tr class="b">
		<td width="20%">Man or Woman:</td>
		<td><html:select property="program.manOrWoman">
			<html:option value="">&nbsp;</html:option>
			<html:option value="Man">Man</html:option>
			<html:option value="Woman">Woman</html:option>
		</html:select></td>
	</tr>

	<tr class="b">
		<td width="20%">Transgender:</td>
		<td><html:checkbox property="program.transgender" /></td>
	</tr>
	<tr class="b">
		<td width="20%">First Nation:</td>
		<td><html:checkbox property="program.firstNation" /></td>
	</tr>
	<tr class="b">
		<td width="20%">Bed Program Affiliated:</td>
		<td><html:checkbox property="program.bedProgramAffiliated" /></td>
	</tr>
	<tr class="b">
		<td width="20%">Alcohol:</td>
		<td><html:checkbox property="program.alcohol" /></td>
	</tr>
	<tr class="b">
		<td width="20%">Abstinence Support?</td>
		<td><html:select property="program.abstinenceSupport">
			<html:option value="">&nbsp;</html:option>
			<html:option value="Harm Reduction" />
			<html:option value="Abstinence Support" />
			<html:option value="Not Applicable" />
		</html:select></td>
	</tr>
	<tr class="b">
		<td width="20%">Physical Health:</td>
		<td><html:checkbox property="program.physicalHealth" /></td>
	</tr>
	<tr class="b">
		<td width="20%">Mental Health:</td>
		<td><html:checkbox property="program.mentalHealth" /></td>
	</tr>
	<tr class="b">
		<td width="20%">Housing:</td>
		<td><html:checkbox property="program.housing" /></td>
	</tr>




</table>
<table>
	<tr>
		<td align="center"><input type="button" value="search"
			onclick="search_programs()" /></td>
		<td align="center"><input type="button" name="reset"
			value="reset" onclick="javascript:resetClientFields();" /></td>
	</tr>
</table>
<br />
<c:if test="${requestScope.do_refer != null}">
	<table class="b" border="0" width="100%">
		<tr>
			<th style="color: black">Program Name</th>
			<th style="color: black">Type</th>
			<th style="color: black">Participation</th>
			<th style="color: black">Phone</th>
			<th style="color: black">Email</th>
		</tr>
		<tr>
			<td><c:out value="${program.name }" /></td>
			<td><c:out value="${program.type }" /></td>
			<td><c:out value="${program.numOfMembers}" />/<c:out
				value="${program.maxAllowed}" /> (<c:out
				value="${program.queueSize}" /> waiting)</td>
			<td><c:out value="${program.phone }" /></td>
			<td><c:out value="${program.email }" /></td>
		</tr>
	</table>
</c:if>
<br />
<c:if test="${requestScope.do_refer != null}">
	<table width="100%" border="1" cellspacing="2" cellpadding="3">
		<tr class="b">
			<td width="20%">Reason for referral:</td>
			<td><html:textarea cols="50" rows="7" property="referral.notes" /></td>
		</tr>
		<tr class="b">
			<td width="20%">Presenting Problems:</td>
			<td><html:textarea cols="50" rows="7"
				property="referral.presentProblems" /></td>
		</tr>
		<tr> <td>Referral Date:</td>
			<td><input type="text" id="referralDate" name="referralDate" value="" readonly ><img titltype="text"e="Calendar" id="cal_referralDate" 
                src="<%=request.getContextPath()%>/images/cal.gif" alt="Calendar" border="0">
                <script type="text/javascript">Calendar.setup({inputField:'referralDate',ifFormat :'%Y-%m-%d',button :'cal_referralDate',align :'cr',singleClick :true,firstDay :1});</script>                                     
                </td>
        </tr>
		
		<c:if test="${program.type eq 'Bed' }">
			<!-- <c:if test="${requestScope.temporaryAdmission == true}"> -->
			<caisi:isModuleLoad moduleName="pmm.refer.temporaryAdmission.enabled">
				<tr class="b">
					<td width="20%">Request Temporary Admission:</td>
					<td><html:checkbox property="referral.temporaryAdmission" /></td>
				</tr>
			</caisi:isModuleLoad>
			<!-- </c:if> -->
		</c:if>
		<tr class="b">
			<td colspan="2"><input type="button" value="Process Referral"
				onclick="do_referral()" /> <input type="button" value="Cancel"
				onclick="document.clientManagerForm.submit()" /></td>
		</tr>
	</table>
</c:if>
