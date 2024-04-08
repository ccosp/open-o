package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import org.oscarehr.common.model.CaisiFormData;
import org.oscarehr.common.model.CaisiFormInstance;
import org.oscarehr.common.model.Demographic;
import org.springframework.stereotype.Repository;

@Repository
public class CaisiFormInstanceDaoImpl extends AbstractDaoImpl<CaisiFormInstance> implements CaisiFormInstanceDao {
    public CaisiFormInstanceDaoImpl() {
        super(CaisiFormInstance.class);
    }
    
    public CaisiFormInstance getCurrentFormById(Integer formInstanceId) {
		return find(formInstanceId);
	}
	
	   public List<CaisiFormInstance> findByFormId(Integer formId) {
	        Query query = entityManager.createQuery("SELECT f FROM CaisiFormInstance f where f.formId = ? order by f.clientId, f.dateCreated");
			query.setParameter(1,formId);
			
			@SuppressWarnings("unchecked")
	        List<CaisiFormInstance> result = query.getResultList();
			
	        return result;
	    }
	   
    public List<CaisiFormInstance> getForms(Integer formId, Integer clientId) {
        Query query = entityManager.createQuery("SELECT f FROM CaisiFormInstance f where f.formId = ? and f.clientId = ? order by f.dateCreated DESC");
		query.setParameter(1,formId);
		query.setParameter(2, clientId);
		@SuppressWarnings("unchecked")
        List<CaisiFormInstance> result = query.getResultList();
		
        return result;
    }
    
    public CaisiFormInstance getLatestForm(Integer formId, Integer clientId) {
		Query query = entityManager.createQuery("SELECT f FROM CaisiFormInstance f where f.formId = ? and f.clientId = ? order by f.dateCreated DESC");
		query.setParameter(1,formId);
		query.setParameter(2, clientId);
		@SuppressWarnings("unchecked")
        List<CaisiFormInstance> result = query.getResultList();
		if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }
    
    public List<CaisiFormInstance> getFormsByFacility(Integer clientId, Integer facilityId) {
        String sSQL="from CaisiFormInstance f where f.clientId = ? and f.formId in " +
        	"(select s.formId from CaisiForm s where s.facilityId =?) order by f.dateCreated DESC";  
       
        Query query = entityManager.createNativeQuery(sSQL);
        query.setParameter(1, clientId);
        query.setParameter(2, facilityId);
        
        @SuppressWarnings("unchecked")
        List<CaisiFormInstance> result = query.getResultList();
        return result;

    }
    
    public List<CaisiFormInstance> getCurrentForms(String formId, List<Demographic> clients) {
        List<CaisiFormInstance> results = new ArrayList<CaisiFormInstance>();

        for (Iterator<Demographic> iter = clients.iterator(); iter.hasNext();) {
            Demographic client = iter.next();
            CaisiFormInstance ofi = getLatestForm(new Integer(formId), client.getDemographicNo());
            results.add(ofi);
        }

        return results;
    }
    
    public List<CaisiFormInstance> getForms(Long clientId) {
    	Query query = entityManager.createQuery("SELECT f FROM CaisiFormInstance f where f.clientId = ? order by f.dateCreated DESC");
		query.setParameter(1, clientId);
		@SuppressWarnings("unchecked")
        List<CaisiFormInstance> result = query.getResultList();
		
        return result;
    }
    
		
    public Integer countAnswersByQuestions(String value, Integer formId, Date startDate, Date endDate, Integer pageNumber, Integer sectionId, Integer questionId) {
    	Query query = entityManager.createQuery("select count(d.value) from CaisiFormInstance i, CaisiFormData d where d.value=? and i.formId=? and i.dateCreated>=? and i.dateCreated<=? and i.id=d.instanceId and d.pageNumber=? and d.sectionId=? and d.questionId= ?");
		query.setParameter(1, value);
		query.setParameter(2, formId);
		query.setParameter(3, startDate);
		query.setParameter(4, endDate);
		query.setParameter(5, pageNumber);
		query.setParameter(6, sectionId);
		query.setParameter(7, questionId);
		
		BigInteger result = (BigInteger)query.getSingleResult();
		
		return result.intValue();
    }
    

    public List<CaisiFormData> query1(Integer formId, Date startDate, Date endDate, int pageNumber, int sectionId, int questionId) {
    	Query query = entityManager.createQuery("select distinct d from CaisiFormInstance i, CaisiFormData d where i.formId=? and i.dateCreated>=? and i.dateCreated<=? and i.id=d.instanceId and d.pageNumber=? and d.sectionId=? and d.questionId= ? group by d.key, d.value");
		query.setParameter(1, formId);
		query.setParameter(2, startDate);
		query.setParameter(3, endDate);
		query.setParameter(4, pageNumber);
		query.setParameter(5, sectionId);
		query.setParameter(6, questionId);
		
		@SuppressWarnings("unchecked")
        List<CaisiFormData> result = query.getResultList();
		
        return result;
    }
    
    public List<CaisiFormData> query2(Integer formId, Date startDate, Date endDate, int pageNumber, int sectionId, int questionId) {
    	Query query = entityManager.createQuery("elect distinct d from CaisiFormInstance i, CaisiFormData d where i.formId=? and i.dateCreated>=? and i.dateCreated<=? and i.id=d.instanceId and d.pageNumber=? and d.sectionId=? and d.questionId= ? group by d.value");
		query.setParameter(1, formId);
		query.setParameter(2, startDate);
		query.setParameter(3, endDate);
		query.setParameter(4, pageNumber);
		query.setParameter(5, sectionId);
		query.setParameter(6, questionId);
		
		@SuppressWarnings("unchecked")
        List<CaisiFormData> result = query.getResultList();
		
        return result;
    }
}
