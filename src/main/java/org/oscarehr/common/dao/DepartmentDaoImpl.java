package org.oscarehr.common.dao;

import org.oscarehr.common.model.Department;
import org.springframework.stereotype.Repository;
import javax.persistence.Query;
import java.util.List;

@Repository
public class DepartmentDaoImpl extends AbstractDaoImpl<Department> implements DepartmentDao {

    public DepartmentDaoImpl() {
        super(Department.class);
    }

    @Override
    public List<Department> findAll() {
        Query query = createQuery("x", null);
        return query.getResultList();
    }
}
