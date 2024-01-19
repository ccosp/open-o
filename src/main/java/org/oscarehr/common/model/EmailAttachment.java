package org.oscarehr.common.model;

import javax.persistence.*;

import org.oscarehr.common.model.enumerator.DocumentType;

@Entity
@Table(name = "emailAttachment")
public class EmailAttachment extends AbstractModel<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fileName;

    private String filePath;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private Integer documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logId")
    private EmailLog emailLog;

    public EmailAttachment() {}

    public EmailAttachment(String fileName, String filePath, DocumentType documentType, Integer documentId) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.documentType = documentType;
        this.documentId = documentId;
    }

    public EmailAttachment(EmailLog emailLog, String fileName, String filePath, DocumentType documentType, Integer documentId) {
        this.emailLog = emailLog;
        this.fileName = fileName;
        this.filePath = filePath;
        this.documentType = documentType;
        this.documentId = documentId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EmailLog getEmailLog() {
        return emailLog;
    }

    public void setEmailLog(EmailLog emailLog) {
        this.emailLog = emailLog;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }
}
