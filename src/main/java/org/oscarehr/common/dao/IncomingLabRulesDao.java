package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.IncomingLabRules;

public interface IncomingLabRulesDao extends AbstractDao<IncomingLabRules> {
    List<IncomingLabRules> findCurrentByProviderNoAndFrwdProvider(String providerNo, String frwdProvider);
    List<IncomingLabRules> findByProviderNoAndFrwdProvider(String providerNo, String frwdProvider);
    List<IncomingLabRules> findCurrentByProviderNo(String providerNo);
    List<IncomingLabRules> findByProviderNo(String providerNo);
    List<Object[]> findRules(String providerNo);
}
