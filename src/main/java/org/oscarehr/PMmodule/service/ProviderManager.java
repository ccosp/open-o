/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * <p>
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * <p>
 * This software was written for
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 * <p>
 * Modifications made by Magenta Health in 2024.
 */
package org.oscarehr.PMmodule.service;

import java.util.List;

import org.oscarehr.PMmodule.dao.AgencyDao;
import org.oscarehr.PMmodule.dao.ProgramProviderDAO;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.PMmodule.dao.SecUserRoleDao;
import org.oscarehr.PMmodule.model.Agency;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.PMmodule.model.SecUserRole;
import org.oscarehr.common.model.Facility;
import org.oscarehr.common.model.Provider;

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
