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
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
      String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
      boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_con" rights="w" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../../securityError.jsp?type=_con");%>
</security:oscarSec>
<%
if(!authed) {
	return;
}
%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html >
<html>
<head>
	<title><bean:message key="oscarEncounter.oscarConsultationRequest.AttachDocPopup.title" /></title>

	<style>
		.container {
			display: flex;
			width: 95vw;
			min-height: 580px;
			max-height: 93vh; 
		}

		.attachmentList {
			overflow: auto;
			min-width: 320px;
		}

		.preview-button {
			padding: 2px 4px;
			color: black;
			border: none;
			border-radius: 2px;
			cursor: pointer;
		}

		.selectAllHeading {
			justify-content: space-between;
			align-items: center;
		}

		.show-all-button {
			padding: 2px 4px;
			color: black;
			border: none;
			border-radius: 2px;
			cursor: pointer;
			margin-left: auto;
			background-color:lightblue;
		}

		#pdfPreview {
			width: 100%;
		}

		#pdfObject {
			width: 100%;
			height: 100%;
		}

		.flex {
			display: flex !important;
		}

		.hide {
			display: none !important;
		}
	</style>

	<script type="text/javascript">
		let pdfCache = [];

		function toggleSelectAll(element, startClassName) {
			jQuery("[class^='"+ startClassName +"']:not(input[disabled='disabled'])").prop('checked', jQuery(element).prop("checked"));
		}

		function addPdfAttachment(attachmentName, attachmentId, base64Data) {
			const newAttachment = {
				attachmentName,
				attachmentId,
				base64Data
			};
			pdfCache.push(newAttachment);
		}

		function getPdfAttachment(attachmentName, attachmentId) {
			const foundAttachment = pdfCache.find(
				attachment =>
					attachment.attachmentName === attachmentName &&
					attachment.attachmentId === attachmentId
			);

			return foundAttachment ? foundAttachment.base64Data : null;
		}

		function showPDF(base64Data) {
			const pdfObject = document.getElementById('pdfObject');
			let newPdfObject = document.createElement('object'); 
			newPdfObject.setAttribute('data', "data:application/pdf;base64," + base64Data);
			newPdfObject.type = "application/pdf";
			newPdfObject.id = "pdfObject"; 
			pdfObject.parentNode.replaceChild(newPdfObject, pdfObject);
		}

		function getPdf(attachmentName, attachmentId, parameters) {
			ShowSpin(true);
			const base64Data = getPdfAttachment(attachmentName, attachmentId);
			if (base64Data !== null) {
				showPDF(base64Data);
				HideSpin();
				return;
			}

			jQuery.ajax({
				type: 'GET',
				url: "${ pageContext.request.contextPath }/attachDocs.do?" + parameters,
				dataType: "json",
				success: function (data) {
					addPdfAttachment(attachmentName, attachmentId, data.base64Data);
					showPDF(data.base64Data);
					HideSpin();
				},
				error: function (xhr, status, error) {
					console.error("Failed to generate PDF:", xhr.status, xhr.statusText);
					HideSpin();
				}
			});
		}

		function showAll(showButton, attachmentType) {
			let hiddenAttachments = document.getElementsByClassName(attachmentType);
			hiddenAttachments.forEach(function(attachment) {
				attachment.classList.remove('hide');
			});
			showButton.classList.add('hide');
			showButton.parentNode.classList.remove('flex');
		}
	</script>

</head>
<body>
<jsp:include page="../../mcedt/mailbox/spinner.jsp" flush="true"/>
<form id="attachDocumentsForm">
	<div class="container">
		<div class="attachmentList">
			<table id="attachDocumentsPanel" >
				<tr>
					<td><h2>Documents</h2></td>
				</tr>
				<tr>
					<td>
						<ul id="documentList" style="list-style-type: none;padding:0px;">
							<li class="selectAllHeading ${allDocuments.size() > 10 ? 'flex' : ''}">
								<input id="selectAllDocuments" type="checkbox" onclick="toggleSelectAll(this, 'document_');" value="document_check" title="Select/un-select all documents."/>
								<label for="selectAllDocuments">Select all</label>
								<button class="show-all-button ${allDocuments.size() > 10 ? '' : 'hide'}" type="button" title="Show All Documents" onclick="showAll(this, 'doc')">Show All Documents</button>
							</li>
							<c:forEach items="${ allDocuments }" var="document" varStatus="loop">
								<li class="doc ${loop.index > 9 ? 'hide' : ''}">
									<input class="document_check" type="checkbox" name="docNo" id="docNo${document.docId}" value="${document.docId}" title="${ document.description }" />
									<label for="docNo${document.docId}"><c:out value="${ document.description } ${ document.observationDate }" /></label>
									<button class="preview-button" type="button" title="Preview" onclick="getPdf('DOC', '${document.docId}', 'method=getDocumentPDF&fileName=${document.fileName}&description=${document.description}&isImage=${document.isImage()}&isPDF=${document.isPDF()}')">Preview</button>
								</li>
							</c:forEach>
						</ul>

					</td>
				</tr>

				<c:if test="${not empty allLabs }" >
					<tr>
						<td><h2>Labs</h2></td>
					</tr>
					<tr>
						<td>
							<ul id="labList" style="list-style-type: none;padding:0px;">
								<li class="selectAllHeading ${allLabs.size() > 10 ? 'flex' : ''}">
									<input id="selectAllLabs" type="checkbox" onclick="toggleSelectAll(this, 'lab_');" value="lab_check" title="Select/un-select all documents."/>
									<label for="selectAllLabs">Select all</label>
									<button class="show-all-button ${allLabs.size() > 10 ? '' : 'hide'}" type="button" title="Show All Labs" onclick="showAll(this, 'lab')">Show All Labs</button>
								</li>
								<c:forEach items="${ allLabs }" var="lab" varStatus="loop">
									<li class="lab ${loop.index > 9 ? 'hide' : ''}">
										<c:set var="labName" value="${fn:trim(lab.label) != '' ? fn:substring(lab.label, 0, 30) : fn:substring(lab.discipline, 0, 30)}" />
										<input class="lab_check" type="checkbox" name="labNo" id="labNo${ lab.segmentID }" value="${lab.segmentID}" title="${ labName }" />
										<label for="labNo${lab.segmentID}" title="${ labName }" ><c:out value="${ labName } ${ lab.dateObj }" /></label>
										<button class="preview-button" type="button" title="Preview" onclick="getPdf('LAB', '${lab.segmentID}', 'method=getLabPDF&segmentID=${lab.segmentID}')">Preview</button>
									</li>
								</c:forEach>
							</ul>
						</td>
					</tr>
				</c:if>

				<tr>
					<td><h2>Forms (current only)</h2></td>
				</tr>
				<tr>
					<td>
						<ul id="formList" style="list-style-type: none;padding:0px;">
							<li class="selectAllHeading ${allForms.size() > 10 ? 'flex' : ''}">
								<input id="selectAllForms" type="checkbox" onclick="toggleSelectAll(this, 'form_');" value="form_check" title="Select/un-select all forms."/>
								<label for="selectAllForms">Select all</label>
								<button class="show-all-button ${allForms.size() > 10 ? '' : 'hide'}" type="button" title="Show All Forms" onclick="showAll(this, 'form')">Show All Forms</button>
							</li>
							<c:forEach items="${ allForms }" var="form" varStatus="loop">
								<li class="form ${loop.index > 9 ? 'hide' : ''}">
									<input class="form_check" type="checkbox" name="formNo" id="formNo${ form.formId }" value="${form.formId}" title="${form.formName}" />
									<label for="formNo${form.formId}">
										<c:out value="${ form.formName } ${ form.getEdited() }" />
									</label>
									<button class="preview-button" type="button" title="Preview" onclick="getPdf('FORM', '${form.formId}', 'method=getFormPDF&formId=${form.formId}&formName=${form.formName}&demographicNo=${form.getDemoNo()}')">Preview</button>
								</li>
							</c:forEach>
						</ul>
					</td>
				</tr>
				<tr>
					<td><h2>eForms</h2></td>
				</tr>
				<tr>
					<td>
						<ul id="eFormList" style="list-style-type: none;padding:0px;">
							<li class="selectAllHeading ${allEForms.size() > 10 ? 'flex' : ''}">
								<input id="selectAllEForms" type="checkbox" onclick="toggleSelectAll(this, 'eForm_');" value="eForm_check" title="Select/un-select all eForms."/>
								<label for="selectAllEForms">Select all</label>
								<button class="show-all-button ${allEForms.size() > 10 ? '' : 'hide'}" type="button" title="Show All eForms" onclick="showAll(this, 'eForm')">Show All eForms</button>
							</li>
							<c:forEach items="${ allEForms }" var="eForm" varStatus="loop">
								<li class="eForm ${loop.index > 9 ? 'hide' : ''}">
									<input class="eForm_check" type="checkbox" name="eFormNo" id="eFormNo${ eForm.id }" value="${eForm.id}" title="${eForm.formName}" />
									<label for="eFormNo${eForm.id}">
										<c:out value="${ eForm.formName } ${ eForm.getFormDate() }" />
									</label>
									<button class="preview-button" type="button" title="Preview" onclick="getPdf('EFORM', '${eForm.id}', 'method=getEFormPDF&eFormId=${eForm.id}&demographicNo=${eForm.demographicId}')">Preview</button>
								</li>
							</c:forEach>
						</ul>
					</td>
				</tr>
				<tr>
					<td><h2>HRM</h2></td>
				</tr>
				<tr>
					<td>
						<ul id="hrmList" style="list-style-type: none;padding:0px;">
							<li class="selectAllHeading ${allHRMDocuments.size() > 10 ? 'flex' : ''}">
								<input id="selectAllHRMS" type="checkbox" onclick="toggleSelectAll(this, 'hrm_');" value="hrm_check" title="Select/un-select all HRM documents."/>
								<label for="selectAllHRMS">Select all</label>
								<button class="show-all-button ${allHRMDocuments.size() > 10 ? '' : 'hide'}" type="button" title="Show All HRM" onclick="showAll(this, 'hrm')">Show All HRM</button>
							</li>
							<c:forEach items="${ allHRMDocuments }" var="hrm" varStatus="loop">
								<li class="hrm ${loop.index > 9 ? 'hide' : ''}">
									<input class="hrm_check" type="checkbox" name="hrmNo" id="hrmNo${ hrm['id'] }" value="${hrm['id']}" title="${hrm['name']}" />
									<label for="hrmNo${hrm['id']}">
										<c:out value="${ hrm['name'] } ${ hrm['report_date'] }" />
									</label>
									<button class="preview-button" type="button" title="Preview" onclick="getPdf('HRM', '${hrm.id}', 'method=getHRMPDF&hrmId=${hrm.id}')">Preview</button>
								</li>
							</c:forEach>
						</ul>
					</td>
				</tr>
			</table>
		</div>

		<div id="pdfPreview">
			<object id="pdfObject" type="application/pdf" data="">
			</object>
		</div>
	</div>
</form>
</body>
</html>
