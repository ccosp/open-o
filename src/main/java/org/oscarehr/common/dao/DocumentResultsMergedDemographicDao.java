package org.oscarehr.common.dao;

import java.util.ArrayList;
import oscar.oscarLab.ca.on.LabResultData;

public interface DocumentResultsMergedDemographicDao extends DocumentResultsDao{
    ArrayList<LabResultData> populateDocumentResultsDataOfAllProviders(String providerNo, String demographicNo, String status);
    ArrayList<LabResultData> populateDocumentResultsDataLinkToProvider(String providerNo, String demographicNo, String status);
    ArrayList<LabResultData> populateDocumentResultsData(String providerNo, String demographicNo, String status);
}
