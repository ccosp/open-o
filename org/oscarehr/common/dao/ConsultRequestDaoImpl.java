package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.oscarehr.common.PaginationQuery;
import org.oscarehr.common.model.ConsultationRequest;
import org.oscarehr.consultations.ConsultationQuery;
import org.oscarehr.consultations.ConsultationRequestSearchFilter;
import org.oscarehr.consultations.ConsultationRequestSearchFilter.SORTMODE;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ConsultRequestDaoImpl extends AbstractDaoImpl<ConsultationRequest> implements ConsultRequestDao {

	public ConsultRequestDaoImpl() {
		super(ConsultationRequest.class);
	}

	// ... rest of the methods implementation
}
