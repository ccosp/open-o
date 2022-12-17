/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.common.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.http.impl.cookie.DateUtils;
import org.apache.logging.log4j.Logger;
import org.oscarehr.common.model.SystemPreferences;
import org.oscarehr.util.SpringUtils;

import oscar.oscarLab.ca.on.LabResultData;
import oscar.util.StringUtils;

public class InboxResultsDao {

	Logger logger = org.oscarehr.util.MiscUtils.getLogger();

	@PersistenceContext
	protected EntityManager entityManager = null;

	/** Creates a new instance of Hl7textResultsData */
	public InboxResultsDao() {
	}

	/**
	 * Populates ArrayList with labs attached to a consultation request
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public ArrayList populateHL7ResultsData(String demographicNo, String consultationId, boolean attached) {
		String sql = "SELECT hl7.label, hl7.lab_no, hl7.obr_date, hl7.discipline, hl7.accessionNum, hl7.final_result_count, patientLabRouting.id "
				+ "FROM hl7TextInfo hl7, patientLabRouting "
				+ "WHERE patientLabRouting.lab_no = hl7.lab_no "
				+ "AND patientLabRouting.lab_type = 'HL7' AND patientLabRouting.demographic_no="
				+ demographicNo
				+ " GROUP BY hl7.lab_no"
				+ " ORDER BY date(hl7.obr_date) DESC";

		String attachQuery = "SELECT consultdocs.document_no FROM consultdocs, patientLabRouting "
				+ "WHERE patientLabRouting.id = consultdocs.document_no AND " + "consultdocs.requestId = "
				+ consultationId
				+ " AND consultdocs.doctype = 'L' AND consultdocs.deleted IS NULL ORDER BY consultdocs.document_no";

		ArrayList labResults = new ArrayList<LabResultData>();
		ArrayList attachedLabs = new ArrayList<LabResultData>();


		try {
			Query q = entityManager.createNativeQuery(attachQuery);

			List<Object[]> result = q.getResultList();
			for (Object[] r : result) {
				LabResultData lbData = new LabResultData(LabResultData.HL7TEXT);
				lbData.labPatientId = (String) r[0];
				attachedLabs.add(lbData);
			}

			LabResultData lbData = new LabResultData(LabResultData.HL7TEXT);
			LabResultData.CompareId c = lbData.getComparatorId();

			q = entityManager.createNativeQuery(sql);
			result = q.getResultList();
			for (Object[] r : result) {
				lbData.segmentID = (String) r[1];
				lbData.labPatientId = (String) r[6];
				lbData.dateTime = (String) r[2];
				lbData.discipline = (String) r[3];
				lbData.accessionNumber = (String) r[4];
				lbData.finalResultsCount = (Integer) r[5];
				lbData.label = (String) r[0];

				if (attached && Collections.binarySearch(attachedLabs, lbData, c) >= 0)
					labResults.add(lbData);
				else if (!attached && Collections.binarySearch(attachedLabs, lbData, c) < 0)
					labResults.add(lbData);

				lbData = new LabResultData(LabResultData.HL7TEXT);
			}
		} catch (Exception e) {
			logger.error("exception in HL7Populate", e);
		}


		return labResults;
	}

	@SuppressWarnings("unchecked")
	public boolean isSentToProvider(String docNo, String providerNo) {
		if (docNo != null && providerNo != null) {
			int dn = Integer.parseInt(docNo.trim());
			providerNo = providerNo.trim();
			String sql = "select * from providerLabRouting plr where plr.lab_type='DOC' and plr.lab_no=" + dn
					+ " and plr.provider_no='" + providerNo + "'";
			try {

				Query q = entityManager.createNativeQuery(sql);
				List<Object[]> rs = q.getResultList();

				logger.debug(sql);
				if (!rs.isEmpty()) {
					return true;
				} else
					return false;
			} catch (Exception e) {
				logger.error(e.toString());
				return false;
			}
		} else {
			return false;
		}
	}

	//retrieve all documents from database
	/**
	 * Wrapper function for non paged document queries.
	 */
	@SuppressWarnings("rawtypes")
    public ArrayList populateDocumentResultsData(String providerNo, String demographicNo, String patientFirstName,
			String patientLastName, String patientHealthNumber, String status) {
		return populateDocumentResultsData(providerNo, demographicNo, patientFirstName, patientLastName,
				patientHealthNumber, status, false, null, null, false, null);
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public ArrayList<LabResultData> populateDocumentResultsData(String providerNo, String demographicNo, String patientFirstName,
			String patientLastName, String patientHealthNumber, String status, boolean isPaged, Integer page,
			Integer pageSize, boolean mixLabsAndDocs, Boolean isAbnormal) {
		
		return populateDocumentResultsData(providerNo, demographicNo, patientFirstName, patientLastName, patientHealthNumber, status, isPaged, page, pageSize, mixLabsAndDocs, isAbnormal, null, null);
	}
	
	@SuppressWarnings({ "unchecked" })
	public ArrayList<LabResultData> populateDocumentResultsData(String providerNo, String demographicNo, String patientFirstName,
			String patientLastName, String patientHealthNumber, String status, boolean isPaged, Integer page,
			Integer pageSize, boolean mixLabsAndDocs, Boolean isAbnormal, Date startDate, Date endDate) {
		if (providerNo == null) {
			providerNo = "";
		}
		boolean searchProvider = !"-1".equals(providerNo);
		if (patientFirstName == null) {
			patientFirstName = "";
		}
		if (patientLastName == null) {
			patientLastName = "";
		}
		if (patientHealthNumber == null) {
			patientHealthNumber = "";
		}
		boolean patientSearch = !"".equals(patientFirstName) || !"".equals(patientLastName)
				|| !"".equals(patientHealthNumber);
		if (status == null) {
			status = "";
		}

		SystemPreferencesDao systemPreferencesDao = SpringUtils.getBean(SystemPreferencesDao.class);
		ArrayList<LabResultData> labResults = new ArrayList<LabResultData>();
		String sql = "";
		String dateSearchType = "serviceObservation";

		SystemPreferences systemPreferences = systemPreferencesDao.findPreferenceByName("inboxDateSearchType");
		if (systemPreferences != null)
		{
			if (systemPreferences.getValue() != null && !systemPreferences.getValue().isEmpty())
			{
				dateSearchType = systemPreferences.getValue();
			}
		}

		String dateSql = "";
		SimpleDateFormat dateSqlFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//Check if the startDate is null, if a date exists then creates the SQL for the startDate
		if (startDate != null) {
			if (dateSearchType.equals("receivedCreated"))
			{
				dateSql += " AND doc.contentdatetime >= '" + dateSqlFormatter.format(startDate) + "'";
			}
			else
			{
				dateSql += " AND doc.observationdate >= '" + dateSqlFormatter.format(startDate) + "'";
			}
		}
		//Check if the startDate is null, if a date exists then creates the SQL for the startDate 
		if (endDate != null)
		{
			if (dateSearchType.equals("receivedCreated"))
			{
				dateSql += " AND doc.contentdatetime <= '" + dateSqlFormatter.format(endDate) + "'";
			}
			else
			{
				dateSql += " AND doc.observationdate <= '" + dateSqlFormatter.format(endDate) + "'";
			}
		}
		String isDocAbnormalSql = "";
		if (isAbnormal != null) {
			if (isAbnormal) {
				isDocAbnormalSql = " AND doc.abnormal = TRUE ";
			} else {
				isDocAbnormalSql = " AND (doc.abnormal = FALSE OR doc.abnormal IS NULL) ";
			}
		}
		
		int docNoLoc = -1;
		int statusLoc = -1;
		int docTypeLoc = -1;
		int lastNameLoc = -1;
		int firstNameLoc = -1;
		int hinLoc = -1;
		int sexLoc = -1;
		int moduleLoc = -1;
		int obsDateLoc = -1;
		int descriptionLoc = -1;
		int updateDateLoc = -1;
		try {

			// Get documents by demographic
			//if (demographicNo != null && !"".equals(demographicNo)) {
			// Get mix from labs
			if (mixLabsAndDocs) {
				if ("0".equals(demographicNo)) {
					docNoLoc = 1; statusLoc = 2; docTypeLoc = 3; lastNameLoc = 4; firstNameLoc = 5; hinLoc = 6; sexLoc = 7; moduleLoc = 8; obsDateLoc = 9; descriptionLoc = 10; updateDateLoc = 11;
					sql = " SELECT X.id, X.lab_no as document_no, X.status, X.lab_type as doctype, d.last_name, d.first_name, hin, sex, d.demographic_no as module_id, "
							+ (dateSearchType.equals("receivedCreated")?"doc.contentdatetime":"doc.observationdate") + ", doc.doctype as description, date(doc.updatedatetime) "
							+ " FROM document doc, "
							+ " (SELECT plr.id, plr.lab_type, plr.lab_no, plr.status "
							+ "  FROM patientLabRouting plr2, providerLabRouting plr, hl7TextInfo info "
							+ "  WHERE plr.lab_no = plr2.lab_no "
							+ (searchProvider ? " AND plr.provider_no = '" + providerNo + "' " : " ")
							+ "    AND plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
							+ "    AND plr.lab_type = 'HL7'   "
							+ "    AND plr2.lab_type = 'HL7' "
							+ "    AND plr2.demographic_no = '0' "
							+ "    AND info.lab_no = plr.lab_no "
							+ (isAbnormal != null && isAbnormal ? "AND info.result_status = 'A'" : isAbnormal != null
									&& !isAbnormal ? "AND (info.result_status IS NULL OR info.result_status != 'A')" : "")
							+ " UNION "
							+ " SELECT plr.id, plr.lab_type, plr.lab_no, plr.status "
							+ " FROM providerLabRouting plr, ctl_document cd  "
							+ " WHERE plr.lab_type = 'DOC' "
							+ " AND plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
							+ " AND cd.document_no  = plr.lab_no "
							+ " AND cd.module_id = -1 "
							+ (searchProvider ? " AND plr.provider_no = '" + providerNo + "' " : " ")
							+ " ORDER BY id DESC "
							+ " ) AS X "
							+ " LEFT JOIN demographic d "
							+ " ON d.demographic_no = -1 "
							+ " WHERE X.lab_type = 'DOC' AND doc.document_no = X.lab_no " 
							+ isDocAbnormalSql
							+ dateSql
							+ " GROUP BY doc.document_no"
							+ " ORDER BY " + (dateSearchType.equals("receivedCreated")?"doc.contentdatetime DESC,":"doc.observationdate DESC," ) + " doc.document_no DESC "
							+ (isPaged ? "	LIMIT " + (page * pageSize) + "," + pageSize : "");

				} else if (demographicNo != null && !"".equals(demographicNo)) {
					docNoLoc = 1; statusLoc = 2; docTypeLoc = 9; lastNameLoc = 3; firstNameLoc = 4; hinLoc = 5; sexLoc = 6; moduleLoc = 7; obsDateLoc = 8; descriptionLoc = 10; updateDateLoc = 11;
					sql = " SELECT plr.id, doc.document_no, plr.status, d.last_name, d.first_name, hin, sex, d.demographic_no as module_id,"
							+ (dateSearchType.equals("receivedCreated")?"doc.contentdatetime":"doc.observationdate") + ", plr.lab_type as doctype, doc.doctype as description, date(doc.updatedatetime) "
							+ " FROM demographic d, providerLabRouting plr, document doc, "
							+ " (SELECT * FROM "
							+ " (SELECT DISTINCT plr.id, plr.lab_type  FROM providerLabRouting plr, ctl_document cd "
							+ " WHERE 	" + " (cd.module_id = '"
							+ demographicNo
							+ "' "
							+ "	AND cd.document_no = plr.lab_no"
							+ "	AND plr.lab_type = 'DOC'  	"
							+ "	AND plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
							+ (searchProvider ? " AND plr.provider_no = '" + providerNo + "' )" : " )")
							+ " ORDER BY id DESC) AS Y"
							+ " UNION"
							+ " SELECT * FROM"
							+ " (SELECT DISTINCT plr.id, plr.lab_type  FROM providerLabRouting plr, patientLabRouting plr2"
							+ " WHERE"
							+ "	plr.lab_type = 'HL7' AND plr2.lab_type = 'HL7'"
							+ "	AND plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
							+ (searchProvider ? " AND plr.provider_no = '" + providerNo + "' " : " ")
							+ " 	AND plr.lab_no = plr2.lab_no AND plr2.demographic_no = '"
							+ demographicNo
							+ "'"
							+ " ORDER BY id DESC) AS Z"
							+ " ORDER BY id DESC "
							+ " ) AS X "
							+ " WHERE X.lab_type = 'DOC' and X.id = plr.id and doc.document_no = plr.lab_no and d.demographic_no = '"
							+ demographicNo + "' "
							+ isDocAbnormalSql
							+ dateSql
							+ " GROUP BY doc.document_no"
							+ " ORDER BY " + (dateSearchType.equals("receivedCreated")?"doc.contentdatetime DESC,":"doc.observationdate DESC," ) + " doc.document_no DESC "
							+ (isPaged ? "	LIMIT " + (page * pageSize) + "," + pageSize : "");
				} else if (patientSearch) { // N arg
					docNoLoc = 1; statusLoc = 2; docTypeLoc = 9; lastNameLoc = 3; firstNameLoc = 4; hinLoc = 5; sexLoc = 6; moduleLoc = 7; obsDateLoc = 8; descriptionLoc = 10; updateDateLoc = 11;
					sql = " SELECT plr.id, doc.document_no, plr.status, d.last_name, d.first_name, hin, sex, d.demographic_no as module_id, "
							+ (dateSearchType.equals("receivedCreated")?"doc.contentdatetime":"doc.observationdate") + ", plr.lab_type as doctype, doc.doctype as description, date(doc.updatedatetime) "
							+ " FROM demographic d, providerLabRouting plr, document doc,  "
							+ " (SELECT * FROM "
							+ "	 (SELECT * FROM  "
							+ "		(SELECT DISTINCT plr.id, plr.lab_type, d.demographic_no "
							+ "			FROM providerLabRouting plr, ctl_document cd, demographic d "
							+ "			WHERE 	 "
							+ "			(d.first_name like '%"
							+ patientFirstName
							+ "%' AND d.last_name like '%"
							+ patientLastName
							+ "%' AND d.hin like '%"
							+ patientHealthNumber
							+ "%' "
							+ "		AND cd.module_id = d.demographic_no 	AND cd.document_no = plr.lab_no	AND plr.lab_type = 'DOC' "
							+ "	AND plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
							+ (searchProvider ? " AND plr.provider_no = '" + providerNo + "' " : " ")
							+ "		) ORDER BY id DESC) AS Y "
							+ " 	UNION "
							+ "	SELECT * FROM "
							+ "		(SELECT DISTINCT plr.id, plr.lab_type, d.demographic_no "
							+ "		FROM providerLabRouting plr, patientLabRouting plr2, demographic d"
							+ (isAbnormal != null ? ", hl7TextInfo info " : " ")
							+ "		WHERE d.first_name like '%"
							+ patientFirstName
							+ "%' AND d.last_name like '%"
							+ patientLastName
							+ "%' AND d.hin like '%"
							+ patientHealthNumber
							+ "%' "
							+ "		AND	plr.lab_type = 'HL7' AND plr2.lab_type = 'HL7' "
							+ (isAbnormal != null ? " AND plr.lab_no = info.lab_no AND (info.result_status IS NULL OR info.result_status != 'A') "
									: " ")
							+ "	AND plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
							+ (searchProvider ? " AND plr.provider_no = '" + providerNo + "' " : " ")
							+ " 	AND plr.lab_no = plr2.lab_no AND plr2.demographic_no = d.demographic_no ORDER BY id DESC) AS Z "
							+ " 			ORDER BY id DESC) AS X "
							+ " 	  ) AS Z  "
							+ " WHERE Z.lab_type = 'DOC' and Z.id = plr.id and doc.document_no = plr.lab_no and d.demographic_no = Z.demographic_no "
							+ isDocAbnormalSql
							+ dateSql
							+ " GROUP BY doc.document_no"
							+ " ORDER BY " + (dateSearchType.equals("receivedCreated")?"doc.contentdatetime DESC,":"doc.observationdate DESC," ) + " doc.document_no DESC "
							+ (isPaged ? "	LIMIT " + (page * pageSize) + "," + pageSize : "");

				} else {
					docNoLoc = 0; statusLoc = 1; docTypeLoc = 8; lastNameLoc = 2; firstNameLoc = 3; hinLoc = 4; sexLoc = 5; moduleLoc = 6; obsDateLoc = 7; descriptionLoc = 9; updateDateLoc = 10;
					// N
					// document_no, status, last_name, first_name, hin, sex, module_id, observationdate
					sql = "SELECT doc.document_no, plr.status, d.last_name, d.first_name, d.hin, d.sex, cd.module_id,"
							+ (dateSearchType.equals("receivedCreated")?"doc.contentdatetime":"doc.observationdate") + ", plr.lab_type, doc.doctype, date(doc.updatedatetime) "
							+ "FROM document doc "
							+ "LEFT JOIN providerLabRouting plr ON doc.document_no = plr.lab_no "
							+ "LEFT JOIN ctl_document cd ON cd.document_no = doc.document_no "
							+ "LEFT JOIN demographic d ON cd.module_id = d.demographic_no "
							+ "WHERE plr.lab_type = 'DOC' "
							+ (searchProvider ? " AND plr.provider_no = '" + providerNo + "' " : " ")
							+ " AND plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
							+ isDocAbnormalSql
							+ dateSql
							+ " GROUP BY doc.document_no "
							+ " ORDER BY " + (dateSearchType.equals("receivedCreated")?"doc.contentdatetime DESC":"doc.observationdate DESC")
							+ (isPaged ? " LIMIT " + (page * pageSize) + "," + pageSize : "");
				}
			} else { // Don't mix labs and docs.
				if ("0".equals(demographicNo)) {
					docNoLoc = 1; statusLoc = 2; docTypeLoc = 5; lastNameLoc = 6; firstNameLoc = 7; hinLoc = 8; sexLoc = 9; moduleLoc = 3; obsDateLoc = 4; descriptionLoc = 10; updateDateLoc = 11;
					sql = "SELECT plr.id, doc.document_no, plr.status, demographic_no as module_id,"
							+ (dateSearchType.equals("receivedCreated")?"doc.contentdatetime":"doc.observationdate") + ", plr.lab_type as doctype, last_name, first_name, hin, sex, docdesc, date(doc.updatedatetime) as updateDateLoc"
							+ " FROM providerLabRouting plr"
							+ " LEFT JOIN document doc ON doc.document_no = plr.lab_no"
							+ " RIGHT JOIN ctl_document cd ON cd.document_no = plr.lab_no AND cd.module_id = -1"
							+ " LEFT JOIN demographic d ON d.demographic_no = -1"
							+ " WHERE plr.lab_type = 'DOC' "
							+ " AND plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
							+ (searchProvider ? " AND plr.provider_no = '" + providerNo + "' " : "")
							+ isDocAbnormalSql
							+ dateSql
							+ " GROUP BY doc.document_no"
							+ " ORDER BY " + (dateSearchType.equals("receivedCreated")?"doc.contentdatetime DESC":"doc.observationdate DESC")
							+ (isPaged ? "	LIMIT " + (page * pageSize) + "," + pageSize : "");
				} else if (demographicNo != null && !"".equals(demographicNo)) {
					docNoLoc = 1; statusLoc = 2; docTypeLoc = 9; lastNameLoc = 3; firstNameLoc = 4; hinLoc = 5; sexLoc = 6; moduleLoc = 7; obsDateLoc = 8; descriptionLoc = 10; updateDateLoc = 11;
					sql = "SELECT plr.id, doc.document_no, plr.status, last_name, first_name, hin, sex, module_id,"
					        + (dateSearchType.equals("receivedCreated")?"doc.contentdatetime":"doc.observationdate") + ", plr.lab_type as doctype, doc.doctype as description, date(doc.updatedatetime) "
							+ "FROM ctl_document cd, demographic d, providerLabRouting plr, document doc "
							+ "WHERE d.demographic_no = '"
							+ demographicNo
							+ "' "
							+ "	AND cd.module_id = d.demographic_no "
							+ "	AND cd.document_no = plr.lab_no "
							+ "	AND plr.lab_type = 'DOC' "
							+ "	AND plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
							+ (searchProvider ? " AND plr.provider_no = '" + providerNo + "' " : "")
							+ "	AND doc.document_no = cd.document_no "
							+ isDocAbnormalSql
							+ dateSql
							+ " GROUP BY doc.document_no"
							+ " ORDER BY " + (dateSearchType.equals("receivedCreated")?"doc.contentdatetime DESC ":"doc.observationdate DESC ")
							+ (isPaged ? "	LIMIT " + (page * pageSize) + "," + pageSize : "");
				} else if (patientSearch) {
					docNoLoc = 1; statusLoc = 2; docTypeLoc = 9; lastNameLoc = 3; firstNameLoc = 4; hinLoc = 5; sexLoc = 6; moduleLoc = 7; obsDateLoc = 8; descriptionLoc = 10; updateDateLoc = 11;
					sql = "SELECT plr.id, doc.document_no, plr.status, last_name, first_name, hin, sex, module_id,"
							+ (dateSearchType.equals("receivedCreated")?"doc.contentdatetime":"doc.observationdate") + ", plr.lab_type as doctype, doc.doctype as description, date(doc.updatedatetime) "
							+ "FROM ctl_document cd, demographic d, providerLabRouting plr, document doc "
							+ "WHERE d.first_name like '%"
							+ patientFirstName
							+ "%' AND d.last_name like '%"
							+ patientLastName
							+ "%' AND d.hin like '%"
							+ patientHealthNumber
							+ "%' "
							+ "	AND cd.module_id = d.demographic_no "
							+ "	AND cd.document_no = plr.lab_no "
							+ "	AND plr.lab_type = 'DOC' "
							+ "	AND plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
							+ (searchProvider ? " AND plr.provider_no = '" + providerNo + "' " : "")
							+ "	AND doc.document_no = cd.document_no "
							+ isDocAbnormalSql
							+ dateSql
							+ " GROUP BY doc.document_no"
							+ " ORDER BY " + (dateSearchType.equals("receivedCreated")?"doc.contentdatetime DESC":"doc.observationdate DESC")
							+ (isPaged ? "	LIMIT " + (page * pageSize) + "," + pageSize : "");
				} else {
					docNoLoc = 1; statusLoc = 2; docTypeLoc = 9; lastNameLoc = 3; firstNameLoc = 4; hinLoc = 5; sexLoc = 6; moduleLoc = 7; obsDateLoc = 8; descriptionLoc = 10; updateDateLoc = 11;
					sql = " SELECT * "
							+ " FROM (SELECT plr.id, doc.document_no, plr.status, last_name, first_name, hin, sex, module_id,"
							+ (dateSearchType.equals("receivedCreated")?"doc.contentdatetime":"doc.observationdate") + ", plr.lab_type as doctype, docdesc, updatedatetime "
							+ " FROM ctl_document cd, demographic d, providerLabRouting plr, document doc "
							+ " WHERE (cd.module_id = d.demographic_no) "
							+ " 	AND cd.document_no = plr.lab_no "
							+ " 	AND plr.lab_type = 'DOC' "
							+ "	AND plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
							+ (searchProvider ? " AND plr.provider_no = '" + providerNo + "' " : "")
							+ " 	AND doc.document_no = cd.document_no  "
							+ isDocAbnormalSql
							+ dateSql
							+ " UNION "
							+ " SELECT X.id, X.lab_no as document_no, X.status, last_name, first_name, hin, sex, X.module_id,"
							+ (dateSearchType.equals("receivedCreated")?"X.contentdatetime":"X.observationdate") + ", X.lab_type as doctype, docdesc, updatedatetime "
							+ " FROM (SELECT plr.id, plr.lab_no, plr.status, plr.lab_type, cd.module_id,"
							+ (dateSearchType.equals("receivedCreated")?"contentdatetime":"observationdate") + ", docdesc, updatedatetime "
							+ " FROM ctl_document cd, providerLabRouting plr, document d "
							+ " WHERE plr.lab_type = 'DOC' "
							+ "	AND plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
							+ (searchProvider ? " AND plr.provider_no = '" + providerNo + "' " : "")
							+ " AND plr.lab_no = cd.document_no " + " AND cd.module_id = -1 "
							+ " AND d.document_no = cd.document_no " 
							+ isDocAbnormalSql.replaceAll("doc.", "d.")
							+ dateSql.replaceAll("doc.", "d.") 
							+ " ) AS X " + " LEFT JOIN demographic d "
							+ " ON d.demographic_no = -1"
							+ ") AS X "
							+ " GROUP BY document_no"
							+ " ORDER BY " + (dateSearchType.equals("receivedCreated")?"contentdatetime DESC":"observationdate DESC")
							+ (isPaged ? "	LIMIT " + (page * pageSize) + "," + pageSize : "");
				}
			}

			logger.debug(sql);

			Query q = entityManager.createNativeQuery(sql);

			List<Object[]> result = q.getResultList();
			for (Object[] r : result) {
				LabResultData lbData = new LabResultData(LabResultData.DOCUMENT);
				lbData.labType = LabResultData.DOCUMENT;

				
				lbData.segmentID = getStringValue(r[docNoLoc]);

				if (demographicNo == null && !providerNo.equals("0")) {
					lbData.acknowledgedStatus = getStringValue(r[statusLoc]);
				} else {
					lbData.acknowledgedStatus = "U";
				}

				lbData.healthNumber = "";
				lbData.patientName = "Not, Assigned";
				lbData.sex = "";

				lbData.isMatchedToPatient = r[lastNameLoc] != null;

				if (lbData.isMatchedToPatient) {
					lbData.patientName = getStringValue(r[lastNameLoc]) + ", " + getStringValue(r[firstNameLoc]);
					lbData.healthNumber = getStringValue(r[hinLoc]);
					lbData.sex = getStringValue(r[sexLoc]);
					lbData.setLabPatientId(getStringValue(r[moduleLoc]));

				}else {
					lbData.patientName = "Not, Assigned";
				}

				logger.debug("DOCUMENT " + lbData.isMatchedToPatient());
				lbData.accessionNumber = "";
				lbData.resultStatus = "N";

                DocumentDao documentDao = (DocumentDao) SpringUtils.getBean("documentDao");
                Date contentDateTime = documentDao.findActiveByDocumentNo(Integer.parseInt(getStringValue(r[docNoLoc]))).get(0).getContentdatetime();

                if(!StringUtils.isNullOrEmpty(getStringValue(r[obsDateLoc]))){
                    //if observation is not null, set that as date
                    lbData.dateTime = getStringValue(r[obsDateLoc]);
                    lbData.setDateObj(DateUtils.parseDate(getStringValue(r[obsDateLoc]), new String[]{
                            "yyyy-MM-dd"
                    }));
                }
                else if (!StringUtils.isNullOrEmpty(getStringValue(r[updateDateLoc]))){
                    //elseif updateDate is not null, set that as date
                    lbData.dateTime = getStringValue(r[updateDateLoc]);
                    lbData.setDateObj(DateUtils.parseDate(getStringValue(r[updateDateLoc]), new String[] {
                            "yyyy-MM-dd"
                    }));
                }
                else if(contentDateTime!=null){
                    //elseif contentDate is not null, set that as date
                    lbData.dateTime = dateSqlFormatter.format(contentDateTime);
                    lbData.setDateObj(contentDateTime);
                }

				String priority = "";
				if (priority != null && !priority.equals("")) {
					switch (priority.charAt(0)) {
						case 'C':
							lbData.priority = "Critical";
							break;
						case 'S':
							lbData.priority = "Stat/Urgent";
							break;
						case 'U':
							lbData.priority = "Unclaimed";
							break;
						case 'A':
							lbData.priority = "ASAP";
							break;
						case 'L':
							lbData.priority = "Alert";
							break;
						default:
							lbData.priority = "Routine";
							break;
					}
				} else {
					lbData.priority = "----";
				}

				lbData.requestingClient = "";

				lbData.reportStatus = "F";


				// the "C" is for corrected excelleris labs
				if (lbData.reportStatus != null && (lbData.reportStatus.equals("F") || lbData.reportStatus.equals("C"))) {
					lbData.finalRes = true;
				} else if (lbData.reportStatus != null && lbData.reportStatus.equals("X")){
					lbData.cancelledReport = true;
				} else{

					lbData.finalRes = false;
				}




				lbData.discipline = getStringValue(r[docTypeLoc]);
				if (lbData.discipline.trim().equals("")) {
					lbData.discipline = null;
				}

				lbData.description = getStringValue(r[descriptionLoc]);
				if( lbData.description.trim().equals("") ) {
					lbData.description = null;
				}
				
				lbData.lastUpdateDate = getStringValue(r[updateDateLoc]);				
				
				lbData.finalResultsCount = 0;//rs.getInt("final_result_count");
				labResults.add(lbData);
			}

		} catch (Exception e) {
			logger.error("exception in DOCPopulate:", e);
		}
		return labResults;
	}

	private String getStringValue(Object value) {
		return value != null ? value.toString() : null;
	}
}
