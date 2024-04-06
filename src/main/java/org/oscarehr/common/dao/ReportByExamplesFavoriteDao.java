package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.ReportByExamplesFavorite;

public interface ReportByExamplesFavoriteDao extends AbstractDao<ReportByExamplesFavorite> {
    List<ReportByExamplesFavorite> findByQuery(String query);
    List<ReportByExamplesFavorite> findByEverything(String providerNo, String favoriteName, String queryString);
    List<ReportByExamplesFavorite> findByProvider(String providerNo);
}
