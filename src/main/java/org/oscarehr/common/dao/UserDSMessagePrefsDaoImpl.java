package org.oscarehr.common.dao;

import java.util.Hashtable;
import java.util.List;
import javax.persistence.Query;
import org.apache.logging.log4j.Logger;
import org.oscarehr.common.model.UserDSMessagePrefs;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class UserDSMessagePrefsDaoImpl extends AbstractDaoImpl<UserDSMessagePrefs> implements UserDSMessagePrefsDao {
    private static Logger logger = MiscUtils.getLogger();

    public UserDSMessagePrefsDaoImpl() {
        super(UserDSMessagePrefs.class);
    }

    @Override
    public void saveProp(UserDSMessagePrefs prop) {
        this.persist(prop);
    }

    @Override
    public void updateProp(UserDSMessagePrefs prop) {
        this.merge(prop);
    }

    @Override
    public UserDSMessagePrefs getMessagePrefsOnType(String prov, String name) {
        // ... rest of the code
    }

    @Override
    public Hashtable<String, Long> getHashofMessages(String providerNo, String name) {
        // ... rest of the code
    }

    @Override
    public List<UserDSMessagePrefs> findMessages(String providerNo, String resourceType, String resourceId, boolean archived) {
        // ... rest of the code
    }

    @Override
    public UserDSMessagePrefs getDsMessage(String providerNo, String resourceType, String resourceId, boolean archived) {
        // ... rest of the code
    }

    @Override
    public List<UserDSMessagePrefs> findAllByResourceId(String resourceId) {
        // ... rest of the code
    }
}
