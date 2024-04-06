package org.oscarehr.common.dao;

import org.oscarehr.common.model.MyGroup;
import org.oscarehr.common.model.Provider;

import java.util.List;

public interface MyGroupDao extends AbstractDao<MyGroup> {
    List<MyGroup> findAll();
    List<String> getGroupDoctors(String groupNo);
    List<String> getGroups();
    List<MyGroup> getGroupByGroupNo(String groupNo);
    void deleteGroupMember(String myGroupNo, String providerNo);
    List<MyGroup> getProviderGroups(String providerNo);
    String getDefaultBillingForm(String myGroupNo);
    List<Provider> search_groupprovider(String groupNo);
    List<MyGroup> search_mygroup(String groupNo);
    List<MyGroup> searchmygroupno();
    List<MyGroup> search_providersgroup(String lastName, String firstName);
}
