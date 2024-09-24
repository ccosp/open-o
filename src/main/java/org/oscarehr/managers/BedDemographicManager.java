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
package org.oscarehr.managers;

import org.oscarehr.common.model.BedDemographic;
import org.oscarehr.common.model.BedDemographicHistorical;
import org.oscarehr.common.model.BedDemographicStatus;

public interface BedDemographicManager {
    /**
     * @see org.oscarehr.PMmodule.service.BedDemographicManager#demographicExists(java.lang.Integer)
     */
    public boolean demographicExists(Integer bedId);

    /**
     * @see org.oscarehr.PMmodule.service.BedDemographicManager#getBedDemographicByBed(java.lang.Integer)
     */
    public BedDemographic getBedDemographicByBed(Integer bedId);

    public BedDemographic getBedDemographicByDemographic(Integer demographicNo, Integer facilityId);

    /**
     * @see org.oscarehr.PMmodule.service.BedDemographicManager#getDefaultBedDemographicStatus()
     */
    public BedDemographicStatus getDefaultBedDemographicStatus();

    /**
     * @see org.oscarehr.PMmodule.service.BedDemographicManager#getBedDemographicStatuses()
     */
    public BedDemographicStatus[] getBedDemographicStatuses();

    /**
     * @see org.oscarehr.PMmodule.service.BedDemographicManager#getExpiredReservations()
     */
    public BedDemographicHistorical[] getExpiredReservations();

    /**
     * @see org.oscarehr.PMmodule.service.BedDemographicManager#saveBedDemographic(org.oscarehr.PMmodule.model.BedDemographic)
     */
    public void saveBedDemographic(BedDemographic bedDemographic);

    /**
     * @see org.oscarehr.PMmodule.service.BedDemographicManager#deleteBedDemographic(BedDemographic)
     */
    public void deleteBedDemographic(BedDemographic bedDemographic);

    public void setAttributes(BedDemographic bedDemographic);

    public void validate(BedDemographic bedDemographic);

    public void validateBedDemographic(BedDemographic bedDemographic);

    public void validateBedDemographicStatus(Integer bedDemographicStatusId);

    public void validateProvider(String providerId);

    public void validateBed(Integer bedId);

    public void validateDemographic(Integer demographicNo);

}
