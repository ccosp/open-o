package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.DocumentExtraReviewer;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentExtraReviewerDaoImpl extends AbstractDaoImpl<DocumentExtraReviewer> implements DocumentExtraReviewerDao {

    public DocumentExtraReviewerDaoImpl() {
        super(DocumentExtraReviewer.class);
    }

    @Override
    public List<DocumentExtraReviewer> findByDocumentNo(Integer documentNo) {
        Query query = entityManager.createQuery("SELECT d FROM DocumentExtraReviewer d WHERE d.documentNo = :documentNo");
        query.setParameter("documentNo", documentNo);
        return query.getResultList();
    }
}
