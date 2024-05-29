/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * Copyright (c) 2005, 2009 IBM Corporation and others.
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
 * Contributors:
 *     <Quatro Group Software Systems inc.>  <OSCAR Team>
 *
 * Modifications made by Magenta Health in 2024.
 */

package org.oscarehr.PMmodule.service;

import org.oscarehr.PMmodule.dao.HealthSafetyDao;
import org.oscarehr.PMmodule.model.HealthSafety;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HealthSafetyManagerImpl implements HealthSafetyManager{
    
    private HealthSafetyDao healthSafetyDao=null;

	public HealthSafetyDao getHealthSafetyDao() {
		return healthSafetyDao;
	}

	public void setHealthSafetyDao(HealthSafetyDao healthSafetyDao) {
		this.healthSafetyDao = healthSafetyDao;
	}

	public HealthSafety getHealthSafetyByDemographic(Long demographicNo) {
		return healthSafetyDao.getHealthSafetyByDemographic(demographicNo);
	}

    public void saveHealthSafetyByDemographic(HealthSafety healthsafety) {
    	healthSafetyDao.saveHealthSafetyByDemographic(healthsafety);
    }
}
