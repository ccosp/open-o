package org.oscarehr.common.dao;

import org.oscarehr.common.model.DemographicStudy;
import org.oscarehr.common.model.DemographicStudyPK;

import javax.persistence.Query;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class DemographicStudyDaoImpl extends AbstractDaoImpl<DemographicStudy> implements DemographicStudyDao {

    public DemographicStudyDaoImpl() {
        super(DemographicStudy.class);
    }

    @Override
    public List<DemographicStudy> findAll() {
        Query query = createQuery("x", null);
        return query.getResultList();
    }

    @Override
    public int removeByDemographicNo(Integer demographicNo) {
        Query query = entityManager.createQuery("delete from DemographicStudy x where x.id.demographicNo=?");
        query.setParameter(1, demographicNo);
        return query.executeUpdate();
    }

    @Override
    public DemographicStudy findByDemographicNoAndStudyNo(int demographicNo, int studyNo) {
        DemographicStudyPK pk = new DemographicStudyPK();
        pk.setDemographicNo(demographicNo);
        pk.setStudyNo(studyNo);

        return find(pk);
    }

    @Override
    public List<DemographicStudy> findByStudyNo(int studyNo) {
        Query query = entityManager.createQuery("select x from DemographicStudy x where x.id.studyNo=?");
        query.setParameter(1, studyNo);
        return query.getResultList();
    }

    @Override
    public List<DemographicStudy> findByDemographicNo(int demographicNo) {
        Query query = entityManager.createQuery("select x from DemographicStudy x where x.id.demographicNo=?");
        query.setParameter(1, demographicNo);
        return query.getResultList();
    }
}
