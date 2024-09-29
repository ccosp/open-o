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

package ca.openosp.openo.common.dao;

import java.util.List;

import javax.persistence.Query;

import ca.openosp.openo.common.model.ConsultResponseDoc;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class ConsultResponseDocDaoImpl extends AbstractDaoImpl<ConsultResponseDoc> implements ConsultResponseDocDao {
    public ConsultResponseDocDaoImpl() {
        super(ConsultResponseDoc.class);
    }

    public ConsultResponseDoc findByResponseIdDocNoDocType(Integer responseId, Integer documentNo, String docType) {
        String sql = "select x from ConsultResponseDoc x where x.responseId=? and x.documentNo=? and x.docType=? and x.deleted IS NULL";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, responseId);
        query.setParameter(1, documentNo);
        query.setParameter(2, docType);

        List<ConsultResponseDoc> results = query.getResultList();
        if (results != null && results.size() > 0) return results.get(0);
        else return null;
    }

    public List<ConsultResponseDoc> findByResponseId(Integer responseId) {
        String sql = "select x from ConsultResponseDoc x where x.responseId=? and x.deleted IS NULL";
        Query query = entityManager.createQuery(sql);
        query.setParameter(0, responseId);

        List<ConsultResponseDoc> results = query.getResultList();
        return results;
    }

    public List<Object[]> findLabs(Integer consultResponseId) {
        String sql = "FROM ConsultResponseDoc crd, PatientLabRouting plr " +
                "WHERE plr.labNo = crd.documentNo " +
                "AND crd.responseId = :consultResponseId " +
                "AND crd.docType = :docType " +
                "AND crd.deleted IS NULL " +
                "ORDER BY crd.documentNo";
        Query q = entityManager.createQuery(sql);
        q.setParameter("consultResponseId", consultResponseId);
        q.setParameter("docType", ConsultResponseDoc.DOCTYPE_LAB);
        return q.getResultList();
    }
}
