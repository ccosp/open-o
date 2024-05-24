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

import java.io.UnsupportedEncodingException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import javax.persistence.Query;

import org.apache.commons.codec.binary.Base64;
import org.oscarehr.common.NativeSql;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.common.model.Hl7TextMessageInfo;
import org.oscarehr.common.model.Hl7TextMessageInfo2;
import org.oscarehr.common.model.SystemPreferences;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.stereotype.Repository;
import oscar.OscarProperties;

public interface Hl7TextInfoDao extends AbstractDao<Hl7TextInfo> {

	public List<Hl7TextInfo> findAll();

	public Hl7TextInfo findLabId(int labId);

	public List<Hl7TextInfo> findByHealthCardNo(String hin);

	public List<Hl7TextInfo> searchByAccessionNumber(String acc);

	public List<Hl7TextInfo> searchByAccessionNumber(String acc1, String acc2);

	public List<Hl7TextInfo> searchByAccessionNumberOrderByObrDate(String accessionNumber);

	public Hl7TextInfo findLatestVersionByAccessionNumberOrFillerNumber(
			String acc, String fillerNumber);

	public List<Hl7TextInfo> searchByFillerOrderNumber(String fon, String sending_facility);

	public void updateReportStatusByLabId(String reportStatus, int labNumber);

	public List<Hl7TextMessageInfo> getMatchingLabs(String hl7msg);

	public List<Hl7TextMessageInfo2> getMatchingLabsByAccessionNo(String accession);

	public List<Hl7TextInfo> getAllLabsByLabNumberResultStatus();

	public void updateResultStatusByLabId(String resultStatus, int labNumber);

	public void createUpdateLabelByLabNumber(String label, int lab_no);

	public List<Hl7TextInfo> findByLabId(Integer labNo);

	public List<Object[]> findByLabIdViaMagic(Integer labNo);

	public List<Object[]> findByDemographicId(Integer demographicNo);

	public List<Hl7TextInfo> findByLabIdList(List<Integer> labIds);

	public List<Object[]> findLabsViaMagic(String status, String providerNo, String patientFirstName,
			String patientLastName, String patientHealthNumber);

	public List<Object[]> findLabAndDocsViaMagic(String providerNo, String demographicNo, String patientFirstName,
			String patientLastName, String patientHealthNumber, String status, boolean isPaged, Integer page,
			Integer pageSize, boolean mixLabsAndDocs, Boolean isAbnormal, boolean searchProvider,
			boolean patientSearch);

	public List<Object[]> findLabAndDocsViaMagic(String providerNo, String demographicNo, String patientFirstName,
			String patientLastName, String patientHealthNumber, String status, boolean isPaged, Integer page,
			Integer pageSize, boolean mixLabsAndDocs, Boolean isAbnormal, boolean searchProvider, boolean patientSearch,
			Date startDate, Date endDate);

	public List<Object> findDisciplines(Integer labid);

	public List<Hl7TextInfo> findByFillerOrderNumber(String fillerOrderNum);
}
