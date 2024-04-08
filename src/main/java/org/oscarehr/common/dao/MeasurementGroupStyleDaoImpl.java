package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.MeasurementGroupStyle;
import org.springframework.stereotype.Repository;

@Repository
public class MeasurementGroupStyleDaoImpl extends AbstractDaoImpl<MeasurementGroupStyle> implements MeasurementGroupStyleDao {

    public MeasurementGroupStyleDaoImpl() {
        super(MeasurementGroupStyle.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MeasurementGroupStyle> findAll() {
        Query query = entityManager.createQuery("SELECT x FROM " + modelClass.getSimpleName() + " x");
        List<MeasurementGroupStyle> results = query.getResultList();
        return results;
    }
    
    @Override
    public List<MeasurementGroupStyle> findByGroupName(String groupName) {
        String sql = "select x from MeasurementGroupStyle x where x.groupName=?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, groupName);

        @SuppressWarnings("unchecked")
        List<MeasurementGroupStyle> results = query.getResultList();
        return results;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<MeasurementGroupStyle> findByCssId(Integer cssId) {
        Query query = createQuery("m", "m.cssId = :cssId");
        query.setParameter("cssId", cssId);
        return query.getResultList();
    }
}
