/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import org.oscarehr.common.model.CustomFilter;
import org.oscarehr.common.model.Tickler;

public interface TicklerDao extends AbstractDao<Tickler>{

	Tickler find(Integer id);

	List<Tickler> findActiveByMessageForPatients(List<Integer> demographicNos, String remString);

	List<Tickler> findActiveByDemographicNoAndMessage(Integer demoNo, String message);

	List<Tickler> findActiveByDemographicNo(Integer demoNo);

	List<Tickler> findByTicklerNoDemo(Integer ticklerNo, Integer demoNo);

	List<Tickler> findByTicklerNoAssignedTo(Integer ticklerNo, String assignedTo, Integer demoNo);

	List<Tickler> findByDemographicIdTaskAssignedToAndMessage(Integer demographicNo, String taskAssignedTo, String message);

	List<Tickler> search_tickler_bydemo(Integer demographicNo, String status, Date beginDate, Date endDate);

	List<Tickler> search_tickler(Integer demographicNo, Date endDate);

	List<Tickler> listTicklers(Integer demographicNo, Date beginDate, Date endDate);

	int getActiveTicklerCount(String providerNo);

	int getActiveTicklerByDemoCount(Integer demographicNo);

	List<Tickler> getTicklers(CustomFilter filter, int offset, int limit);

	List<Tickler> getTicklers(CustomFilter filter);

	int getNumTicklers(CustomFilter filter);
}
