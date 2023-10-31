package org.oscarehr.documentManager;

import org.oscarehr.common.dao.ConsultDocsDao;
import org.oscarehr.common.dao.EFormDocsDao;
import org.oscarehr.common.model.ConsultDocs;
import org.oscarehr.common.model.EFormData;
import org.oscarehr.common.model.EFormDocs;
import org.oscarehr.hospitalReportManager.HRMUtil;
import org.oscarehr.managers.*;
import org.oscarehr.common.model.enumerator.DocumentType;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.PDFGenerationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.oscarEncounter.data.EctFormData;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.util.ConcatPDF;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class DocumentAttachmentManager {
	@Autowired
	private ConsultDocsDao consultDocsDao;
	@Autowired
	private EFormDocsDao eFormDocsDao;

	@Autowired
	private ConsultationManager consultationManager;
	@Autowired
	private DocumentManager documentManager;
	@Autowired
	private EformDataManager eformDataManager;
	@Autowired
	private FormsManager formsManager;
	@Autowired
	private LabManager labManager;
	@Autowired
	private NioFileManager nioFileManager;
	@Autowired
	private SecurityInfoManager securityInfoManager;

	public List<String> getConsultAttachments(LoggedInInfo loggedInInfo, Integer requestId, DocumentType documentType, Integer demographicNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_con", SecurityInfoManager.READ, demographicNo)) {
			throw new RuntimeException("missing required security object (_con)");
		}

		List<String> consultAttachments = new ArrayList<>();
		List<ConsultDocs> consultDocs = consultDocsDao.findByRequestIdDocType(requestId, documentType.getType());
		for (ConsultDocs consultDocs1 : consultDocs) {
			consultAttachments.add(String.valueOf(consultDocs1.getDocumentNo()));
		}
		return consultAttachments;
	}

	public List<String> getEFormAttachments(LoggedInInfo loggedInInfo, Integer fdid, DocumentType documentType, Integer demographicNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_eform", SecurityInfoManager.READ, demographicNo)) {
			throw new RuntimeException("missing required security object (_eform)");
		}

		List<String> eFormAttachments = new ArrayList<>();
		List<EFormDocs> eFormDocs = eFormDocsDao.findByFdidIdDocType(fdid, documentType.getType());
		for (EFormDocs eFormDocs1 : eFormDocs) {
			eFormAttachments.add(String.valueOf(eFormDocs1.getDocumentNo()));
		}
		return eFormAttachments;
	}

	public List<LabResultData> getAllLabsSortedByVersions(LoggedInInfo loggedInInfo, String demographicNo) {
		/*
		 * Once refactoring is done, we will move the lab version sorting code from attachDocument.jsp to here.
		 */
		CommonLabResultData commonLabResultData = new CommonLabResultData();
		List<LabResultData> allLabs = commonLabResultData.populateLabResultsData(loggedInInfo, "", demographicNo, "", "", "", "U");
		Collections.sort(allLabs);
		return allLabs;
	}

	public void attachToConsult(LoggedInInfo loggedInInfo, DocumentType documentType, String[] attachments, String providerNo, Integer requestId, Integer demographicNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_con", SecurityInfoManager.WRITE, demographicNo)) {
			throw new RuntimeException("missing required security object (_con)");
		}

		DocumentAttach documentAttach = new DocumentAttach();
		documentAttach.attachToConsult(attachments, documentType, providerNo, requestId);
	}

	public void attachToEForm(LoggedInInfo loggedInInfo, DocumentType documentType, String[] attachments, String providerNo, Integer fdid, Integer demographicNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_eform", SecurityInfoManager.WRITE, demographicNo)) {
			throw new RuntimeException("missing required security object (_eform)");
		}

		DocumentAttach documentAttach = new DocumentAttach();
		documentAttach.attachToEForm(attachments, documentType, providerNo, fdid);
	}

	/**
	 * This renderDocument method is written specifically to render Forms.
	 *
	 * @param request      The HttpServletRequest object.
	 * @param response     The HttpServletResponse object.
	 * @param documentType The type of the document to be rendered.
	 * @return The Path to the rendered document.
	 */
	public Path renderDocument(HttpServletRequest request, HttpServletResponse response, DocumentType documentType) throws PDFGenerationException {
		return renderDocument(null, request, response, documentType, 0);
	}

	/**
	 * This renderDocument method is written to render EForms, Docs, HRMs and Labs.
	 *
	 * @param loggedInInfo  The LoggedInInfo object.
	 * @param documentType  The type of the document to be rendered.
	 * @param documentId    The documentId integer.
	 * @return The Path to the rendered document.
	 */
	public Path renderDocument(LoggedInInfo loggedInInfo, DocumentType documentType, Integer documentId) throws PDFGenerationException {
		return renderDocument(loggedInInfo, null, null, documentType, documentId);
	}

	public Path renderConsultationFormWithAttachments(HttpServletRequest request, HttpServletResponse response) throws PDFGenerationException {
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String requestId = (String) request.getAttribute("reqId");
		String demographicId = (String) request.getAttribute("demographicId");
		Path consultationFormPDFPath = consultationManager.renderConsultationForm(request);

		List<EFormData> attachedEForms = consultationManager.getAttachedEForms(requestId);
		List<EDoc> attachedEDocs = EDocUtil.listDocs(loggedInInfo, demographicId, requestId, EDocUtil.ATTACHED);
		CommonLabResultData labResultData = new CommonLabResultData();
		List<LabResultData> attachedLabs = labResultData.populateLabResultsData(loggedInInfo, demographicId, requestId, CommonLabResultData.ATTACHED);
		ArrayList<HashMap<String, ? extends Object>> attachedHRMs = consultationManager.getAttachedHRMDocuments(loggedInInfo, demographicId, requestId);
		List<EctFormData.PatientForm> attachedForms = consultationManager.getAttachedForms(loggedInInfo, Integer.parseInt(requestId), Integer.parseInt(demographicId));

		ArrayList<Object> pdfDocumentList = new ArrayList<>();
		pdfDocumentList.add(consultationFormPDFPath.toString());
		attachEFormPDFs(loggedInInfo, attachedEForms, pdfDocumentList);
		attachEDocPDFs(loggedInInfo, attachedEDocs, pdfDocumentList);
		attachLabPDFs(loggedInInfo, attachedLabs, pdfDocumentList);
		attachHRMPDFs(loggedInInfo, attachedHRMs, pdfDocumentList);
		attachFormPDFs(request, response, attachedForms, pdfDocumentList);

		return concatPDF(pdfDocumentList);
	}

	public String convertPDFToBase64(Path renderedDocument) throws PDFGenerationException {
		String base64 = "";
		if(renderedDocument == null) { return base64; }
		try {
			byte[] bytes = Files.readAllBytes(renderedDocument);
			base64 = Base64.getEncoder().encodeToString(bytes);
		} catch (IOException e) {
			throw new PDFGenerationException("An error occurred while processing the PDF file", e);
		}
		return base64 != null ? base64 : "";
	}

	private Path renderDocument(LoggedInInfo loggedInInfo, HttpServletRequest request, HttpServletResponse response, DocumentType documentType, Integer documentId) throws PDFGenerationException {
		Path path = null;
		switch (documentType) {
			case DOC:
				path = documentManager.renderDocument(loggedInInfo, String.valueOf(documentId));
				break;
			case LAB:
				path = labManager.renderLab(loggedInInfo, documentId);
				break;
			case EFORM:
				path = eformDataManager.createEformPDF(loggedInInfo, documentId);
				break;
			case HRM:
				path = HRMUtil.renderHRM(loggedInInfo, documentId);
				break;
			case FORM:
				path = formsManager.renderForm(request, response, null);
				break;
		}
		return path;
	}

	private void attachEFormPDFs(LoggedInInfo loggedInInfo, List<EFormData> attachedEForms, ArrayList<Object> pdfDocumentList) throws PDFGenerationException {
		for (EFormData eForm : attachedEForms) {
			Path path = renderDocument(loggedInInfo, DocumentType.EFORM, eForm.getId());
			pdfDocumentList.add(path.toString());
		}
	}

	private void attachEDocPDFs(LoggedInInfo loggedInInfo, List<EDoc> attachedEDocs, ArrayList<Object> pdfDocumentList) throws PDFGenerationException {
		for (EDoc eDoc : attachedEDocs) {
			Path path = documentManager.renderDocument(loggedInInfo, eDoc);
			pdfDocumentList.add(path.toString());
		}
	}

	private void attachLabPDFs(LoggedInInfo loggedInInfo, List<LabResultData> attachedLabs, ArrayList<Object> pdfDocumentList) throws PDFGenerationException {
		for (LabResultData lab : attachedLabs) {
			Path path = renderDocument(loggedInInfo, DocumentType.LAB, Integer.parseInt(lab.getSegmentID()));
			pdfDocumentList.add(path.toString());
		}
	}

	private void attachHRMPDFs(LoggedInInfo loggedInInfo, ArrayList<HashMap<String, ? extends Object>> attachedHRMs, ArrayList<Object> pdfDocumentList) throws PDFGenerationException {
		for (HashMap<String, ?> hrm : attachedHRMs) {
			Path path = renderDocument(loggedInInfo, DocumentType.HRM, (Integer) hrm.get("id"));
			pdfDocumentList.add(path.toString());
		}
	}

	private void attachFormPDFs(HttpServletRequest request, HttpServletResponse response, List<EctFormData.PatientForm> attachedForms, ArrayList<Object> pdfDocumentList) throws PDFGenerationException {
		for (EctFormData.PatientForm form : attachedForms) {
			Path path = formsManager.renderForm(request, response, form);
			pdfDocumentList.add(path.toString());
		}
	}

	private Path concatPDF(ArrayList<Object> pdfDocumentList) throws PDFGenerationException {
		Path path = null;
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			ConcatPDF.concat(pdfDocumentList, outputStream);
			path = nioFileManager.saveTempFile("combinedPDF" + new Date().getTime(), outputStream);
		} catch (IOException e) {
			throw new PDFGenerationException("An error occurred while concatenating PDF.", e);
		}
		return path;
	}
}
