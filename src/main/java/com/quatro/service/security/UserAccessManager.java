package com.quatro.service.security;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import oscar.OscarProperties;

import com.quatro.dao.security.UserAccessDao;
import com.quatro.model.security.UserAccessValue;
import com.quatro.service.LookupManager;

public interface UserAccessManager {
    SecurityManager getUserSecurityManager(String providerNo, Integer shelterId, LookupManager lkManager);
    List getAccessListForFunction(List list, int startIdx);
    void setUserAccessDao(UserAccessDao dao);
}
