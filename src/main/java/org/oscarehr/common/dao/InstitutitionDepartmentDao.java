package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.InstitutionDepartment;

public interface InstitutitionDepartmentDao extends AbstractDao<InstitutionDepartment> {
    List<InstitutionDepartment> findByInstitutionId(int institutionId);
}
