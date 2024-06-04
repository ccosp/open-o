/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
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
 *
 * Modifications made by Magenta Health in 2024.
 */
package org.oscarehr.PMmodule.dao;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.oscarehr.PMmodule.model.Vacancy;
import org.oscarehr.PMmodule.service.VacancyTemplateManager;
import org.oscarehr.PMmodule.wlmatch.CriteriaBO;
import org.oscarehr.PMmodule.wlmatch.CriteriasBO;
import org.oscarehr.PMmodule.wlmatch.MatchBO;
import org.oscarehr.PMmodule.wlmatch.VacancyDisplayBO;
import org.oscarehr.common.model.EFormData;
import org.oscarehr.match.client.ClientData;
import org.oscarehr.match.vacancy.VacancyData;
import org.oscarehr.match.vacancy.VacancyTemplateData;
import org.springframework.stereotype.Repository;

public interface WaitlistDao {
	static final String QUERY_ALL_CLIENT_DATA = "SELECT DISTINCT demographic_no, fdid, var_name, var_value "
            + "FROM eform_values LEFT JOIN client_referral cr ON cr.client_id=demographic_no, "
            + "(SELECT demographic_no AS dmb,MAX(fdid) AS ffdid FROM eform_values GROUP BY demographic_no) xyz "
            + "WHERE cr.referral_id IS NULL AND " + "demographic_no= xyz.dmb AND fdid=xyz.ffdid";

    static final String QUERY_ALL_CLIENT_DATA_BY_PROGRAMID = "SELECT DISTINCT ef.demographic_no, ef.fdid, ef.var_name, ef.var_value "
            + "FROM eform_values ef LEFT JOIN client_referral cr ON cr.client_id=ef.demographic_no"
            + " where cr.program_id=?1 and ef.var_name in ('age-years','gender','current-housing','preferred-language','location-preferences','referrer-contact-province','contact-province','Age category','prepared-live-toronto','bed_community_program_id','has-mental-illness-primary','current-legal-involvements')"
            + " and LENGTH(ef.var_value)>0 and not exists (select * from eform_values where demographic_no=ef.demographic_no and var_name=ef.var_name and fdid>ef.fdid)";

    static final String QUERY_GET_CLIENT_DATA = "SELECT DISTINCT ef.demographic_no, ef.fdid, ef.var_name, ef.var_value "
            + "FROM eform_values ef WHERE ef.demographic_no= ?1 and "
            + " ef.var_name in ('age-years','gender','current-housing','preferred-language','location-preferences','referrer-contact-province','contact-province','Age category','prepared-live-toronto','bed_community_program_id','has-mental-illness-primary','current-legal-involvements')"
            + "and LENGTH(ef.var_value)>0 AND not exists (select * from eform_values where ef.demographic_no=demographic_no and var_name=ef.var_name and fdid>ef.fdid)";

	static final String QUERY_VACANCY_DATA = "SELECT v.id, v.wlProgramId, ct.field_name,ct.field_type,"
            + "c.criteria_value,cso.option_value,c.range_start_value,c.range_end_value "
            + "FROM criteria c JOIN criteria_type ct ON c.CRITERIA_TYPE_ID=ct.CRITERIA_TYPE_ID "
            + "LEFT JOIN criteria_selection_option cso ON cso.CRITERIA_ID=c.CRITERIA_ID "
            + "JOIN vacancy v ON v.id=c.VACANCY_ID WHERE v.id=?1";

    static final String QUERY_VACANCY_DATA_BY_PROGRAMID = "SELECT v.id, v.wlProgramId, ct.field_name,ct.field_type,"
            + "c.criteria_value,cso.option_value,c.range_start_value,c.range_end_value "
            + "FROM criteria c JOIN criteria_type ct ON c.CRITERIA_TYPE_ID=ct.CRITERIA_TYPE_ID "
            + "LEFT JOIN criteria_selection_option cso ON cso.CRITERIA_ID=c.CRITERIA_ID "
            + "JOIN vacancy v ON v.id=c.VACANCY_ID WHERE v.id=?1 and v.wlProgramId=?2";

    static final String field_type_multiple = "select_multiple";
    static final String field_type_range = "select_one_range";

	public List<MatchBO> getClientMatches(int vacancyId);

	public List<MatchBO> getClientMatchesWithMinPercentage(int vacancyId, double percentage);

	public Collection<EFormData> searchForMatchingEforms(CriteriasBO crits);

	public List<VacancyDisplayBO> listDisplayVacanciesForWaitListProgram(int programID);

	public List<VacancyDisplayBO> listDisplayVacanciesForAllWaitListPrograms();

	public List<VacancyDisplayBO> getDisplayVacanciesForAgencyProgram(int programID);

	public VacancyDisplayBO getDisplayVacancy(int vacancyID);

	public void loadStats(VacancyDisplayBO bo);

	public Integer getProgramIdByVacancyId(int vacancyId);

	public List<VacancyDisplayBO> listNoOfVacanciesForWaitListProgram();

	public List<VacancyDisplayBO> listVacanciesForWaitListProgram();

	public List<ClientData> getAllClientsData();

	public List<ClientData> getAllClientsDataByProgramId(int wlProgramId);

	public ClientData getClientData(int clientId);

	// private static final String field_type_one = "select_one";
	// private static final String field_type_number = "number";

	public VacancyData loadVacancyData(final int vacancyId);

	public VacancyData loadVacancyData(final int vacancyId, final int wlProgramId);

}
