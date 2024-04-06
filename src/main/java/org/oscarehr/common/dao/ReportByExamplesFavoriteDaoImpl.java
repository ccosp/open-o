package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.ReportByExamplesFavorite;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class ReportByExamplesFavoriteDaoImpl extends AbstractDaoImpl<ReportByExamplesFavorite> implements ReportByExamplesFavoriteDao {

    public ReportByExamplesFavoriteDaoImpl() {
        super(ReportByExamplesFavorite.class);
    }

    @Override
    public List<ReportByExamplesFavorite> findByQuery(String query) {
        Query q = createQuery("ex", "ex.query LIKE :query");
        q.setParameter("query", query);
        return q.getResultList();
    }

    @Override
    public List<ReportByExamplesFavorite> findByEverything(String providerNo, String favoriteName, String queryString) {
        Query query = createQuery("ex", "ex.providerNo = :providerNo AND ex.name LIKE :name OR ex.query LIKE :query");
        query.setParameter("providerNo", providerNo);
        query.setParameter("name", favoriteName);
        query.setParameter("query", queryString);
        return query.getResultList();
    }

    @Override
    public List<ReportByExamplesFavorite> findByProvider(String providerNo) {
        Query query = createQuery("ex", "ex.providerNo = :providerNo");
        query.setParameter("providerNo", providerNo);
        return query.getResultList();
    }
}
