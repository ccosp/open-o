package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.BillingONCHeader1;
import org.oscarehr.common.model.BillingONItem;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class BillingONItemDaoImpl extends AbstractDaoImpl<BillingONItem> implements BillingONItemDao {

    public BillingONItemDaoImpl() {
        super(BillingONItem.class);
    }

    public List<BillingONItem> getBillingItemByCh1Id(Integer ch1_id) {
        String queryStr = "FROM BillingONItem b WHERE b.ch1Id = ?1";
        Query q = entityManager.createQuery(queryStr);
        q.setParameter(1, ch1_id);
        return q.getResultList();
    }

    public List<BillingONItem> getActiveBillingItemByCh1Id(Integer ch1_id) {
        String queryStr = "FROM BillingONItem b WHERE b.ch1Id = ?1 AND b.status <> ?2";
        Query q = entityManager.createQuery(queryStr);
        q.setParameter(1, ch1_id);
        q.setParameter(2, "D");
        return q.getResultList();
    }

    public List<BillingONCHeader1> getCh1ByDemographicNo(Integer demographic_no) {
        String queryStr = "FROM BillingONCHeader1 b WHERE b.demographicNo = ?1 ORDER BY b.id DESC";
        Query q = entityManager.createQuery(queryStr);
        q.setParameter(1, demographic_no);
        return q.getResultList();
    }

    public List<BillingONItem> findByCh1Id(Integer id) {
        Query query = createQuery("bi", "bi.ch1Id = :ch1Id AND bi.status <> 'D' AND bi.status <> 'S'");
        query.setParameter("ch1Id", id);
        return query.getResultList();
    }

    public List<BillingONItem> findByCh1IdAndStatusNotEqual(Integer chId, String string) {
        Query query = createQuery("i", "i.ch1Id= :chId AND i.status != 'D'");
        query.setParameter("chId", chId);
        return query.getResultList();
    }

    public List<BillingONCHeader1> getCh1ByDemographicNoSince(Integer demographic_no, Date lastUpdateDate) {
        String queryStr = "FROM BillingONCHeader1 b WHERE b.demographicNo = "+demographic_no+" and b.timestamp > ? ORDER BY b.id";
        Query q = entityManager.createQuery(queryStr);
        q.setParameter(1, lastUpdateDate);
        return q.getResultList();
    }

    public List<Integer> getDemographicNoSince(Date lastUpdateDate) {
        String queryStr = "select b.demographicNo FROM BillingONCHeader1 b WHERE b.timestamp > ? ORDER BY b.id";
        Query q = entityManager.createQuery(queryStr);
        q.setParameter(1, lastUpdateDate);
        return q.getResultList();
    }
}
