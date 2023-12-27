<!DOCTYPE html>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ taglib prefix="html" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
<title>OSCAR Email</title>

	<c:set var="ctx" value="${ pageContext.request.contextPath }" scope="page" />
	<link rel="stylesheet" href="${ctx}/library/bootstrap/3.0.0/css/bootstrap.min.css" type="text/css" />
    <link href="${ctx}/library/jquery/jquery-ui.min.css" rel="stylesheet" type="text/css"/>

	<script type="text/javascript" src="${ctx}/library/jquery/jquery-1.12.0.min.js" ></script>  
	<script type="text/javascript" src="${ctx}/library/jquery/jquery.validate.min.js" ></script> 	              
	<script type="text/javascript" src="${ctx}/library/jquery/jquery-ui.min.js" ></script>	
	<script type="text/javascript" src="${ctx}/library/bootstrap/3.0.0/js/bootstrap.min.js" ></script> 
	
	<%-- 
		Action return flashy confirmation messages.
	--%>
	<c:if test="${ not empty emailSuccessful }">
		<script type="text/javascript" >
			$(document).ready(function() {
				$("#page-body").slideUp("slow");
			})
		</script>
	</c:if>
	
	<style type="text/css">
	
	* {
		font-family: Arial,Helvetica,sans-serif;
		font-size: small;
	}

	img {
		max-width: 100%;
		height: auto;
		width: auto\9;
	}
	
	#additionalRecipientControlPanel, #form-control-buttons {
		margin-bottom: 15px;
	}
	
	#form-control-buttons button {
		margin-left: 15px;
	}
	
	ul.ui-widget {
		margin: 10px;
		max-width: 100%;
		height: auto;
		max-height: 400px;
		overflow-y: scroll;
	}
	
	.recipientGroup {
		margin-bottom: 3px;
	}
		
	#oscarEmailHeader {
		width: 100%;
		border-collapse: collapse;
		margin-top: .5%;
		margin-bottom: 15px;
	}
	
	table#oscarEmailHeader tr td {
		padding: 1px 5px;
		background-color: #F3F3F3;
	}
	
	#oscarEmailHeader #oscarEmailHeaderLeftColumn {
		width: 19.5% !important;
		background-color: white;
		padding: 0px;
		padding-right: .5% !important;
		width: 20%;
	}
	
	#oscarEmailHeader #oscarEmailHeaderLeftColumn h1 {
		margin: 0px;
		padding: 7px !important;
		display: block;
		font-size: large !important;
		background-color: black;
		color: white;
		font-weight: bold;
	}
	#oscarEmailHeaderRightColumn {
		vertical-align: top;
		text-align: right;
		padding-top: 3px;
		padding-right: 3px;
	}

	span.HelpAboutLogout a {
		font-size: x-small;
		color: black;
		float: right;
		padding: 0 3px;
	}
	
	label.invalid {
		color:red;
		font-weight: normal;
	}
	
	input.invalid {
		border-color:red;
	}

</style>

</head>
<body>
<jsp:include page="../images/spinner.jsp" flush="true"/>
<div id="bodyrow" class="container-fluid">

	<div id="bodycolumn" class="col-sm-12">

		<div id="page-header">
			<table id="oscarEmailHeader">
				<tr>
					<td id="oscarEmailHeaderLeftColumn"><h1>OSCAR Email</h1></td>
	
					<td id="oscarEmailHeaderRightColumn" align=right>
						<span class="HelpAboutLogout"> 
							<a style="font-size: 10px; font-style: normal;" href="${ ctx }oscarEncounter/About.jsp" target="_new">About</a>
							<a style="font-size: 10px; font-style: normal;" target="_blank"
										href="http://www.oscarmanual.org/search?SearchableText=&Title=Chart+Interface&portal_type%3Alist=Document">Help</a>		
						</span>
					</td>
				</tr>
			</table>
		</div>
		
		<div id="page-body">
		
			<c:set var="emailSendAction" value="${ctx}/email/emailSendAction.do?method=sendEmail" />
			
			<form id="emailComposeForm" class="form-inline" action='${ emailSendAction }' method="post" novalidate>							
				<div class="panel panel-default">
				  	<div class="panel-heading">
						<h3 class="panel-title">From</h3>
					</div>
					<div class="panel-body">
						<div class="container">
							<div class="row">	
							<div class="col-sm-12">				
							  <label for="senderEmail">Sender Email</label>
							  <select class="form-control" name="senderEmailAddress"  id="senderEmailAddress">
									<c:forEach items="${ senderAccounts }" var="senderAccount">
							    		<option value="${ senderAccount.senderEmail }" ${ senderAccount.senderEmail eq letterheadEmail or senderAccount.senderEmail eq param.letterheadEmail ? 'selected' : '' }>
							    			<c:out value="${ senderAccount.senderFirstName }"/> <c:out value="${ senderAccount.senderLastname }"/> <c:out value="(${ senderAccount.senderEmail })"/>
							    		</option>
									</c:forEach>
							  </select>
							</div>
							</div>
						</div>
					</div>
				</div>
				
				<div class="panel panel-default">
				  	<div class="panel-heading">
						<h3 class="panel-title">To</h3>
					</div>
				  	<div class="panel-body">
						<div class="container">
						  	<div class="row" id="receiverEmail">	
								<div class="col-sm-6 form-group">
									<label for="receiverName">Name</label>
								 	<input class="autocomplete form-control" type="text" name="recipient" value="${ receiverName }"
								 		id="receiverName" placeholder="Search: last, first" disabled/>
								 </div>	
								 <div class="col-sm-6 form-group">
									<label for="receiverEmail">Email</label>
									<input class="form-control" type="text" name="receiverEmailAddress" value="${ receiverEmail }"
										id="receiverEmail" placeholder="example@example.com"  disabled/>
									<c:if test="${not empty receiverEmail}">
										<input type="hidden" name="receiverEmailAddress" value="${receiverEmail}" />
									</c:if>
								</div>
							</div>
						</div>
					</div>
				</div>

				<div class="panel panel-default">
				  	<div class="panel-heading">
						<h3 class="panel-title">Subject</h3>
					</div>
					<div class="panel-body">
						<div class="container">
							<div class="row">	
							<div class="col-sm-12">				
							  <input class="form-control" type="text" name="subjectEmail" value="" id="subjectEmail" placeholder="" />
							</div>
							</div>
						</div>
					</div>
				</div>

				<div class="panel panel-default">
				  	<div class="panel-heading">
						<h3 class="panel-title">Body</h3>
					</div>
					<div class="panel-body">
						<div class="container">
							<div class="row">	
							<div class="col-sm-12">				
							  <textarea class="form-control" name="bodyEmail" id="bodyEmail" rows="10"></textarea>
							</div>
							</div>
						</div>
					</div>
				</div>
		
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title">Attachments</h3>
                    </div>
                    <div class="panel-body">
                        <div class="container">
                            <div class="row">
                                <ol class="list-group col-sm-12">
                                    <c:forEach items="${ emailAttachmentList }" var="emailAttachment">
                                        <li class="list-group-item"><c:out value="${ emailAttachment.fileName }" /></li>
                                    </c:forEach>
                                </ol>
                            </div>
                        </div>
                    </div>
                </div>
				
				<div class="container" id="form-control-buttons">
					<div class="row">
					<div class="col-sm-12">
						<input type="hidden" id="submitMethod" name="method" value="queue" />
						<button type="submit" id="btnSend" class="btn btn-primary btn-md pull-right" value="Send">
							<span class="btn-label"><i class="glyphicon glyphicon-send"></i></span>
							Send
						</button>					
						<button formnovalidate="formnovalidate" id="btnCancel" type="submit" class="btn btn-danger btn-md pull-right" value="Cancel" 
							onclick="window.close();" >
							<span class="btn-label"><i class="glyphicon glyphicon-remove-circle"></i></span>
							Cancel
						</button>
					</div>
					</div>
				</div>
		</form>
	</div>
	
	<%-- the confirmation tags. --%>
	<c:if test="${ not empty emailSuccessful }">
		<c:choose>
			<c:when test="${ emailLog.status eq 'FAILED' }">
				<div class="alert alert-danger" role="alert">
					Failed to email: <c:out value="${ emailLog.fromEmail } at ${ emailLog.toEmail }" />
				</div>								
			</c:when>
			<c:otherwise>
				<div class="alert alert-success" role="alert">
					Successfully emailed: <c:out value="${ emailLog.fromEmail } at ${ emailLog.toEmail }" />
				</div>	
			</c:otherwise>				
		</c:choose>
		<input type="button" class="btn btn-danger btn-md pull-right" value="Close" onclick="window.close();" />
	</c:if>
</div>
</div>

<script type="text/javascript" >
var ctx = "${ ctx }";

$(document).ready(function() {

	/*
	* Auto complete methods.
	*/
	$( "#receiverEmail .autocomplete" ).autocomplete({				
		source: function( request, response ) { 
			var url = ctx + "/demographic/Contact.do?method=searchAllContacts&searchMode=search_name&orderBy=c.lastName,c.firstName";
			jQuery.ajax({
				url: url,
				type: "GET",
				dataType: "json",
				data: {
				 	term: request.term   			  
				},
				contentType: "application/json",
				success: function( data ) {
					response(jQuery.map(data, function( item ) {					
						return {
						  label: item.lastName + ", " 
						  + item.firstName + " :: "
						  + item.residencePhone
						  + " :: " + item.address 
						  + " " + item.city,
						  value: item.id,
						  contact: item
						 }						 
					}));
				}
			});
	    },
	    minLength: 2,  
	    focus: function( event, ui ) {
	    		event.preventDefault();
	        return false;
	    },
	    select: function(event, ui) { 
	    		event.preventDefault(); 
	    		$("#" + this.id).val(ui.item.contact.lastName + ", " + ui.item.contact.firstName);
	           	$("#" + this.id.split("_")[0] + "_fax").val(ui.item.contact.fax);
	    }
	});
	
	
	/*
	 * Validate the form before submission 
	 */
 	$('#emailComposeForm').validate({		
		rules: {
			subjectEmail: {
				required: true
			},
			bodyEmail: {
				required: true
			}
		},		
		messages: {
			subjectEmail: "Subject is required",
			bodyEmail: "Body is required"
		},
		errorClass: 'invalid',
		validClass: 'valid',
		submitHandler: function (form) {
			ShowSpin(true);
			form.submit();
		}
	});
	 
})

</script>
</body>
</html>