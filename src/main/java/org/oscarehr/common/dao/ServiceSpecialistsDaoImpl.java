package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.common.model.ServiceSpecialists;
import org.springframework.stereotype.Repository;

@Repository
public class ServiceSpecialistsDaoImpl extends AbstractDaoImpl<ServiceSpecialists> implements ServiceSpecialistsDao {

    public ServiceSpecialistsDaoImpl() {
        super(ServiceSpecialists.class);    
    }
    
    public List<ServiceSpecialists> findByServiceId(int serviceId) {
        Query q = entityManager.createQuery("select x from ServiceSpecialists x where x.id.serviceId = ?");
        q.setParameter(1, serviceId);
        
        @SuppressWarnings("unchecked")
        List<ServiceSpecialists> results = q.getResultList();
        
        return results;
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> findSpecialists(Integer servId) {
        String sql = "FROM ServiceSpecialists ser, " + ProfessionalSpecialist.class.getSimpleName() + " pro " +
                "WHERE pro.id = ser.id.specId and ser.id.serviceId = :serviceId " +
                "ORDER BY pro.lastName";
        Query query = entityManager.createQuery(sql);
        query.setParameter("serviceId", servId);
        return query.getResultList();
    }
}
