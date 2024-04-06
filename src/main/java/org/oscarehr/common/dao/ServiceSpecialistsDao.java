package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.common.model.ServiceSpecialists;

public interface ServiceSpecialistsDao extends AbstractDao<ServiceSpecialists> {
    List<ServiceSpecialists> findByServiceId(int serviceId);
    List<Object[]> findSpecialists(Integer servId);
}
