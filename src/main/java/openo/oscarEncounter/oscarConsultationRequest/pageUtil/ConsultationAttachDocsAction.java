//CHECKSTYLE:OFF
/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * <p>
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


package openo.oscarEncounter.oscarConsultationRequest.pageUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.DocumentException;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.model.ConsultDocs;
import org.oscarehr.common.model.EFormData;
import org.oscarehr.hospitalReportManager.HRMUtil;
import org.oscarehr.hospitalReportManager.HRMPDFCreator;
import org.oscarehr.managers.ConsultationManager;
import org.oscarehr.managers.FaxManager;
import org.oscarehr.managers.FormsManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import org.oscarehr.documentManager.EDoc;
import org.oscarehr.documentManager.EDocUtil;
import oscar.eform.EFormUtil;
import oscar.form.util.FormTransportContainer;
import oscar.oscarEncounter.data.EctFormData;
import oscar.oscarLab.ca.all.pageUtil.LabPDFCreator;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;

public class ConsultationAttachDocsAction extends DispatchAction {

    private final Logger logger = MiscUtils.getLogger();
    FaxManager faxManager = SpringUtils.getBean(FaxManager.class);

    @SuppressWarnings("unused")
    public ActionForward fetchAll(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                  HttpServletResponse response) {

        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        String demographicNo = request.getParameter("demographicNo");
        String requestId = request.getParameter("requestId");
        FormsManager formsManager = SpringUtils.getBean(FormsManager.class);
        ConsultationManager consultationManager = SpringUtils.getBean(ConsultationManager.class);

        //Get all LAB information for demographic, along with which are already attached
        List<String> attachedLabIds = new ArrayList<String>();
        CommonLabResultData commonLabResultData = new CommonLabResultData();
        List<LabResultData> allLabs = commonLabResultData.populateLabResultsData(loggedInInfo, "", demographicNo, "", "", "", "U");
        Collections.sort(allLabs);
        List<LabResultData> attachedLabs = commonLabResultData.populateLabResultsData(loggedInInfo, demographicNo, requestId, CommonLabResultData.ATTACHED);
        if (attachedLabs != null) {
            for (LabResultData labResultData : attachedLabs) {
                attachedLabIds.add(labResultData.segmentID);
            }
        }

        //Get all DOCUMENT information for demographic, along with which are already attached
        List<String> attachedDocumentIds = new ArrayList<String>();
        List<EDoc> allDocuments = EDocUtil.listDocs(loggedInInfo, "demographic", demographicNo, null, EDocUtil.PRIVATE, EDocUtil.EDocSort.OBSERVATIONDATE);
        List<EDoc> attachedDocuments = EDocUtil.listDocs(loggedInInfo, demographicNo, requestId, EDocUtil.ATTACHED);
        if (attachedDocuments != null) {
            for (EDoc document : attachedDocuments) {
                attachedDocumentIds.add(document.getDocId());
            }
        }

        //Get all FORM information for demographic, along with which are already attached
        List<String> attachedFormIds = new ArrayList<String>();
        List<EctFormData.PatientForm> allForms = formsManager.getEncounterFormsbyDemographicNumber(loggedInInfo, Integer.parseInt(demographicNo), false, true);
        List<EctFormData.PatientForm> attachedForms = null;
        if (requestId != null && !requestId.isEmpty() && !"null".equals(requestId)) {
            attachedForms = consultationManager.getAttachedForms(loggedInInfo, Integer.parseInt(requestId), Integer.parseInt(demographicNo));
            if (attachedForms != null) {
                for (EctFormData.PatientForm attachedForm : attachedForms) {
                    attachedFormIds.add(attachedForm.formId + "");
                }
            }
        }

        //Get all EFORM information for demographic, along with which are already attached
        List<String> attachedEFormIds = new ArrayList<String>();
        List<EFormData> allEForms = EFormUtil.listPatientEformsCurrent(new Integer(demographicNo), true);
        List<EFormData> attachedEForms = null;
        if (requestId != null && !requestId.isEmpty() && !"null".equals(requestId)) {
            attachedEForms = consultationManager.getAttachedEForms(requestId);
            if (attachedEForms != null) {
                for (EFormData attachedEForm : attachedEForms) {
                    attachedEFormIds.add(attachedEForm.getId() + "");
                }
            }
        }

        //Get all HRM information for demographic, along with which are already attached
        List<String> attachedHRMDocumentIds = new ArrayList<String>();
        ArrayList<HashMap<String, ? extends Object>> allHRMDocuments = HRMUtil.listHRMDocuments(loggedInInfo, "report_date", false, demographicNo, false);
        List<ConsultDocs> attachedHRMDocuments = null;
        if (requestId != null && !requestId.isEmpty() && !"null".equals(requestId)) {
            attachedHRMDocuments = consultationManager.getAttachedDocumentsByType(loggedInInfo, Integer.parseInt(requestId), ConsultDocs.DOCTYPE_HRM);
            if (attachedHRMDocuments != null) {
                for (ConsultDocs consultDocs : attachedHRMDocuments) {
                    attachedHRMDocumentIds.add(String.valueOf(consultDocs.getDocumentNo()));
                }
            }
        }

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

        return mapping.findForward("fetchAll");
    }

    public void getDocumentPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        //TODO: refactor this function, and similar code in EctConsultationFormRequestPrincAction2.java
        //      and EctConsultationFormFaxAction.java as part of extending this attach item functionality
        //      to eforms and ticklers

        String isImage = request.getParameter("isImage");
        String isPDF = request.getParameter("isPDF");
        String fileName = request.getParameter("fileName");
        String description = request.getParameter("description");

        String path = OscarProperties.getInstance().getProperty("DOCUMENT_DIR");
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }

        request.setAttribute("imagePath", path + fileName);
        request.setAttribute("imageTitle", description);

        if (Objects.equals(isImage, "true")) {
            try (ByteOutputStream byteOutputStream = new ByteOutputStream()) {
                ImagePDFCreator imagePDFCreator = new ImagePDFCreator(request, byteOutputStream);
                imagePDFCreator.printPdf();
                generateResponse(response, getBase64(byteOutputStream.getBytes()));
            } catch (DocumentException | IOException e) {
                logger.error("An error occurred while creating the pdf of the image: " + e.getMessage(), e);
            }
        } else if (Objects.equals(isPDF, "true")) {
            Path eDocPDFPath = Paths.get(path + fileName);
            generateResponse(response, getBase64(eDocPDFPath));
        }
    }

    public void getLabPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        //TODO: refactor this function, and similar code in EctConsultationFormRequestPrincAction2.java
        //      and EctConsultationFormFaxAction.java as part of extending this attach item functionality
        //      to eforms and ticklers

        String segmentID = request.getParameter("segmentID");
        request.setAttribute("segmentID", segmentID);
        try {
            File tempLabPDF = File.createTempFile("lab" + segmentID, "pdf");
            try (
                    FileOutputStream fileOutputStream = new FileOutputStream(tempLabPDF);
                    ByteOutputStream byteOutputStream = new ByteOutputStream();
            ) {
                LabPDFCreator labPDFCreator = new LabPDFCreator(request, fileOutputStream);
                labPDFCreator.printPdf();
                labPDFCreator.addEmbeddedDocuments(tempLabPDF, byteOutputStream);
                generateResponse(response, getBase64(byteOutputStream.getBytes()));
            }
            tempLabPDF.delete();
        } catch (IOException e) {
            logger.error("An error occurred: " + e.getMessage(), e);
        }
    }

    public void getFormPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        //TODO: refactor this function, and similar code in EctConsultationFormRequestPrincAction2.java
        //      and EctConsultationFormFaxAction.java as part of extending this attach item functionality
        //      to eforms and ticklers

        String formId = request.getParameter("formId");
        String formName = request.getParameter("formName");
        String demographicNo = request.getParameter("demographicNo");
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        try {
            FormTransportContainer formTransportContainer = new FormTransportContainer(
                    response, request, "/form/forwardshortcutname.jsp"
                    + "?method=fetch&formname="
                    + formName
                    + "&demographic_no="
                    + demographicNo
                    + "&formId="
                    + formId);
            formTransportContainer.setDemographicNo(demographicNo);
            formTransportContainer.setProviderNo(loggedInInfo.getLoggedInProviderNo());
            formTransportContainer.setSubject(formName + " Form ID " + formId);
            formTransportContainer.setFormName(formName);
            formTransportContainer.setRealPath(getServlet().getServletContext().getRealPath(File.separator));
            Path formPDF = faxManager.renderFaxDocument(loggedInInfo, FaxManager.TransactionType.FORM, formTransportContainer);
            generateResponse(response, getBase64(formPDF));
        } catch (ServletException | IOException e) {
            logger.error("An error occurred while processing the form: " + e.getMessage(), e);
        }
    }

    public void getEFormPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        //TODO: refactor this function, and similar code in EctConsultationFormRequestPrincAction2.java
        //      and EctConsultationFormFaxAction.java as part of extending this attach item functionality
        //      to eforms and ticklers

        String eFormId = request.getParameter("eFormId");
        String demographicNo = request.getParameter("demographicNo");
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        Path eFormPDF = faxManager.renderFaxDocument(loggedInInfo, FaxManager.TransactionType.EFORM, Integer.parseInt(eFormId), Integer.parseInt(demographicNo));
        generateResponse(response, getBase64(eFormPDF));
    }

    public void getHRMPDF(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        //TODO: refactor this function, and similar code in EctConsultationFormRequestPrincAction2.java
        //      and EctConsultationFormFaxAction.java as part of extending this attach item functionality
        //      to eforms and ticklers

        String hrmId = request.getParameter("hrmId");
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        try (ByteOutputStream byteOutputStream = new ByteOutputStream()) {
            HRMPDFCreator hrmPdf = new HRMPDFCreator(byteOutputStream, Integer.parseInt(hrmId), loggedInInfo);
            hrmPdf.printPdf();
            generateResponse(response, getBase64(byteOutputStream.getBytes()));
        }
    }

    private void generateResponse(HttpServletResponse response, String base64Data) {
        JSONObject json = new JSONObject();
        json.put("base64Data", base64Data);
        response.setContentType("text/javascript");
        try {
            response.getWriter().write(json.toString());
        } catch (IOException e) {
            logger.error("An error occurred while writing JSON response to the output stream: " + e.getMessage(), e);
        }
    }

    private String getBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private String getBase64(Path pdfPath) {
        try {
            return getBase64(Files.readAllBytes(pdfPath));
        } catch (IOException e) {
            logger.error("An error occurred while processing the PDF file: " + e.getMessage(), e);
            return null;
        }
    }
}
