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


package oscar.oscarMDS.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.common.dao.SystemPreferencesDao;
import org.oscarehr.common.model.SystemPreferences;
import org.oscarehr.util.DbConnectionFilter;
import org.oscarehr.util.SpringUtils;

public class CategoryData {

	private final SystemPreferencesDao systemPreferencesDao = SpringUtils.getBean(SystemPreferencesDao.class);

	private int totalDocs = 0;
	private int totalLabs = 0;
	private int unmatchedLabs = 0;
	private int unmatchedDocs = 0;
    private int totalNumDocs = 0;
    private int abnormalCount = 0;
    private int normalCount = 0;
    private int unmatchedHRMCount = 0;
    private int matchedHRMCount = 0;

    private HashMap<Integer,PatientInfo> patients;

	public int getTotalDocs() {
		return totalDocs;
	}

	public int getTotalLabs() {
		return totalLabs;
	}

	public int getUnmatchedLabs() {
		return unmatchedLabs;
	}

	public int getUnmatchedDocs() {
		return unmatchedDocs;
	}

	public int getTotalNumDocs() {
		return totalNumDocs;
	}

	public int getAbnormalCount() {
		return abnormalCount;
	}

	public int getNormalCount() {
		return normalCount;
	}

	public HashMap<Integer, PatientInfo> getPatients() {
		return patients;
	}

	private String patientLastName;
	private String searchProviderNo;
	private String status;
	private String patientFirstName;
	private String patientHealthNumber;
	private boolean patientSearch;
	private boolean providerSearch;
	private String documentDateSql = "";
	private String documentAbnormalSql = "";
	private String documentJoinSql = "";
	private String labDateSql = "";
	private String labAbnormalSql = "";
	private String hrmDateSql = "";
	private String hrmProviderSql = "";

	public CategoryData(String patientLastName, String patientFirstName, String patientHealthNumber, boolean patientSearch,
					    boolean providerSearch, String searchProviderNo, String status, String abnormalStatus, 
						String startDate, String endDate)  {

		this.patientLastName = patientLastName;
		this.searchProviderNo = searchProviderNo;
		this.status = status;
		this.patientFirstName = patientFirstName;
		this.patientHealthNumber = patientHealthNumber;
		this.patientSearch = patientSearch;
		this.providerSearch = providerSearch;

    	totalDocs = 0;
		totalLabs = 0;
		unmatchedLabs = 0;
	    unmatchedDocs = 0;
	    totalNumDocs = 0;
	    abnormalCount = 0;
	    normalCount = 0;

        patients = new HashMap<Integer,PatientInfo>();

	}
	public void populateCountsAndPatients() throws SQLException {

		// Retrieving documents and labs.
		totalDocs += getDocumentCountForPatientSearch();
        totalLabs += getLabCountForPatientSearch();

        // If this is not a patient search, then we need to find the unmatched documents.
        if (!patientSearch) {
            unmatchedDocs += getDocumentCountForUnmatched();
            unmatchedLabs += getLabCountForUnmatched();
            totalDocs += unmatchedDocs;
            totalLabs += unmatchedLabs;
        }

        // The total overall items is the sum of docs and labs.
        totalNumDocs = totalDocs + totalLabs;

        // Retrieving abnormal labs.
        abnormalCount = getAbnormalCount(true);

        // Cheaper to subtract abnormal from total to find the number of normal docs.
        normalCount = totalNumDocs - abnormalCount;
	}

	public String getDateSearchType() {
		SystemPreferences systemPreferences = systemPreferencesDao.findPreferenceByName(SystemPreferences.LAB_DISPLAY_PREFERENCE_KEYS.inboxDateSearchType);
		if (systemPreferences != null)
		{
			if (systemPreferences.getValue() != null && !systemPreferences.getValue().isEmpty())
			{
				return systemPreferences.getValue();
			}
		}

		return "serviceObservation";
	}

	public int getLabCountForUnmatched()
			throws SQLException {
		String dateSearchType = getDateSearchType();
		String sql = " SELECT HIGH_PRIORITY COUNT(DISTINCT accessionNum) as count "
				+ " FROM providerLabRouting plr "
				+ " LEFT JOIN patientLabRouting plr2 ON plr.lab_no = plr2.lab_no AND plr2.lab_type = 'HL7'"
				+ " RIGHT JOIN hl7TextInfo info ON plr.lab_no = info.lab_no"
				+ (dateSearchType.equals("receivedCreated")?" RIGHT JOIN hl7TextMessage message ON plr.lab_no = message.lab_id":"")
				+ " WHERE plr.lab_type = 'HL7' "
				+ (providerSearch ? " AND plr.provider_no = '"+searchProviderNo+"' " : "")
				+ " AND plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
				+ labAbnormalSql
				+ labDateSql
				+ " AND (plr2.demographic_no IS NULL"
				+ " OR plr2.demographic_no = '0')";




		Connection c  = DbConnectionFilter.getThreadLocalDbConnection();
		PreparedStatement ps = c.prepareStatement(sql);
		ResultSet rs= ps.executeQuery(sql);

		return (rs.next() ? rs.getInt("count") : 0);
	}

	public int getAbnormalCount(boolean isAbnormal) throws SQLException {
		ResultSet rs;
		String sql;
		if (patientSearch) {
        	sql = " SELECT HIGH_PRIORITY COUNT(1) as count "
        		+ " FROM patientLabRouting cd, demographic d, providerLabRouting plr, hl7TextInfo info "
        		+ " WHERE d.last_name" + (StringUtils.isEmpty(patientLastName) ? " IS NOT NULL " : " like '%"+patientLastName+"%'  ")
        		+ " 	AND d.first_name" + (StringUtils.isEmpty(patientFirstName) ? " IS NOT NULL " : " like '%"+patientFirstName+"%' ")
        		+ " 	AND d.hin" + (StringUtils.isEmpty(patientHealthNumber) ? " IS NOT NULL " : " like '%"+patientHealthNumber+"%' ")
        		+ " 	AND plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
        		+ (providerSearch ? "AND plr.provider_no = '"+searchProviderNo+"' " : "")
        		+ " 	AND plr.lab_type = 'HL7' "
        		+ " 	AND cd.lab_type = 'HL7' "
        		+ " 	AND cd.lab_no = plr.lab_no "
        		+ " 	AND cd.demographic_no = d.demographic_no "
        		+ " 	AND info.lab_no = plr.lab_no "
        		+ " 	AND result_status "+(isAbnormal ? "" : "!")+"= 'A' ";
        }
        else if (providerSearch || !"".equals(status)){ // providerSearch
        	sql = "SELECT HIGH_PRIORITY COUNT(1) as count "
				+ " FROM providerLabRouting plr, hl7TextInfo info "
				+ " WHERE plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
				+ (providerSearch ? " AND plr.provider_no = '"+searchProviderNo+"' " : " ")
				+ " AND plr.lab_type = 'HL7'  "
				+ " AND info.lab_no = plr.lab_no"
				+ " AND result_status "+(isAbnormal ? "" : "!")+"= 'A' ";
        }
        else {
        	sql = " SELECT HIGH_PRIORITY COUNT(1) as count "
            	+ " FROM hl7TextInfo info "
            	+ " WHERE result_status "+(isAbnormal ? "" : "!")+"= 'A' ";
        }
		Connection c  = DbConnectionFilter.getThreadLocalDbConnection();
		PreparedStatement ps = c.prepareStatement(sql);
		rs= ps.executeQuery(sql);
        return (rs.next() ? rs.getInt("count") : 0);
	}

	public int getDocumentCountForUnmatched()
			throws SQLException {
		String sql = " SELECT HIGH_PRIORITY COUNT(1) as count "
					+ " FROM ctl_document cd " 
					+ "LEFT JOIN providerLabRouting plr ON plr.lab_no = cd.document_no"
					+ documentJoinSql
					+ " WHERE plr.lab_type = 'DOC' " 
					+ " AND plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
					+ (providerSearch ? " AND plr.provider_no ='"+searchProviderNo+"' " : "")
					+ " AND 	cd.module_id = -1 "
					+ documentAbnormalSql
					+ documentDateSql;
		Connection c  = DbConnectionFilter.getThreadLocalDbConnection();
		PreparedStatement ps = c.prepareStatement(sql);
		ResultSet rs= ps.executeQuery(sql);
		return (rs.next() ? rs.getInt("count") : 0);
	}

	public int getLabCountForPatientSearch() throws SQLException {
		PatientInfo info;
		String dateSearchType = getDateSearchType();
		String sql = " SELECT HIGH_PRIORITY d.demographic_no, d.last_name, d.first_name, COUNT(*) as count "
        	+ " FROM patientLabRouting cd" 
			+ " LEFT JOIN demographic d ON cd.demographic_no = d.demographic_no" 
			+ " LEFT JOIN providerLabRouting plr ON cd.lab_no = plr.lab_no"
			+ " LEFT JOIN hl7TextInfo info ON cd.lab_no = info.lab_no"
			+ (dateSearchType.equals("receivedCreated")?" LEFT JOIN hl7TextMessage message ON cd.lab_no = message.lab_id":"")
        	+ " WHERE   d.last_name" + (StringUtils.isEmpty(patientLastName) ? " IS NOT NULL " : "  like '%"+patientLastName+"%' ")
        	+ " 	AND d.first_name" + (StringUtils.isEmpty(patientFirstName) ? " IS NOT NULL " : " like '%"+patientFirstName+"%' ")
        	+ " 	AND d.hin" + (StringUtils.isEmpty(patientHealthNumber) ? " IS NOT NULL " : " like '%"+patientHealthNumber+"%' ")
        	+ " 	AND plr.lab_type = 'HL7' "
        	+ " 	AND cd.lab_type = 'HL7' "
        	+ " 	AND plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
			+ (dateSearchType.equals("receivedCreated")?" AND message.lab_id IS NOT NULL ":" AND info.lab_no IS NOT NULL ")
        	+ (providerSearch ? " AND plr.provider_no = '"+searchProviderNo+"' " : "")
			+ labAbnormalSql
			+ labDateSql
        	+ " GROUP BY demographic_no, info.accessionNum ";

		Connection c  = DbConnectionFilter.getThreadLocalDbConnection();
		PreparedStatement ps = c.prepareStatement(sql);
		ResultSet rs= ps.executeQuery(sql);
        int count = 0;
        while(rs.next()){
        	int id = rs.getInt("demographic_no");
        	// Updating patient info if it already exists.
        	if (patients.containsKey(id)) {
        		info = patients.get(id);
        		info.setLabCount(rs.getInt("count"));
        	}
        	// Otherwise adding a new patient record.
        	else {
        		info = new PatientInfo(id, rs.getString("first_name"), rs.getString("last_name"));
        		info.setLabCount(rs.getInt("count"));
        		patients.put(info.id, info);
        	}
        	count += info.getLabCount();
        }
        return count;
	}

	public int getLabCountForDemographic(String demographicNo) throws SQLException {
		String sql = " SELECT HIGH_PRIORITY d.demographic_no, last_name, first_name, COUNT(1) as count "
        	+ " FROM patientLabRouting cd,  demographic d, providerLabRouting plr "
        	+ " WHERE   d.demographic_no = " + demographicNo
        	+ " 	AND cd.demographic_no = d.demographic_no "
        	+ " 	AND cd.lab_no = plr.lab_no "
        	+ " 	AND plr.lab_type = 'HL7' "
        	+ " 	AND cd.lab_type = 'HL7' "
        	+ " 	AND plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
        	+ (providerSearch ? "AND plr.provider_no = '"+searchProviderNo+"' " : "")
        	+ " GROUP BY demographic_no ";

		Connection c  = DbConnectionFilter.getThreadLocalDbConnection();
		PreparedStatement ps = c.prepareStatement(sql);
		ResultSet rs= ps.executeQuery(sql);
        return (rs.next() ? rs.getInt("count") : 0);
	}

	/*
	 * This will return the total documents found for this patients.
	 * it will also add to the patients map (demographicNo, PatientInfo) with a document count for the patient.
	 * 
	 */
	public int getDocumentCountForPatientSearch() throws SQLException {
		PatientInfo info;
		
		String sql = " SELECT HIGH_PRIORITY demographic_no, last_name, first_name, COUNT( distinct cd.document_no) as count "
					+ " FROM ctl_document cd "
					+ "LEFT JOIN demographic d  ON cd.module_id = d.demographic_no "
					+ "LEFT JOIN providerLabRouting plr ON cd.document_no = plr.lab_no "
					+ documentJoinSql
					+ " WHERE   d.last_name" + (StringUtils.isEmpty(patientLastName) ? " IS NOT NULL " : " like '%"+patientLastName+"%'  ")
					+ " 	AND d.hin" + (StringUtils.isEmpty(patientHealthNumber) ? " IS NOT NULL " : " like '%"+patientHealthNumber+"%' ")
					+ " 	AND d.first_name" + (StringUtils.isEmpty(patientFirstName) ? " IS NOT NULL " : " like '%"+patientFirstName+"%' ")
					+ " 	AND plr.lab_type = 'DOC' "
					+ " 	AND plr.status " + ("".equals(status) ? " IS NOT NULL " : " = '"+status+"' ")
					+ (providerSearch ? "AND plr.provider_no = '"+searchProviderNo+"' " : "")
					+ documentAbnormalSql
					+ documentDateSql
					+ " GROUP BY demographic_no ";
		Connection c  = DbConnectionFilter.getThreadLocalDbConnection();
		PreparedStatement ps = c.prepareStatement(sql);
		ResultSet rs= ps.executeQuery(sql);
        int count = 0;
        while(rs.next()){
        	info = new PatientInfo(rs.getInt("demographic_no"), rs.getString("first_name"), rs.getString("last_name"));
        	info.setDocCount(rs.getInt("count"));
        	patients.put(info.id, info);
        	count += info.getDocCount();
        }
        return count;
	}


	public Long getCategoryHash() {
		return Long.valueOf("" + (int)'A' + totalNumDocs)
			 + Long.valueOf("" + (int)'D' + totalDocs)
			 + Long.valueOf("" + (int)'L' + totalLabs);
	}
}
