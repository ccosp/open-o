//CHECKSTYLE:OFF
package ca.openosp.openo.documentManager.actions;

import net.sf.json.JSONObject;
import ca.openosp.openo.eform.EFormUtil;
import ca.openosp.openo.oscarEncounter.data.EctFormData;

import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import ca.openosp.openo.common.model.EFormData;
import ca.openosp.openo.common.model.enumerator.DocumentType;
import ca.openosp.openo.documentManager.DocumentAttachmentManager;
import ca.openosp.openo.documentManager.EDoc;
import ca.openosp.openo.documentManager.EDocUtil;
import ca.openosp.openo.documentManager.data.AttachmentLabResultData;
import ca.openosp.openo.hospitalReportManager.HRMUtil;
import ca.openosp.openo.managers.FormsManager;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.PDFGenerationException;
import ca.openosp.openo.ehrutil.SpringUtils;

import ca.openosp.openo.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DocumentPreviewAction extends DispatchAction {
    private static final Logger logger = MiscUtils.getLogger();
    private final DocumentAttachmentManager documentAttachmentManager = SpringUtils.getBean(DocumentAttachmentManager.class);
    private final FormsManager formsManager = SpringUtils.getBean(FormsManager.class);

    private List<EDoc> allDocuments = new ArrayList<>();
    private List<EFormData> allEForms = new ArrayList<>();
    private ArrayList<HashMap<String, ? extends Object>> allHRMDocuments = new ArrayList<>();
    private List<AttachmentLabResultData> allLabsSortedByVersions = new ArrayList<>();
    private List<EctFormData.PatientForm> allForms = new ArrayList<>();

    public void renderEDocPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        String eDocId = request.getParameter("eDocId");
        try {
            Path docPDFPath = documentAttachmentManager.renderDocument(loggedInInfo, DocumentType.DOC, Integer.parseInt(eDocId));
            generateResponse(response, docPDFPath);
        } catch (PDFGenerationException e) {
            logger.error("Error occured while rendering eDoc. " + e.getMessage(), e);
            generateResponse(response, e.getMessage());
        }
    }

    public void renderEFormPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        String eFormId = request.getParameter("eFormId");
        try {
            Path eFormPDFPath = documentAttachmentManager.renderDocument(loggedInInfo, DocumentType.EFORM, Integer.parseInt(eFormId));
            generateResponse(response, eFormPDFPath);
        } catch (PDFGenerationException e) {
            logger.error("Error occured while rendering eForm. " + e.getMessage(), e);
            generateResponse(response, e.getMessage());
        }
    }

    public void renderHrmPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        String hrmId = request.getParameter("hrmId");
        try {
            Path hrmPDFPath = documentAttachmentManager.renderDocument(loggedInInfo, DocumentType.HRM, Integer.parseInt(hrmId));
            generateResponse(response, hrmPDFPath);
        } catch (PDFGenerationException e) {
            logger.error("Error occured while rendering HRM. " + e.getMessage(), e);
            generateResponse(response, e.getMessage());
        }
    }

    public void renderLabPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        String segmentID = request.getParameter("segmentId");
        try {
            Path labPDFPath = documentAttachmentManager.renderDocument(loggedInInfo, DocumentType.LAB, Integer.parseInt(segmentID));
            generateResponse(response, labPDFPath);
        } catch (PDFGenerationException e) {
            logger.error("Error occured while rendering Lab. " + e.getMessage(), e);
            generateResponse(response, e.getMessage());
        }
    }

    public void renderFormPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        try {
            Path formPDFPath = documentAttachmentManager.renderDocument(request, response, DocumentType.FORM);
            generateResponse(response, formPDFPath);
        } catch (PDFGenerationException e) {
            logger.error("Error occured while rendering Form. " + e.getMessage(), e);
            generateResponse(response, e.getMessage());
        }
    }

    public void renderPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        String pdfPathString = StringUtils.isNullOrEmpty(request.getParameter("pdfPath")) ? "" : request.getParameter("pdfPath");
        Path pdfPath = Paths.get(pdfPathString);
        response.setContentType("application/pdf");
        try (InputStream inputStream = Files.newInputStream(pdfPath);
             BufferedInputStream bfis = new BufferedInputStream(inputStream);
             ServletOutputStream outs = response.getOutputStream()) {

            int data;
            while ((data = bfis.read()) != -1) {
                outs.write(data);
            }

            outs.flush();
        } catch (IOException e) {
            logger.error("Error", e);
        }
    }

    public ActionForward fetchConsultDocuments(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

        String demographicNo = StringUtils.isNullOrEmpty(request.getParameter("demographicNo")) ? "0" : request.getParameter("demographicNo");

        allDocuments = EDocUtil.listDocs(loggedInInfo, "demographic", demographicNo, null, EDocUtil.PRIVATE, EDocUtil.EDocSort.OBSERVATIONDATE);
        allEForms = EFormUtil.listPatientEformsCurrent(new Integer(demographicNo), true);
        allHRMDocuments = HRMUtil.listHRMDocuments(loggedInInfo, "report_date", false, demographicNo, false);
        allLabsSortedByVersions = documentAttachmentManager.getAllLabsSortedByVersions(loggedInInfo, demographicNo);
        allForms = formsManager.getEncounterFormsbyDemographicNumber(loggedInInfo, Integer.parseInt(demographicNo), false, true);

        return forwardDocuments(mapping, request);
    }

    public ActionForward fetchEFormDocuments(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

        String demographicNo = StringUtils.isNullOrEmpty(request.getParameter("demographicNo")) ? "0" : request.getParameter("demographicNo");
        String fdid = StringUtils.isNullOrEmpty(request.getParameter("fdid")) ? "0" : request.getParameter("fdid");

        allDocuments = EDocUtil.listDocs(loggedInInfo, "demographic", demographicNo, null, EDocUtil.PRIVATE, EDocUtil.EDocSort.OBSERVATIONDATE);
        allEForms = documentAttachmentManager.getAllEFormsExpectFdid(loggedInInfo, Integer.parseInt(demographicNo), Integer.parseInt(fdid));
        allHRMDocuments = HRMUtil.listHRMDocuments(loggedInInfo, "report_date", false, demographicNo, false);
        allLabsSortedByVersions = documentAttachmentManager.getAllLabsSortedByVersions(loggedInInfo, demographicNo);
        allForms = formsManager.getEncounterFormsbyDemographicNumber(loggedInInfo, Integer.parseInt(demographicNo), false, true);

        return forwardDocuments(mapping, request);
    }

    private void generateResponse(HttpServletResponse response, Path pdfPath) throws PDFGenerationException {
        JSONObject json = new JSONObject();
        String base64Data = documentAttachmentManager.convertPDFToBase64(pdfPath);
        json.put("base64Data", base64Data);
        response.setContentType("text/javascript");
        try {
            response.getWriter().write(json.toString());
        } catch (IOException e) {
            throw new PDFGenerationException("An error occurred while writing JSON response to the output stream", e);
        }
    }

    private void generateResponse(HttpServletResponse response, String errorMessage) {
        JSONObject json = new JSONObject();
        json.put("errorMessage", errorMessage);
        response.setContentType("text/javascript");
        try {
            response.getWriter().write(json.toString());
        } catch (IOException e) {
            logger.error("An error occurred while writing JSON response to the output stream", e);
        }
    }

    private ActionForward forwardDocuments(ActionMapping mapping, HttpServletRequest request) {
        request.setAttribute("allDocuments", allDocuments);
        request.setAttribute("allLabsSortedByVersions", allLabsSortedByVersions);
        request.setAttribute("allForms", allForms);
        request.setAttribute("allEForms", allEForms);
        request.setAttribute("allHRMDocuments", allHRMDocuments);
        return mapping.findForward("fetchDocuments");
    }
}
