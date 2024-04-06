package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.CustomFilter;

public interface CustomFilterDao extends AbstractDao<CustomFilter> {
    CustomFilter findByName(String name);
    CustomFilter findByNameAndProviderNo(String name, String providerNo);
    List<CustomFilter> getCustomFilters();
    List<CustomFilter> findByProviderNo(String providerNo);
    List<CustomFilter> getCustomFilterWithShortCut(String providerNo);
    void deleteCustomFilter(String name);
}
