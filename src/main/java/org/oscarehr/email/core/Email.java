package org.oscarehr.email.core;

import java.nio.file.Path;
import java.util.List;

import org.oscarehr.common.model.EmailConfig;

public class Email {
    private EmailConfig senderConfig;
    private String recipient;
    private String subject;
    private String content;
    private List<Path> attachments;

    public Email() {
    }

    public Email(EmailConfig senderConfig, String recipient, String subject, String content, List<Path> attachments) {
        this.senderConfig = senderConfig;
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
        this.attachments = attachments;
    }

    public EmailConfig getSenderConfig() {
        return senderConfig;
    }

    public void setSenderConfig(EmailConfig senderConfig) {
        this.senderConfig = senderConfig;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Path> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Path> attachments) {
        this.attachments = attachments;
    }
}


