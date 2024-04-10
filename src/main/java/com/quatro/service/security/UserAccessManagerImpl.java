package com.quatro.service.security;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import oscar.OscarProperties;

import com.quatro.dao.security.UserAccessDao;
import com.quatro.model.security.UserAccessValue;
import com.quatro.service.LookupManager;

public class UserAccessManagerImpl implements UserAccessManager {
    private UserAccessDao _dao = null;

    @Override
    public SecurityManager getUserSecurityManager(String providerNo, Integer shelterId, LookupManager lkManager) {
        // ... existing implementation ...
    }

    @Override
    public List getAccessListForFunction(List list, int startIdx) {
        // ... existing implementation ...
    }

    @Override
    public void setUserAccessDao(UserAccessDao dao) {
        _dao = dao;
    }
}
