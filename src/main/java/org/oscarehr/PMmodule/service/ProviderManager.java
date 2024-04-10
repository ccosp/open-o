package org.oscarehr.PMmodule.service;

import org.oscarehr.PMmodule.model.Agency;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.PMmodule.model.SecUserRole;
import org.oscarehr.common.model.Facility;
import org.oscarehr.common.model.Provider;

import java.util.List;

public interface ProviderManager {
    void setProviderDao(ProviderDao providerDao);
    void setAgencyDao(AgencyDao agencyDao);
    void setProgramProviderDAO(ProgramProviderDAO dao);
    void setSecUserRoleDao(SecUserRoleDao secUserRoleDao);
    Provider getProvider(String providerNo);
    String getProviderName(String providerNo);
    List<Provider> getProviders();
    List<Provider> getActiveProviders();
    List<Provider> getActiveProviders(String facilityId, String programId);
    List<Provider> getActiveProviders(String providerNo, Integer shelterId);
    List<Provider> search(String name);
    List<ProgramProvider> getProgramDomain(String providerNo);
    List<ProgramProvider> getProgramDomainByFacility(String providerNo, Integer facilityId);
    List<Facility> getFacilitiesInProgramDomain(String providerNo);
    List getShelterIds(String provider_no);
    List<Agency> getAgencyDomain(String providerNo);
    List<Provider> getProvidersByType(String type);
    List<SecUserRole> getSecUserRoles(String providerNo);
    void saveUserRole(SecUserRole sur);
}
