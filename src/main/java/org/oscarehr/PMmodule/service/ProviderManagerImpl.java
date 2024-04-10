package org.oscarehr.PMmodule.service;

import org.oscarehr.PMmodule.dao.AgencyDao;
import org.oscarehr.PMmodule.dao.ProgramProviderDAO;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.PMmodule.dao.SecUserRoleDao;
import org.oscarehr.PMmodule.model.Agency;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.PMmodule.model.SecUserRole;
import org.oscarehr.common.model.Facility;
import org.oscarehr.common.model.Provider;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
public class ProviderManagerImpl implements ProviderManager {
    private ProviderDao providerDao;
    private AgencyDao agencyDao;
    private ProgramProviderDAO programProviderDAO;
    private SecUserRoleDao secUserRoleDao;

    public void setProviderDao(ProviderDao providerDao) {
        this.providerDao = providerDao;
    }

    public void setAgencyDao(AgencyDao agencyDao) {
        this.agencyDao = agencyDao;
    }

    public void setProgramProviderDAO(ProgramProviderDAO dao) {
        this.programProviderDAO = dao;
    }

    public void setSecUserRoleDao(SecUserRoleDao secUserRoleDao) {
        this.secUserRoleDao = secUserRoleDao;
    }

    public Provider getProvider(String providerNo) {
        return providerDao.getProvider(providerNo);
    }

    public String getProviderName(String providerNo) {
        return providerDao.getProviderName(providerNo);
    }

    public List<Provider> getProviders() {
        return providerDao.getProviders();
    }

    public List<Provider> getActiveProviders() {
        return providerDao.getActiveProviders();
    }

    public List<Provider> getActiveProviders(String facilityId, String programId) {
        return providerDao.getActiveProviders(facilityId, programId);
    }

    public List<Provider> getActiveProviders(String providerNo, Integer shelterId) {
        return providerDao.getActiveProviders(providerNo, shelterId);
    }

    public List<Provider> search(String name) {
        return providerDao.search(name);
    }

    public List<ProgramProvider> getProgramDomain(String providerNo) {
        return programProviderDAO.getProgramDomain(providerNo);
    }

    public List<ProgramProvider> getProgramDomainByFacility(String providerNo, Integer facilityId) {
        return programProviderDAO.getProgramDomainByFacility(providerNo, facilityId);
    }

    public List<Facility> getFacilitiesInProgramDomain(String providerNo) {
        return programProviderDAO.getFacilitiesInProgramDomain(providerNo);
    }

    public List getShelterIds(String provider_no) {
        return providerDao.getShelterIds(provider_no);
    }

    public List<Agency> getAgencyDomain(String providerNo) {
        Agency localAgency = agencyDao.getLocalAgency();
        List<Agency> agencies = new ArrayList<Agency>();
        agencies.add(localAgency);
        return agencies;
    }

    public List<Provider> getProvidersByType(String type) {
        return providerDao.getProvidersByType(type);
    }

    public List<SecUserRole> getSecUserRoles(String providerNo) {
        return secUserRoleDao.getUserRoles(providerNo);
    }

    public void saveUserRole(SecUserRole sur) {
        secUserRoleDao.save(sur);
    }
}
