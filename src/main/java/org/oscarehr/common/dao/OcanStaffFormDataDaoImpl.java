package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.OcanStaffFormData;
import org.springframework.stereotype.Repository;

@Repository
public class OcanStaffFormDataDaoImpl extends AbstractDaoImpl<OcanStaffFormData> implements OcanStaffFormDataDao {

    public OcanStaffFormDataDaoImpl() {
        super(OcanStaffFormData.class);
    }
    
    @Override
    public List<OcanStaffFormData> findByQuestion(Integer ocanStaffFormId, String question) {
        String sqlCommand = "select x from OcanStaffFormData x where x.ocanStaffFormId=?1 and x.question=?2";
        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, ocanStaffFormId);
        query.setParameter(2, question);
        @SuppressWarnings("unchecked")
        List<OcanStaffFormData> results=query.getResultList();
        return (results);
    }
    
    @Override
    public OcanStaffFormData findLatestByQuestion(Integer ocanStaffFormId, String question) {
        String sqlCommand = "select x from OcanStaffFormData x where x.ocanStaffFormId=?1 and x.question=?2 order by ocanStaffFormId Desc";
        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, ocanStaffFormId);
        query.setParameter(2, question);
        @SuppressWarnings("unchecked")
        List<OcanStaffFormData> results=query.getResultList();
        if(results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }
    
    @Override
    public List<OcanStaffFormData> findByForm(Integer ocanStaffFormId) {
        String sqlCommand = "select x from OcanStaffFormData x where x.ocanStaffFormId=?1";
        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, ocanStaffFormId);
        @SuppressWarnings("unchecked")
        List<OcanStaffFormData> results=query.getResultList();
        return (results);
    }

    @Override
    public OcanStaffFormData findByAnswer(Integer ocanStaffFormId, String answer) {
        String sqlCommand = "select x from OcanStaffFormData x where x.ocanStaffFormId=?1 and x.answer=?2";
        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, ocanStaffFormId);
        query.setParameter(2, answer);
        return (getSingleResultOrNull(query));
    }
}
