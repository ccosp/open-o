package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.decisionSupport.model.DSGuidelineProviderMapping;
import org.springframework.stereotype.Repository;

@Repository
public class DSGuidelineProviderMappingDaoImpl extends AbstractDaoImpl<DSGuidelineProviderMapping> implements DSGuidelineProviderMappingDao {

    public DSGuidelineProviderMappingDaoImpl() {
        super(DSGuidelineProviderMapping.class);
    }

    public List<DSGuidelineProviderMapping> getMappingsByProvider(String providerNo) {
        String sql = "select c from DSGuidelineProviderMapping c where c.providerNo = ?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, providerNo);
        @SuppressWarnings("unchecked")
        List<DSGuidelineProviderMapping> list = query.getResultList();
        return list;
    }

    public boolean mappingExists(DSGuidelineProviderMapping dsGuidelineProviderMapping) {
        String sql ="select m from DSGuidelineProviderMapping m where m.guidelineUUID = ? and m.providerNo = ?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, dsGuidelineProviderMapping.getGuidelineUUID());
        query.setParameter(2, dsGuidelineProviderMapping.getProviderNo());
        @SuppressWarnings("unchecked")
        List<DSGuidelineProviderMapping> list = query.getResultList();
        if (list == null || list.size() == 0){
            return false;
        }
        return true;
    }
}
