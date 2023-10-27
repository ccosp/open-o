package org.oscarehr.documentManager.actions;

import net.sf.json.JSONObject;
import oscar.eform.EFormUtil;
import oscar.oscarEncounter.data.EctFormData;
import oscar.oscarLab.ca.on.LabResultData;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.model.EFormData;
import org.oscarehr.common.model.enumerator.DocumentType;
import org.oscarehr.documentManager.DocumentAttachmentManager;
import org.oscarehr.documentManager.EDoc;
import org.oscarehr.documentManager.EDocUtil;
import org.oscarehr.hospitalReportManager.HRMUtil;
import org.oscarehr.managers.FormsManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DocumentPreviewAction extends DispatchAction {
	private final DocumentAttachmentManager documentAttachmentManager = SpringUtils.getBean(DocumentAttachmentManager.class);
	private final FormsManager formsManager = SpringUtils.getBean(FormsManager.class);

	private List<EDoc> allDocuments = new ArrayList<>();
	private List<EFormData> allEForms = new ArrayList<>();
	private ArrayList<HashMap<String,? extends Object>> allHRMDocuments = new ArrayList<>();
	private List<LabResultData> allLabs = new ArrayList<>();
	private List<EctFormData.PatientForm> allForms = new ArrayList<>();
	private List<String> attachedDocumentIds = new ArrayList<>();
	private List<String> attachedEFormIds = new ArrayList<>();
	private List<String> attachedHRMDocumentIds = new ArrayList<>();
	private List<String> attachedLabIds = new ArrayList<>();
	private List<String> attachedFormIds = new ArrayList<>();

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
		String segmentID = request.getParameter("segmentId");
		Path labPDFPath = documentAttachmentManager.renderDocument(loggedInInfo, DocumentType.LAB, Integer.parseInt(segmentID));
		generateResponse(response, labPDFPath);
	}

	public void renderFormPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		Path formPDFPath = documentAttachmentManager.renderDocument(request, response, DocumentType.FORM);
		generateResponse(response, formPDFPath);
	}

	public ActionForward fetchConsultDocuments(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		
		String demographicNo = Objects.equals(request.getParameter("demographicNo"), new String("null")) ? "0" : request.getParameter("demographicNo");
		String requestId = Objects.equals(request.getParameter("requestId"), new String("null")) ? "0" : request.getParameter("requestId");

		allDocuments = EDocUtil.listDocs(loggedInInfo, "demographic", demographicNo, null, EDocUtil.PRIVATE, EDocUtil.EDocSort.OBSERVATIONDATE);
		allEForms = EFormUtil.listPatientEformsCurrent(new Integer(demographicNo), true);
		allHRMDocuments = HRMUtil.listHRMDocuments(loggedInInfo, "report_date", false, demographicNo,false);
		allLabs = documentAttachmentManager.getAllLabsSortedByVersions(loggedInInfo, demographicNo);
		allForms = formsManager.getEncounterFormsbyDemographicNumber(loggedInInfo, Integer.parseInt(demographicNo), false, true);

		attachedDocumentIds = documentAttachmentManager.getConsultAttachments(loggedInInfo, Integer.parseInt(requestId), DocumentType.DOC, Integer.parseInt(demographicNo));
		attachedEFormIds = documentAttachmentManager.getConsultAttachments(loggedInInfo, Integer.parseInt(requestId), DocumentType.EFORM, Integer.parseInt(demographicNo));
		attachedHRMDocumentIds = documentAttachmentManager.getConsultAttachments(loggedInInfo, Integer.parseInt(requestId), DocumentType.HRM, Integer.parseInt(demographicNo));
		attachedLabIds = documentAttachmentManager.getConsultAttachments(loggedInInfo, Integer.parseInt(requestId), DocumentType.LAB, Integer.parseInt(demographicNo));
		attachedFormIds = documentAttachmentManager.getConsultAttachments(loggedInInfo, Integer.parseInt(requestId), DocumentType.FORM, Integer.parseInt(demographicNo));

		return forwardDocuments(mapping, request);
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

	private ActionForward forwardDocuments(ActionMapping mapping, HttpServletRequest request) {
		request.setAttribute("attachedDocumentIds", attachedDocumentIds);
		request.setAttribute("attachedLabIds", attachedLabIds);
		request.setAttribute("attachedFormIds", attachedFormIds);
		request.setAttribute("attachedEFormIds", attachedEFormIds);
		request.setAttribute("attachedHRMDocumentIds", attachedHRMDocumentIds);

		request.setAttribute("allDocuments", allDocuments);
		request.setAttribute("allLabs", allLabs);
		request.setAttribute("allForms", allForms);
		request.setAttribute("allEForms", allEForms);
		request.setAttribute("allHRMDocuments", allHRMDocuments);

		return mapping.findForward("fetchDocuments");
	}
}
