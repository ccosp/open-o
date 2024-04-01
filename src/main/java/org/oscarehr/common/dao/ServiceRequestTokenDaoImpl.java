package org.oscarehr.common.dao;
 
import javax.persistence.Query;
import org.oscarehr.common.model.ServiceRequestToken;
import org.springframework.stereotype.Repository;
import java.util.List;
 
@Repository
public class ServiceRequestTokenDaoImpl extends AbstractDaoImpl<ServiceRequestToken> implements ServiceRequestTokenDao{
 
    public ServiceRequestTokenDaoImpl() {
        super(ServiceRequestToken.class);
    }

    @Override
public void persist(ServiceRequestToken token) {
    this.entityManager.persist(token);
}
 
@Override
public void remove(ServiceRequestToken token) {
    this.entityManager.remove(token);
}
 
@Override
public ServiceRequestToken merge(ServiceRequestToken token) {
    return this.entityManager.merge(token);
}
 
    @Override
    public List<ServiceRequestToken> findAll() {
        Query query = this.entityManager.createQuery("SELECT x FROM ServiceRequestToken x", ServiceRequestToken.class);
        return query.getResultList();
    }
 
    @Override
    public ServiceRequestToken findByTokenId(String token) {
        Query query = this.entityManager.createQuery("SELECT x FROM ServiceRequestToken x WHERE x.tokenId = :token", ServiceRequestToken.class);
        query.setParameter("token", token);
        return this.getSingleResultOrNull(query);
    }
}