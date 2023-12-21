package org.oscarehr.common.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "email_log")
public class EmailLog extends AbstractModel<Integer> implements Comparable<EmailLog> {

    public enum EmailStatus {
        SUCCESS,
        FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "from_email")
    private String fromEmail;

    @Column(name = "to_email")
    private String toEmail;

    @Column(name = "subject")
    private String subject;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EmailStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp")
    private Date timestamp;

    @Column(name = "encrypted_message", columnDefinition = "TEXT")
    private String encryptedMessage;

    @Column(name = "password")
    private String password;

    @Column(name = "is_encrypted")
    private Boolean isEncrypted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_id")
    private EmailConfig emailConfig;

    @OneToMany(mappedBy = "emailLog", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<EmailAttachment> emailAttachments;

    public EmailLog() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EmailConfig getEmailConfig() {
        return emailConfig;
    }

    public void setEmailConfig(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public EmailStatus getStatus() {
        return status;
    }

    public void setStatus(EmailStatus status) {
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getEncryptedMessage() {
        return encryptedMessage;
    }

    public void setEncryptedMessage(String encryptedMessage) {
        this.encryptedMessage = encryptedMessage;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsEncrypted() {
        return isEncrypted;
    }

    public void setIsEncrypted(Boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    public List<EmailAttachment> getEmailAttachments() {
        return emailAttachments;
    }

    public void setEmailAttachments(List<EmailAttachment> emailAttachments) {
        this.emailAttachments = emailAttachments;
    }

    @Override
    public int compareTo(EmailLog other) {
        // Compare based on the timestamp
        return this.timestamp.compareTo(other.timestamp);
    }
}
