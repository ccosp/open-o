package org.oscarehr.common.dao;

import org.oscarehr.common.model.IntegratorControl;
import java.util.List;

public interface IntegratorControlDao extends AbstractDao<IntegratorControl> {
    List<IntegratorControl> getAllByFacilityId(Integer facilityId);
    boolean readRemoveDemographicIdentity(Integer facilityId);
    void saveRemoveDemographicIdentity(Integer facilityId, boolean removeDemoId);
    Integer readUpdateInterval(Integer facilityId);
    void saveUpdateInterval(Integer facilityId, Integer updateInterval);
    void save(IntegratorControl ic);
}
