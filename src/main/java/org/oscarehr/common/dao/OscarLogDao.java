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

package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;

import org.oscarehr.common.model.OscarLog;

public interface OscarLogDao extends AbstractDao<OscarLog> {

    public List<OscarLog> findByDemographicId(Integer demographicId);

    public List<OscarLog> findByProviderNo(String providerNo);

    public boolean hasRead(String providerNo, String content, String contentId);

    public List<OscarLog> findByActionAndData(String action, String data);

    public List<OscarLog> findByAction(String action, int start, int length, String orderBy, String orderByDirection);

    public List<OscarLog> findByActionContentAndDemographicId(String action, String content, Integer demographicId);

    public List<Integer> getDemographicIdsOpenedSinceTime(Date value);

    public List<Integer> getRecentDemographicsAccessedByProvider(String providerNo, int startPosition,
                                                                 int itemsToReturn);

    public List<Object[]> getRecentDemographicsViewedByProvider(String providerNo, int startPosition,
                                                                int itemsToReturn);

    public List<Object[]> getRecentDemographicsViewedByProviderAfterDateIncluded(String providerNo, Date date,
                                                                                 int startPosition, int itemsToReturn);

    public int purgeLogEntries(Date maxDateToRemove);

}
