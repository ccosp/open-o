/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * This software was written for
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 *
 * Modifications made by Magenta Health in 2024.
 */
package org.oscarehr.common.dao;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.PMmodule.web.formbean.ClientListsReportFormBean;
import org.oscarehr.PMmodule.web.formbean.ClientSearchFormBean;
import org.oscarehr.caisi_integrator.ws.MatchingDemographicParameters;
import org.oscarehr.common.DemographicSearchResultTransformer;
import org.oscarehr.common.Gender;
import org.oscarehr.common.NativeSql;
import org.oscarehr.common.model.Admission;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.DemographicExt;
import org.oscarehr.event.DemographicCreateEvent;
import org.oscarehr.event.DemographicUpdateEvent;
import org.oscarehr.integration.hl7.generators.HL7A04Generator;
import org.oscarehr.util.DbConnectionFilter;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.to.model.DemographicSearchRequest;
import org.oscarehr.ws.rest.to.model.DemographicSearchRequest.SEARCHMODE;
import org.oscarehr.ws.rest.to.model.DemographicSearchRequest.SORTMODE;
import org.oscarehr.ws.rest.to.model.DemographicSearchResult;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;

import oscar.MyDateFormat;
import oscar.OscarProperties;
import oscar.util.SqlUtils;

/**
 */
public interface DemographicDao {

	public List<Integer> getMergedDemographics(Integer demographicNo);

	public Demographic getDemographic(String demographic_no);

	public List getDemographics();

	public List<Demographic> getDemographics(List<Integer> demographicIds);

	public Long getActiveDemographicCount();

	public List<Demographic> getActiveDemographics(final int offset, final int limit);

	public Demographic getDemographicById(Integer demographic_id);

	public List<Demographic> getDemographicByProvider(String providerNo);

	public List<Demographic> getDemographicByProvider(String providerNo, boolean onlyActive);

	public List<Integer> getDemographicNosByProvider(String providerNo, boolean onlyActive);

	public Demographic getDemographicByMyOscarUserName(String myOscarUserName);

	public List getActiveDemographicByProgram(int programId, Date dt, Date defdt);

	public List<Demographic> getActiveDemosByHealthCardNo(String hcn, String hcnType);

	public Set getArchiveDemographicByProgramOptimized(int programId, Date dt, Date defdt);

	public List getProgramIdByDemoNo(Integer demoNo);

	public void clear();

	public List getDemoProgram(Integer demoNo);

	public List getDemoProgramCurrent(Integer demoNo);

	public List<Integer> getDemographicIdsAdmittedIntoFacility(int facilityId);

	public List<Demographic> searchDemographic(String searchStr);

	public List<Demographic> searchDemographicByNameString(String searchString, int startIndex, int itemsToReturn);

	public List<Demographic> searchDemographicByName(String searchStr, int limit, int offset, String providerNo,
			boolean outOfDomain);

	public List<Demographic> searchDemographicByNameAndNotStatus(String searchStr, List<String> statuses, int limit,
			int offset, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByNameAndStatus(String searchStr, List<String> statuses, int limit,
			int offset, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByName(String searchStr, int limit, int offset, String orderBy,
			String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByNameAndNotStatus(String searchStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByNameAndStatus(String searchStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByNameAndStatus(String searchStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain, boolean ignoreStatuses);

	public List<Demographic> searchDemographicByNameAndStatus(String searchStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain, boolean ignoreStatuses,
			boolean ignoreMerged);

	public List<Demographic> searchMergedDemographicByName(String searchStr, int limit, int offset, String providerNo,
			boolean outOfDomain);

	public List<Demographic> searchDemographicByDOB(String dobStr, int limit, int offset, String providerNo,
			boolean outOfDomain);

	public List<Demographic> searchDemographicByDOBWithMerged(String dobStr, int limit, int offset, String providerNo,
			boolean outOfDomain);

	public List<Demographic> getByHinAndGenderAndDobAndLastName(String hin, String gender, String dob, String lastName);

	public List<Demographic> searchDemographicByDOBAndNotStatus(String dobStr, List<String> statuses, int limit,
			int offset, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByDOBAndStatus(String dobStr, List<String> statuses, int limit,
			int offset, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByDOB(String dobStr, int limit, int offset, String orderBy,
			String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByDOBAndNotStatus(String dobStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByDOBAndStatus(String dobStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByDOBAndStatus(String dobStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain, boolean ignoreStatuses);

	public List<Demographic> searchDemographicByDOBAndStatus(String dobStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain, boolean ignoreStatuses,
			boolean ignoreMerged);

	public List<Demographic> searchMergedDemographicByDOB(String dobStr, int limit, int offset, String providerNo,
			boolean outOfDomain);

	public List<Demographic> searchDemographicByPhone(String phoneStr, int limit, int offset, String providerNo,
			boolean outOfDomain);

	public List<Demographic> searchDemographicByPhoneAndNotStatus(String phoneStr, List<String> statuses, int limit,
			int offset, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByPhoneAndStatus(String phoneStr, List<String> statuses, int limit,
			int offset, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByPhone(String phoneStr, int limit, int offset, String orderBy,
			String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByPhoneAndNotStatus(String phoneStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByPhoneAndStatus(String phoneStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByPhoneAndStatus(String phoneStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain, boolean ignoreStatuses);

	public List<Demographic> searchDemographicByPhoneAndStatus(String phoneStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain, boolean ignoreStatuses,
			boolean ignoreMerged);

	public List<Demographic> searchMergedDemographicByPhone(String phoneStr, int limit, int offset, String providerNo,
			boolean outOfDomain);

	public List<Demographic> searchDemographicByHIN(String hinStr);

	public List<Demographic> searchDemographicByHIN(String hinStr, int limit, int offset, String providerNo,
			boolean outOfDomain);

	public List<Demographic> searchDemographicByHINAndNotStatus(String hinStr, List<String> statuses, int limit,
			int offset, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByHINAndStatus(String hinStr, List<String> statuses, int limit,
			int offset, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByHIN(String hinStr, int limit, int offset, String orderBy,
			String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByHINAndNotStatus(String hinStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByHINAndStatus(String hinStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByHINAndStatus(String hinStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain, boolean ignoreStatuses);

	public List<Demographic> searchDemographicByHINAndStatus(String hinStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain, boolean ignoreStatuses,
			boolean ignoreMerged);

	public List<Demographic> findByAttributes(
			String hin,
			String firstName,
			String lastName,
			Gender gender,
			Calendar dateOfBirth,
			String city,
			String province,
			String phone,
			String email,
			String alias,
			int startIndex,
			int itemsToReturn);

	public List<Demographic> findByAttributes(
			String hin,
			String firstName,
			String lastName,
			Gender gender,
			Calendar dateOfBirth,
			String city,
			String province,
			String phone,
			String email,
			String alias,
			int startIndex,
			int itemsToReturn,
			boolean orderByName);

	public List<Demographic> searchMergedDemographicByHIN(String hinStr, int limit, int offset, String providerNo,
			boolean outOfDomain);

	public List<Demographic> searchDemographicByAddress(String addressStr, int limit, int offset, String providerNo,
			boolean outOfDomain);

	public List<Demographic> searchDemographicByAddressAndStatus(String addressStr, List<String> statuses, int limit,
			int offset, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByAddressAndNotStatus(String addressStr, List<String> statuses, int limit,
			int offset, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByAddress(String addressStr, int limit, int offset, String orderBy,
			String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByAddressAndStatus(String addressStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByAddressAndNotStatus(String addressStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByAddressAndStatus(String addressStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain, boolean ignoreStatuses);

	public List<Demographic> searchDemographicByAddressAndStatus(String addressStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain, boolean ignoreStatuses,
			boolean ignoreMerged);

	public List<Demographic> searchDemographicByExtKeyAndValueLike(DemographicExt.DemographicProperty key, String value,
			int limit, int offset, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByExtKeyAndValueLikeAndNotStatus(DemographicExt.DemographicProperty key,
			String value, List<String> statuses, int limit, int offset, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByExtKeyAndValueLikeAndStatus(DemographicExt.DemographicProperty key,
			String value, List<String> statuses, int limit, int offset, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByExtKeyAndValueLike(DemographicExt.DemographicProperty key, String value,
			int limit, int offset, String orderBy, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByExtKeyAndValueLikeWithMerged(DemographicExt.DemographicProperty key,
			String value, int limit, int offset, String orderBy, String providerNo, boolean outOfDomain);

	public List<Demographic> searchDemographicByExtKeyAndValueLikeAndNotStatus(DemographicExt.DemographicProperty key,
			String value, List<String> statuses, int limit, int offset, String orderBy, String providerNo,
			boolean outOfDomain);

	public List<Demographic> searchDemographicByExtKeyAndValueLikeAndStatus(DemographicExt.DemographicProperty key,
			String value, List<String> statuses, int limit, int offset, String orderBy, String providerNo,
			boolean outOfDomain);

	public List<Demographic> searchDemographicByExtKeyAndValueLikeAndStatus(DemographicExt.DemographicProperty key,
			String value, List<String> statuses, int limit, int offset, String orderBy, String providerNo,
			boolean outOfDomain, boolean ignoreStatuses);

	public List<Demographic> searchDemographicByExtKeyAndValueLikeAndStatus(DemographicExt.DemographicProperty key,
			String value, List<String> statuses, int limit, int offset, String orderBy, String providerNo,
			boolean outOfDomain, boolean ignoreStatuses, boolean ignoreMerged);

	public List<Demographic> searchMergedDemographicByAddress(String addressStr, int limit, int offset,
			String providerNo, boolean outOfDomain);

	public List<Demographic> findDemographicByChartNo(String chartNoStr, int limit, int offset, String providerNo,
			boolean outOfDomain);

	public List<Demographic> findDemographicByChartNoAndStatus(String chartNoStr, List<String> statuses, int limit,
			int offset, String providerNo, boolean outOfDomain);

	public List<Demographic> findDemographicByChartNoAndNotStatus(String chartNoStr, List<String> statuses, int limit,
			int offset, String providerNo, boolean outOfDomain);

	public List<Demographic> findDemographicByChartNo(String chartNoStr, int limit, int offset, String orderBy,
			String providerNo, boolean outOfDomain);

	public List<Demographic> findDemographicByChartNoAndStatus(String chartNoStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain);

	public List<Demographic> findDemographicByChartNoAndNotStatus(String chartNoStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain);

	public List<Demographic> findDemographicByChartNoAndStatus(String chartNoStr, List<String> statuses, int limit,
			int offset, String orderBy, String providerNo, boolean outOfDomain, boolean ignoreStatuses);

	public List<Demographic> findDemographicByDemographicNo(String demographicNoStr, int limit, int offset,
			String providerNo, boolean outOfDomain);

	public List<Demographic> findDemographicByDemographicNoAndStatus(String demographicNoStr, List<String> statuses,
			int limit, int offset, String providerNo, boolean outOfDomain);

	public List<Demographic> findDemographicByDemographicNoAndNotStatus(String demographicNoStr, List<String> statuses,
			int limit, int offset, String providerNo, boolean outOfDomain);

	public List<Demographic> findDemographicByDemographicNo(String demographicNoStr, int limit, int offset,
			String orderBy, String providerNo, boolean outOfDomain);

	public List<Demographic> findDemographicByDemographicNoAndStatus(String demographicNoStr, List<String> statuses,
			int limit, int offset, String orderBy, String providerNo, boolean outOfDomain);

	public List<Demographic> findDemographicByDemographicNoAndNotStatus(String demographicNoStr, List<String> statuses,
			int limit, int offset, String orderBy, String providerNo, boolean outOfDomain);

	public List<Demographic> findDemographicByDemographicNoAndStatus(String demographicNoStr, List<String> statuses,
			int limit, int offset, String orderBy, String providerNo, boolean outOfDomain, boolean ignoreStatuses);

	public void save(Demographic demographic);

	public String getOrderField(String orderBy, boolean nativeQuery);

	public String getOrderField(String orderBy);

	public List<Integer> getDemographicIdsAlteredSinceTime(Date value);

	public List<Integer> getDemographicIdsOpenedChartSinceTime(String value);

	public List<String> getRosterStatuses();

	public List<String> getAllRosterStatuses();

	public List<String> getAllPatientStatuses();

	public List<String> search_ptstatus();

	public List<String> getAllProviderNumbers();

	public boolean clientExists(Integer demographicNo);

	public boolean clientExistsThenEvict(Integer demographicNo);

	public Demographic getClientByDemographicNo(Integer demographicNo);

	public List<Demographic> getClients();

	public List<Demographic> search(ClientSearchFormBean bean, boolean returnOptinsOnly, boolean excludeMerged);

	public List<Demographic> search(ClientSearchFormBean bean);

	public void saveClient(Demographic client);

	// public Map<String, ClientListsReportResults>
	// findByReportCriteria(ClientListsReportFormBean x);

	public List<Demographic> getClientsByChartNo(String chartNo);

	public List<Demographic> getClientsByHealthCard(String num, String type);

	public List<Demographic> searchByHealthCard(String hin, String hcType);

	public List<Demographic> searchByHealthCard(String hin);

	public Demographic getDemographicByNamePhoneEmail(String firstName, String lastName, String hPhone, String wPhone,
			String email);

	public List<Demographic> getDemographicWithLastFirstDOB(String lastname, String firstname, String year_of_birth,
			String month_of_birth, String date_of_birth);

	public List<Demographic> getDemographicWithLastFirstDOBExact(String lastname, String firstname,
			String year_of_birth, String month_of_birth, String date_of_birth);

	public List<Demographic> getDemographicsByHealthNum(String hin);

	public List<Integer> getActiveDemographicIds();

	public List<Integer> getDemographicIds();

	public List<Demographic> getDemographicWithGreaterThanYearOfBirth(int yearOfBirth);

	public List<Demographic> search_catchment(String rosterStatus, int offset, int limit);

	public List<Demographic> findByField(String fieldName, Object fieldValue, String orderBy, int offset);

	// public List<Demographic> findByCriterion(DemographicCriterion c);

	public List<Object[]> findDemographicsForFluReport(String providerNo);

	public List<Integer> getActiveDemographicIdsOlderThan(int age);

	public void setApplicationEventPublisher(ApplicationEventPublisher publisher);

	public List<Integer> getDemographicIdsAddedSince(Date value);

	public List<Demographic> getDemographicByRosterStatus(String rosterStatus, String patientStatus);

	public Integer searchPatientCount(LoggedInInfo loggedInInfo, DemographicSearchRequest searchRequest);

	public List<DemographicSearchResult> searchPatients(LoggedInInfo loggedInInfo,
			DemographicSearchRequest searchRequest, int startIndex, int itemsToReturn);

	public List<Integer> getDemographicIdsWithMyOscarAccounts(Integer startDemographicIdExclusive, int itemsToReturn);

	public List<Integer> getMissingExtKey(String keyName);

	public List<Integer> getBORNKidsMissingExtKey(String keyName);

	public List<Demographic> getActiveDemographicAfter(Date afterDatetimeExclusive);

	public List<Demographic> findByLastNameAndDob(String lastName, Calendar dateOfBirth);

	public List<Demographic> findByFirstAndLastName(String name, String start, String end);

	public List<Demographic> findByDob(Calendar dateOfBirth, String start, int numToReturn);

	public List<Demographic> findByPhone(String phone, String start, int numToReturn);

	public List<Demographic> findByHin(String hin, String start, int numToReturn);

}