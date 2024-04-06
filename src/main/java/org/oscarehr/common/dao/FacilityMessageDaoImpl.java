package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.FacilityMessage;
import org.springframework.stereotype.Repository;

@Repository
public class FacilityMessageDaoImpl extends AbstractDaoImpl<FacilityMessage> implements FacilityMessageDao {

    public FacilityMessageDaoImpl() {
        super(FacilityMessage.class);
    }
    
    @SuppressWarnings("unchecked")
    public List<FacilityMessage> getMessages() {
        String sql = "select fm from FacilityMessage fm order by fm.expiryDate desc";
        Query query = entityManager.createQuery(sql);
        
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<FacilityMessage> getMessagesByFacilityId(Integer facilityId) {
        String sql = "select fm from FacilityMessage fm where fm.facilityId=? order by fm.expiryDate desc";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, facilityId);
        
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<FacilityMessage> getMessagesByFacilityIdOrNull(Integer facilityId) {
        String sql = "select fm from FacilityMessage fm where (fm.facilityId=? or fm.facilityId IS NULL or fm.facilityId=0) order by fm.expiryDate desc";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, facilityId);
        
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<FacilityMessage> getMessagesByFacilityIdAndProgramId(Integer facilityId, Integer programId) {
        String sql = "select fm from FacilityMessage fm where fm.facilityId=? and fm.programId = ? order by fm.expiryDate desc";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, facilityId);
        query.setParameter(2, programId);
        
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<FacilityMessage> getMessagesByFacilityIdOrNullAndProgramIdOrNull(Integer facilityId, Integer programId) {
        String sql = "select fm from FacilityMessage fm where (fm.facilityId=? or fm.facilityId IS NULL or fm.facilityId=0) and (fm.programId = ? or fm.programId IS NULL) order by fm.expiryDate desc";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, facilityId);
        query.setParameter(2, programId);
        return query.getResultList();
    }
}
