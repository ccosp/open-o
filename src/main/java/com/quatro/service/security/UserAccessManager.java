package com.quatro.service.security;

import com.quatro.service.LookupManager;
import java.util.List;

public interface UserAccessManager {
    SecurityManager getUserSecurityManager(String providerNo, Integer shelterId, LookupManager lkManager);
    List getAccessListForFunction(List list, int startIdx);
    void setUserAccessDao(UserAccessDao dao);
}
