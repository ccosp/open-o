package org.oscarehr.email.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.oscarehr.common.model.EmailAttachment;
import org.oscarehr.common.model.EmailConfig;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.EmailSendingException;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

public class APISendGridEmailSender {
    private final Logger logger = MiscUtils.getLogger();
    private LoggedInInfo loggedInInfo;

    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

    private EmailConfig emailConfig;
    private String[] recipients = new String[0];
    private String subject;
    private String body;
    private List<EmailAttachment> attachments;

    private APISendGridEmailSender() { }

    public APISendGridEmailSender(LoggedInInfo loggedInInfo, EmailConfig emailConfig, String[] recipients, String subject, String body, List<EmailAttachment> attachments) {
        this.loggedInInfo = loggedInInfo;
        this.emailConfig = emailConfig;
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
        this.attachments = attachments;
    }

    public void send() throws EmailSendingException {
        if (!securityInfoManager.hasPrivilege(loggedInInfo, "_email", SecurityInfoManager.WRITE, null)) {
			throw new RuntimeException("missing required security object (_email)");
		}

        Mail mail = new Mail();       
        mail.setFrom(new Email(emailConfig.getSenderEmail(), emailConfig.getSenderFullName()));
        addToEmail(mail, recipients);
        mail.setSubject(subject);
        mail.addContent(new Content("text/plain", body));
        addAttachments(mail, attachments);

        String apiKey = getAPIKey();
        SendGrid sendGrid = new SendGrid(apiKey);
        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            logger.info(response.getStatusCode());
            logger.info(response.getBody());
            logger.info(response.getHeaders());
        } catch (Exception e) {
            throw new EmailSendingException(e.getMessage(), e);
        }
    }

    private void addToEmail(Mail mail, String[] recipients) {
        Personalization personalization = new Personalization();
        for (String toEmail : recipients) { 
            personalization.addTo(new Email(toEmail)); 
        }
        mail.addPersonalization(personalization);
    }

    private void addAttachments(Mail mail, List<EmailAttachment> attachments) throws EmailSendingException {
        for (EmailAttachment emailAttachment : attachments) {
            try {
                Path path = Paths.get(emailAttachment.getFilePath());
                String attachmentContent = Base64.encodeBase64String(Files.readAllBytes(path)); 
                Attachments sendGridAttachment = new Attachments();
                sendGridAttachment.setContent(attachmentContent);
                sendGridAttachment.setType("application/pdf");
                sendGridAttachment.setFilename(emailAttachment.getFileName());
                mail.addAttachments(sendGridAttachment);
            } catch (Exception e) {
                throw new EmailSendingException("Failed to attach " + emailAttachment.getFileName() + " while sending email using SendGrid.");
            }          
        }
    }

    private String getAPIKey() throws EmailSendingException {
        String apiKey;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(emailConfig.getConfigDetailsJson());
            apiKey = jsonNode.get("api_key").asText();
        } catch (IOException e) {
            throw new EmailSendingException("Invalid credentials configured for " + emailConfig.getSenderEmail());
        }
        return apiKey;
    }
}
