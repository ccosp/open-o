package org.oscarehr.common.dao;

public interface PregnancyFormsDao {
    Integer getLatestFormIdByPregnancy(Integer episodeId);
    Integer getLatestFormIdByDemographicNo(Integer demographicNo);
    Integer getLatestAR2005FormIdByDemographicNo(Integer demographicNo);
}
