package org.oscarehr.common.dao;

import java.util.Collection;
import java.util.List;
import org.oscarehr.common.model.SecObjPrivilege;

public interface SecObjPrivilegeDao extends AbstractDao<SecObjPrivilege> {
    List<SecObjPrivilege> findByRoleUserGroupAndObjectName(String roleUserGroup, String objectName);
    List<SecObjPrivilege> findByObjectNames(Collection<String> objectNames);
    List<SecObjPrivilege> findByRoleUserGroup(String roleUserGroup);
    List<SecObjPrivilege> findByObjectName(String objectName);
    int countObjectsByName(String objName);
    List<Object[]> findByFormNamePrivilegeAndProviderNo(String formName, String privilege, String providerNo);
}
