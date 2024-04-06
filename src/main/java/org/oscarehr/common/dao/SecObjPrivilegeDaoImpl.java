package org.oscarehr.common.dao;

import java.util.Collection;
import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.SecObjPrivilege;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class SecObjPrivilegeDaoImpl extends AbstractDaoImpl<SecObjPrivilege> implements SecObjPrivilegeDao {

    public SecObjPrivilegeDaoImpl() {
        super(SecObjPrivilege.class);
    }

    @Override
    public List<SecObjPrivilege> findByRoleUserGroupAndObjectName(String roleUserGroup, String objectName) {
        // ... implementation code ...
    }

    @Override
    public List<SecObjPrivilege> findByObjectNames(Collection<String> objectNames) {
        // ... implementation code ...
    }

    @Override
    public List<SecObjPrivilege> findByRoleUserGroup(String roleUserGroup) {
        // ... implementation code ...
    }

    @Override
    public List<SecObjPrivilege> findByObjectName(String objectName) {
        // ... implementation code ...
    }

    @Override
    public int countObjectsByName(String objName) {
        // ... implementation code ...
    }

    @Override
    public List<Object[]> findByFormNamePrivilegeAndProviderNo(String formName, String privilege, String providerNo) {
        // ... implementation code ...
    }
}
