//CHECKSTYLE:OFF
/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * <p>
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * This software was written for
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 * <p>
 * Modifications made by Magenta Health in 2024.
 */

package ca.openosp.openo.PMmodule.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import ca.openosp.openo.PMmodule.model.OcanSubmissionLog;
import ca.openosp.openo.PMmodule.model.OcanSubmissionRecordLog;
import ca.openosp.openo.common.dao.AbstractDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class OcanSubmissionLogDaoImpl extends AbstractDaoImpl<OcanSubmissionLog> implements OcanSubmissionLogDao {

    public OcanSubmissionLogDaoImpl() {
        super(OcanSubmissionLog.class);
    }

    public void persistRecord(OcanSubmissionRecordLog rec) {
        entityManager.persist(rec);
    }


    public List<OcanSubmissionLog> findBySubmissionDate(Date submissionDate) {
        Query query = entityManager.createQuery("select l from OcanSubmissionLog l where date(l.submitDateTime)=?  order by l.submitDateTime DESC");
        query.setParameter(0, submissionDate);
        @SuppressWarnings("unchecked")
        List<OcanSubmissionLog> results = query.getResultList();
        return results;
    }

    public List<OcanSubmissionLog> findBySubmissionDateType(Date submissionDate, String type) {
        Query query = entityManager.createQuery("select l from OcanSubmissionLog l where date(l.submitDateTime)=?  and submissionType=? order by l.submitDateTime DESC");
        query.setParameter(0, submissionDate);
        query.setParameter(1, type);
        @SuppressWarnings("unchecked")
        List<OcanSubmissionLog> results = query.getResultList();
        return results;
    }

    public List<OcanSubmissionLog> findBySubmissionDateType(Date submissionStartDate, Date submissionEndDate, String type) {
        Query query = entityManager.createQuery("select l from OcanSubmissionLog l where submitDateTime>=?  and l.submitDateTime<=? and submissionType=? order by l.submitDateTime DESC");
        query.setParameter(0, submissionStartDate);
        query.setParameter(1, submissionEndDate);
        query.setParameter(2, type);
        @SuppressWarnings("unchecked")
        List<OcanSubmissionLog> results = query.getResultList();
        return results;
    }

    public List<OcanSubmissionLog> findAllByType(String type) {
        Query query = entityManager.createQuery("select l from OcanSubmissionLog l where l.submissionType=? order by l.submitDateTime DESC");
        query.setParameter(0, type);
        @SuppressWarnings("unchecked")
        List<OcanSubmissionLog> results = query.getResultList();
        return results;
    }

    public List<OcanSubmissionLog> findFailedSubmissionsByType(String type) {
        Query query = entityManager.createQuery("select l from OcanSubmissionLog l where l.submissionType=? and l.result=? order by l.submitDateTime DESC");
        query.setParameter(0, type);
        query.setParameter(1, "false");
        @SuppressWarnings("unchecked")
        List<OcanSubmissionLog> results = query.getResultList();
        return results;
    }
}
