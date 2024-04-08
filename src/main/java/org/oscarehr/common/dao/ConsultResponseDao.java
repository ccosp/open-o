package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.ConsultationResponse;
import org.oscarehr.consultations.ConsultationResponseSearchFilter;

public interface ConsultResponseDao extends AbstractDao<ConsultationResponse> {
    int getConsultationCount(ConsultationResponseSearchFilter filter);
    List<Object[]> search(ConsultationResponseSearchFilter filter);
}
