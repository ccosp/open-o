package org.oscarehr.common.dao;

import java.util.ArrayList;
import java.util.List;
import org.oscarehr.common.model.Document;
import oscar.oscarLab.ca.on.LabResultData;

public interface DocumentResultsDao extends AbstractDao<Document> {
    boolean isSentToValidProvider(String docNo);
    boolean isSentToProvider(String docNo, String providerNo);
    ArrayList<LabResultData> populateDocumentResultsDataOfAllProviders(String providerNo, String demographicNo, String status);
    ArrayList<LabResultData> populateDocumentResultsDataLinkToProvider(String providerNo, String demographicNo, String status);
    ArrayList<LabResultData> populateDocumentResultsData(String providerNo, String demographicNo, String status);
    List<Document> getPhotosByAppointmentNo(int appointmentNo);
}
