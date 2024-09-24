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
import java.util.UUID;

import javax.persistence.Query;

import org.oscarehr.common.model.OscarAnnotation;
import org.springframework.stereotype.Repository;

@Repository
public class OscarAnnotationDaoImpl extends AbstractDaoImpl<OscarAnnotation> implements OscarAnnotationDao {

    public OscarAnnotationDaoImpl() {
        super(OscarAnnotation.class);
    }

    @Override
    public OscarAnnotation getAnnotations(String demoNo, String tableName, Long tableId) {
        Query query = entityManager.createQuery("select a from OscarAnnotation a where a.demographicNo=? and a.tableName=? and a.tableId=?");
        query.setParameter(0, demoNo);
        query.setParameter(1, tableName);
        query.setParameter(2, tableId);
        @SuppressWarnings("unchecked")
        List<OscarAnnotation> codeList = query.getResultList();
        if (!codeList.isEmpty()) {
            return codeList.get(0);
        }
        return null;
    }

    @Override
    public void save(OscarAnnotation anno) {
        if (anno.isUuidSet()) {
            UUID uuid = UUID.randomUUID();
            anno.setUuid(uuid.toString());
        }
        persist(anno);
    }

    @Override
    public int getNumberOfNotes(String demoNo, String tableName, Long tableId) {
        Query query = entityManager.createQuery("select a from OscarAnnotation a where a.demographicNo=? and a.tableName=? and a.tableId=?");
        query.setParameter(0, demoNo);
        query.setParameter(1, tableName);
        query.setParameter(2, tableId);

        @SuppressWarnings("unchecked")
        List<OscarAnnotation> codeList = query.getResultList();

        return codeList.size();
    }
}
