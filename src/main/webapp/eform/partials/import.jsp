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
<html>
<%@ page import="oscar.eform.data.*, oscar.eform.*, java.util.*"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<%
String status = (String) request.getAttribute("status");    		
%>
<head>
	<link href="<%=request.getContextPath()%>/css/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/jquery_css/smoothness/jquery-ui-1.10.2.custom.min.css"/>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/jquery-1.9.1.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/jquery-ui-1.10.2.custom.min.js"></script>
	<style>
	body{background-color:#f5f5f5;}
	</style>
</head>

<body>

<c:if test="${ not empty status }">
    <div class="alert alert-success">
    	<button type="button" class="close" data-dismiss="alert">&times;</button>
    	<strong>Success!</strong> Your eform was imported.
    </div>

	<script>
	window.top.location.href = "<%=request.getContextPath()%>/administration/?show=Forms";
	</script>

</c:if>
<form action="<%=request.getContextPath()%>/eform/manageEForm.do" method="POST" enctype="multipart/form-data" id="eformImportForm">
    	
    	<input type="hidden" name="method" value="importEForm">
    	
    	<%List<String> importErrors = (List<String>) request.getAttribute("importErrors");
        if (importErrors != null && importErrors.size() > 0) {%>
	        <div class="row-fluid">
	        	<html:errors />
	        	<ul>
	                <%for (String importError: importErrors) {%>
	                <li class="text-error"><%=importError%></li>
	                <%}%>
	          	</ul> 
	        </div>
        <%}%>
        
        <div class="control-group">
        	<div class="controls">
        		<label class="control-label" for="zippedForm">Import eForm:</label>
		        <input type="file" class="input-file" id="zippedForm" name="zippedForm" size="50" required/>
		        <span style="color:red;">
		         <i class="icon-warning-sign" title="<bean:message key="global.uploadWarningBody"/>" ></i>
		         </span>                      
		        <input type="submit" name="subm" value="Import" class="btn btn-primary upload" disabled>
	        </div>
        </div>
        <div class="row-fluid">
	        <span class="label label-info">Info: </span> 
	        <span>Zip file format only</span>
        </div>

</form>

<script type="text/javascript">
$( document ).ready(function() {
	$(".input-file").change(validate).keyup(validate);
});

function validate() {
	var v = $(this).val();
	var id = $(this).attr("id");
	var formHtml = $("#zippedForm").val();

	if (v && formHtml) {
	    $('.upload').removeAttr("disabled");
		$('.upload').addClass("btn-success");
    } else if(inputCheck=="exists"){
	    $('.upload').attr("disabled", "disabled");
		$('.upload').removeClass("btn-success");
    }else{
	    $('.upload').attr("disabled", "disabled");
		$('.upload').removeClass("btn-success");
    } 
}
	

</script>

</body>
</html>
