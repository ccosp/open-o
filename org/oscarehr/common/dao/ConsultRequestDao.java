package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import org.oscarehr.common.PaginationQuery;
import org.oscarehr.common.model.ConsultationRequest;
import org.oscarehr.consultations.ConsultationQuery;
import org.oscarehr.consultations.ConsultationRequestSearchFilter;

public interface ConsultRequestDao extends AbstractDao<ConsultationRequest> {

	int getConsultationCount(PaginationQuery paginationQuery);

	List<ConsultationRequest> listConsultationRequests(ConsultationQuery consultationQuery);

	int getConsultationCount2(ConsultationRequestSearchFilter filter);

	List<Object[]> search(ConsultationRequestSearchFilter filter);
}
