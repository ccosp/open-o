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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Query;

import org.oscarehr.PMmodule.model.Program;
import org.oscarehr.common.NativeSql;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.AppointmentArchive;
import org.oscarehr.common.model.Facility;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import org.apache.commons.lang.StringUtils;
import oscar.util.UtilDateUtilities;

@Repository
@SuppressWarnings("unchecked")
public class OscarAppointmentDaoImpl extends AbstractDaoImpl<Appointment> implements OscarAppointmentDao {

    public OscarAppointmentDaoImpl() {
        super(Appointment.class);
    }

    @Override
    public boolean checkForConflict(Appointment appt) {
        String sb = "select a from Appointment a where a.appointmentDate = ? and a.startTime >= ? and a.endTime <= ? and a.providerNo = ? and a.status != 'N' and a.status != 'C'";

        Query query = entityManager.createQuery(sb);

        query.setParameter(0, appt.getAppointmentDate());
        query.setParameter(1, appt.getStartTime());
        query.setParameter(2, appt.getEndTime());
        query.setParameter(3, appt.getProviderNo());

        List<Facility> results = query.getResultList();

        if (!results.isEmpty())
            return true;

        return false;
    }

    @Override
    public List<Appointment> getAppointmentHistory(Integer demographicNo, Integer offset, Integer limit) {
        String sql = "select a from Appointment a where a.demographicNo=? and a.status not in ('D') order by a.appointmentDate DESC, a.startTime DESC";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, demographicNo);
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<Appointment> result = query.getResultList();

        return result;
    }

    @Override
    public List<Appointment> getAllAppointmentHistory(Integer demographicNo, Integer offset, Integer limit) {
        String sql = "select a from Appointment a where a.demographicNo=? order by a.appointmentDate DESC, a.startTime DESC";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, demographicNo);
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<Appointment> result = query.getResultList();

        return result;
    }

    @Override
    public List<AppointmentArchive> getDeletedAppointmentHistory(Integer demographicNo, Integer offset, Integer limit) {

        List<Object> result = new ArrayList<Object>();

        String sql2 = "select a from AppointmentArchive a where a.demographicNo=? order by a.appointmentDate DESC, a.startTime DESC, id desc";
        Query query2 = entityManager.createQuery(sql2);
        query2.setParameter(0, demographicNo);
        query2.setFirstResult(offset);
        query2.setMaxResults(limit);

        List<AppointmentArchive> results = query2.getResultList();

        return results;
    }

    @Override
    public List<Appointment> getAppointmentHistory(Integer demographicNo) {
        String sql = "select a from Appointment a where a.demographicNo=? and a.status not in ('C','D') order by a.appointmentDate DESC, a.startTime DESC";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, demographicNo);

        List<Appointment> rs = query.getResultList();

        return rs;
    }

    @Override
    public void archiveAppointment(int appointmentNo) {
        Appointment appointment = this.find(appointmentNo);
        if (appointment != null) {
            AppointmentArchive apptArchive = new AppointmentArchive();
            String[] ignores = { "id" };
            BeanUtils.copyProperties(appointment, apptArchive, ignores);
            apptArchive.setAppointmentNo(appointment.getId());
            entityManager.persist(apptArchive);
        }
    }

    @Override
    public List<Appointment> getAllByDemographicNo(Integer demographicNo) {
        String sql = "SELECT a FROM Appointment a WHERE a.demographicNo = " + demographicNo + " ORDER BY a.id";
        Query query = entityManager.createQuery(sql);

        List<Appointment> rs = query.getResultList();

        return rs;
    }

    /**
     * @return results ordered by lastUpdateDate
     */
    @Override
    public List<Appointment> findByUpdateDate(Date updatedAfterThisDateExclusive, int itemsToReturn) {
        String sqlCommand = "select x from " + modelClass.getSimpleName()
                + " x where x.updateDateTime>?1 order by x.updateDateTime";

        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, updatedAfterThisDateExclusive);
        setLimit(query, itemsToReturn);

        @SuppressWarnings("unchecked")
        List<Appointment> results = query.getResultList();
        return (results);
    }

    /**
     * @return results ordered by lastUpdateDate
     */
    @Override
    public List<Appointment> findByDemographicIdUpdateDate(Integer demographicId, Date updatedAfterThisDateExclusive) {
        String sqlCommand = "select x from " + modelClass.getSimpleName()
                + " x where x.demographicNo=?1 and x.updateDateTime>?2 order by x.updateDateTime";

        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, demographicId);
        query.setParameter(2, updatedAfterThisDateExclusive);

        @SuppressWarnings("unchecked")
        List<Appointment> results = query.getResultList();
        return (results);
    }

    @Override
    public List<Appointment> getAllByDemographicNoSince(Integer demographicNo, Date lastUpdateDate) {
        String sql = "SELECT a FROM Appointment a WHERE a.demographicNo = " + demographicNo
                + " and a.updateDateTime > ? ORDER BY a.id";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, lastUpdateDate);

        List<Appointment> rs = query.getResultList();
        return rs;
    }

    @Override
    public List<Integer> getAllDemographicNoSince(Date lastUpdateDate, List<Program> programs) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Program p : programs) {
            if (i++ > 0)
                sb.append(",");
            sb.append(p.getId());
        }
        String sql = "select a.demographicNo FROM Appointment a WHERE a.updateDateTime > ? and program_id in ("
                + sb.toString() + ") ORDER BY a.id";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, lastUpdateDate);

        List<Integer> rs = query.getResultList();
        return rs;
    }

    @Override
    public List<Appointment> findByDateRange(Date startTime, Date endTime) {
        String sql = "SELECT a FROM Appointment a WHERE a.appointmentDate >=? and a.appointmentDate < ?";

        Query query = entityManager.createQuery(sql);
        query.setParameter(0, startTime);
        query.setParameter(1, endTime);

        List<Appointment> rs = query.getResultList();

        return rs;
    }

    @Override
    public List<Appointment> findByDateRangeAndProvider(Date startTime, Date endTime, String providerNo) {
        String sql = "SELECT a FROM Appointment a WHERE a.appointmentDate >=? and a.appointmentDate < ? and providerNo = ?";

        Query query = entityManager.createQuery(sql);
        query.setParameter(0, startTime);
        query.setParameter(1, endTime);
        query.setParameter(2, providerNo);

        List<Appointment> rs = query.getResultList();

        return rs;
    }

    @Override
    public List<Appointment> getByProviderAndDay(Date date, String providerNo) {
        String sql = "SELECT a FROM Appointment a WHERE a.providerNo=? and a.appointmentDate = ? and a.status != 'N' and a.status != 'C' order by a.startTime";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, providerNo);
        query.setParameter(1, date);

        List<Appointment> rs = query.getResultList();

        return rs;
    }

    @Override
    public List<Appointment> getByDemoNoAndDay(int demoNo, Date date) {
        Query q = entityManager.createQuery(
                "select a from Appointment a where a.demographicNo=?1 and a.appointmentDate = ?2 and a.status !='D'");
        q.setParameter(1, demoNo);
        q.setParameter(2, new Date());
        return q.getResultList();
    }

    @Override
    public List<Appointment> findByProviderAndDayandNotStatuses(String providerNo, Date date, String[] notThisStatus) {
        String sql = "SELECT a FROM Appointment a WHERE a.providerNo=?1 and a.appointmentDate = ?2 and a.status NOT IN ( ?3 )";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, providerNo);
        query.setParameter(2, date);
        query.setParameter(3, Arrays.asList(notThisStatus));

        List<Appointment> results = query.getResultList();
        return results;
    }

    @Override
    public List<Appointment> findByProviderAndDayandNotStatus(String providerNo, Date date, String notThisStatus) {
        String sql = "SELECT a FROM Appointment a WHERE a.providerNo=?1 and a.appointmentDate = ?2 and a.status != ?3 ORDER BY a.appointmentDate, a.startTime";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, providerNo);
        query.setParameter(2, date);
        query.setParameter(3, notThisStatus);

        List<Appointment> results = query.getResultList();
        return results;
    }

    @Override
    public List<Appointment> findByProviderDayAndStatus(String providerNo, Date date, String status) {
        String sql = "SELECT a FROM Appointment a WHERE a.providerNo=? and a.appointmentDate = ? and a.status=?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, providerNo);
        query.setParameter(1, date);
        query.setParameter(2, status);

        List<Appointment> rs = query.getResultList();

        return rs;
    }

    @Override
    public List<Appointment> findByDayAndStatus(Date date, String status) {
        String sql = "SELECT a FROM Appointment a WHERE a.appointmentDate = ? and a.status=?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, date);
        query.setParameter(1, status);

        List<Appointment> rs = query.getResultList();

        return rs;
    }

    @Override
    public List<Appointment> find(Date date, String providerNo, Date startTime, Date endTime, String name,
            String notes, String reason, Date createDateTime, String creator, Integer demographicNo) {

        String sql = "SELECT a FROM Appointment a " +
                "WHERE a.appointmentDate = ? and a.providerNo=? and a.startTime=?" +
                "and a.endTime=? and a.name=? and a.notes=? and a.reason=? and a.createDateTime=?" +
                "and a.creator=? and a.demographicNo=?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, date);
        query.setParameter(1, providerNo);
        query.setParameter(2, startTime);
        query.setParameter(3, endTime);
        query.setParameter(4, name);
        query.setParameter(5, notes);
        query.setParameter(6, reason);
        query.setParameter(7, createDateTime);
        query.setParameter(8, creator);
        query.setParameter(9, demographicNo);

        List<Appointment> rs = query.getResultList();

        return rs;
    }

    /**
     * @return return results ordered by appointmentDate, most recent first
     */
    @Override
    public List<Appointment> findByDemographicId(Integer demographicId, int startIndex, int itemsToReturn) {
        String sql = "SELECT a FROM Appointment a WHERE a.demographicNo = ?1 ORDER BY a.appointmentDate desc";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, demographicId);
        query.setFirstResult(startIndex);
        query.setMaxResults(itemsToReturn);

        List<Appointment> rs = query.getResultList();

        return rs;
    }

    @Override
    public List<Appointment> findAll() {
        String sql = "SELECT a FROM Appointment a";
        Query query = entityManager.createQuery(sql);

        List<Appointment> rs = query.getResultList();

        return rs;
    }

    @Override
    public List<Appointment> findNonCancelledFutureAppointments(Integer demographicId) {
        Query query = entityManager.createQuery(
                "FROM Appointment appt WHERE appt.demographicNo = :demographicNo AND appt.status NOT LIKE '%C%' AND appt.status NOT LIKE '%D%'"
                        +
                        " AND appt.appointmentDate >= CURRENT_DATE ORDER BY appt.appointmentDate");
        query.setParameter("demographicNo", demographicId);
        return query.getResultList();
    }

    /**
     * Finds appointment after current date and time for the specified demographic
     * 
     * @param demographicId
     *                      Demographic to find appointment for
     * @return
     *         Returns the next non-cancelled future appointment or null if there
     *         are no appointments
     *         scheduled
     */
    @Override
    public Appointment findNextAppointment(Integer demographicId) {
        Query query = entityManager.createQuery(
                "FROM Appointment appt WHERE appt.demographicNo = :demographicNo AND appt.status NOT LIKE '%C%' " +
                        "	AND (appt.appointmentDate > CURRENT_DATE OR (appt.appointmentDate = CURRENT_DATE AND appt.startTime >= CURRENT_TIME)) ORDER BY appt.appointmentDate");
        query.setParameter("demographicNo", demographicId);
        query.setMaxResults(1);
        return getSingleResultOrNull(query);
    }

    @Override
    public Appointment findDemoAppointmentToday(Integer demographicNo) {
        Appointment appointment = null;

        String sql = "SELECT a FROM Appointment a WHERE a.demographicNo = ? AND a.appointmentDate=DATE(NOW())";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, demographicNo);

        try {
            appointment = (Appointment) query.getSingleResult();
        } catch (Exception e) {
            MiscUtils.getLogger().info("Couldn't find appointment for demographic " + demographicNo + " today.");
        }

        return appointment;
    }

    @Override
    public List<Appointment> findByEverything(Date appointmentDate, String providerNo, Date startTime, Date endTime,
            String name, String notes, String reason, Date createDateTime, String creator, int demographicNo) {
        String sql = "SELECT a FROM Appointment a WHERE a.appointmentDate=? and a.providerNo=? and a.startTime=? and a.endTime=? and a.name=? and a.notes=? and a.reason=? and a.createDateTime like ? and a.creator = ? and a.demographicNo=?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, appointmentDate);
        query.setParameter(1, providerNo);
        query.setParameter(2, startTime);
        query.setParameter(3, endTime);
        query.setParameter(4, name);
        query.setParameter(5, notes);
        query.setParameter(6, reason);
        query.setParameter(7, createDateTime);
        query.setParameter(8, creator);
        query.setParameter(9, demographicNo);

        List<Appointment> rs = query.getResultList();

        return rs;
    }

    @Override
    public List<Appointment> findByProviderAndDate(String providerNo, Date appointmentDate) {
        Query query = createQuery("a", "a.providerNo = :pNo and a.appointmentDate= :aDate");
        query.setParameter("pNo", providerNo);
        query.setParameter("aDate", appointmentDate);
        return query.getResultList();
    }

    @Override
    public List<Object[]> findAppointments(Date sDate, Date eDate) {
        String sql = "FROM Appointment a, Demographic d " +
                "WHERE a.demographicNo = d.DemographicNo " +
                "AND d.Hin <> '' " +
                "AND a.appointmentDate >= :sDate " +
                "AND a.appointmentDate <= :eDate " +
                "AND (" +
                "	UPPER(d.HcType) = 'ONTARIO' " +
                "	OR d.HcType='ON' " +
                ") GROUP BY d.DemographicNo " +
                "ORDER BY d.LastName";
        Query query = entityManager.createQuery(sql);
        query.setParameter("sDate", sDate == null ? new Date(Long.MIN_VALUE) : sDate);
        query.setParameter("eDate", eDate == null ? new Date(Long.MAX_VALUE) : eDate);
        return query.getResultList();
    }

    @Override
    public List<Object[]> findPatientAppointments(String providerNo, Date from, Date to) {
        StringBuilder sql = new StringBuilder("FROM Demographic d, Appointment a, Provider p " +
                "WHERE a.demographicNo = d.DemographicNo " +
                "AND a.providerNo = p.ProviderNo ");

        Map<String, Object> params = new HashMap<String, Object>();
        if (providerNo != null && !providerNo.trim().equals("")) {
            sql.append("and a.providerNo = :pNo ");
            params.put("pNo", providerNo);
        }

        if (from != null) {
            sql.append("AND a.appointmentDate >= :from ");
            params.put("from", from);
        }
        if (to != null) {
            sql.append("AND a.appointmentDate <= :to ");
            params.put("to", to);
        }
        sql.append("ORDER BY a.appointmentDate");

        Query query = entityManager.createQuery(sql.toString());
        for (Entry<String, Object> e : params.entrySet()) {
            query.setParameter(e.getKey(), e.getValue());
        }
        return query.getResultList();
    }

    @Override
    public List<Appointment> search_unbill_history_daterange(String providerNo, Date startDate, Date endDate) {
        String sql = "select a from Appointment a where a.providerNo=? and a.appointmentDate >=? and a.appointmentDate<=? and (a.status='P' or a.status='H' or a.status='PV' or a.status='PS') and a.demographicNo <> 0 order by a.appointmentDate desc, a.startTime desc";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, providerNo);
        query.setParameter(1, startDate);
        query.setParameter(2, endDate);

        return query.getResultList();
    }

    @Override
    public List<Appointment> findByDateAndProvider(Date date, String provider_no) {
        Query query = createQuery("a",
                "a.providerNo = :provider_no and a.appointmentDate = :date order by a.startTime asc");
        query.setParameter("provider_no", provider_no);
        query.setParameter("date", date);
        return query.getResultList();
    }

    @Override
    public List<Appointment> search_appt(Date startTime, Date endTime, String providerNo) {
        String sql = "SELECT a FROM Appointment a WHERE a.appointmentDate >=? and a.appointmentDate <= ? and a.providerNo = ? order by a.appointmentDate,a.startTime,a.endTime";

        Query query = entityManager.createQuery(sql);
        query.setParameter(0, startTime);
        query.setParameter(1, endTime);
        query.setParameter(2, providerNo);

        List<Appointment> rs = query.getResultList();

        return rs;
    }

    // search_appt_name
    @Override
    public List<Appointment> search_appt(Date date, String providerNo, Date startTime1, Date startTime2, Date endTime1,
            Date endTime2, Date startTime3, Date endTime3, Integer programId) {
        String sql = "select a from Appointment a where a.appointmentDate = ? and a.providerNo = ? and a.status <>'C' and a.status<>'D' and ((a.startTime >= ? and a.startTime<= ?) or (a.endTime>= ? and a.endTime<= ?) or (a.startTime <= ? and a.endTime>= ?) ) and program_id=?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, date);
        query.setParameter(1, providerNo);
        query.setParameter(2, startTime1);
        query.setParameter(3, startTime2);
        query.setParameter(4, endTime1);
        query.setParameter(5, endTime2);
        query.setParameter(6, startTime3);
        query.setParameter(7, endTime3);
        query.setParameter(8, programId);

        List<Appointment> rs = query.getResultList();

        return rs;
    }

    @Override
    public List<Object[]> search_appt_future(Integer demographicNo, Date from, Date to) {
        StringBuilder sql = new StringBuilder("FROM Appointment a, Provider p " +
                "WHERE a.providerNo = p.ProviderNo and " +
                "a.demographicNo = ? and " +
                "a.appointmentDate >= ? and " +
                "a.appointmentDate < ?  " +
                "order by a.appointmentDate desc, a.startTime desc");

        Query query = entityManager.createQuery(sql.toString());
        query.setParameter(0, demographicNo);
        query.setParameter(1, from);
        query.setParameter(2, to);

        return query.getResultList();
    }

    @Override
    public List<Object[]> search_appt_past(Integer demographicNo, Date from, Date to) {
        StringBuilder sql = new StringBuilder("FROM Appointment a, Provider p " +
                "WHERE a.providerNo = p.ProviderNo and " +
                "a.demographicNo = ? and " +
                "a.appointmentDate < ? and " +
                "a.appointmentDate > ?  " +
                "order by a.appointmentDate desc, a.startTime desc");

        Query query = entityManager.createQuery(sql.toString());
        query.setParameter(0, demographicNo);
        query.setParameter(1, from);
        query.setParameter(2, to);

        return query.getResultList();
    }

    @Override
    public Appointment search_appt_no(String providerNo, Date appointmentDate, Date startTime, Date endTime,
            Date createDateTime, String creator, Integer demographicNo) {
        String sql = "select a from Appointment a where a.providerNo=? and a.appointmentDate=? and a.startTime=? and " +
                "a.endTime=? and a.createDateTime=? and a.creator=? and a.demographicNo=? order by a.id desc";
        Query query = entityManager.createQuery(sql.toString());
        query.setParameter(0, providerNo);
        query.setParameter(1, appointmentDate);
        query.setParameter(2, startTime);
        query.setParameter(3, endTime);
        query.setParameter(4, createDateTime);
        query.setParameter(5, creator);
        query.setParameter(6, demographicNo);
        query.setMaxResults(1);

        return this.getSingleResultOrNull(query);
    }

    @Override
    public List<Object[]> search_appt_data1(String providerNo, Date appointmentDate, Date startTime, Date endTime,
            Date createDateTime, String creator, Integer demographicNo) {
        String sql = "from Provider prov, Appointment app " +
                "where app.providerNo = prov.id and " +
                "app.providerNo=? and " +
                "app.appointmentDate=? and " +
                "app.startTime=? and " +
                "app.endTime=? and " +
                "app.createDateTime=? and " +
                "app.creator=? and " +
                "app.demographicNo=? " +
                "order by app.id desc";
        Query query = entityManager.createQuery(sql);
        query.setMaxResults(1);
        query.setParameter(0, providerNo);

        query.setParameter(1, appointmentDate);
        query.setParameter(2, startTime);
        query.setParameter(3, endTime);
        query.setParameter(4, createDateTime);
        query.setParameter(5, creator);
        query.setParameter(6, demographicNo);

        return query.getResultList();
    }

    @Override
    public List<Object[]> export_appt(Integer demographicNo) {
        String sql = "from Appointment app, Provider prov where app.providerNo = prov.id and app.demographicNo = ?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, demographicNo);

        return query.getResultList();
    }

    @Override
    public List<Appointment> search_otherappt(Date appointmentDate, Date startTime1, Date endTime1, Date startTime2,
            Date startTime3) {
        String sql = "from Appointment a where a.appointmentDate=? and ((a.startTime <= ? and a.endTime >= ?) or (a.startTime > ? and a.startTime < ?) ) order by a.providerNo, a.startTime";

        Query query = entityManager.createQuery(sql);
        query.setParameter(0, appointmentDate);
        query.setParameter(1, startTime1);
        query.setParameter(2, endTime1);
        query.setParameter(3, startTime2);
        query.setParameter(4, startTime3);

        return query.getResultList();
    }

    @Override
    public List<Appointment> search_group_day_appt(String myGroup, Integer demographicNo, Date appointmentDate) {
        String sql = "select a  from Appointment a, MyGroup m " +
                "where m.id.providerNo = a.providerNo " +
                "and a.status <> 'C' " +
                "and m.id.myGroupNo = ? " +
                "and a.demographicNo = ? " +
                "and a.appointmentDate = ?";

        Query query = entityManager.createQuery(sql);
        query.setParameter(0, myGroup);
        query.setParameter(1, demographicNo);
        query.setParameter(2, appointmentDate);

        return query.getResultList();
    }

    @Override
    public Appointment findByDate(Date appointmentDate) {
        Query query = createQuery("a", "a.appointmentDate < :appointmentDate ORDER BY a.appointmentDate DESC");
        query.setMaxResults(1);
        query.setParameter("appointmentDate", appointmentDate);
        return getSingleResultOrNull(query);
    }

    @Override
    public List<Object[]> findAppointmentAndProviderByAppointmentNo(Integer apptNo) {
        String sql = "FROM Appointment a, Provider p WHERE a.providerNo = p.ProviderNo AND a.id = :apptNo";
        Query query = entityManager.createQuery(sql);
        query.setParameter("apptNo", apptNo);
        return query.getResultList();
    }

    @Override
    public List<Appointment> searchappointmentday(String providerNo, Date appointmentDate, Integer programId) {
        Query query = createQuery("appt",
                "appt.providerNo = :providerNo AND appt.appointmentDate = :appointmentDate AND appt.programId = :programId AND appt.status <> :status ORDER BY appt.startTime, appt.status DESC");
        query.setParameter("providerNo", providerNo);
        query.setParameter("appointmentDate", appointmentDate);
        query.setParameter("programId", programId);
        query.setParameter("status", "D");
        return query.getResultList();
    }

    @Override
    public List<Appointment> searchAppointmentDaySite(String providerNo, Date appointmentDate, Integer programId,
            String selectedSiteId) {
        Query query;
        if (selectedSiteId == null || selectedSiteId.equalsIgnoreCase("none")) {
            query = createQuery("appt",
                    "appt.providerNo = :providerNo AND appt.appointmentDate = :appointmentDate AND appt.programId = :programId AND appt.status <> :status ORDER BY appt.startTime, appt.status DESC");
        } else {
            query = createQuery("appt",
                    "appt.providerNo = :providerNo AND appt.appointmentDate = :appointmentDate AND appt.programId = :programId AND appt.status <> :status AND appt.location = :siteId ORDER BY appt.startTime, appt.status DESC");
            query.setParameter("siteId", selectedSiteId);
        }
        query.setParameter("providerNo", providerNo);
        query.setParameter("appointmentDate", appointmentDate);
        query.setParameter("programId", programId);
        query.setParameter("status", "D");
        return query.getResultList();
    }

    @NativeSql({ "demographic", "appointment", "drugs", "provider" })
    @Override
    public List<Object[]> findAppointmentsByDemographicIds(Set<String> demoIds, Date from, Date to) {
        String sql = "" +
                "select " +
                "a.appointment_date, " +
                "concat(pAppt.first_name, ' ', pAppt.last_name), " +
                "concat(pFam.first_name, ' ', pFam.last_name), " +
                "bi.service_code, " +
                "drugs.BN, " +
                "concat(pDrug.first_name,' ',pDrug.last_name), " +
                "a.demographic_no, " +
                "drugs.GN, " +
                "drugs.customName " +
                "from demographic d," +
                "appointment a left outer join drugs " +
                "on drugs.demographic_no = a.demographic_no and drugs.rx_date = a.appointment_date and a.appointment_date >= :from and a.appointment_date <= :to and a.demographic_no in (:demoIds) "
                +
                " left join provider pDrug on pDrug.provider_no = drugs.provider_no, billing_on_cheader1 bc, billing_on_item bi, provider pAppt, provider pFam where a.appointment_date >= :from and a.appointment_date <= :to and a.demographic_no = d.demographic_no"
                +
                " and a.provider_no = pAppt.provider_no and d.provider_no = pFam.provider_no and bc.appointment_no = a.appointment_no and bi.ch1_id = bc.id and a.demographic_no in (:demoIds) order by a.demographic_no, a.appointment_date";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("demoIds", demoIds);
        query.setParameter("from", from);
        query.setParameter("to", to);
        return query.getResultList();
    }

    /**
     * Get billed appointment history.
     * Used if using the Clinicaid billing integration.
     */
    @Override
    public List<Appointment> findPatientBilledAppointmentsByProviderAndAppointmentDate(
            String providerNo,
            Date startAppointmentDate,
            Date endAppointmentDate) {
        String queryString = "FROM Appointment WHERE " +
                "providerNo = ? AND " +
                "appointmentDate >= ? AND " +
                "appointmentDate <= ? AND " +
                "status = 'B' AND " +
                "demographicNo <> 0 " +
                "ORDER BY appointmentDate DESC, startTime DESC ";

        Query q = entityManager.createQuery(queryString);
        q.setParameter(0, providerNo);
        q.setParameter(1, startAppointmentDate);
        q.setParameter(2, endAppointmentDate);

        @SuppressWarnings("unchecked")
        List<Appointment> results = q.getResultList();

        return results;
    }

    /**
     * Get unbilled appointment history.
     * Used if using the Clinicaid billing integration.
     */
    @Override
    public List<Appointment> findPatientUnbilledAppointmentsByProviderAndAppointmentDate(
            String providerNo,
            Date startAppointmentDate,
            Date endAppointmentDate) {

        String queryString = "FROM Appointment WHERE " +
                "providerNo = ? AND " +
                "appointmentDate >= ? AND " +
                "appointmentDate <= ? AND " +
                "status NOT LIKE 'B%' AND " +
                "status NOT LIKE 'C%' AND " +
                "status NOT LIKE 'N%' AND " +
                "status NOT LIKE 'T%' AND " +
                "status NOT LIKE 't%' AND " +
                "demographicNo != 0 " +
                "ORDER BY appointmentDate DESC, startTime DESC";

        Query q = entityManager.createQuery(queryString);
        q.setParameter(0, providerNo);
        q.setParameter(1, startAppointmentDate);
        q.setParameter(2, endAppointmentDate);

        @SuppressWarnings("unchecked")
        List<Appointment> results = q.getResultList();

        return results;
    }

    @Override
    public List<Appointment> findByProgramProviderDemographicDate(Integer programId, String providerNo,
            Integer demographicId, Date updatedAfterThisDateExclusive, int itemsToReturn) {
        Query query = entityManager.createQuery(
                "select x from Appointment x where (x.programId=?1 or x.programId is null or x.programId=0) and x.demographicNo=?2 and x.providerNo=?3 and x.updateDateTime>?4 order by x.updateDateTime");
        query.setParameter(1, programId);
        query.setParameter(2, demographicId);
        query.setParameter(3, providerNo);
        query.setParameter(4, updatedAfterThisDateExclusive);

        setLimit(query, itemsToReturn);

        List<Appointment> results = query.getResultList();
        return results;
    }

    /**
     * @param programId can be null for all
     */
    @Override
    public List<Integer> findAllDemographicIdByProgramProvider(Integer programId, String providerNo) {
        String sql = "select distinct(x.demographicNo)from Appointment x where x.providerNo=?1"
                + (programId == null ? "" : " and x.programId=?2");
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, providerNo);
        if (programId != null)
            query.setParameter(2, programId);

        setDefaultLimit(query);

        List<Integer> rs = query.getResultList();
        return rs;
    }

    @Override
    public List<Appointment> findDemoAppointmentsToday(Integer demographicNo) {
        String sql = "SELECT a FROM Appointment a WHERE a.demographicNo = ? AND a.appointmentDate=DATE(NOW()) ORDER BY a.appointmentDate, a.startTime";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, demographicNo);

        @SuppressWarnings("unchecked")
        List<Appointment> results = query.getResultList();

        return results;
    }

    @Override
    public List<Appointment> findDemoAppointmentsOnDate(Integer demographicNo, Date date) {
        String sql = "SELECT a FROM Appointment a WHERE a.demographicNo = ? AND a.appointmentDate=? ORDER BY a.appointmentDate, a.startTime";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, demographicNo);
        query.setParameter(1, date);

        @SuppressWarnings("unchecked")
        List<Appointment> results = query.getResultList();

        return results;
    }

    @Override
    public int findProvideAppointmentTodayNum(String provide, String appdate) {
        Date appointDate = UtilDateUtilities.StringToDate(appdate);
        String sql = "SELECT COUNT(a) FROM Appointment a WHERE a.providerNo = ?1 AND a.status != 'C' AND a.status != 'D' AND a.status != 'CS' AND a.appointmentDate= ?2";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, provide);
        query.setParameter(2, appointDate);
        return Integer.parseInt(String.valueOf(query.getSingleResult()));
    }

    @Override
    public int updateApptStatus(String ids, String status) {
        // remove non-number value
        StringBuilder idClean = new StringBuilder();
        for (String id : ids.split(",")) {
            if (!StringUtils.isNumeric(id)) {
                continue;
            }
            idClean.append(id + ",");
        }
        if (idClean.length() == 0) {
            return 0;
        }
        idClean.deleteCharAt(idClean.length() - 1);
        Query q = entityManager
                .createQuery("update Appointment set status=?1 where id in (" + idClean.toString() + ")");
        q.setParameter(1, status);
        return q.executeUpdate();
    }

    @Override
    public List<Object[]> listAppointmentsByPeriodProvider(Date sDate, Date eDate, List<Integer> providerNos) {
        String sql = "SELECT a.appointment_no, a.provider_no, a.appointment_date, a.start_time, a.demographic_no, a.notes, a.location, "
                +
                "a.resources, a.status,d.last_name, d.first_name, d.phone, d.phone2,  d.email, e1.value as demo_cell, e2.value as reminderPreference, "
                +
                "e3.value as hPhoneExt,  e4.value as wPhoneExt " +
                "FROM appointment a LEFT JOIN demographic d ON a.demographic_no = d.demographic_no " +
                getJoin4LatestDemographicExtValue("demo_cell", 1) +
                getJoin4LatestDemographicExtValue("reminderPreference", 2) +
                getJoin4LatestDemographicExtValue("hPhoneExt", 3) +
                getJoin4LatestDemographicExtValue("wPhoneExt", 4) +
                "WHERE a.provider_no IN (:providers) AND a.appointment_date >= :startDate AND a.appointment_date <= :endDate";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("providers", providerNos != null && providerNos.size() > 0 ? providerNos : Arrays.asList());
        query.setParameter("startDate", sDate == null ? new Date(Long.MIN_VALUE) : sDate);
        query.setParameter("endDate", eDate == null ? new Date(Long.MAX_VALUE) : eDate);
        return query.getResultList();
    }

    private String getJoin4LatestDemographicExtValue(String keyval, int seqNum) {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb, Locale.US);
        f.format("LEFT JOIN demographicExt %1$s ON a.demographic_no = %1$s.demographic_no AND %1$s.key_val = '%3$s' " +
                "AND %1$s.date_time=(SELECT max(%2$s.date_time) from demographicExt %2$s WHERE %2$s.demographic_no=a.demographic_no "
                + "and key_val='%3$s') ", "e" + seqNum, "e1" + seqNum, keyval);
        return sb.toString();
    }

    /*
     * List active providers and their appontments count
     */
    @Override
    public List<Object[]> listProviderAppointmentCounts(Date sDate, Date eDate) {
        String sql = "SELECT a.provider_no, p.first_name, p.last_name, COUNT(a.provider_no) as count " +
                "FROM appointment a JOIN provider p ON a.provider_no=p.provider_no " +
                "WHERE a.appointment_date BETWEEN :startDate AND :endDate AND p.status=1 " +
                "GROUP BY a.provider_no";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("startDate", sDate == null ? new Date(Long.MIN_VALUE) : sDate);
        query.setParameter("endDate", eDate == null ? new Date(Long.MIN_VALUE) : eDate);
        return query.getResultList();
    }

}
