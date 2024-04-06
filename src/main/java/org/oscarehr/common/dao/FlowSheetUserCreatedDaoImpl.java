package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.FlowSheetUserCreated;
import org.springframework.stereotype.Repository;

@Repository
public class FlowSheetUserCreatedDaoImpl extends AbstractDaoImpl<FlowSheetUserCreated> implements FlowSheetUserCreatedDao {

    public FlowSheetUserCreatedDaoImpl() {
        super(FlowSheetUserCreated.class);
    }

    public List<FlowSheetUserCreated> findActiveNoTemplate(){
        Query query = entityManager.createQuery("SELECT f FROM FlowSheetUserCreated f WHERE f.archived=? and f.template IS NULL or f.template = ''");
        query.setParameter(1, false);
        return query.getResultList();
    }

    public List<FlowSheetUserCreated> getAllUserCreatedFlowSheets(){
        Query query = entityManager.createQuery("SELECT f FROM FlowSheetUserCreated f WHERE f.archived=?");
        query.setParameter(1, false);
        return query.getResultList();                
    }

    public List<FlowSheetUserCreated> findActiveByScope(String scope){
        Query query = entityManager.createQuery("SELECT f FROM FlowSheetUserCreated f WHERE f.archived=? AND f.scope = ?");
        query.setParameter(1, false);
        query.setParameter(2, scope);
        return query.getResultList();
    }

    public FlowSheetUserCreated findByPatientScope(String template, Integer demographicNo){
        Query query = entityManager.createQuery("SELECT f FROM FlowSheetUserCreated f WHERE f.archived=? AND f.scope = ? AND f.scopeDemographicNo = ? AND f.template = ?");
        query.setParameter(1, false);
        query.setParameter(2, FlowSheetUserCreated.SCOPE_PATIENT);
        query.setParameter(3, demographicNo);
        query.setParameter(4, template);
        return this.getSingleResultOrNull(query);
    }

    public FlowSheetUserCreated findByProviderScope(String template, String providerNo){
        Query query = entityManager.createQuery("SELECT f FROM FlowSheetUserCreated f WHERE f.archived=? AND f.scope = ? AND f.scopeProviderNo = ? AND f.template = ?");
        query.setParameter(1, false);
        query.setParameter(2, FlowSheetUserCreated.SCOPE_PROVIDER);
        query.setParameter(3, providerNo);
        query.setParameter(4, template);
        return this.getSingleResultOrNull(query);
    }

    public FlowSheetUserCreated findByClinicScope(String template){
        Query query = entityManager.createQuery("SELECT f FROM FlowSheetUserCreated f WHERE f.archived=? AND f.scope = ? AND f.template = ?");
        query.setParameter(1, false);
        query.setParameter(2, FlowSheetUserCreated.SCOPE_CLINIC);
        query.setParameter(3, template);
        return this.getSingleResultOrNull(query);
    }

    public FlowSheetUserCreated findByPatientScopeName(String name, Integer demographicNo){
        Query query = entityManager.createQuery("SELECT f FROM FlowSheetUserCreated f WHERE f.archived=? AND f.scope = ? AND f.scopeDemographicNo = ? AND f.name = ?");
        query.setParameter(1, false);
        query.setParameter(2, FlowSheetUserCreated.SCOPE_PATIENT);
        query.setParameter(3, demographicNo);
        query.setParameter(4, name);
        return this.getSingleResultOrNull(query);
    }

    public FlowSheetUserCreated findByProviderScopeName(String name, String providerNo){
        Query query = entityManager.createQuery("SELECT f FROM FlowSheetUserCreated f WHERE f.archived=? AND f.scope = ? AND f.scopeProviderNo = ? AND f.name = ?");
        query.setParameter(1, false);
        query.setParameter(2, FlowSheetUserCreated.SCOPE_PROVIDER);
        query.setParameter(3, providerNo);
        query.setParameter(4, name);
        return this.getSingleResultOrNull(query);
    }

    public FlowSheetUserCreated findByClinicScopeName(String name){
        Query query = entityManager.createQuery("SELECT f FROM FlowSheetUserCreated f WHERE f.archived=? AND f.scope = ? AND f.name = ?");
        query.setParameter(1, false);
        query.setParameter(2, FlowSheetUserCreated.SCOPE_CLINIC);
        query.setParameter(3, name);
        return this.getSingleResultOrNull(query);
    }
}
