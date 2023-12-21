package org.oscarehr.managers;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.oscarehr.common.dao.EmailConfigDao;
import org.oscarehr.common.model.EmailAttachment;
import org.oscarehr.common.model.EmailConfig;
import org.oscarehr.common.model.enumerator.DocumentType;
import org.oscarehr.documentManager.DocumentAttachmentManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.PDFGenerationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EmailComposeManager {
    private final Logger logger = MiscUtils.getLogger();

    @Autowired
    private EmailConfigDao emailConfigDao;

    @Autowired
    private DocumentAttachmentManager documentAttachmentManager;
    @Autowired
    private FormsManager formsManager;
    
    public List<EmailAttachment> prepareEFormAttachments(LoggedInInfo loggedInInfo, String fdid, String[] attachedEForms) throws PDFGenerationException {
        List<String> attachedEFormIds = convertToList(attachedEForms);
        if (fdid != null) { attachedEFormIds.add(0, fdid); }

        List<EmailAttachment> emailAttachments = new ArrayList<>();
        for (String eFormId : attachedEFormIds) {
            Path eFormPDFPath = documentAttachmentManager.renderDocument(loggedInInfo, DocumentType.EFORM, Integer.parseInt(eFormId));
            if (eFormPDFPath != null) { emailAttachments.add(new EmailAttachment(eFormPDFPath.getFileName().toString(), eFormPDFPath.toString(), DocumentType.EFORM, Integer.parseInt(eFormId))); }
        }

        return emailAttachments;
    }

    public List<EmailAttachment> prepareEDocAttachments(LoggedInInfo loggedInInfo, String[] attachedDocuments) throws PDFGenerationException {
        List<String> attachedEDocIds = convertToList(attachedDocuments);

        List<EmailAttachment> emailAttachments = new ArrayList<>();
        for (String eDocId : attachedEDocIds) {
            Path eDocPDFPath = documentAttachmentManager.renderDocument(loggedInInfo, DocumentType.DOC, Integer.parseInt(eDocId));
            if (eDocPDFPath != null) { emailAttachments.add(new EmailAttachment(eDocPDFPath.getFileName().toString(), eDocPDFPath.toString(), DocumentType.DOC, Integer.parseInt(eDocId))); }
        }

        return emailAttachments;
    }

    public List<EmailAttachment> prepareLabAttachments(LoggedInInfo loggedInInfo, String[] attachedLabs) throws PDFGenerationException {
        List<String> attachedLabIds = convertToList(attachedLabs);

        List<EmailAttachment> emailAttachments = new ArrayList<>();
        for (String labId : attachedLabIds) {
            Path labPDFPath = documentAttachmentManager.renderDocument(loggedInInfo, DocumentType.LAB, Integer.parseInt(labId));
            if (labPDFPath != null) { emailAttachments.add(new EmailAttachment(labPDFPath.getFileName().toString(), labPDFPath.toString(), DocumentType.LAB, Integer.parseInt(labId))); }
        }

        return emailAttachments;
    }

    public List<EmailAttachment> prepareHRMAttachments(LoggedInInfo loggedInInfo, String[] attachedHRMDocuments) throws PDFGenerationException {
        List<String> attachedHRMIds = convertToList(attachedHRMDocuments);

        List<EmailAttachment> emailAttachments = new ArrayList<>();
        for (String hrmId : attachedHRMIds) {
            Path hrmPDFPath = documentAttachmentManager.renderDocument(loggedInInfo, DocumentType.HRM, Integer.parseInt(hrmId));
            if (hrmPDFPath != null) { emailAttachments.add(new EmailAttachment(hrmPDFPath.getFileName().toString(), hrmPDFPath.toString(), DocumentType.HRM, Integer.parseInt(hrmId))); }
        }

        return emailAttachments;
    }

    public List<EmailAttachment> prepareFormAttachments(HttpServletRequest request, HttpServletResponse response, String[] attachedForms) throws PDFGenerationException {
        List<String> attachedFormIds = convertToList(attachedForms);

        List<EmailAttachment> emailAttachments = new ArrayList<>();
        for (String formId : attachedFormIds) {
            Path formPDFPath = formsManager.renderForm(request, response, formId);
            if (formPDFPath != null) { emailAttachments.add(new EmailAttachment(formPDFPath.getFileName().toString(), formPDFPath.toString(), DocumentType.FORM, Integer.parseInt(formId))); }
        }

        return emailAttachments;
    }

    public List<EmailConfig> getAllSenderAccounts() {
        return emailConfigDao.fillAllActiveEmailConfigs();
    }

    private List<String> convertToList(String[] stringArray) {
        List<String> stringList = new ArrayList<>();
        if (stringArray != null) { Collections.addAll(stringList, stringArray); }
        return stringList;
    }

}
