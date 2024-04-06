package org.oscarehr.common.dao;

import org.oscarehr.common.model.DemographicStudy;
import org.oscarehr.common.model.DemographicStudyPK;

import java.util.List;

public interface DemographicStudyDao extends AbstractDao<DemographicStudy> {
    List<DemographicStudy> findAll();
    int removeByDemographicNo(Integer demographicNo);
    DemographicStudy findByDemographicNoAndStudyNo(int demographicNo, int studyNo);
    List<DemographicStudy> findByStudyNo(int studyNo);
    List<DemographicStudy> findByDemographicNo(int demographicNo);
}
