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

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.FaxJob;
import org.springframework.stereotype.Repository;

@Repository
public class FaxJobDaoImpl extends AbstractDaoImpl<FaxJob> implements FaxJobDao {

    public FaxJobDaoImpl() {
        super(FaxJob.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FaxJob> getFaxStatusByDateDemographicProviderStatusTeam(String demographic_no, String provider_no,
                                                                        String status, String team, Date beginDate, Date endDate) {

        StringBuilder sql = new StringBuilder("select job from FaxJob job ");

        if (demographic_no != null || status != null || team != null || beginDate != null || endDate != null
                || provider_no != null) {
            sql.append("where");
        }

        boolean firstclause = false;

        if (demographic_no != null) {
            firstclause = true;
            sql.append(" job.demographicNo = :demo");
        }

        if (status != null) {
            if (firstclause) {
                sql.append(" and job.status = :status");
            } else {
                firstclause = true;
                sql.append(" job.status = :status");
            }

        }

        if (team != null) {
            if (firstclause) {
                sql.append(" and job.user = '" + team + "'");
            } else {
                firstclause = true;
                sql.append(" job.user = '" + team + "'");
            }
        }

        if (beginDate != null) {
            if (firstclause) {
                sql.append(" and job.stamp >= :beginDate");
            } else {
                firstclause = true;
                sql.append(" job.stamp >= :beginDate");
            }
        }

        if (endDate != null) {
            if (firstclause) {
                sql.append(" and job.stamp <= :endDate");
            } else {
                firstclause = true;
                sql.append(" job.stamp <= :endDate");
            }
        }

        if (provider_no != null) {
            if (firstclause) {
                sql.append(" and job.oscarUser = '" + provider_no + "'");
            } else {
                sql.append(" job.oscarUser = '" + provider_no + "'");
            }
        }

        Query query = entityManager.createQuery(sql.toString());

        if (beginDate != null) {
            query.setParameter("beginDate", beginDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        if (status != null) {
            query.setParameter("status", FaxJob.STATUS.valueOf(status));
        }

        if (demographic_no != null) {
            query.setParameter("demo", Integer.parseInt(demographic_no));
        }

        List<FaxJob> faxJobList = query.getResultList();

        Collections.sort(faxJobList);

        return faxJobList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FaxJob> getReadyToSendFaxes(String number) {
        Query query = entityManager.createQuery(
                "select job from FaxJob job where job.status = ?1 and job.fax_line = ?2 and job.jobId is null");

        // these faxes are "waiting" to be sent
        // they become "sent" after they clear the api
        query.setParameter(1, FaxJob.STATUS.WAITING);
        query.setParameter(2, number);

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FaxJob> getInprogressFaxesByJobId() {
        Query query = entityManager.createQuery(
                "select job from FaxJob job where (job.status = ?1 or job.status = ?2) and job.jobId is not null");

        query.setParameter(1, FaxJob.STATUS.SENT);
        query.setParameter(2, FaxJob.STATUS.WAITING);

        List<FaxJob> faxJobList = query.getResultList();

        Collections.sort(faxJobList);

        return faxJobList;
    }

}
