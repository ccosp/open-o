package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.ScratchPad;
import org.springframework.stereotype.Repository;

@Repository
public class ScratchPadDaoImpl extends AbstractDaoImpl<ScratchPad> implements ScratchPadDao {

    public ScratchPadDaoImpl() {
        super(ScratchPad.class);
    }

    public boolean isScratchFilled(String providerNo) {
        String sSQL = "SELECT s FROM ScratchPad s WHERE s.providerNo = ? AND status=1 order by s.id";
        Query query = entityManager.createQuery(sSQL);
        query.setParameter(1, providerNo);

        @SuppressWarnings("unchecked")
        List<ScratchPad> results = query.getResultList();
        if (results.size() > 0 && results.get(0).getText().trim().length() > 0) {
            return true;
        }
        return false;
    }

    public ScratchPad findByProviderNo(String providerNo) {
        Query query = createQuery("sp", "sp.providerNo = :providerNo AND sp.status=1 order by sp.id DESC");
        query.setMaxResults(1);
        query.setParameter("providerNo", providerNo);
        return getSingleResultOrNull(query);
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> findAllDatesByProviderNo(String providerNo) {
        String sql = "Select sp.dateTime, sp.id from ScratchPad sp where sp.providerNo = :providerNo AND sp.status=1 order by sp.dateTime DESC";
        Query query = entityManager.createQuery(sql);
        query.setParameter("providerNo", providerNo);
        return query.getResultList();
    }
}
