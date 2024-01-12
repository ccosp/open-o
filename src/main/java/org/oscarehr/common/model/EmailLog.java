package org.oscarehr.common.model;

import javax.persistence.*;

import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "email_log")
public class EmailLog extends AbstractModel<Integer> implements Comparable<EmailLog> {

    public enum EmailStatus {
        SUCCESS,
        FAILED,
        DRAFT,
        OUTBOX
    }

    public enum ChartDisplayOption {
        WITHOUT_NOTE,
        WITH_FULL_NOTE
    }

    public enum DocumentGenerationOption {
        WITHOUT_PDF,
        WITH_FULL_PDF
    }

    public enum TransactionType {
        EFORM,
        CONSULTATION,
        TICKLER,
        DIRECT
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

    @Lob
    @Column(name = "body", columnDefinition = "BLOB")
    private byte[] body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EmailStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp")
    private Date timestamp = new Date();

    @Lob
    @Column(name = "encrypted_message", columnDefinition = "BLOB")
    private byte[] encryptedMessage;

    @Column(name = "password")
    private String password;

    @Column(name = "password_clue")
    private String passwordClue;

    @Column(name = "is_encrypted")
    private Boolean isEncrypted;

    @Column(name = "is_attachment_encrypted")
    private Boolean isAttachmentEncrypted;

    @Enumerated(EnumType.STRING)
    @Column(name = "chart_display_option")
    private ChartDisplayOption chartDisplayOption;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_generation_option")
    private DocumentGenerationOption documentGenerationOption;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @Column(name = "demographic_no")
    private Integer demographicNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_id")
    private EmailConfig emailConfig;

    @OneToMany(mappedBy = "emailLog", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<EmailAttachment> emailAttachments;

    public EmailLog() {}

    public EmailLog(EmailConfig emailConfig, String fromEmail, String[] toEmail, String subject, String body, EmailStatus status) {
        this.emailConfig = emailConfig;
        this.fromEmail = fromEmail;
        this.toEmail = toEmail != null ? String.join(";", toEmail) : "";
        this.subject = subject;
        this.body = Base64.encodeBase64(body.getBytes(StandardCharsets.UTF_8));
        this.status = status;
        this.timestamp = new Date();
    }

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

    public String[] getToEmail() {
        return toEmail.split(";");
    }

    public void setToEmail(String[] toEmail) {
        this.toEmail = toEmail != null ? String.join(";", toEmail) : "";
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return new String(Base64.decodeBase64(body), StandardCharsets.UTF_8);
    }

    public void setBody(String body) {
        this.body = Base64.encodeBase64(body.getBytes(StandardCharsets.UTF_8));
    }

    public EmailStatus getStatus() {
        return status;
    }

    public void setStatus(EmailStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getEncryptedMessage() {
        return new String(Base64.decodeBase64(encryptedMessage), StandardCharsets.UTF_8);
    }

    public void setEncryptedMessage(String encryptedMessage) {
        this.encryptedMessage = Base64.encodeBase64(encryptedMessage.getBytes(StandardCharsets.UTF_8));
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordClue() {
        return passwordClue;
    }

    public void setPasswordClue(String passwordClue) {
        this.passwordClue = passwordClue;
    }

    public Boolean getIsEncrypted() {
        return isEncrypted;
    }

    public void setIsEncrypted(Boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    public Boolean getIsAttachmentEncrypted() {
        return isAttachmentEncrypted;
    }

    public void setIsAttachmentEncrypted(Boolean isAttachmentEncrypted) {
        this.isAttachmentEncrypted = isAttachmentEncrypted;
    }

    public ChartDisplayOption getChartDisplayOption() {
        return chartDisplayOption;
    }

    public void setChartDisplayOption(ChartDisplayOption chartDisplayOption) {
        this.chartDisplayOption = chartDisplayOption;
    }

    public DocumentGenerationOption getDocumentGenerationOption() {
        return documentGenerationOption;
    }

    public void setDocumentGenerationOption(DocumentGenerationOption documentGenerationOption) {
        this.documentGenerationOption = documentGenerationOption;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getDemographicNo() {
        return demographicNo;
    }

    public void setDemographicNo(Integer demographicNo) {
        this.demographicNo = demographicNo;
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
