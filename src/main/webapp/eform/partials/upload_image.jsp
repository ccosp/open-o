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
<%@ page import="oscar.eform.data.*, oscar.OscarProperties, oscar.eform.*, java.util.*"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<html:html locale="true">

<head>
<link href="<%=request.getContextPath()%>/css/bootstrap.min.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/jquery_css/smoothness/jquery-ui-1.10.2.custom.min.css"/>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/jquery-1.9.1.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/jquery-ui-1.10.2.custom.min.js"></script>

<script>
$(function() {
    $( document ).tooltip();
  });
</script>
</head>

<style>
.message div{
font-size: 12px;
display:inline; 
}

h3{
margin:0px; 
display:inline; 
font-weight:normal;
font-size: 12px;
}

.message ul{
margin:0px;
padding:0px;
display:inline;
}

.message li{
list-style: none; 
margin:0px;
display:inline;  
padding-left:6px;
}

</style>
<body>

<c:if test="${ not empty status }">
    <div class="alert alert-success">
    <button type="button" class="close" data-dismiss="alert">&times;</button>
    <strong>Success!</strong> Your image was uploaded.
    </div>
    <script>
	window.top.location.href = "<%=request.getContextPath()%>/administration/?show=ImageUpload";
	</script> 
    
</c:if>



	<div class="row-fluid">
		<div class="well">		
		<html:form action="/eform/imageUpload" enctype="multipart/form-data" method="post">

		<div class="text-error message row-fluid"><html:errors /></div>
		<div class="control-group">
        	<div class="controls">
        		<label class="control-label" for="zippedForm"><bean:message key="eform.uploadimages.msgFileName" /></label>
				<input type="file" name="image" id="image" class="check" size="40"  required>
				<span style="color:red;">
		         <i class="icon-warning-sign" title="<bean:message key="global.uploadWarningBody"/>" ></i>
		         </span>
				<input type="submit" class="btn btn-primary upload" name="subm" value="<bean:message key="eform.uploadimages.btnUpload"/>" disabled>
			</div>
		</div>
		</html:form>
		</div>
	</div>

<script src="<%=request.getContextPath() %>/js/jquery-1.9.1.min.js"></script>  

<script>
$( document ).ready(function() {
$(".check").change(validate).keyup(validate);
});

function validate()
{

var id = $(this).attr("id");
var formHtml = $("#image").val();
var file = $('#image')[0].files[0];
//var filename = file.name;
//var fileSize = file.size;

//var inputCheck=checkRow(filename);

if (formHtml!="") {
	    $('.upload').removeAttr("disabled");
		$('.upload').addClass("btn-success");
    }else{
	    $('.upload').attr("disabled", "disabled");
		$('.upload').removeClass("btn-success");
    } 
}

</script>
</body>
</html:html>