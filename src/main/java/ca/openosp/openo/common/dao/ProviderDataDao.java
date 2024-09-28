//CHECKSTYLE:OFF
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
package ca.openosp.openo.common.dao;

import java.util.List;

import ca.openosp.openo.common.model.ProviderData;

public interface ProviderDataDao extends AbstractDao<ProviderData> {
    ProviderData findByOhipNumber(String ohipNumber);

    ProviderData findByProviderNo(String providerNo);

    List<ProviderData> findByProviderNo(String providerNo, String status, int limit, int offset);

    List<ProviderData> findByProviderName(String searchStr, String status, int limit, int offset);

    List<ProviderData> findAllOrderByLastName();

    List<ProviderData> findByProviderSite(String providerNo);

    List<Object[]> findProviderSecUserRoles(String lastName, String firstName);

    List<ProviderData> findByProviderTeam(String providerNo);

    List<ProviderData> findAllBilling(String active);

    List<ProviderData> findByTypeAndOhip(String providerType, String insuranceNo);

    List<ProviderData> findByType(String providerType);

    List<ProviderData> findByName(String firstName, String lastName, boolean onlyActive);

    List<ProviderData> findAll();

    List<ProviderData> findAll(boolean inactive);

    Integer getLastId();
}
