package org.oscarehr.managers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.oscarehr.common.dao.EmailConfigDao;
import org.oscarehr.common.dao.EmailLogDao;
import org.oscarehr.common.model.EmailAttachment;
import org.oscarehr.common.model.EmailConfig;
import org.oscarehr.common.model.EmailLog;
import org.oscarehr.common.model.EmailLog.EmailStatus;
import org.oscarehr.documentManager.ConvertToEdoc;
import org.oscarehr.documentManager.DocumentAttachmentManager;
import org.oscarehr.email.core.Email;
import org.oscarehr.email.core.EmailSender;
import org.oscarehr.util.EmailSendingException;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.PDFEncryptionUtil;
import org.oscarehr.util.PDFGenerationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailManager {
    private final Logger logger = MiscUtils.getLogger();

    @Autowired
    private EmailConfigDao emailConfigDao;
    @Autowired
    private EmailLogDao emailLogDao;

    @Autowired
    private DocumentAttachmentManager documentAttachmentManager;

    public EmailLog sendEmail(Email email) throws EmailSendingException {
        EmailLog emailLog = prepareEmailForOutbox(email);
        EmailSender emailSender = new EmailSender(emailLog);
        try {
            emailSender.send();
            updateEmailStatus(emailLog, EmailStatus.SUCCESS, "");
        } catch (EmailSendingException e) {
            updateEmailStatus(emailLog, EmailStatus.FAILED, e.getMessage());
            throw new EmailSendingException("Failed to send email", e);
        }
        return emailLog;
    }

    public EmailLog prepareEmailForOutbox(Email email) {
        EmailConfig emailConfig = emailConfigDao.findActiveEmailConfig(email.getSender());
        EmailLog emailLog = new EmailLog(emailConfig, email.getSender(), email.getRecipient(), email.getSubject(), email.getMessage(), EmailStatus.OUTBOX);
        setEmailAttachments(emailLog, email.getAttachments());
        emailLogDao.saveEmailLog(emailLog);
        return emailLog;
    }

    public EmailLog updateEmailStatus(EmailLog emailLog, EmailStatus emailStatus, String errorMessage) {
        emailLog.setStatus(emailStatus);
        emailLog.setErrorMessage(errorMessage);
        emailLog.setTimestamp(new Date());
        emailLogDao.saveEmailLog(emailLog);
        return emailLog;
    }

    private void setEmailAttachments(EmailLog emailLog, List<EmailAttachment> emailAttachments) {
        List<EmailAttachment> emailAttachmentList = new ArrayList<>();
        for (EmailAttachment emailAttachment : emailAttachments) {
            emailAttachmentList.add(new EmailAttachment(emailLog, emailAttachment.getFileName(), emailAttachment.getFilePath(), emailAttachment.getDocumentType(), emailAttachment.getDocumentId()));
        }
        emailLog.setEmailAttachments(emailAttachmentList);  
    }

    private Path createEncryptedBodyWithAttachments(String body, List<Path> attachments, String password) {
        Path encryptedPDF = null;
        try {
            Path combinedPDFPath = concatBodyWithAttachments(body, attachments);
            encryptedPDF = PDFEncryptionUtil.encryptPDF(combinedPDFPath, password);
        } catch (IOException | PDFGenerationException e) {
            logger.error("Failed to create encrypted email attachments", e);
        }
        return encryptedPDF;
    }

    private Path concatBodyWithAttachments(String body, List<Path> attachments) throws PDFGenerationException {
        if (attachments == null) { attachments = new ArrayList<>(); }
        
        Path htmlBodyPDF = ConvertToEdoc.saveAsTempPDF(body);
        if (htmlBodyPDF != null) { attachments.add(0, htmlBodyPDF); }

        return documentAttachmentManager.concatPDF(attachments);
    }
}
