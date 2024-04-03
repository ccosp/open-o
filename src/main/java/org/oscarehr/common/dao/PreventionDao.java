package org.oscarehr.common.dao;
import java.util.Date;
import java.util.List;
import org.oscarehr.common.model.Prevention;

public interface PreventionDao extends AbstractDao<Prevention>{
    List<Prevention> findByDemographicId(Integer demographicId);
    List<Prevention> findByUpdateDate(Date updatedAfterThisDateExclusive, int itemsToReturn);
    List<Prevention> findByDemographicIdAfterDatetime(Integer demographicId, Date dateTime);
    List<Prevention> findByDemographicIdAfterDatetimeExclusive(Integer demographicId, Date dateTime);
    List<Integer> findDemographicIdsAfterDatetime(Date dateTime);
    List<Prevention> findByProviderDemographicLastUpdateDate(String providerNo, Integer demographicId, Date updatedAfterThisDateExclusive, int itemsToReturn);
    List<Prevention> findNotDeletedByDemographicIdAfterDatetime(Integer demographicId, Date dateTime);
    List<Integer> findNonDeletedIdsByDemographic(Integer demographicId);
    List<Prevention> findNotDeletedByDemographicId(Integer demographicId);
    List<Prevention> findByTypeAndDate(String preventionType, Date startDate, Date endDate);
    List<Prevention> findByTypeAndDemoNo(String preventionType, Integer demoNo);
    List<Prevention> findActiveByDemoId(Integer demoId);
    List<Prevention> findUniqueByDemographicId(Integer demographicId);
    List<Integer> findNewPreventionsSinceDemoKey(String keyName);
}
