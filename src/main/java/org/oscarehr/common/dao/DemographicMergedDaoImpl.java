package org.oscarehr.common.dao;

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.DemographicMerged;
import org.springframework.stereotype.Repository;

@Repository
public class DemographicMergedDaoImpl extends AbstractDaoImpl<DemographicMerged> implements DemographicMergedDao {

    public DemographicMergedDaoImpl() {
		super(DemographicMerged.class);
	}

    @Override
    public List<DemographicMerged> findCurrentByMergedTo(int demographicNo) {
        Query q = entityManager.createQuery("select d from DemographicMerged d where d.mergedTo=? and d.deleted=0");
		q.setParameter(1, demographicNo);
		
		@SuppressWarnings("unchecked")
		List<DemographicMerged> results = q.getResultList();
		
		return results;
    }

    @Override
    public List<DemographicMerged> findCurrentByDemographicNo(int demographicNo) {
        Query q = entityManager.createQuery("select d from DemographicMerged d where d.demographicNo=? and d.deleted=0");
		q.setParameter(1, demographicNo);
		
		@SuppressWarnings("unchecked")
		List<DemographicMerged> results = q.getResultList();
		
		return results;
    }

    @Override
    public List<DemographicMerged> findByDemographicNo(int demographicNo) {
        Query q = entityManager.createQuery("select d from DemographicMerged d where d.demographicNo=?");
		q.setParameter(1, demographicNo);
		
		@SuppressWarnings("unchecked")
		List<DemographicMerged> results = q.getResultList();
		
		return results;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DemographicMerged> findByParentAndChildIds(Integer parentId, Integer childId) {
        Query q = createQuery("d", "d.demographicNo = :childId AND d.mergedTo = :parentId");
		q.setParameter("parentId", parentId);
		q.setParameter("childId", childId);
		return q.getResultList();
    }
}
