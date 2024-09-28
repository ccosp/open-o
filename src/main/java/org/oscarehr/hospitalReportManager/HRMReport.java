//CHECKSTYLE:OFF
/**
 * Copyright (c) 2008-2012 Indivica Inc.
 * <p>
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.hospitalReportManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import omd.hrm.PersonNameSimple;
import org.apache.commons.codec.binary.Base64;
import omd.hrm.DateFullOrPartial;
import omd.hrm.Demographics;
import omd.hrm.OmdCds;
import omd.hrm.PersonNameStandard;
import omd.hrm.PersonNameStandard.LegalName.OtherName;
import omd.hrm.ReportFormat;
import omd.hrm.ReportsReceived.OBRContent;
import org.oscarehr.util.MiscUtils;
import ca.openosp.openo.util.StringUtils;

public class HRMReport {

    private OmdCds hrmReport;
    private Demographics demographics;
    private String fileLocation;
    private String fileData;

    private Integer hrmDocumentId;
    private Integer hrmParentDocumentId;

    public HRMReport(OmdCds hrmReport) {
        this.hrmReport = hrmReport;
        this.demographics = hrmReport.getPatientRecord().getDemographics();
    }

    public HRMReport(OmdCds root, String hrmReportFileLocation, String fileData) {
        this.fileData = fileData;
        this.fileLocation = hrmReportFileLocation;
        this.hrmReport = root;
        this.demographics = hrmReport.getPatientRecord().getDemographics();
    }

    public OmdCds getDocumentRoot() {
        return hrmReport;
    }

    public String getFileData() {
        return fileData;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getLegalName() {
        PersonNameStandard name = demographics.getNames();
        return name.getLegalName().getLastName().getPart() + ", " + name.getLegalName().getFirstName().getPart();
    }

    public String getLegalLastName() {
        PersonNameStandard name = demographics.getNames();
        return name.getLegalName().getLastName().getPart();
    }

    public String getLegalFirstName() {
        PersonNameStandard name = demographics.getNames();
        return name.getLegalName().getFirstName().getPart();
    }

    public List<String> getLegalOtherNames() {
        LinkedList<String> otherNames = new LinkedList<String>();
        PersonNameStandard name = demographics.getNames();
        for (OtherName otherName : name.getLegalName().getOtherName()) {
            otherNames.add(otherName.getPart());
        }

        return otherNames;
    }

    public List<Integer> getDateOfBirth() {
        List<Integer> dateOfBirthList = new ArrayList<Integer>();
        XMLGregorianCalendar fullDate = dateFP(demographics.getDateOfBirth());
        dateOfBirthList.add(fullDate.getYear());
        dateOfBirthList.add(fullDate.getMonth());
        dateOfBirthList.add(fullDate.getDay());

        return dateOfBirthList;
    }

    public String getDateOfBirthAsString() {
        List<Integer> dob = getDateOfBirth();
        return dob.get(0) + "-" + String.format("%02d", dob.get(1)) + "-" + String.format("%02d", dob.get(2));
    }

    public String getHCN() {
        return demographics.getHealthCard().getNumber();
    }

    public String getHCNVersion() {
        return demographics.getHealthCard().getVersion();
    }

    public Calendar getHCNExpiryDate() {
        return demographics.getHealthCard().getExpirydate().toGregorianCalendar();
    }

    public String getHCNProvinceCode() {
        return demographics.getHealthCard().getProvinceCode();
    }

    public String getGender() {
        return demographics.getGender().value();
    }

    public String getUniqueVendorIdSequence() {
        return demographics.getUniqueVendorIdSequence();
    }

    public String getAddressLine1() {
        if (demographics.getAddress() == null || demographics.getAddress().isEmpty()) {
            return "";
        }
        return demographics.getAddress().get(0).getStructured().getLine1();
    }

    public String getAddressLine2() {
        if (demographics.getAddress() == null || demographics.getAddress().isEmpty()) {
            return "";
        }
        return demographics.getAddress().get(0).getStructured().getLine2();
    }

    public String getAddressCity() {
        if (demographics.getAddress() == null || demographics.getAddress().isEmpty()) {
            return "";
        }
        return demographics.getAddress().get(0).getStructured().getCity();
    }

    public String getCountrySubDivisionCode() {
        if (demographics.getAddress() == null || demographics.getAddress().isEmpty()) {
            return "";
        }
        return demographics.getAddress().get(0).getStructured().getCountrySubdivisionCode();
    }

    public String getPostalCode() {
        if (demographics.getAddress() == null || demographics.getAddress().isEmpty()) {
            return "";
        }
        return demographics.getAddress().get(0).getStructured().getPostalZipCode().getPostalCode();

    }

    public String getZipCode() {
        if (demographics.getAddress() == null || demographics.getAddress().isEmpty()) {
            return "";
        }
        return demographics.getAddress().get(0).getStructured().getPostalZipCode().getZipCode();
    }

    public String getPhoneNumber() {
        if (demographics.getPhoneNumber() == null || demographics.getPhoneNumber().isEmpty()) {
            return "";
        }
        return demographics.getPhoneNumber().get(0).getContent().get(0).getValue();
    }

    public String getEnrollmentStatus() {
        return demographics.getEnrollmentStatus();
    }

    public String getPersonStatus() {
        return demographics.getPersonStatusCode().value();
    }

    public boolean isBinary() {
        if (hrmReport.getPatientRecord().getReportsReceived() != null || hrmReport.getPatientRecord().getReportsReceived().isEmpty()) {
            if (hrmReport.getPatientRecord().getReportsReceived().get(0).getFormat() == ReportFormat.BINARY) {
                return true;
            }
        }
        return false;
    }

    public String getFileExtension() {
        if (hrmReport.getPatientRecord().getReportsReceived() == null || hrmReport.getPatientRecord().getReportsReceived().isEmpty()) {
            return "";
        }
        return hrmReport.getPatientRecord().getReportsReceived().get(0).getFileExtensionAndVersion();
    }

    public String getFirstReportTextContent() {
        String result = null;
        if (hrmReport.getPatientRecord().getReportsReceived() != null || hrmReport.getPatientRecord().getReportsReceived().isEmpty()) {
            if (hrmReport.getPatientRecord().getReportsReceived().get(0).getFormat() == ReportFormat.BINARY) {
                return new Base64().encodeToString(getBinaryContent());
            }

            try {
                result = hrmReport.getPatientRecord().getReportsReceived().get(0).getContent().getTextContent();
            } catch (Exception e) {
                MiscUtils.getLogger().error("error", e);
            }
        }
        return result;
    }

    //this is actually BASE64, so using as ASCII ok.
    public byte[] getBinaryContent() {

        try {
            byte[] tmp = hrmReport.getPatientRecord().getReportsReceived().get(0).getContent().getMedia();
            return tmp;
        } catch (Exception e) {
            MiscUtils.getLogger().error("error", e);
        }
        return null;
    }

    public String getFirstReportClass() {
        if (hrmReport.getPatientRecord().getReportsReceived() == null || hrmReport.getPatientRecord().getReportsReceived().isEmpty()) {
            return "";
        }
        return hrmReport.getPatientRecord().getReportsReceived().get(0).getClazz().value();
    }

    public String getFirstReportSubClass() {
        if (hrmReport.getPatientRecord().getReportsReceived() == null || hrmReport.getPatientRecord().getReportsReceived().isEmpty()) {
            return "";
        }
        return hrmReport.getPatientRecord().getReportsReceived().get(0).getSubClass();
    }

    public Calendar getFirstReportEventTime() {

        if (hrmReport.getPatientRecord().getReportsReceived() != null &&
                !hrmReport.getPatientRecord().getReportsReceived().isEmpty() &&
                hrmReport.getPatientRecord().getReportsReceived().get(0).getEventDateTime() != null)
            return dateFP(hrmReport.getPatientRecord().getReportsReceived().get(0).getEventDateTime()).toGregorianCalendar();
        return null;
    }

    public String getMediaType() {
        String mediaType = "";
        try {
            mediaType = hrmReport.getPatientRecord().getReportsReceived().get(0).getMedia().value();
        } catch (Exception e) {
            MiscUtils.getLogger().error("error", e);
        }

        return mediaType;
    }

    public List<String> getFirstReportAuthorPhysician() {
        List<String> physicianName = new ArrayList<String>();


        if (hrmReport.getPatientRecord().getReportsReceived().get(0).getAuthorPhysician() != null) {

            String physicianHL7String = hrmReport.getPatientRecord().getReportsReceived().get(0).getAuthorPhysician().getLastName();
            if (physicianHL7String != null) {

                if (physicianHL7String.split("\\^").length == 7) {
                    String[] physicianNameArray = physicianHL7String.split("\\^");
                    physicianName.add(physicianNameArray[0]);
                    physicianName.add(physicianNameArray[1]);
                    physicianName.add(physicianNameArray[2]);
                    physicianName.add(physicianNameArray[3]);
                    physicianName.add(physicianNameArray[6]);
                    return physicianName;

                }

                for (String n : physicianHL7String.split("\\^")) {
                    physicianName.add(n);
                }
            }

            physicianHL7String = hrmReport.getPatientRecord().getReportsReceived().get(0).getAuthorPhysician().getFirstName();
            if (physicianHL7String != null) {
                for (String n : physicianHL7String.split("\\^")) {
                    physicianName.add(n);
                }
            }

        }

        return physicianName;
    }

    public String getSendingAuthor() {
        String sourceAuthor = "";
        if (hrmReport.getPatientRecord().getReportsReceived() != null && !hrmReport.getPatientRecord().getReportsReceived().isEmpty()) {
            PersonNameSimple authorPhysician = hrmReport.getPatientRecord().getReportsReceived().get(0).getAuthorPhysician();
            if (authorPhysician != null) {
                sourceAuthor = (StringUtils.noNull(authorPhysician.getFirstName()).trim() + " " + StringUtils.noNull(authorPhysician.getLastName()).trim()).trim();
            }
        }


        return sourceAuthor;
    }


    public String getSendingFacilityId() {
        if (hrmReport.getPatientRecord().getReportsReceived() == null || hrmReport.getPatientRecord().getReportsReceived().isEmpty()) {
            return "";
        }
        return hrmReport.getPatientRecord().getReportsReceived().get(0).getSendingFacility();
    }

    public String getSendingFacilityReportNo() {
        if (hrmReport.getPatientRecord().getReportsReceived() == null || hrmReport.getPatientRecord().getReportsReceived().isEmpty()) {
            return "";
        }
        return hrmReport.getPatientRecord().getReportsReceived().get(0).getSendingFacilityReportNumber();
    }

    public String getResultStatus() {
        if (hrmReport.getPatientRecord().getReportsReceived() == null || hrmReport.getPatientRecord().getReportsReceived().isEmpty()) {
            return "";
        }
        return hrmReport.getPatientRecord().getReportsReceived().get(0).getResultStatus();
    }

    public List<List<Object>> getAccompanyingSubclassList() {
        LinkedList<List<Object>> subclassList = new LinkedList<List<Object>>();

        if (hrmReport.getPatientRecord().getReportsReceived() != null || !hrmReport.getPatientRecord().getReportsReceived().isEmpty()) {
            for (OBRContent o : hrmReport.getPatientRecord().getReportsReceived().get(0).getOBRContent()) {
                LinkedList<Object> obrContentList = new LinkedList<Object>();

                obrContentList.add(o.getAccompanyingSubClass());
                obrContentList.add(o.getAccompanyingMnemonic());
                obrContentList.add(o.getAccompanyingDescription());

                if (o.getObservationDateTime() != null) {
                    GregorianCalendar calendar = dateFP(o.getObservationDateTime()).toGregorianCalendar();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                    sdf.setTimeZone(calendar.getTimeZone());

                    Date date = calendar.getTime();
                    String formattedDate = sdf.format(calendar.getTime());

                    obrContentList.add(date);
                    obrContentList.add(formattedDate);
                }

                subclassList.add(obrContentList);
            }
        }
        return subclassList;
    }

    public String getFirstAccompanyingSubClassDateTime() {
        if (hrmReport.getPatientRecord().getReportsReceived() != null &&
                !hrmReport.getPatientRecord().getReportsReceived().isEmpty() &&
                hrmReport.getPatientRecord().getReportsReceived().get(0).getOBRContent() != null &&
                hrmReport.getPatientRecord().getReportsReceived().get(0).getOBRContent().get(0) != null &&
                hrmReport.getPatientRecord().getReportsReceived().get(0).getOBRContent().get(0).getObservationDateTime() != null) {

            GregorianCalendar calendar = dateFP(hrmReport.getPatientRecord().getReportsReceived().get(0).getOBRContent().get(0).getObservationDateTime()).toGregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            sdf.setTimeZone(calendar.getTimeZone());

            return sdf.format(calendar.getTime());
        }

        return "";
    }

    public String getFirstAccompanyingSubClass() {
        if (hrmReport.getPatientRecord().getReportsReceived() != null &&
                !hrmReport.getPatientRecord().getReportsReceived().isEmpty() &&
                hrmReport.getPatientRecord().getReportsReceived().get(0).getOBRContent() != null &&
                hrmReport.getPatientRecord().getReportsReceived().get(0).getOBRContent().get(0) != null &&
                hrmReport.getPatientRecord().getReportsReceived().get(0).getOBRContent().get(0).getObservationDateTime() != null) {
            return (hrmReport.getPatientRecord().getReportsReceived().get(0).getOBRContent().get(0).getAccompanyingDescription());
        }

        return null;
    }

    public String getMessageUniqueId() {
        if (hrmReport.getPatientRecord().getTransactionInformation() == null || hrmReport.getPatientRecord().getTransactionInformation().isEmpty()) {
            return "";
        }
        return hrmReport.getPatientRecord().getTransactionInformation().get(0).getMessageUniqueID();
    }

    public String getDeliverToUserId() {
        if (hrmReport.getPatientRecord().getTransactionInformation() == null || hrmReport.getPatientRecord().getTransactionInformation().isEmpty()) {
            return "";
        }
        return hrmReport.getPatientRecord().getTransactionInformation().get(0).getDeliverToUserID();
    }

    public String getDeliverToUserIdFirstName() {
        if (hrmReport.getPatientRecord().getTransactionInformation() == null || hrmReport.getPatientRecord().getTransactionInformation().isEmpty()) {
            return "";
        }
        if (hrmReport.getPatientRecord().getTransactionInformation().get(0).getProvider() == null)
            return null;
        return hrmReport.getPatientRecord().getTransactionInformation().get(0).getProvider().getFirstName();
    }

    public String getDeliverToUserIdLastName() {
        if (hrmReport.getPatientRecord().getTransactionInformation() == null || hrmReport.getPatientRecord().getTransactionInformation().isEmpty()) {
            return "";
        }
        if (hrmReport.getPatientRecord().getTransactionInformation().get(0).getProvider() == null)
            return null;
        return hrmReport.getPatientRecord().getTransactionInformation().get(0).getProvider().getLastName();
    }

    public String getDeliveryToUserIdFormattedName() {
        String name = "";
        if (getDeliverToUserIdLastName() != null) {
            name = getDeliverToUserIdLastName();
        }
        if (getDeliverToUserIdFirstName() != null) {
            if (!name.isEmpty()) {
                name = name + ", " + getDeliverToUserIdFirstName();
            } else {
                name = getDeliverToUserIdFirstName();
            }
        }

        return name;
    }

    public Integer getHrmDocumentId() {
        return hrmDocumentId;
    }

    public void setHrmDocumentId(Integer hrmDocumentId) {
        this.hrmDocumentId = hrmDocumentId;
    }

    public Integer getHrmParentDocumentId() {
        return hrmParentDocumentId;
    }

    public void setHrmParentDocumentId(Integer hrmParentDocumentId) {
        this.hrmParentDocumentId = hrmParentDocumentId;
    }


    XMLGregorianCalendar dateFP(DateFullOrPartial dfp) {
        if (dfp == null) return null;

        if (dfp.getDateTime() != null) return dfp.getDateTime();
        else if (dfp.getFullDate() != null) return dfp.getFullDate();
        else if (dfp.getYearMonth() != null) return dfp.getYearMonth();
        else if (dfp.getYearOnly() != null) return dfp.getYearOnly();
        return null;
    }


}
