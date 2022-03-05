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
package org.oscarehr.integration.mcedt;

import org.apache.logging.log4j.Logger;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.integration.ebs.client.EdtClientBuilder;
import org.oscarehr.integration.ebs.client.EdtClientBuilderConfig;
import org.oscarehr.integration.mcedt.mailbox.ActionUtils;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.OscarProperties;
import ca.ontario.health.edt.EDTDelegate;

import java.util.HashMap;
import java.util.Map;

public class DelegateFactory {
	
	private static Logger logger = MiscUtils.getLogger();
	private static UserPropertyDAO userPropertyDAO = SpringUtils.getBean(UserPropertyDAO.class);
	private static Map<String, EDTDelegate> edtDelegates = new HashMap<>();

	public static EDTDelegate getEDTDelegateInstance(String serviceId) {
		if (edtDelegates.get(serviceId) == null) {
			edtDelegates.put(serviceId, newDelegate(serviceId));
		}
		return edtDelegates.get(serviceId);
	}

	public static EDTDelegate getEDTDelegateInstance() {
		OscarProperties props = OscarProperties.getInstance();
		return getEDTDelegateInstance(props.getProperty("mcedt.service.id"));
	}
	
	private static EDTDelegate newDelegate(String serviceId) {
		OscarProperties props = OscarProperties.getInstance();
		EdtClientBuilderConfig config = new EdtClientBuilderConfig();
		config.setLoggingRequired(!Boolean.valueOf(props.getProperty("mcedt.logging.skip")));
		config.setKeystoreUser(props.getProperty("mcedt.keystore.user"));
		config.setKeystorePassword(props.getProperty("mcedt.keystore.pass"));
		config.setUserNameTokenUser(props.getProperty("mcedt.service.user"));
		//config.setUserNameTokenPassword(props.getProperty("mcedt.service.pass"));
		UserProperty prop = userPropertyDAO.getProp(UserProperty.MCEDT_ACCOUNT_PASSWORD);
		config.setUserNameTokenPassword((prop==null||prop.getValue()==null || prop.getValue().trim().equals(""))?props.getProperty("mcedt.service.pass"):prop.getValue());
		config.setServiceUrl(props.getProperty("mcedt.service.url"));
		config.setConformanceKey(props.getProperty("mcedt.service.conformanceKey"));
		config.setServiceId((serviceId==null ||serviceId.trim().equals(""))?props.getProperty("mcedt.service.id"):serviceId);
		config.setMtomEnabled(true);
		EdtClientBuilder builder = new EdtClientBuilder(config);
		EDTDelegate edtDelegate = builder.build(EDTDelegate.class);
		if (logger.isInfoEnabled()) {
			logger.info("Created new EDT delegate " + edtDelegate);
		}
		return edtDelegate;
	}

	/**
	 * Refreshes created delegates in the event that information has been updated
	 */
	public static void refreshDelegates() {
		// For each service id, create a new updated delegate
		for (String serviceId : ActionUtils.getServiceIds()){
			edtDelegates.put(serviceId, newDelegate(serviceId));
		}
		// Suggests for the Garbage Collector to run in an effort to clean up any lingering connections
		System.gc();
	}
	
	public static UserPropertyDAO getUserPropertyDAO() {
		return userPropertyDAO;
	}

	public static void setUserPropertyDAO(UserPropertyDAO userPropertyDAO) {
		DelegateFactory.userPropertyDAO = userPropertyDAO;
	}
	
}