package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.OcanClientFormData;
import org.springframework.stereotype.Repository;

@Repository
public class OcanClientFormDataDaoImpl extends AbstractDaoImpl<OcanClientFormData> implements OcanClientFormDataDao {

    public OcanClientFormDataDaoImpl() {
        super(OcanClientFormData.class);
    }
    
    public List<OcanClientFormData> findByQuestion(Integer ocanClientFormId, String question) {
        String sqlCommand = "select x from OcanClientFormData x where x.ocanClientFormId=?1 and x.question=?2";
        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, ocanClientFormId);
        query.setParameter(2, question);
        @SuppressWarnings("unchecked")
        List<OcanClientFormData> results=query.getResultList();
        return (results);
    }

    public OcanClientFormData findByAnswer(Integer ocanClientFormId, String answer) {
        String sqlCommand = "select x from OcanClientFormData x where x.ocanClientFormId=?1 and x.answer=?2";
        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, ocanClientFormId);
        query.setParameter(2, answer);
        return (getSingleResultOrNull(query));
    }

    public List<OcanClientFormData> findByForm(Integer ocanClientFormId) {
        String sqlCommand = "select x from OcanClientFormData x where x.ocanClientFormId=?1";
        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, ocanClientFormId);
        @SuppressWarnings("unchecked")
        List<OcanClientFormData> results=query.getResultList();
        return (results);
    }
}
