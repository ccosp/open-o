package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.ServiceClient;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ServiceClientDaoImpl extends AbstractDaoImpl<ServiceClient> implements ServiceClientDao {

    public ServiceClientDaoImpl() {
        super(ServiceClient.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ServiceClient> findAll() {
        Query query = createQuery("x", null);
        return query.getResultList();
    }

    @Override
    public ServiceClient findByName(String name) {
        Query query = entityManager.createQuery("SELECT x FROM ServiceClient x WHERE x.name=?");
        query.setParameter(1,name);
        return this.getSingleResultOrNull(query);
    }

    @Override
    public ServiceClient findByKey(String key) {
        Query query = entityManager.createQuery("SELECT x FROM ServiceClient x WHERE x.key=?");
        query.setParameter(1,key);
        return this.getSingleResultOrNull(query);
    }

    @Override
    public ServiceClient find(Integer id) {
        return this.entityManager.find(ServiceClient.class, id);
    }
}
