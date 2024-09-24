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
package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;

import org.oscarehr.common.model.BedCheckTime;

public interface BedCheckTimeDao extends AbstractDao<BedCheckTime> {

    public boolean bedCheckTimeExists(Integer programId, Date time);

    public BedCheckTime getBedCheckTime(Integer id);

    public BedCheckTime[] getBedCheckTimes(Integer programId);

    public void saveBedCheckTime(BedCheckTime bedCheckTime);

    public void deleteBedCheckTime(BedCheckTime bedCheckTime);

    public String getBedCheckTimesQuery(Integer programId);

    public Object[] getBedCheckTimesValues(Integer programId);

    public List<BedCheckTime> getBedCheckTimes(String queryStr, Object[] values);
}
