package org.oscarehr.ws.rest.to.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@XmlRootElement
public class DocumentReviewTo1 implements Serializable {
    private Integer id;
    private Integer documentNo;
    private String providerNo;
    private ProviderTo1 reviewer;
    private Date dateTimeReviewed;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDocumentNo() {
        return documentNo;
    }

    public void setDocumentNo(Integer documentNo) {
        this.documentNo = documentNo;
    }

    public String getProviderNo() {
        return providerNo;
    }

    public void setProviderNo(String providerNo) {
        this.providerNo = providerNo;
    }

    public ProviderTo1 getReviewer() {
        return reviewer;
    }

    public void setReviewer(ProviderTo1 reviewer) {
        this.reviewer = reviewer;
    }

    public Date getDateTimeReviewed() {
        return dateTimeReviewed;
    }

    public void setDateTimeReviewed(Date dateTimeReviewed) {
        this.dateTimeReviewed = dateTimeReviewed;
    }
}
