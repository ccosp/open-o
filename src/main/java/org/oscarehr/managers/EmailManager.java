package org.oscarehr.managers;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLContext;

import org.apache.logging.log4j.Logger;
import org.oscarehr.common.dao.EmailConfigDao;
import org.oscarehr.common.model.EmailAttachment;
import org.oscarehr.common.model.EmailConfig;
import org.oscarehr.common.model.EmailLog;
import org.oscarehr.documentManager.ConvertToEdoc;
import org.oscarehr.documentManager.DocumentAttachmentManager;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.PDFEncryptionUtil;
import org.oscarehr.util.PDFGenerationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmailManager {
    private final Logger logger = MiscUtils.getLogger();
    @Autowired
    private DocumentAttachmentManager documentAttachmentManager;
    @Autowired
    private EmailConfigDao emailConfigDao;
    @Autowired
    private JavaMailSender javaMailSender;

    public Boolean sendEmail(EmailLog emailLog) {
        List<Path> attachments = new ArrayList<>();
        for (EmailAttachment emailAttachment : emailLog.getEmailAttachments()) {
            attachments.add(Paths.get(emailAttachment.getFilePath()));
        }
        return sendEmail(emailLog.getEmailConfig(), emailLog.getToEmail(), emailLog.getSubject(), emailLog.getBody(), attachments);
    }

    public Boolean sendEmail(EmailConfig config, String toEmail, String subject, String body, List<Path> attachments) {
        javaMailSender = createMailSender(config);
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(config.getSenderEmail());
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);
            addAttachments(helper, attachments);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Failed to send email to: " + toEmail, e);
            return false;
        }
        return true;
    }

    public Boolean sendEncryptedEmail(EmailConfig config, String toEmail, String subject, String primaryBody, String secondaryBody, List<Path> attachments, String password) {
        Path encryptedBodyWithAttachmentsPath = createEncryptedBodyWithAttachments(primaryBody, attachments, password);
        return sendEmail(config, toEmail, subject, secondaryBody, Arrays.asList(encryptedBodyWithAttachmentsPath)); 
    }

    private JavaMailSender createMailSender(EmailConfig emailConfig) {
        switch (emailConfig.getEmailProvider()) {
            case GMAIL:
                javaMailSender = createTLSMailSender(emailConfig);
                break;                
            case OUTLOOK:
                javaMailSender = createTLSMailSender(emailConfig);
                break;
            case SENDGRID:
                break;
            default:
                break;
        }
        return javaMailSender; 
    }

    private JavaMailSender createTLSMailSender(EmailConfig emailConfig) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(emailConfig.getConfigDetailsJson());
            String host = jsonNode.get("host").asText();
            String port = jsonNode.get("port").asText();
            String username = jsonNode.get("username").asText();
            String password = jsonNode.get("password").asText();

            mailSender.setHost(host);
            mailSender.setPort(Integer.parseInt(port));
            mailSender.setUsername(username);
            mailSender.setPassword(password);

            Properties properties = new Properties();
            properties.put("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.starttls.required", "true");
            properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
            properties.put("mail.debug", "false");

            mailSender.setJavaMailProperties(properties);
        } catch (IOException e) {
            logger.error("Failed to create mail sender", e);
        }
        return mailSender;
    }

    private void addAttachments(MimeMessageHelper helper, List<Path> attachments) throws MessagingException {
        if (attachments == null) { return; }

        for (Path attachment : attachments) {
            helper.addAttachment(attachment.getFileName().toString(), attachment.toFile());
        }
    }

    private Path createEncryptedBodyWithAttachments(String body, List<Path> attachments, String password) {
        Path combinedPDFPath = concatBodyWithAttachments(body, attachments);
        try {
            return PDFEncryptionUtil.encryptPDF(combinedPDFPath, password);
        } catch (IOException e) {
            logger.error("Failed to encrypt email", e);
            return null;
        }
    }

    private Path concatBodyWithAttachments(String body, List<Path> attachments) {
        if (attachments == null) { attachments = new ArrayList<>(); }
        
        Path htmlBodyPDF = ConvertToEdoc.saveAsTempPDF(body);
        if (htmlBodyPDF != null) { attachments.add(0, htmlBodyPDF); }

        return concatPDF(attachments);
    }

    private Path concatPDF(List<Path> attachments) {
        ArrayList<Object> pdfDocumentList = new ArrayList<>();
        for (Path attachment : attachments) { pdfDocumentList.add(attachment.toString()); }
        
        try {
            return documentAttachmentManager.concatPDF(pdfDocumentList);
        } catch (PDFGenerationException e) {
            logger.error("Failed to concat pdf", e);
            return null;
        }
    }

    private void checkProtocolSupport() {
        logger.info("JavaMail version: " + javax.mail.Session.class.getPackage().getImplementationVersion());
        try {
            logger.info(String.join(" ", SSLContext.getDefault().getSupportedSSLParameters().getProtocols()));
        } catch (NoSuchAlgorithmException e) {
            logger.info("NoSuchAlgorithmException", e);
        }
    }
}
