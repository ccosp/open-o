package org.oscarehr.common.dao;

import org.oscarehr.common.model.MyGroup;
import org.oscarehr.common.model.Provider;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
public class MyGroupDaoImpl extends AbstractDaoImpl<MyGroup> implements MyGroupDao {

    public MyGroupDaoImpl() {
        super(MyGroup.class);
    }

    @Override
    public List<MyGroup> findAll() {
        Query query = createQuery("x", null);
        return query.getResultList();
    }

    @Override
    public List<String> getGroupDoctors(String groupNo) {
        // ... rest of the implementation
    }

    // ... rest of the methods implementation
}
