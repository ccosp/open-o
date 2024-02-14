package org.oscarehr.email.core;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLContext;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.Logger;
import org.oscarehr.common.model.EmailAttachment;
import org.oscarehr.common.model.EmailConfig;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.EmailSendingException;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EmailSender {
    private final Logger logger = MiscUtils.getLogger();
    private LoggedInInfo loggedInInfo;

    private JavaMailSender javaMailSender = SpringUtils.getBean(JavaMailSender.class);
    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

    private EmailConfig emailConfig;
    private String[] recipients = new String[0];
    private String subject;
    private String body;
    private List<EmailAttachment> attachments;

    private EmailSender() { }

    public EmailSender(LoggedInInfo loggedInInfo, EmailConfig emailConfig, Email email) {
        this.loggedInInfo = loggedInInfo;
        this.emailConfig = emailConfig;
        this.recipients = email.getRecipients();
        this.subject = email.getSubject();
        this.body = StringEscapeUtils.unescapeHtml(email.getBody());
        this.attachments = email.getAttachments();
    }

    public EmailSender(LoggedInInfo loggedInInfo, EmailConfig emailConfig, String[] recipients, String subject, String body, List<EmailAttachment> attachments) {
        this.loggedInInfo = loggedInInfo;
        this.emailConfig = emailConfig;
        this.recipients = recipients;
        this.subject = subject;
        this.body = StringEscapeUtils.unescapeHtml(body);
        this.attachments = attachments;
    }
    
    public void send() throws EmailSendingException {
        if (!securityInfoManager.hasPrivilege(loggedInInfo, "_email", SecurityInfoManager.WRITE, null)) {
			throw new RuntimeException("missing required security object (_email)");
		}

        javaMailSender = createMailSender(emailConfig);
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(emailConfig.getSenderEmail(), emailConfig.getSenderFullName());
            helper.setTo(recipients);
            helper.setSubject(subject);
            helper.setText(body, true);
            addAttachments(helper, attachments);
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new EmailSendingException(e.getMessage(), e);
        }
    }

    /**
     * This framework currently supports SMTP-based email services.
     * However, the design of this framework is intended to allow for future expansion to facilitate API-based email services (such as SendGrid, ProtonMail, etc.).
     */
    private JavaMailSender createMailSender(EmailConfig emailConfig) {
        switch (emailConfig.getEmailType()) {
            case SMTP:
                javaMailSender = createTLSMailSender(emailConfig);
                break;
            case API:
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

    private void addAttachments(MimeMessageHelper helper, List<EmailAttachment> attachments) throws MessagingException {
        if (attachments == null) { return; }

        for (EmailAttachment attachment : attachments) {
            helper.addAttachment(attachment.getFileName(), new File(attachment.getFilePath()));
        }
    }

    // For debugging
    private void checkProtocolSupport() {
        logger.info("JavaMail version: " + javax.mail.Session.class.getPackage().getImplementationVersion());
        try {
            logger.info(String.join(" ", SSLContext.getDefault().getSupportedSSLParameters().getProtocols()));
        } catch (NoSuchAlgorithmException e) {
            logger.info("NoSuchAlgorithmException", e);
        }
    }
}
