package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import org.oscarehr.common.model.Prevention;

public interface PreventionMergedDemographicDao {
    List<Prevention> findByDemographicId(Integer demographicId);
    List<Prevention> findByDemographicIdAfterDatetime(Integer demographicId, final Date dateTime);
    List<Prevention> findNotDeletedByDemographicId(Integer demographicId);
    List<Prevention> findByTypeAndDemoNo(final String preventionType, Integer demoNo);
}
