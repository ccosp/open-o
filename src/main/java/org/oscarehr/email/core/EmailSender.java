package org.oscarehr.email.core;

import java.nio.file.Path;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.oscarehr.common.model.EmailConfig;
import org.oscarehr.managers.EmailManager;
import org.oscarehr.util.SpringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class EmailSender {
    private JavaMailSender javaMailSender = SpringUtils.getBean(JavaMailSender.class);
    
    // public Boolean sendEmail(EmailConfig config, String toEmail, String subject, String body, List<Path> attachments) {
    //     javaMailSender = createMailSender(config);
    //     MimeMessage message = javaMailSender.createMimeMessage();
    //     try {
    //         MimeMessageHelper helper = new MimeMessageHelper(message, true);
    //         helper.setFrom(config.getSenderEmail());
    //         helper.setTo(toEmail);
    //         helper.setSubject(subject);
    //         helper.setText(body, true);
    //         addAttachments(helper, attachments);
    //         javaMailSender.send(message);
    //     } catch (MessagingException e) {
    //         logger.error("Failed to send email to: " + toEmail, e);
    //         return false;
    //     }
    //     return true;
    // }
}
