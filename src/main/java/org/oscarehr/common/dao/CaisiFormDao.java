package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.CaisiForm;

public interface CaisiFormDao extends AbstractDao<CaisiForm> {
    List<CaisiForm> getActiveForms();
    List<CaisiForm> getCaisiForms();
    void updateStatus(Integer formId, Integer status);
    List<CaisiForm> getCaisiForms(Integer formId, Integer clientId);
    List<CaisiForm> getCaisiFormsByClientId(Integer clientId);
    List<CaisiForm> findActiveByFacilityIdOrNull(Integer facilityId);
}
