package org.oscarehr.common.dao;

import org.oscarehr.common.model.AppUser;
import org.springframework.stereotype.Repository;

@Repository
public class AppUserDaoImpl extends AbstractDaoImpl<AppUser> implements AppUserDao {

    @Override
    public AppUser findForProvider(int appId, String providerNo) {
        // Implement the method here
        // This is just a placeholder. Replace with actual implementation.
        return null;
    }
}
