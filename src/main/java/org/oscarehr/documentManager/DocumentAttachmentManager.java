package org.oscarehr.documentManager;

import org.apache.commons.lang3.tuple.Pair;
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

import oscar.eform.EFormUtil;
import oscar.oscarEncounter.data.EctFormData;
import oscar.oscarLab.ca.all.Hl7textResultsData;
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

public interface DocumentAttachmentManager {

	public List<String> getConsultAttachments(LoggedInInfo loggedInInfo, Integer requestId, DocumentType documentType, Integer demographicNo);

	public List<String> getEFormAttachments(LoggedInInfo loggedInInfo, Integer fdid, DocumentType documentType, Integer demographicNo);

	public List<EctFormData.PatientForm> getFormsAttachedToEForms(LoggedInInfo loggedInInfo, Integer fdid, DocumentType documentType, Integer demographicNo);
	/**
	 * This method is responsible for lab version sorting and is intended for use in the attachment window (attachDocument.jsp).
	 * In other parts of the application, developers should utilize CommonLabResultData.populateLabResultsData() to access all available lab data.
	 */
	public Pair<List<LabResultData>, String> getAllLabsSortedByVersions(LoggedInInfo loggedInInfo, String demographicNo);

	/**
	 * This method is intended for use in the attachment window (attachDocument.jsp) and is designed to retrieve a list of eForms except one.
	 * In other parts of the application, developers are encouraged to use EFormUtil.listPatientEformsCurrent() to access all available eForms.
	 * The reason for this function is to ensure a user cannot attach an eForm to itself.
	 */
	public List<EFormData> getAllEFormsExpectFdid(LoggedInInfo loggedInInfo, Integer demographicNo, Integer fdid);

	public void attachToConsult(LoggedInInfo loggedInInfo, DocumentType documentType, String[] attachments, String providerNo, Integer requestId, Integer demographicNo);

	public void attachToEForm(LoggedInInfo loggedInInfo, DocumentType documentType, String[] attachments, String providerNo, Integer fdid, Integer demographicNo);

	/**
	 * This renderDocument method is written specifically to render Forms.
	 *
	 * @param request      The HttpServletRequest object.
	 * @param response     The HttpServletResponse object.
	 * @param documentType The type of the document to be rendered.
	 * @return The Path to the rendered document.
	 */
	public Path renderDocument(HttpServletRequest request, HttpServletResponse response, DocumentType documentType) throws PDFGenerationException;

	/**
	 * This renderDocument method is written to render EForms, Docs, HRMs and Labs.
	 *
	 * @param loggedInInfo  The LoggedInInfo object.
	 * @param documentType  The type of the document to be rendered.
	 * @param documentId    The documentId integer.
	 * @return The Path to the rendered document.
	 */
	public Path renderDocument(LoggedInInfo loggedInInfo, DocumentType documentType, Integer documentId) throws PDFGenerationException;

	public Path renderConsultationFormWithAttachments(HttpServletRequest request, HttpServletResponse response) throws PDFGenerationException;
	public Path renderEFormWithAttachments(HttpServletRequest request, HttpServletResponse response) throws PDFGenerationException;

	public Integer saveEFormAsEDoc(HttpServletRequest request, HttpServletResponse response) throws PDFGenerationException;

	public String convertPDFToBase64(Path renderedDocument) throws PDFGenerationException;
}

	
