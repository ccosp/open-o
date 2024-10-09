//CHECKSTYLE:OFF
/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * <p>
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 * <p>
 * Modifications made by Magenta Health in 2024.
 */
package org.oscarehr.common.dao;

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.logging.log4j.Logger;
import org.oscarehr.common.model.ConsultationResponse;
import org.oscarehr.consultations.ConsultationResponseSearchFilter;
import org.oscarehr.consultations.ConsultationResponseSearchFilter.SORTMODE;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ConsultResponseDaoImpl extends AbstractDaoImpl<ConsultationResponse> implements ConsultResponseDao {
    private Logger logger = MiscUtils.getLogger();

    public ConsultResponseDaoImpl() {
        super(ConsultationResponse.class);
    }

    public int getConsultationCount(ConsultationResponseSearchFilter filter) {
        String sql = getSearchQuery(filter, true);
        logger.debug("sql=" + sql);

        Query query = entityManager.createQuery(sql);
        if (filter.getAppointmentStartDate() != null) {
            query.setParameter(1, FastDateFormat.getInstance("yyyy-MM-dd").format(filter.getAppointmentStartDate()));
        }
        if (filter.getAppointmentEndDate() != null) {
            query.setParameter(2, DateFormatUtils.ISO_DATE_FORMAT.format(filter.getAppointmentEndDate()) + " 23:59:59");
        }
        if (filter.getReferralStartDate() != null) {
            query.setParameter(3, DateFormatUtils.ISO_DATE_FORMAT.format(filter.getReferralStartDate()));
        }
        if (filter.getReferralEndDate() != null) {
            query.setParameter(4, DateFormatUtils.ISO_DATE_FORMAT.format(filter.getReferralEndDate()) + " 23:59:59");
        }
        if (filter.getResponseStartDate() != null) {
            query.setParameter(5, DateFormatUtils.ISO_DATE_FORMAT.format(filter.getResponseStartDate()));
        }
        if (filter.getResponseEndDate() != null) {
            query.setParameter(6, DateFormatUtils.ISO_DATE_FORMAT.format(filter.getResponseEndDate()) + " 23:59:59");
        }
        if (filter.getStatus() != null) {
            query.setParameter(7, filter.getStatus());
        }
        if (StringUtils.isNotBlank(filter.getTeam())) {
            query.setParameter(8, filter.getTeam());
        }
        if (StringUtils.isNotBlank(filter.getUrgency())) {
            query.setParameter(9, filter.getUrgency());
        }
        if (filter.getDemographicNo() != null && filter.getDemographicNo() > 0) {
            query.setParameter(10, filter.getDemographicNo());
        }
        if (filter.getMrpNo() != null && filter.getMrpNo() > 0) {
            query.setParameter(11, filter.getMrpNo());
        }
        Long count = this.getCountResult(query);


        return count.intValue();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> search(ConsultationResponseSearchFilter filter) {
        String sql = this.getSearchQuery(filter, false);
        logger.debug("sql=" + sql);

        Query query = entityManager.createQuery(sql);
        if (filter.getAppointmentStartDate() != null) {
            query.setParameter(1, FastDateFormat.getInstance("yyyy-MM-dd").format(filter.getAppointmentStartDate()));
        }
        if (filter.getAppointmentEndDate() != null) {
            query.setParameter(2, DateFormatUtils.ISO_DATE_FORMAT.format(filter.getAppointmentEndDate()) + " 23:59:59");
        }
        if (filter.getReferralStartDate() != null) {
            query.setParameter(3, DateFormatUtils.ISO_DATE_FORMAT.format(filter.getReferralStartDate()));
        }
        if (filter.getReferralEndDate() != null) {
            query.setParameter(4, DateFormatUtils.ISO_DATE_FORMAT.format(filter.getReferralEndDate()) + " 23:59:59");
        }
        if (filter.getResponseStartDate() != null) {
            query.setParameter(5, DateFormatUtils.ISO_DATE_FORMAT.format(filter.getResponseStartDate()));
        }
        if (filter.getResponseEndDate() != null) {
            query.setParameter(6, DateFormatUtils.ISO_DATE_FORMAT.format(filter.getResponseEndDate()) + " 23:59:59");
        }
        if (filter.getStatus() != null) {
            query.setParameter(7, filter.getStatus());
        }
        if (StringUtils.isNotBlank(filter.getTeam())) {
            query.setParameter(8, filter.getTeam());
        }
        if (StringUtils.isNotBlank(filter.getUrgency())) {
            query.setParameter(9, filter.getUrgency());
        }
        if (filter.getDemographicNo() != null && filter.getDemographicNo() > 0) {
            query.setParameter(10, filter.getDemographicNo());
        }
        if (filter.getMrpNo() != null && filter.getMrpNo() > 0) {
            query.setParameter(11, filter.getMrpNo());
        }
        query.setFirstResult(filter.getStartIndex());
        query.setMaxResults(filter.getNumToReturn());
        return query.getResultList();
    }

    private String getSearchQuery(ConsultationResponseSearchFilter filter, boolean selectCountOnly) {
        StringBuilder sql = new StringBuilder(
                "select " + (selectCountOnly ? "count(*)" : "cr,sp,d,p") +
                        " from ConsultationResponse cr , ProfessionalSpecialist sp, Demographic d left outer join d.provider p" +
                        " where sp.id = cr.referringDocId and d.DemographicNo = cr.demographicNo ");

        if (filter.getAppointmentStartDate() != null) {
            sql.append("and cr.appointmentDate >=  ?1 ");
        }
        if (filter.getAppointmentEndDate() != null) {
            sql.append("and cr.appointmentDate <=  ?2 ");
        }
        if (filter.getReferralStartDate() != null) {
            sql.append("and cr.referralDate >=  ?3 ");
        }
        if (filter.getReferralEndDate() != null) {
            sql.append("and cr.referralDate <=  ?4 ");
        }
        if (filter.getResponseStartDate() != null) {
            sql.append("and cr.responseDate >=  ?5 ");
        }
        if (filter.getResponseEndDate() != null) {
            sql.append("and cr.responseDate <=  ?6 ");
        }
        if (filter.getStatus() != null) {
            sql.append("and cr.status = ?7 ");
        } else {
            sql.append("and cr.status!=4 and cr.status!=5 ");
        }
        if (StringUtils.isNotBlank(filter.getTeam())) {
            sql.append("and cr.sendTo = ?8 ");
        }
        if (StringUtils.isNotBlank(filter.getUrgency())) {
            sql.append("and cr.urgency = ?9 ");
        }
        if (filter.getDemographicNo() != null && filter.getDemographicNo() > 0) {
            sql.append("and cr.demographicNo = ?10 ");
        }
        if (filter.getMrpNo() != null && filter.getMrpNo() > 0) {
            sql.append("and d.ProviderNo = ?11 ");
        }

        String orderBy = "cr.referralDate";
        String orderDir = "desc";

        if (filter.getSortDir() != null) {
            orderDir = filter.getSortDir().toString();
        }
        if (SORTMODE.AppointmentDate.equals(filter.getSortMode())) {
            orderBy = "cr.appointmentDate " + orderDir + ",cr.appointmentTime " + orderDir;
        } else if (SORTMODE.Demographic.equals(filter.getSortMode())) {
            orderBy = "d.LastName " + orderDir + ",d.FirstName " + orderDir;
        } else if (SORTMODE.ReferringDoctor.equals(filter.getSortMode())) {
            orderBy = "sp.lastName " + orderDir + ",sp.firstName " + orderDir;
        } else if (SORTMODE.Team.equals(filter.getSortMode())) {
            orderBy = "cr.sendTo " + orderDir;
        } else if (SORTMODE.Status.equals(filter.getSortMode())) {
            orderBy = "cr.status " + orderDir;
        } else if (SORTMODE.Provider.equals(filter.getSortMode())) {
            orderBy = "p.LastName " + orderDir + ",p.FirstName " + orderDir;
        } else if (SORTMODE.FollowUpDate.equals(filter.getSortMode())) {
            orderBy = "cr.followUpDate " + orderDir;
        } else if (SORTMODE.ReferralDate.equals(filter.getSortMode())) {
            orderBy = "cr.referralDate " + orderDir;
        } else if (SORTMODE.ResponseDate.equals(filter.getSortMode())) {
            orderBy = "cr.responseDate " + orderDir;
        } else if (SORTMODE.Urgency.equals(filter.getSortMode())) {
            orderBy = "cr.urgency " + orderDir;
        }

        orderBy = " ORDER BY " + orderBy;
        sql.append(orderBy);

        return sql.toString();
    }
}
