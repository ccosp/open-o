package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.ProviderSite;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class ProviderSiteDaoImpl extends AbstractDaoImpl<ProviderSite> implements ProviderSiteDao {

    public ProviderSiteDaoImpl() {
        super(ProviderSite.class);
    }

    @Override
    public List<ProviderSite> findByProviderNo(String providerNo) {
        String sql = "select x from ProviderSite x where x.id.providerNo=?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1,providerNo);

        List<ProviderSite> results = query.getResultList();
        return results;
    }

    @Override
    public List<Provider> findActiveProvidersWithSites(String provider_no) { 
        String sql = "FROM Provider p where p.Status = '1' AND p.OhipNo != '' " +
                        "AND EXISTS( " +
                        "   FROM ProviderSite s WHERE p.ProviderNo = s.id.providerNo " +
                        "   AND s.id.siteId IN ( " +
                        "       SELECT ss.id.siteId FROM ProviderSite ss WHERE ss.id.providerNo = :pNo " +
                        "   )" +
                        ")" +
                        "ORDER BY p.LastName, p.FirstName";
        Query query = entityManager.createQuery(sql);
        query.setParameter("pNo", provider_no);
        return query.getResultList();
    }

    @Override
    public List<String> findByProviderNoBySiteName(String siteName) {
        String sql = "select x.id.providerNo from ProviderSite x, Site s where x.id.siteId=s.siteId and s.name=?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1,siteName);

        @SuppressWarnings("unchecked")
        List<String> results = query.getResultList();
        return results;
    }

    @Override
    public List<ProviderSite> findBySiteId(Integer siteId) {
        String sql = "select x from ProviderSite x where x.id.siteId=?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1,siteId);

        @SuppressWarnings("unchecked")
        List<ProviderSite> results = query.getResultList();
        return results;
    }
}
