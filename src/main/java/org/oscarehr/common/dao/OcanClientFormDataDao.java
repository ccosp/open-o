package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.OcanClientFormData;

public interface OcanClientFormDataDao extends AbstractDao<OcanClientFormData> {
    List<OcanClientFormData> findByQuestion(Integer ocanClientFormId, String question);
    OcanClientFormData findByAnswer(Integer ocanClientFormId, String answer);
    List<OcanClientFormData> findByForm(Integer ocanClientFormId);
}
