//CHECKSTYLE:OFF
/**
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
 */


package ca.openosp.openo.ehrweb;

import java.util.ArrayList;
import java.util.List;

import ca.openosp.openo.common.dao.DemographicDao;
import ca.openosp.openo.common.dao.OcanStaffFormDao;
import ca.openosp.openo.common.model.Demographic;
import ca.openosp.openo.ehrutil.SpringUtils;

public class OcanReportingBean {

    private static OcanStaffFormDao ocanStaffFormDao = (OcanStaffFormDao) SpringUtils.getBean(OcanStaffFormDao.class);
    private static DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean(DemographicDao.class);

    public static List<Demographic> getOCANClients(Integer facilityId) {
        List<Integer> demographicIds = ocanStaffFormDao.getAllOcanClients(facilityId);
        List<Demographic> demographics = new ArrayList<Demographic>();
        for (Integer id : demographicIds) {
            demographics.add(demographicDao.getClientByDemographicNo(id));
        }
        return demographics;
    }
}
