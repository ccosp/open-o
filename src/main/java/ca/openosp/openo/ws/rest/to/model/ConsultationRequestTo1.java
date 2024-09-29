//CHECKSTYLE:OFF
/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * <p>
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


package ca.openosp.openo.ws.rest.to.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ConsultationRequestTo1 implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_NOT_COMPLETE = "1";
    private static final String DEFAULT_NON_URGENT = "2";

    private Integer id;
    private Date referralDate = new Date();
    private Integer serviceId;
    private ProfessionalSpecialistTo1 professionalSpecialist;
    private Date appointmentDate;
    private Date appointmentTime;
    private String reasonForReferral;
    private String clinicalInfo;
    private String currentMeds;
    private String allergies;
    private String providerNo;
    private Integer demographicId;
    private String status = DEFAULT_NOT_COMPLETE;
    private String statusText;
    private String sendTo;
    private String concurrentProblems;
    private String urgency = DEFAULT_NON_URGENT;
    private boolean patientWillBook;
    private String siteName;
    private Date followUpDate;
    private String signatureImg;
    private String letterheadName;
    private String letterheadAddress;
    private String letterheadPhone;
    private String letterheadFax;
    private List<ConsultationAttachmentTo1> attachments = Collections.emptyList();

    private List<LetterheadTo1> letterheadList;
    private List<FaxConfigTo1> faxList;
    private List<ConsultationServiceTo1> serviceList;
    private List<String> sendToList;

    private List<ConsultationRequestExtTo1> extras = Collections.emptyList();


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getReferralDate() {
        return referralDate;
    }

    public void setReferralDate(Date referralDate) {
        this.referralDate = referralDate;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public ProfessionalSpecialistTo1 getProfessionalSpecialist() {
        return professionalSpecialist;
    }

    public void setProfessionalSpecialist(ProfessionalSpecialistTo1 professionalSpecialist) {
        this.professionalSpecialist = professionalSpecialist;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Date getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(Date appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getReasonForReferral() {
        return reasonForReferral;
    }

    public void setReasonForReferral(String reasonForReferral) {
        this.reasonForReferral = reasonForReferral;
    }

    public String getClinicalInfo() {
        return clinicalInfo;
    }

    public void setClinicalInfo(String clinicalInfo) {
        this.clinicalInfo = clinicalInfo;
    }

    public String getCurrentMeds() {
        return currentMeds;
    }

    public void setCurrentMeds(String currentMeds) {
        this.currentMeds = currentMeds;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getProviderNo() {
        return providerNo;
    }

    public void setProviderNo(String providerNo) {
        this.providerNo = providerNo;
    }

    public Integer getDemographicId() {
        return demographicId;
    }

    public void setDemographicId(Integer demographicId) {
        this.demographicId = demographicId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public String getConcurrentProblems() {
        return concurrentProblems;
    }

    public void setConcurrentProblems(String concurrentProblems) {
        this.concurrentProblems = concurrentProblems;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public boolean isPatientWillBook() {
        return patientWillBook;
    }

    public void setPatientWillBook(boolean patientWillBook) {
        this.patientWillBook = patientWillBook;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Date getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(Date followUpDate) {
        this.followUpDate = followUpDate;
    }

    public String getSignatureImg() {
        return signatureImg;
    }

    public void setSignatureImg(String signatureImg) {
        this.signatureImg = signatureImg;
    }

    public String getLetterheadName() {
        return letterheadName;
    }

    public void setLetterheadName(String letterheadName) {
        this.letterheadName = letterheadName;
    }

    public String getLetterheadAddress() {
        return letterheadAddress;
    }

    public void setLetterheadAddress(String letterheadAddress) {
        this.letterheadAddress = letterheadAddress;
    }

    public String getLetterheadPhone() {
        return letterheadPhone;
    }

    public void setLetterheadPhone(String letterheadPhone) {
        this.letterheadPhone = letterheadPhone;
    }

    public String getLetterheadFax() {
        return letterheadFax;
    }

    public void setLetterheadFax(String letterheadFax) {
        this.letterheadFax = letterheadFax;
    }

    public List<ConsultationAttachmentTo1> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ConsultationAttachmentTo1> attachments) {
        this.attachments = attachments;
    }

    public List<LetterheadTo1> getLetterheadList() {
        return letterheadList;
    }

    public void setLetterheadList(List<LetterheadTo1> letterheadList) {
        this.letterheadList = letterheadList;
    }

    public List<FaxConfigTo1> getFaxList() {
        return faxList;
    }

    public void setFaxList(List<FaxConfigTo1> faxList) {
        this.faxList = faxList;
    }

    public List<ConsultationServiceTo1> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<ConsultationServiceTo1> serviceList) {
        this.serviceList = serviceList;
    }

    public List<String> getSendToList() {
        return sendToList;
    }

    public void setSendToList(List<String> sendToList) {
        this.sendToList = sendToList;
    }

    public List<ConsultationRequestExtTo1> getExtras() {
        return extras;
    }

    public void setExtras(List<ConsultationRequestExtTo1> extras) {
        this.extras = extras;
    }
}
