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


package oscar;

import org.apache.logging.log4j.Logger;
import org.oscarehr.common.dao.BillingServiceDao;
import org.oscarehr.common.model.BillingService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.oscarDB.DBPreparedHandler;

public class dbBillingData {
	private Logger logger = MiscUtils.getLogger();
	private BillingServiceDao billingServiceDao = SpringUtils.getBean(BillingServiceDao.class);
	

	DBPreparedHandler accessDB = null;
	private String db_service_code = null;
	private String service_code = null;
	private String description = null;
	private String value = null;
	private String percentage = null;

	public dbBillingData() {
	}

	public void setService_code(String value) {
		service_code = value;
	}

	public String[] ejbLoad() {
		for(BillingService bs:billingServiceDao.findByServiceCode(service_code)) {
			db_service_code = bs.getServiceCode();
			description = bs.getDescription();
			value = bs.getValue();
			percentage = bs.getPercentage();
		}
		
		if (db_service_code == null) return null; 

		if (service_code.equals(db_service_code)) {
			String[] strAuth = new String[4];
			strAuth[0] = db_service_code;
			strAuth[1] = description;
			strAuth[2] = value;
			strAuth[3] = percentage;
			return strAuth;
		} else { 
			return null;
		}
	}
}
