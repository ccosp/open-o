<!DOCTYPE html>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ taglib prefix="html" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
<title>OSCAR Email</title>

	<c:set var="ctx" value="${ pageContext.request.contextPath }" scope="page" />
	<link rel="stylesheet" href="${ctx}/library/bootstrap/5.0.2/css/bootstrap.min.css" type="text/css" />
    <link href="${ctx}/library/jquery/jquery-ui.min.css" rel="stylesheet" type="text/css"/>
	<link href="${ctx}/css/font-awesome.min.css" rel="stylesheet">

	<script type="text/javascript" src="${ctx}/library/jquery/jquery-1.12.0.min.js" ></script>  
	<script type="text/javascript" src="${ctx}/library/jquery/jquery.validate.min.js" ></script> 	              
	<script type="text/javascript" src="${ctx}/library/jquery/jquery-ui.min.js" ></script>	
	<script type="text/javascript" src="${ctx}/library/bootstrap/5.0.2/js/bootstrap.min.js" ></script> 
	
	<%-- 
		Action return flashy confirmation messages.
	--%>
	<c:if test="${ not empty isEmailSuccessful }">
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

	.encryptionLock {
		float: right;
	}

	#isEncryption {
		color: green;
		font-size: 15px;
	}

	#isEncryption.off {
		color: red;
	}

	.accordion-button i {
		margin-right: 5px; 
	}

	.hide {
		display: none; 
	}

	.error-message {
		color: red;
		font-size: 12px;
		margin-top: 5px;
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
		
			<c:set var="emailSendAction" value="${ctx}/email/emailSendAction.do?method=sendEFormEmail" />
			
			<form id="emailComposeForm" class="form-inline" action='${ emailSendAction }' method="post" onsubmit="return validateEmailForm()" novalidate>	
				<input type="hidden" name="demographicId" value="${demographicId}" />
				<input type="hidden" name="fdid" value="${fdid}" />
				<input type="hidden" name="openEFormAfterEmail" value="${openEFormAfterEmail}" />
				<input type="hidden" name="transactionType" value="${transactionType}" />	

				<div class="card">
				  	<div class="card-header">
						<h5 class="card-title">From</h5>
					</div>
					<div class="card-body">
						<div class="container">
							<div class="row">	
							<div class="col-sm-12 form-group">
								<label for="senderEmailAddress">Sender</label>
								<select class="form-select" name="senderEmailAddress"  id="senderEmailAddress">
									<c:forEach items="${ senderAccounts }" var="senderAccount">
										<option value="${ senderAccount.senderEmail }" ${ senderAccount.senderEmail eq senderEmail or senderAccount.senderEmail eq param.senderEmail ? 'selected' : '' }>
											<c:out value="${ senderAccount.senderFirstName }"/> <c:out value="${ senderAccount.senderLastname }"/> <c:out value="(${ senderAccount.senderEmail })"/>
										</option>
									</c:forEach>
								</select>
							</div>
							</div>
						</div>
					</div>
				</div>
				
				<div class="card mt-4">
				  	<div class="card-header">
						<h5 class="card-title">To</h5>
					</div>
				  	<div class="card-body">
						<div class="container">
						  	<div class="row form-group">	
								<div class="col-sm-1">
									<label for="receiverName">Patient</label>
								</div>
								<div class="col-sm-10">
									<input class="autocomplete form-control" type="text" name="recipient" value="${ receiverName }" id="receiverName" placeholder="Search: last, first" disabled/>
								</div>
							</div>
							<div id="receiverEmailsContainer">	
								<c:forEach items="${ receiverEmailList }" var="receiverEmail">
								<div class="row form-group mt-3">
									<div class="col-sm-1">
										<label for="receiverEmailAddress">Email(s)</label>
									</div>
									<div class="col-sm-10">
										<input class="form-control" type="email" name="receiverEmailAddress" value="${ receiverEmail }" id="receiverEmail" placeholder="example@example.com"  disabled/>
										<c:if test="${not empty receiverEmail}">
											<input type="hidden" name="receiverEmailAddress" value="${receiverEmail}" />
										</c:if>
									</div>
									<div class="col-sm-1">
										<button type="button" title="Remove Email" class="btn btn-danger" onclick="removeReceiverEmail(this)"><i class="icon-remove"></i></button>
									</div>
								</div>
								</c:forEach>
							</div>
						</div>
					</div>
					<div class="card-footer">
						<span class="icon-warning-sign"></span> Patient's consent for email communication: <b><c:out value="${ emailConsentStatus }" /></b>
						<input type="hidden" name="emailConsentStatus" value="${emailConsentStatus}" />
					</div>
				</div>

				<div class="card mt-4">
				  	<div class="card-header">
						<h5 class="card-title">Subject</h5>
					</div>
					<div class="card-body">
						<div class="container">
							<div class="row">	
							<div class="col-sm-12">	
								<input class="form-control" type="text" name="subjectEmail" id="subjectEmail" placeholder="Subject" value='${ param.subjectEmail }' autocomplete="off" />			
							  <div class="error-message" id="subjectError"></div>
							</div>
							</div>
						</div>
					</div>
				</div>

				<div class="card mt-4">
				  	<div class="card-header">
						<h5 class="card-title">Body</h5>
					</div>
					<div class="card-body">
						<div class="container">
							<div class="row">	
							<div class="col-sm-12">				
							  <textarea class="form-control" name="bodyEmail" id="bodyEmail" rows="7" placeholder="@message..."><c:out value="${ param.bodyEmail }" /></textarea>
							  <div class="error-message" id="bodyError"></div>
							</div>
							</div>
						</div>
					</div>
					<div class="card-footer">
						<span class="icon-warning-sign"></span> Unencrypted Body of Email
					</div>
				</div>

				<div class="card mt-4">
				  	<div class="card-header">
						<h5 class="card-title">
							<span class="icon-lock"></span> Encryption <span id="encryptionOptionsInfo" class="icon-info-sign" data-toggle="tooltip" data-placement="auto right" title="Emails will be sent encrypted by default. Encryption settings can be modified by disabling this feature."></span>
							<div class="form-check form-switch encryptionLock">
								<input class="form-check-input" type="checkbox" id="encryptionSwitch" checked onClick="showEncryptionOptions(this)">
								<label class="form-check-label" for="encryptionSwitch" id="isEncryption">ON</label>
							</div>
						</h5>
					</div>
					<div class="card-body" id="encryptionOptions">
						<div class="container">
							<div class="row">	
								<div class="col-sm-12 form-group"> 
									<label>Encrypted message <span id="encryptedMessageInfo" class="icon-info-sign" data-toggle="tooltip" data-placement="auto right" title="Message will be added into the encrypted pdf"></span></label>
									<textarea class="form-control" name="encryptedMessage" id="encryptedMessage" rows="5" placeholder="..."><c:out value="${ param.encryptedMessageEmail }" /></textarea>
									<div class="error-message" id="encryptedMessageError"></div>
								</div>
							</div>
							<div class="row mt-3 form-group">	
								<div class="col-sm-2"> 
									<label>Password</label>
								</div>
								<div class="col-sm-10"> 
									<input class="form-control" type="text" name="emailPDFPassword" id="emailPDFPassword" placeholder="YYYYMMDDHIN" value='${not empty param.passwordEmail ? param.passwordEmail : emailPDFPassword}' autocomplete="off" /> 
									<div class="error-message" id="emailPDFPasswordError"></div>
								</div>
							</div>
							<div class="row mt-3 form-group">
								<div class="col-sm-2">				
									<label>Clue <span id="clueInfo" class="icon-info-sign" data-toggle="tooltip" data-placement="auto right" title="Clue will be added into the email body (visible)"></span></label>
								</div>
								<div class="col-sm-10">
									<c:set var="passwordClue" value="${not empty param.passwordClueEmail ? param.passwordClueEmail : 'To protect your privacy, the PDF attachments in this email have been encrypted with a 18 digit password - your date of birth in the format YYYYMMDD followed by the 10 digits of your health insurance number.'}" />
									<textarea class="form-control" name="emailPDFPasswordClue" id="emailPDFPasswordClue" rows="2" placeholder="..."><c:out value="${ passwordClue }" /></textarea>
									<div class="error-message" id="emailPDFPasswordClueError"></div>
								</div>	
							</div>
							<div class="row mt-3 form-group">
								<div class="col-sm-2">				
									<label>Encrypt Attachments <span id="encryptAttachmentInfo" class="icon-info-sign" data-toggle="tooltip" data-placement="auto right" title="Email attachments will be encrypted when enabled"></span></label>
								</div>
								<div class="col-sm-10">
									<div class="form-check form-switch">
										<input class="form-check-input" type="checkbox" id="encryptAttachmentSwitch" onClick="toggleEncryptAttachmentStatus(this)" ${ emailAttachmentList.size() gt 0 or isEmailAttachmentEncrypted ? 'checked' : 'disabled' }>
									</div>
								</div>	
							</div>
							<input type="hidden" name="isEmailAttachmentEncrypted" id="isEmailAttachmentEncrypted" value="${ emailAttachmentList.size() gt 0 or isEmailAttachmentEncrypted ? 'true' : 'false' }"/>
							<input type="hidden" name="isEmailEncrypted" id="isEmailEncrypted" value="true"/>
						</div>
					</div>
				</div>

				<div class="card mt-4">
				  	<div class="card-header">
						<h5 class="card-title">
							Additional options
						</h5>
					</div>
					<div class="card-body">
						<div class="container">
							<div class="row">	
								<div class="col-sm-12"> 
									<label>Chart options</label>
									<div class="form-check">
										<div class="form-check-label">
											<input class="form-check-input" type="radio" name="patientChartOption" value="doNotAddAsNote">
											Do not add to patient chart
										</div>
									</div>
									<div class="form-check">
										<div class="form-check-label">
											<input class="form-check-input" type="radio" name="patientChartOption" value="addFullNote" checked>
											Chart as new note in patient's chart
										</div>
									</div>
								</div>
							</div>
							<hr/>
							<div class="row">	
								<div class="col-sm-12"> 
									<label>Document options</label>
									<div class="form-check">
										<div class="form-check-label">
											<input class="form-check-input" type="radio" name="emailPDFOption" value="doNotAddAsPDF" checked>
											Do not add as PDF to patient's chart
										</div>
									</div>
									<div class="form-check">
										<div class="form-check-label">
											<input class="form-check-input" type="radio" name="emailPDFOption" value="addFullPDF">
											Add full email (excluding attachments) as PDF to patient's chart
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
		
                <div class="card mt-4">
                    <div class="card-header">
                        <h5 class="card-title">Attachments</h5>
                    </div>
                    <div class="card-body">
                        <div class="container">
                            <div class="row">
								<div class="accordion col-sm-12" id="emailAttachmentList">
									<c:forEach items="${ emailAttachmentList }" var="emailAttachment" varStatus="loop">
										<div class="accordion-item emailAttachmentItem">
											<div class="accordion-header" id="emailAttachmentHeader${loop.index + 1}">
												<button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#emailAttachmentBody${loop.index + 1}" aria-expanded="false" aria-controls="emailAttachmentBody${loop.index + 1}">
													<i class="icon-file"></i> <c:out value="${emailAttachment.fileName}" />
												</button>
											</div>
											<div id="emailAttachmentBody${loop.index + 1}" class="accordion-collapse collapse" aria-labelledby="emailAttachmentHeader${loop.index + 1}" data-bs-parent="#emailAttachmentList">
												<div class="accordion-body">
													<object id="emailAttachmentPDF${loop.index + 1}" data="${ctx}/previewDocs.do?method=renderPDF&pdfPath=${emailAttachment.filePath}" type="application/pdf" width="100%" height="500">
													</object>
												</div>
											</div>
										</div>
                                    </c:forEach>
								</div>
                            </div>
                        </div>
                    </div>
                </div>
				
				<div class="container mt-4" id="form-control-buttons">
					<div class="row">
					<div class="col-sm-12">
						<input type="hidden" id="submitMethod" name="method" value="queue" />
						<button type="submit" id="btnSend" class="btn btn-primary btn-md pull-right" value="Send">
							<span class="btn-label"><i class="icon-location-arrow"></i></span>
							Send
						</button>					
						<button formnovalidate="formnovalidate" id="btnCancel" type="submit" class="btn btn-danger btn-md pull-right" value="Cancel" 
							onclick="window.close();" >
							<span class="btn-label"><i class="icon-remove"></i></span>
							Cancel
						</button>
					</div>
					</div>
				</div>
		</form>
	</div>
	
	<%-- the confirmation tags. --%>
	<c:if test="${ not empty isEmailSuccessful }">
		<c:choose>
			<c:when test="${ emailLog.status eq 'SUCCESS' }">
				<div class="alert alert-success" role="alert">
					Successfully emailed from <c:out value="${emailLog.fromEmail}" /> to: <c:out value="${fn:join(emailLog.toEmail, ', ')}" />
				</div>								
			</c:when>
			<c:otherwise>
				<div class="alert alert-danger" role="alert">
					Failed to email from: <c:out value="${ emailLog.fromEmail }" /> to: <c:out value="${fn:join(emailLog.toEmail, ', ')}" /> <br/>
				</div>
			</c:otherwise>				
		</c:choose>
		<input type="button" class="btn btn-danger btn-md pull-right" value="Close" onclick="window.close();" />
	</c:if>
</div>
</div>

<script type="text/javascript" >
document.addEventListener("DOMContentLoaded", function () {
	// Open EForm again on sent
	openEFormAfterSend();
	// Auto-send email
	autoSendEmail();
});

function validateEmailForm() {
	if (!validateForm()) { return false; } 
	ShowSpin(true);
	return true;
}

function validateForm() {
	const subjectEmail = document.getElementById('subjectEmail');
	const bodyEmail = document.getElementById('bodyEmail');
	const isEncrypted = document.getElementById('encryptionSwitch').checked;
	const hasEncryptedMessage = document.getElementById('encryptedMessage').value.trim() !== '';
	const isAttachmentEncrypted = document.getElementById('encryptAttachmentSwitch').checked;
	const emailPDFPassword = document.getElementById('emailPDFPassword');
	const emailPDFPasswordClue = document.getElementById('emailPDFPasswordClue');
	const hasAttachments = document.querySelectorAll('.emailAttachmentItem').length > 0;

	const errors = {};

	validateField(subjectEmail, 'Subject is required', errors, 'subjectError');
	validateField(bodyEmail, 'Body is required', errors, 'bodyError');
	if (isEncrypted) {
		if (!hasEncryptedMessage && hasAttachments && isAttachmentEncrypted) {
			validateField(emailPDFPassword, 'Password is required', errors, 'emailPDFPasswordError');
			validateField(emailPDFPasswordClue, 'Clue is required', errors, 'emailPDFPasswordClueError');
		} else if (hasEncryptedMessage && !hasAttachments && !isAttachmentEncrypted) {
			validateField(emailPDFPassword, 'Password is required', errors, 'emailPDFPasswordError');
			validateField(emailPDFPasswordClue, 'Clue is required', errors, 'emailPDFPasswordClueError');
		} else if (hasEncryptedMessage && hasAttachments && !isAttachmentEncrypted) {
			validateField(emailPDFPassword, 'Password is required', errors, 'emailPDFPasswordError');
			validateField(emailPDFPasswordClue, 'Clue is required', errors, 'emailPDFPasswordClueError');
		} else if (hasEncryptedMessage && hasAttachments && isAttachmentEncrypted) {
			validateField(emailPDFPassword, 'Password is required', errors, 'emailPDFPasswordError');
			validateField(emailPDFPasswordClue, 'Clue is required', errors, 'emailPDFPasswordClueError');
		} else {
			clearError('emailPDFPasswordError');
			clearError('emailPDFPasswordClueError');
		}
	}

	if (Object.keys(errors).length === 0) {
		return true;
	}
	return false;
}

function validateField(field, errorMessage, errors, errorElementId) {
	clearError(errorElementId);

	if (field.value.trim() === '') {
		errors[field.name] = errorMessage;
		displayError(errorElementId, errorMessage);
	} else if (field.value.trim().length < 5 && field.id === 'emailPDFPassword') {
		errorMessage = 'Password must be at least 5 characters long.';
		errors[field.name] = errorMessage;
		displayError(errorElementId, errorMessage);
	}
}

function displayError(errorElementId, errorMessage) {
	const errorElement = document.getElementById(errorElementId);
	errorElement.innerHTML = errorMessage;
}

function clearError(errorElementId) {
	const errorElement = document.getElementById(errorElementId);
	errorElement.innerHTML = '';
}

function showEncryptionOptions(checkbox) {
	document.getElementById("encryptionOptions").classList.toggle('hide', !checkbox.checked);
	document.getElementById("isEmailEncrypted").value = checkbox.checked ? "true" : "false";
	document.getElementById("isEncryption").innerHTML = checkbox.checked ? "ON" : "OFF";
	document.getElementById("isEncryption").classList.toggle("off", !checkbox.checked);
}

function toggleEncryptAttachmentStatus(checkbox) {
	document.getElementById("isEmailAttachmentEncrypted").value = checkbox.checked ? "true" : "false";
}

function removeReceiverEmail(button) {
	let receiverEmailsContainer = document.getElementById("receiverEmailsContainer");
	let formGroup = button.closest('.form-group');
	if (receiverEmailsContainer.children.length > 1) {
		receiverEmailsContainer.removeChild(formGroup);
	} else {
		alert("Need one email");
	}
}

// Open EForm again on sent
function openEFormAfterSend() {
	const isOpenEForm = "${isOpenEForm}" === "true";
	if (isOpenEForm) { window.open("${ctx}/eform/efmshowform_data.jsp?fdid=${fdid}", "_blank", "width=800,height=600"); }
}

// Auto-send email
function autoSendEmail() {
	const emailComposeForm = document.getElementById('emailComposeForm');
	const isAutoSend = "${isEmailAutoSend}" === "true";
	if (isAutoSend && validateForm()) {
		ShowSpin(true); 
		emailComposeForm.submit(); 
	}
}

</script>
</body>
</html>