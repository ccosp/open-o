package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.MyGroupAccessRestriction;

public interface MyGroupAccessRestrictionDao extends AbstractDao<MyGroupAccessRestriction> {
    List<MyGroupAccessRestriction> findByGroupId(String myGroupNo);
    List<MyGroupAccessRestriction> findByProviderNo(String providerNo);
    MyGroupAccessRestriction findByGroupNoAndProvider(String myGroupNo, String providerNo);
}
