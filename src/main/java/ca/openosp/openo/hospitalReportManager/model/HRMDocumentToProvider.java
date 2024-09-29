//CHECKSTYLE:OFF
/**
 * Copyright (c) 2008-2012 Indivica Inc.
 * <p>
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package ca.openosp.openo.hospitalReportManager.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import ca.openosp.openo.common.model.AbstractModel;

@Entity
public class HRMDocumentToProvider extends AbstractModel<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String providerNo;
    private Integer hrmDocumentId;
    private Integer signedOff = 0;
    private Date signedOffTimestamp;
    private Integer viewed = 0;

    @Override
    public Integer getId() {
        return id;
    }

    public String getProviderNo() {
        return providerNo;
    }

    public void setProviderNo(String providerNo) {
        this.providerNo = providerNo;
    }

    public Integer getSignedOff() {
        return signedOff;
    }

    public void setSignedOff(Integer signedOff) {
        this.signedOff = signedOff;
    }

    public Date getSignedOffTimestamp() {
        return signedOffTimestamp;
    }

    public void setSignedOffTimestamp(Date signedOffTimestamp) {
        this.signedOffTimestamp = signedOffTimestamp;
    }

    public Integer getHrmDocumentId() {
        return hrmDocumentId;
    }

    public void setHrmDocumentId(Integer hrmDocumentId) {
        this.hrmDocumentId = hrmDocumentId;
    }

    public Integer getViewed() {
        return viewed;
    }

    public void setViewed(Integer viewed) {
        this.viewed = viewed;
    }


}
