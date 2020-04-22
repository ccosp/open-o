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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>

<%
    String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_admin,_admin.fax" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_admin&type=_admin.fax");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>

<html>
<head>
<title>OSCAR Fax</title>

<c:set var="ctx" value="${ pageContext.request.contextPath }" scope="page" />

<link rel="stylesheet" type="text/css" href="${ctx}/css/encounterStyles.css" />
<link rel="stylesheet" href="${ctx}/library/bootstrap/3.0.0/css/bootstrap.min.css" type="text/css" />
<link rel="stylesheet" href="${ctx}/css/font-awesome.min.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="${ctx}/share/css/OscarStandardLayout.css" />

<script type="text/javascript" src="${ctx}/library/jquery/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="${ctx}/library/bootstrap/3.0.0/js/bootstrap.min.js" ></script>

</head>
<body>

<div id="bodyrow" class="container-fluid">

	<div id="bodycolumn" class="col-sm-12">
	
	<div class="page-header">
	  <h1>OSCAR Fax <small>${ transType }</small></h1>
	</div>

	<form class="form-horizontal" action='${ctx}/oscarEncounter/oscarConsultationRequest/ConsultationFormFax.do' method="post">
	
			<input type="hidden" name="requestId" value="${ reqId }" />
			<input type="hidden" name="reqId" value="${ reqId }" />
			<input type="hidden" name="transType" value="${ transType }" />
			<input type="hidden" name="demographicNo" value="${ not empty demographicNo ? demographicNo : param.demographicNo }" />
			<input type="hidden" name="sendersFax" value="${ not empty letterheadFax ? letterheadFax : param.letterheadFax }" />
			
			<div class="panel panel-default">
			  	<div class="panel-heading">
					<h3 class="panel-title">Fax to</h3>
				</div>
			  	<div class="panel-body">
					<c:out value="${ professionalSpecialistName }" />
					<c:out value="${ not empty fax ? fax : param.fax }" />
					<input type="hidden" name="recipient" value="${ professionalSpecialistName }" />	
					<input type="hidden" name="recipientsFaxNumber" value="${ not empty fax ? fax : param.fax }" />		
				</div>
			</div>
	
			<div class="panel panel-default">
			  	<div class="panel-heading">
					<h3 class="panel-title">Copy(s) to</h3>
				</div>
			  	<div class="panel-body">
			  		<ol class="list-group col-sm-12" >
			  			<c:forEach items="${ copytoRecipients }" var="recipient">
							<li class="list-group-item"> 
								<c:out value="${ recipient.name }" /> <c:out value="${ recipient.fax }" />
							</li>
						</c:forEach>
						<c:forEach items="${ paramValues.faxRecipients }" var="recipient">
							<input type="hidden" name="faxRecipients" value='<c:out value="${ recipient }" />' />
						</c:forEach>
					</ol>
				</div>
			</div>
	
			<div class="panel panel-default">
			  	<div class="panel-heading">
					<h3 class="panel-title">Attachments</h3>
				</div>
			  	<div class="panel-body">
					<ol class="list-group col-sm-12">
						<c:if test="${ not empty documents }">
							<c:forEach items="${ documents }" var="document">
								<li class="list-group-item"><c:out value="${ document }" /></li>
							</c:forEach>
						</c:if>
					</ol>
				</div>
			</div>
			
			<div class="panel panel-default">
			  	<div class="panel-heading">
					<h3 class="panel-title">Add cover page</h3>
				</div>
			  	<div class="panel-body">
					<label class="radio-inline" for="coverpageyes">
						<input type="radio" name="coverpage" id="coverpageyes" value="true" 
							onchange="document.getElementById('comments_container').style.display = 'block';"  />Yes
					</label>
					<label class="radio-inline" for="coverpageno">
						<input type="radio" checked="checked" name="coverpage" id="coverpageno" 
							value="false" onchange="document.getElementById('comments_container').style.display = 'none';" />No
					</label>
					<div class="row" id="comments_container" style="display:none;" >
						<div class="col-sm-12">
							<label for="form-control">Comments</label>
							<textarea class="form-control" name="comments" rows="10" ></textarea>
						</div>
					</div>				
				</div>
			</div>
	
			<div class="form-group">
				<div class="col-sm-12">
					<input class="btn btn-default pull-right" type="submit" value="Send" />
					<a href="${ ctx }/oscarEncounter/ViewRequest.do?de=${ param.demographicNo }&requestId=${ reqId }" 
						class="btn btn-default btn-md pull-right" role="button" aria-pressed="true">Cancel</a>
				</div>
			</div>
	</form>
</div>
</div>
</body>
</html>