package org.oscarehr.common.dao;

import org.oscarehr.common.model.MyGroup;
import org.oscarehr.common.model.WaitingListName;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public interface WaitingListNameDao extends AbstractDao<WaitingListName> {
    long countActiveWatingListNames();
    List<WaitingListName> findCurrentByNameAndGroup(String name, String group);
    List<WaitingListName> findByMyGroups(String providerNo, List<MyGroup> myGroups);
    List<WaitingListName> findCurrentByGroup(String group);
}
