package org.oscarehr.documentManager.actions;

import net.sf.json.JSONObject;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.model.enumerator.DocumentType;
import org.oscarehr.documentManager.DocumentAttachmentManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;

public class DocumentPreviewAction extends DispatchAction {
	private final DocumentAttachmentManager documentAttachmentManager = SpringUtils.getBean(DocumentAttachmentManager.class);

	public void renderEDocPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String eDocId = request.getParameter("eDocId");
		Path docPDFPath = documentAttachmentManager.renderDocument(loggedInInfo, DocumentType.DOC, Integer.parseInt(eDocId));
		generateResponse(response, docPDFPath);
	}

	public void renderEFormPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String eFormId = request.getParameter("eFormId");
		Path eFormPDFPath = documentAttachmentManager.renderDocument(loggedInInfo, DocumentType.EFORM, Integer.parseInt(eFormId));
		generateResponse(response, eFormPDFPath);
	}

	public void renderHrmPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String hrmId = request.getParameter("hrmId");
		Path hrmPDFPath = documentAttachmentManager.renderDocument(loggedInInfo, DocumentType.HRM, Integer.parseInt(hrmId));
		generateResponse(response, hrmPDFPath);
	}

	public void renderLabPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String segmentID = request.getParameter("segmentID");
		Path labPDFPath = documentAttachmentManager.renderDocument(loggedInInfo, DocumentType.LAB, Integer.parseInt(segmentID));
		generateResponse(response, labPDFPath);
	}

	public void renderFormPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		Path formPDFPath = documentAttachmentManager.renderDocument(request, response, DocumentType.FORM);
		generateResponse(response, formPDFPath);
	}

	private void generateResponse(HttpServletResponse response, Path pdfPath) {
		JSONObject json = new JSONObject();
		String base64Data = documentAttachmentManager.convertPDFToBase64(pdfPath);
		json.put("base64Data", base64Data);
		response.setContentType("text/javascript");
		try {
			response.getWriter().write(json.toString());
		} catch (IOException e) {
			MiscUtils.getLogger().error("An error occurred while writing JSON response to the output stream: " + e.getMessage(), e);
		}
	}
}
