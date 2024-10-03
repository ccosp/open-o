//CHECKSTYLE:OFF
/**
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
 */
package org.oscarehr.billing.CA.BC.dao;

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.billing.CA.BC.model.Hl7Orc;
import org.oscarehr.common.dao.AbstractDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class Hl7OrcDao extends AbstractDaoImpl<Hl7Orc> {

    public Hl7OrcDao() {
        super(Hl7Orc.class);
    }

    public List<Object[]> findFillerAndStatusChageByMessageId(Integer messageId) {
        String sql = "SELECT orc.fillerOrderNumber, MAX(obr.resultsReportStatusChange) " +
                "FROM Hl7Orc orc, Hl7Pid pid, Hl7Obr obr " +
                "WHERE obr.pidId = pid.id " +
                "AND orc.pidId = pid.id " +
                "AND pid.messageId = ?1 " +
                "GROUP BY pid.messageId";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, messageId);
        return query.getResultList();
    }

    public List<Object[]> findOrcAndPidByMessageId(Integer messageId) {
        String sql = "FROM Hl7Orc orc, Hl7Pid pid WHERE orc.pidId = pid.id AND pid.messageId = ?1";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, messageId);
        return query.getResultList();
    }
}
