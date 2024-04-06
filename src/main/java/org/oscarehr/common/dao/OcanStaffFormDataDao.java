package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.OcanStaffFormData;

public interface OcanStaffFormDataDao extends AbstractDao<OcanStaffFormData> {
    List<OcanStaffFormData> findByQuestion(Integer ocanStaffFormId, String question);
    OcanStaffFormData findLatestByQuestion(Integer ocanStaffFormId, String question);
    List<OcanStaffFormData> findByForm(Integer ocanStaffFormId);
    OcanStaffFormData findByAnswer(Integer ocanStaffFormId, String answer);
}
