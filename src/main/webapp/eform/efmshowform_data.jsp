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
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<%@ page import="java.sql.*, oscar.eform.data.*"%>
<%@ page import="org.oscarehr.util.LoggedInInfo"%>
<%@ page import="oscar.oscarEncounter.data.EctFormData"%>
<%@ page import="org.oscarehr.common.model.enumerator.DocumentType"%>
<%@ page import="org.oscarehr.documentManager.DocumentAttachmentManager"%>
<%@ page import="org.oscarehr.util.SpringUtils"%>
<%@ page import="oscar.util.StringUtils" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>

<%--
	Addition of a floating global toolbar specifically for activation of the 
	Fax and eDocument functions.
--%>
    <jsp:include page="../images/spinner.jsp" flush="true"/>

<%
    /**
     * TODO: Move all JSP scriptlet code from efmshowform_data.jsp and efmformadd_data.jsp to the ShowEFormAction.java (create if necessary) action file.
     */
	String id = request.getParameter("fid");
	String messageOnFailure = "No eform or appointment is available";
  if (id == null) {  // form exists in patient
      id = StringUtils.isNullOrEmpty(request.getParameter("fdid")) ? ((String) request.getAttribute("fdid")) : request.getParameter("fdid");
      String appointmentNo = request.getParameter("appointment");
      String eformLink = request.getParameter("eform_link");

      EForm eForm = new EForm(id);
      eForm.setContextPath(request.getContextPath());
      eForm.setOscarOPEN(request.getRequestURI());
      eForm.setFdid(id);
      
      if ( appointmentNo != null ) eForm.setAppointmentNo(appointmentNo);
      if ( eformLink != null ) eForm.setEformLink(eformLink);

      String parentAjaxId = request.getParameter("parentAjaxId");
      if( parentAjaxId != null ) eForm.setAction(parentAjaxId);
      request.setAttribute("demographicId", eForm.getDemographicNo());
      request.setAttribute("fid", eForm.getFid());
      out.print(eForm.getFormHtml());
  } else {  //if form is viewed from admin screen
      EForm eForm = new EForm(id, "-1"); //form cannot be submitted, demographic_no "-1" indicate this specialty
      eForm.setContextPath(request.getContextPath());
      eForm.setupInputFields();
      eForm.setOscarOPEN(request.getRequestURI());
      eForm.setImagePath();
      eForm.setFdid("");
      out.print(eForm.getFormHtml());
  }
%>

<c:if test="${ sessionScope.useIframeResizing }" >
	<script type="text/javascript" src="${ pageContext.servletContext.contextPath }/library/pym.js"></script>
	<script type="text/javascript">
	    var pymChild = new pym.Child({ polling: 500 });
	</script>
</c:if>

<!DOCTYPE html>
<html>
<head>
	<title>Show EForm</title>
	<script type="text/javascript" src="<%=request.getContextPath()%>/library/jquery/jquery-3.6.4.min.js" ></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/library/jquery/jquery-ui-1.12.1.min.js" ></script>
	<link href="<%=request.getContextPath() %>/library/jquery/jquery-ui-1.12.1.min.css" rel="stylesheet" media="screen" />
	<script type="text/javascript" src="${ pageContext.servletContext.contextPath }/eform/eformFloatingToolbar/eform_floating_toolbar.js" ></script>
    <script type="text/javascript">
        const eFormPDFName = '<%=request.getAttribute("eFormPDFName")%>';
        const eFormPDF = '<%=request.getAttribute("eFormPDF")%>';
        const isDownloadEForm = '<%=request.getAttribute("isDownload")%>';
        if (eFormPDF !== 'null' && eFormPDFName !== 'null' && isDownloadEForm === 'true') {
            downloadEForm(eFormPDFName, eFormPDF);
        }

        // NOTE: Do not add onload methods here; instead, find alternative approaches to achieve the same result, as it may disrupt eForms' onload functionalities.
        window.resizeTo(1100, 1100);
    </script>
</head>
<body>
    <%
        /**
        * TODO: Move all JSP scriptlet code from efmshowform_data.jsp and efmformadd_data.jsp to the ShowEFormAction.java (create if necessary) action file.
        */
        DocumentAttachmentManager documentAttachmentManager = SpringUtils.getBean(DocumentAttachmentManager.class);
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        String fdid = StringUtils.isNullOrEmpty(request.getParameter("fdid")) ? ((String) request.getAttribute("fdid")) : request.getParameter("fdid");
        String demographicNo = StringUtils.isNullOrEmpty(request.getParameter("demographic_no")) ? ((String) request.getAttribute("demographicId")) : request.getParameter("demographic_no");
        List<String> attachedDocumentIds = new ArrayList<>();
        List<String> attachedEFormIds = new ArrayList<>();
        List<String> attachedHRMDocumentIds = new ArrayList<>();
        List<String> attachedLabIds = new ArrayList<>();
        List<EctFormData.PatientForm> attachedForms = new ArrayList<>();
        if (fdid != null && demographicNo != null) {
            attachedDocumentIds = documentAttachmentManager.getEFormAttachments(loggedInInfo, Integer.parseInt(fdid), DocumentType.DOC, Integer.parseInt(demographicNo));
            attachedEFormIds = documentAttachmentManager.getEFormAttachments(loggedInInfo, Integer.parseInt(fdid), DocumentType.EFORM, Integer.parseInt(demographicNo));
            attachedHRMDocumentIds = documentAttachmentManager.getEFormAttachments(loggedInInfo, Integer.parseInt(fdid), DocumentType.HRM, Integer.parseInt(demographicNo));
            attachedLabIds = documentAttachmentManager.getEFormAttachments(loggedInInfo, Integer.parseInt(fdid), DocumentType.LAB, Integer.parseInt(demographicNo));
            attachedForms = documentAttachmentManager.getFormsAttachedToEForms(loggedInInfo, Integer.parseInt(fdid), DocumentType.FORM, Integer.parseInt(demographicNo));
        }
        if (request.getParameter("error") != null) {
			String errorMessage = (String) request.getAttribute("errorMessage");
			if (StringUtils.isNullOrEmpty(errorMessage)) {
				errorMessage = "Failed to process eForm. Please refer to the server logs for more details.";
			}
			%>
				<SCRIPT LANGUAGE="JavaScript">
			        alert('<%= errorMessage %>');
			    </SCRIPT>
			<%
		}
    %>
	<input type="hidden" id="context" value="${ pageContext.request.contextPath }">
	<input type="hidden" id="demographicNo" value="<%=demographicNo%>">
	<input type="hidden" id="fdid" value="<%=fdid%>">
    <input type="hidden" id="fid" value="${fid}">
    <c:set var="attachedDocumentIds" value="<%=attachedDocumentIds%>" />
    <c:set var="attachedEFormIds" value="<%=attachedEFormIds%>" />
    <c:set var="attachedHRMDocumentIds" value="<%=attachedHRMDocumentIds%>" />
    <c:set var="attachedLabIds" value="<%=attachedLabIds%>" />
    <c:set var="attachedForms" value="<%=attachedForms%>" />
    <c:forEach items="${attachedDocumentIds}" var="attachmentId" varStatus="status">
        <input type="hidden" id="delegate_docNo${attachmentId}" name="docNo" value="${attachmentId}" class="delegateAttachment" />
    </c:forEach>
    <c:forEach items="${attachedEFormIds}" var="attachmentId" varStatus="status">
        <input type="hidden" id="delegate_eFormNo${attachmentId}" name="eFormNo" value="${attachmentId}" class="delegateAttachment" />
    </c:forEach>
    <c:forEach items="${attachedHRMDocumentIds}" var="attachmentId" varStatus="status">
        <input type="hidden" id="delegate_hrmNo${attachmentId}" name="hrmNo" value="${attachmentId}" class="delegateAttachment" />
    </c:forEach>
    <c:forEach items="${attachedLabIds}" var="attachmentId" varStatus="status">
        <input type="hidden" id="delegate_labNo${attachmentId}" name="labNo" value="${attachmentId}" class="delegateAttachment" />
    </c:forEach>
    <c:forEach items="${attachedForms}" var="attachment" varStatus="status">
        <!-- this input field will be useful when latest form is not attached -->
        <input type="hidden" id="entry_formNo${ attachment.formId }" class="delegateOldFormAttachment" data-formName="${ attachment.formName }" data-formDate="${ attachment.getEdited() }" />
        <input type="hidden" id="delegate_formNo${attachment.formId}" name="formNo" value="${attachment.formId}" class="delegateAttachment" />
    </c:forEach>
</body>
</html>