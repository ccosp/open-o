package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.Security;

public interface SecurityDao extends AbstractDao<Security> {
    List<Security> findAllOrderBy(String columnName);
    List<Security> findByProviderNo(String providerNo);
    List<Security> findByLikeProviderNo(String providerNo);
    List<Security> findByUserName(String userName);
    List<Security> findByOneIdKey(String ssoKey);
    void updateOneIdKey(Security securityRecord);
    List<Security> findByLikeUserName(String userName);
    Security getByProviderNo(String providerNo);
    List<Object[]> findProviders();
    List<Security> findByProviderSite(String providerNo);
}
