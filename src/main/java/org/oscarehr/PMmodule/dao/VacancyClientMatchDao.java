/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 * <p>
 * Modifications made by Magenta Health in 2024.
 */
package org.oscarehr.PMmodule.dao;

import java.util.List;

import org.oscarehr.PMmodule.model.VacancyClientMatch;
import org.oscarehr.common.dao.AbstractDao;

public interface VacancyClientMatchDao extends AbstractDao<VacancyClientMatch> {

    public List<VacancyClientMatch> findByClientIdAndVacancyId(int clientId, int vacancyId);

    public List<VacancyClientMatch> findByClientId(int clientId);

    public List<VacancyClientMatch> findBystatus(String status);

    public void updateStatus(String status, int clientId, int vacancyId);

    public void updateStatusAndRejectedReason(String status, String rejectedReason, int clientId, int vacancyId);

}
