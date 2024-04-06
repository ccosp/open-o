package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import org.oscarehr.common.model.CdsClientForm;

public interface CdsClientFormDao extends AbstractDao<CdsClientForm> {
    CdsClientForm findLatestByFacilityClient(Integer facilityId, Integer clientId);
    CdsClientForm findLatestByFacilityAdmissionId(Integer facilityId, Integer admissionId, Boolean signed);
    List<CdsClientForm> findByFacilityClient(Integer facilityId, Integer clientId);
    List<CdsClientForm> findSignedCdsForms(Integer facilityId, String formVersion, Date startDate, Date endDate);
}
