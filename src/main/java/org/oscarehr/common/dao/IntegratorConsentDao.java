package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import org.oscarehr.common.model.IntegratorConsent;

public interface IntegratorConsentDao extends AbstractDao<IntegratorConsent> {
    IntegratorConsent findLatestByFacilityDemographic(int facilityId, int demographicId);
    List<IntegratorConsent> findByFacilityAndDemographic(int facilityId, int demographicId);
    List<IntegratorConsent> findByFacilityAndDemographicSince(int facilityId, int demographicId, Date lastDataUpdated);
    List<Integer> findDemographicIdsByFacilitySince(int facilityId, Date lastDataUpdated);
}
