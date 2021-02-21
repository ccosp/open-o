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
package org.oscarehr.managers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.common.dao.BillingServiceDao;
import org.oscarehr.common.dao.CtlBillingServiceDao;
import org.oscarehr.common.model.BillingService;
import org.oscarehr.managers.model.ServiceType;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillingManager {

	@Autowired
	private CtlBillingServiceDao ctlBillingServiceDao;
	
	@Autowired
	private SecurityInfoManager securityInfoManager;
	
	@Autowired
	private BillingServiceDao billingServiceDao;
	/*
	 * I'm only doing that conversion in the manager because I don't have time to refactor the DAO method..but given more time..that's where I would do it.
	 * Regardless those other calls should be moved over to calling this one.
	 */
	public List<ServiceType> getUniqueServiceTypes(LoggedInInfo loggedInInfo) {
		return getUniqueServiceTypes(loggedInInfo,CtlBillingServiceDao.DEFAULT_STATUS);
	}
	
	/*
	 * I'm only doing that conversion in the manager because I don't have time to refactor the DAO method..but given more time..that's where I would do it.
	 * Regardless those other calls should be moved over to calling this one.
	 */
	public List<ServiceType> getUniqueServiceTypes(LoggedInInfo loggedInInfo, String type) {
		List<ServiceType> result = new ArrayList<ServiceType>(); 
		
		for(Object[] r:ctlBillingServiceDao.getUniqueServiceTypes(type)) {
			result.add(new ServiceType((String)r[0],(String)r[1]));
		}
	
		return result;
	}

	/**
	 * Update the description of an MSP service code. Will not write to database if
	 * there is no change to the original description
	 * 
	 * @param loggedInInfo
	 * @param servicecodeid
	 * @param description
	 * @return BillingService.id as failed save will return id = 0
	 */
	public int updateMSPServiceCodeDescription(LoggedInInfo loggedInInfo, int servicecodeid, String description) {
		if (! securityInfoManager.hasPrivilege(loggedInInfo, "_admin", SecurityInfoManager.WRITE, null)) {
			throw new RuntimeException("Access Denied");
		}
		
		description = StringUtils.trimToEmpty(description);
		int updateid = 0;
		BillingService billingService = billingServiceDao.find(servicecodeid);
		if(billingService != null && ! description.equals(billingService.getDescription()))
		{
			billingService.setDescription(description);
			billingServiceDao.merge(billingService);
			updateid = billingService.getId();
		}
		return updateid;
	}
}


