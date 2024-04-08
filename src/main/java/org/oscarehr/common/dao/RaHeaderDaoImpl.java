package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.RaHeader;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class RaHeaderDaoImpl extends AbstractDaoImpl<RaHeader> implements RaHeaderDao {

    public RaHeaderDaoImpl() {
        super(RaHeader.class);
    }     

    @Override
    public List<RaHeader> findCurrentByFilenamePaymentDate(String filename, String paymentDate) {
        Query query = entityManager.createQuery("SELECT r from RaHeader r WHERE r.filename = :filename and r.paymentDate = :pd and status != :status ORDER BY r.paymentDate");
        query.setParameter("filename", filename);
        query.setParameter("pd",paymentDate);
        query.setParameter("status","D");
        return query.getResultList();
    }

    @Override
    public List<RaHeader> findByFilenamePaymentDate(String filename, String paymentDate) {
        Query query = entityManager.createQuery("SELECT r from RaHeader r WHERE r.filename = :filename and r.paymentDate = :pd  ORDER BY r.paymentDate");
        query.setParameter("filename", filename);
        query.setParameter("pd",paymentDate);
        return query.getResultList();
    }

    @Override
    public List<RaHeader> findAllExcludeStatus(String status) {
        Query query = entityManager.createQuery("SELECT r FROM RaHeader r WHERE r.status != :status ORDER BY r.paymentDate DESC, r.readDate DESC");
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public List<RaHeader> findByHeaderDetailsAndProviderMagic(String status, String providerNo) {
        String sql =
            "SELECT r " +
            "FROM RaHeader r, RaDetail t, Provider p " +
            "WHERE r.id = t.raHeaderNo " +
            "AND p.OhipNo = t.providerOhipNo " +
            "AND r.status <> :status " + 
            "AND (" +
            "   p.ProviderNo = :providerNo" +
            "   OR p.Team = (" +
            "       SELECT pp.Team FROM Provider pp WHERE pp.ProviderNo = :providerNo " +
            "   ) " +
            ") GROUP BY r.id" +
            " ORDER BY r.paymentDate DESC, r.readDate DESC";
        Query query = entityManager.createQuery(sql);
        query.setParameter("status", status);
        query.setParameter("providerNo", providerNo);
        return query.getResultList();
    }

    @Override
    public List<RaHeader> findByStatusAndProviderMagic(String status, String providerNo) {
        String sql = "SELECT r " + 
            "FROM RaHeader r, RaDetail t, Provider p " +
            "WHERE r.id = t.raHeaderNo " +
            "AND p.OhipNo = t.providerOhipNo " +
            "AND r.status <> :status " + 
            "AND EXISTS (" +
            "   FROM ProviderSite s " +
            "   WHERE p.ProviderNo = s.id.providerNo " +
            "   AND s.id.siteId IN (" +
            "       SELECT ss.id.siteId FROM ProviderSite ss WHERE ss.id.providerNo = :providerNo " +
            "   ) " +
            ") " +
            "GROUP BY r.id " +
            "ORDER BY r.paymentDate DESC, r.readDate DESC";
        Query query = entityManager.createQuery(sql);
        query.setParameter("status", status);
        query.setParameter("providerNo", providerNo);
        return query.getResultList();
    }

    @Override
    public List<Object[]> findHeadersAndProvidersById(Integer id) {
        String sql = "FROM RaDetail r, Provider p " + 
            "WHERE p.OhipNo = r.providerOhipNo " +
            "AND r.raHeaderNo = :raId " +
            "GROUP BY r.providerOhipNo";
        Query query = entityManager.createQuery(sql);
        query.setParameter("raId", id);
        return query.getResultList();
    }
}
