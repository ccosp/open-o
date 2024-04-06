package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.DemographicMerged;
import org.springframework.stereotype.Repository;

@Repository
public class DemographicMergedDaoImpl extends AbstractDaoImpl<DemographicMerged> implements DemographicMergedDao {

    @Override
    public List<DemographicMerged> findCurrentByMergedTo(int demographicNo) {
        // Implement the method here
    }

    @Override
    public List<DemographicMerged> findCurrentByDemographicNo(int demographicNo) {
        // Implement the method here
    }

    @Override
    public List<DemographicMerged> findByDemographicNo(int demographicNo) {
        // Implement the method here
    }

    @Override
    public List<DemographicMerged> findByParentAndChildIds(Integer parentId, Integer childId) {
        // Implement the method here
    }
}
