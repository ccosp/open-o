package org.oscarehr.common.dao;

import org.oscarehr.caseload.CaseloadCategory;

import java.util.List;
import java.util.Map;

public interface CaseloadDao {
    List<Integer> getCaseloadDemographicSet(String searchQuery, String[] searchParams, String[] sortParams, CaseloadCategory category, String sortDir, int page, int pageSize);
    List<Map<String, Object>> getCaseloadDemographicData(String searchQuery, Object[] params);
    Integer getCaseloadDemographicSearchSize(String searchQuery, String[] searchParams);
}
