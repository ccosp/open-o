package org.oscarehr.common.dao;

import org.oscarehr.common.model.ReadLab;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional
@SuppressWarnings("unchecked")
public class ReadLabDaoImpl extends AbstractDaoImpl<ReadLab> implements ReadLabDao {

	public ReadLabDaoImpl() {
		super(ReadLab.class);
	}

	@Override
	public void markAsRead(String providerNo, String labType, Integer labId) {
        ReadLab readLab = getByProviderNoAndLabTypeAndLabId(providerNo, labType, labId);
        if (readLab == null) {
            readLab = new ReadLab(providerNo, labType, labId);
            persist(readLab);
        }
    }

	@Override
    public boolean isRead(String providerNo, String labType, Integer labId) {
        return getByProviderNoAndLabTypeAndLabId(providerNo, labType, labId) != null;
    }

	@Override
    public ReadLab getByProviderNoAndLabTypeAndLabId(String providerNo, String labType, Integer labId) {
        String sql = "SELECT x FROM ReadLab x WHERE x.id.providerNo = :providerNo " +
                "AND x.id.labType = :labType AND x.id.labId = :labId";
        Query query = entityManager.createQuery(sql);
        query.setParameter("providerNo", providerNo);
        query.setParameter("labType", labType);
        query.setParameter("labId", labId);

        @SuppressWarnings("unchecked")
        List<ReadLab> results = query.getResultList();
        return (results.size() > 0) ? results.get(0) : null;
    }
}
