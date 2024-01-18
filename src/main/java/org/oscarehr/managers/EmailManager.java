package org.oscarehr.managers;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.Logger;
import org.oscarehr.common.dao.EmailConfigDao;
import org.oscarehr.common.dao.EmailLogDao;
import org.oscarehr.common.model.EmailAttachment;
import org.oscarehr.common.model.EmailConfig;
import org.oscarehr.common.model.EmailLog;
import org.oscarehr.common.model.EmailLog.EmailStatus;
import org.oscarehr.common.model.enumerator.DocumentType;
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

import oscar.util.StringUtils;

@Service
public class EmailManager {
    private final Logger logger = MiscUtils.getLogger();

    @Autowired
    private EmailConfigDao emailConfigDao;
    @Autowired
    private EmailLogDao emailLogDao;

    @Autowired
    private DocumentAttachmentManager documentAttachmentManager;

    public EmailLog sendEmail(Email email) {
        sanitizeEmailFields(email);
        EmailLog emailLog = prepareEmailForOutbox(email);
        try {
            if (email.getIsEncrypted()) { encryptEmail(email); }
            EmailSender emailSender = new EmailSender(emailLog.getEmailConfig(), email);
            emailSender.send();
            updateEmailStatus(emailLog, EmailStatus.SUCCESS, "");
        } catch (EmailSendingException e) {
            updateEmailStatus(emailLog, EmailStatus.FAILED, e.getMessage());
            logger.error("Failed to send email", e);
        }
        return emailLog;
    }

    public EmailLog prepareEmailForOutbox(Email email) {
        EmailConfig emailConfig = emailConfigDao.findActiveEmailConfig(email.getSender());
        EmailLog emailLog = new EmailLog(emailConfig, email.getSender(), email.getRecipients(), email.getSubject(), email.getBody(), EmailStatus.OUTBOX);
        setEmailAttachments(emailLog, email.getAttachments());
        emailLog.setEncryptedMessage(email.getEncryptedMessage());
        emailLog.setPassword(email.getPassword());
        emailLog.setPasswordClue(email.getPasswordClue());
        emailLog.setIsEncrypted(email.getIsEncrypted());
        emailLog.setIsAttachmentEncrypted(email.getIsAttachmentEncrypted());
        emailLog.setChartDisplayOption(email.getChartDisplayOption());
        emailLog.setTransactionType(email.getTransactionType());
        emailLog.setDemographicNo(email.getDemographicNo());
        emailLogDao.persist(emailLog);
        return emailLog;
    }

    public EmailLog updateEmailStatus(EmailLog emailLog, EmailStatus emailStatus, String errorMessage) {
        emailLog.setStatus(emailStatus);
        emailLog.setErrorMessage(errorMessage);
        emailLog.setTimestamp(new Date());
        emailLogDao.merge(emailLog);
        return emailLog;
    }

    private void setEmailAttachments(EmailLog emailLog, List<EmailAttachment> emailAttachments) {
        List<EmailAttachment> emailAttachmentList = new ArrayList<>();
        for (EmailAttachment emailAttachment : emailAttachments) {
            emailAttachmentList.add(new EmailAttachment(emailLog, emailAttachment.getFileName(), emailAttachment.getFilePath(), emailAttachment.getDocumentType(), emailAttachment.getDocumentId()));
        }
        emailLog.setEmailAttachments(emailAttachmentList);  
    }

    private void sanitizeEmailFields(Email email) {
        if (StringUtils.isNullOrEmpty(email.getEncryptedMessage()) && email.getAttachments().isEmpty()) {
            email.setIsEncrypted(false);
            email.setIsAttachmentEncrypted(false);
            email.setPassword("");
            email.setPasswordClue("");
        } else if (StringUtils.isNullOrEmpty(email.getEncryptedMessage()) && email.getAttachments().size() > 0 && !email.getIsAttachmentEncrypted()) {
            email.setIsEncrypted(false);
            email.setIsAttachmentEncrypted(false);
            email.setPassword("");
            email.setPasswordClue("");
        } else if (email.getAttachments().isEmpty()) {
            email.setIsAttachmentEncrypted(false);
        } else if (!email.getIsEncrypted()) {
            email.setIsAttachmentEncrypted(false);
            email.setPassword("");
            email.setPasswordClue("");
        }
    }

    private void encryptEmail(Email email) throws EmailSendingException {
        // Encrypt message and attachment
        List<EmailAttachment> encryptableAttachments = new ArrayList<>();
        if (!StringUtils.isNullOrEmpty(email.getEncryptedMessage())) { encryptableAttachments.add(createMessageAttachment(email.getEncryptedMessage())); }
        if (email.getIsAttachmentEncrypted() && !email.getAttachments().isEmpty()) { encryptableAttachments.addAll(email.getAttachments()); }
        encryptAttachments(encryptableAttachments, email.getPassword());

        List<EmailAttachment> emailAttachments = new ArrayList<>();
        emailAttachments.addAll(encryptableAttachments);
        if (!email.getIsAttachmentEncrypted() && !email.getAttachments().isEmpty()) { emailAttachments.addAll(email.getAttachments()); }
        addPasswordClue(email);
        email.setAttachments(emailAttachments);
    }

    private EmailAttachment createMessageAttachment(String message) {
        if (StringUtils.isNullOrEmpty(message)) { return null; }
        Path encryptedMessagePDF = ConvertToEdoc.saveAsTempPDF(StringEscapeUtils.unescapeHtml(message));
        EmailAttachment emailAttachment = new EmailAttachment("message.pdf", encryptedMessagePDF.toString(), DocumentType.DOC, -1);
        return emailAttachment;
    }

    private void encryptAttachments(List<EmailAttachment> encryptableAttachments, String password) throws EmailSendingException {
        for (EmailAttachment attachment: encryptableAttachments) {
            try {
                Path attachmentPDFPath = Paths.get(attachment.getFilePath());
                attachmentPDFPath = PDFEncryptionUtil.encryptPDF(attachmentPDFPath, password);
                attachment.setFilePath(attachmentPDFPath.toString());
            } catch (IOException e) {
                logger.error("Failed to create encrypted email attachments", e);
                throw new EmailSendingException("Failed to create encrypted email attachments", e);
            }
        }
    }

    private Path concatPDFs(List<EmailAttachment> attachments) throws PDFGenerationException {
        if (attachments == null || attachments.isEmpty()) { return null; }
        
        List<Path> attachmentPathList = new ArrayList<>();
        for (EmailAttachment emailAttachment : attachments) {
            attachmentPathList.add(Paths.get(emailAttachment.getFilePath()));
        }

        return documentAttachmentManager.concatPDF(attachmentPathList);
    }

    private void addPasswordClue(Email email) {
        String passwordClue = StringEscapeUtils.escapeHtml("<br><br><i>" + email.getPasswordClue() + "</i>");
        String bodyWithClue = email.getBody() + passwordClue;
        email.setBody(bodyWithClue);
    }
}
