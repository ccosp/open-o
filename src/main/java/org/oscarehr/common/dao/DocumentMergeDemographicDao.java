package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import org.oscarehr.common.model.Document;
import org.oscarehr.documentManager.EDocUtil.EDocSort;

public interface DocumentMergeDemographicDao {
    List<Object[]> findDocuments(String module, String moduleid, String docType, boolean includePublic, boolean includeDeleted, boolean includeActive, EDocSort sort, Date since);
    List<Document> findByDemographicId(String demoNo);
}
