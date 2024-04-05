package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.ProviderSite;

public interface ProviderSiteDao extends AbstractDao<ProviderSite> {
    List<ProviderSite> findByProviderNo(String providerNo);
    List<Provider> findActiveProvidersWithSites(String provider_no);
    List<String> findByProviderNoBySiteName(String siteName);
    List<ProviderSite> findBySiteId(Integer siteId);
}
