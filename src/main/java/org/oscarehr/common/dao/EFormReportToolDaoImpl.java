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

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.oscarehr.common.model.EForm;
import org.oscarehr.common.model.EFormReportTool;
import org.oscarehr.common.model.EFormValue;
import org.springframework.stereotype.Repository;

@Repository
public class EFormReportToolDaoImpl extends AbstractDaoImpl<EFormReportTool> implements EFormReportToolDao {

	public EFormReportToolDaoImpl() {
		super(EFormReportTool.class);
	}

	@SuppressWarnings("unchecked")
	public void markLatest(Integer eformReportToolId) {
		// ... rest of the code
	}

	public void addNew(EFormReportTool eformReportTool, EForm eform, List<String> fields, String providerNo) {
		// ... rest of the code
	}

	public void populateReportTableItem(EFormReportTool eft, List<EFormValue> values, Integer fdid, Integer demographicNo, Date dateFormCreated, String providerNo) {
		// ... rest of the code
	}

	public void deleteAllData(EFormReportTool eft) {
		// ... rest of the code
	}

	public void drop(EFormReportTool eft) {
		// ... rest of the code
	}

	public Integer getNumRecords(EFormReportTool eformReportTool) {
		// ... rest of the code
	}
	
}
