package org.oscarehr.email.core;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLContext;

import org.apache.logging.log4j.Logger;
import org.oscarehr.common.model.EmailAttachment;
import org.oscarehr.common.model.EmailConfig;
import org.oscarehr.common.model.EmailLog;
import org.oscarehr.util.EmailSendingException;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EmailSender {
    private final Logger logger = MiscUtils.getLogger();
    private JavaMailSender javaMailSender = SpringUtils.getBean(JavaMailSender.class);
    private EmailConfig emailConfig;
    private String recipient;
    private String subject;
    private String content;
    private List<EmailAttachment> attachments;

    public EmailSender() { }

    public EmailSender(EmailLog emailLog) {
        this.emailConfig = emailLog.getEmailConfig();
        this.recipient = emailLog.getToEmail();
        this.subject = emailLog.getSubject();
        this.content = emailLog.getBody();
        this.attachments = emailLog.getEmailAttachments();
    }

    public EmailSender(EmailConfig emailConfig, String recipient, String subject, String content, List<EmailAttachment> attachments) {
        this.emailConfig = emailConfig;
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
        this.attachments = attachments;
    }
    
    public void send() throws EmailSendingException {
        javaMailSender = createMailSender(emailConfig);
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(emailConfig.getSenderEmail());
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(content, true);
            addAttachments(helper, attachments);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send email to: " + emailConfig.getSenderEmail(), e);
        } catch (MailAuthenticationException e) {
            throw new EmailSendingException("Authentication failed while sending email to: " + emailConfig.getSenderEmail(), e);
        } catch (MailSendException e) {
            throw new EmailSendingException("Failed to send email to: " + emailConfig.getSenderEmail(), e);
        } catch (MailParseException e) {
            throw new EmailSendingException("Error parsing the email content for: " + emailConfig.getSenderEmail(), e);
        } catch (Exception e) {
            throw new EmailSendingException("Unexpected error while sending email to: " + emailConfig.getSenderEmail(), e);
        }
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
