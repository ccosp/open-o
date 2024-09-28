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


package ca.openosp.openo.ehrweb.eform;

import org.apache.commons.lang.StringUtils;
import ca.openosp.openo.common.dao.DemographicDao;
import ca.openosp.openo.common.model.Demographic;
import ca.openosp.openo.ehrutil.SpringUtils;


public final class EfmPatientFormList {

    private static DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean(DemographicDao.class);

    private EfmPatientFormList() {
        // not meant to instantiate this
    }

    /**
     * MyOscar is only available if 2 conditions are met :
     * - the given demographic must have a myoscar account i.e. demographic.pin
     */
    public static boolean isMyOscarAvailable(int demographicId) {

        Demographic demographic = demographicDao.getDemographicById(demographicId);
        if (demographic != null) {
            String temp = StringUtils.trimToNull(demographic.getMyOscarUserName());
            return (temp != null);
        }

        return (false);
    }
}
