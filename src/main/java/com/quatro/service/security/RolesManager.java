package com.quatro.service.security;

import java.util.List;

import com.quatro.model.security.Secobjprivilege;
import com.quatro.model.security.Secrole;

public interface RolesManager {

    List<Secrole> getRoles();

    Secrole getRole(String id);

    Secrole getRole(int id);

    Secrole getRoleByRolename(String roleName);

    void save(Secrole secrole);

    void saveFunction(Secobjprivilege secobjprivilege);

    void saveFunctions(Secrole secrole, List newLst, String roleName);

    List<Secobjprivilege> getFunctions(String roleName);

    String getFunctionDesc(String function_code);

    String getAccessDesc(String accessType_code);
}
