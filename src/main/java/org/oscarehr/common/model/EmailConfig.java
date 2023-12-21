package org.oscarehr.common.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "email_config")
public class EmailConfig extends AbstractModel<Integer> {

    public enum EmailType {
        SMTP,
        API
    }

    public enum EmailProvider {
        GMAIL,
        OUTLOOK,
        SENDGRID
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email_type")
    @Enumerated(EnumType.STRING)
    private EmailType emailType;

    @Column(name = "email_provider")
    @Enumerated(EnumType.STRING)
    private EmailProvider emailProvider;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "sender_email")
    private String senderEmail;

    @Column(name = "config_details", columnDefinition = "TEXT")
    private String configDetailsJson;

    @OneToMany(mappedBy = "emailConfig", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<EmailLog> emailLogs;

    public EmailConfig() {}

    public EmailConfig(EmailType emailType, EmailProvider emailProvider, String senderEmail) {
        this.emailType = emailType;
        this.emailProvider = emailProvider;
        this.senderEmail = senderEmail;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
    }

    public EmailProvider getEmailProvider() {
        return emailProvider;
    }

    public void setEmailProvider(EmailProvider emailProvider) {
        this.emailProvider = emailProvider;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getConfigDetailsJson() {
        return configDetailsJson;
    }

    public void setConfigDetailsJson(String configDetailsJson) {
        this.configDetailsJson = configDetailsJson;
    }

    public List<EmailLog> getEmailLogs() {
        return emailLogs;
    }

    public void setEmailLogs(List<EmailLog> emailLogs) {
        this.emailLogs = emailLogs;
    }
}
