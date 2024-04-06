package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.DocumentExtraReviewer;

public interface DocumentExtraReviewerDao extends AbstractDao<DocumentExtraReviewer> {
    List<DocumentExtraReviewer> findByDocumentNo(Integer documentNo);
}
