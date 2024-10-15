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

import org.oscarehr.common.model.Relationships;
import org.springframework.stereotype.Repository;
import oscar.util.ConversionUtils;

@Repository
public class RelationshipsDaoImpl extends AbstractDaoImpl<Relationships> implements RelationshipsDao {

    public RelationshipsDaoImpl() {
        super(Relationships.class);
    }

    @Override
    public List<Relationships> findAll() {
        String sql = "select x from Relationships x order by x.demographicNo";
        Query query = entityManager.createQuery(sql);
        @SuppressWarnings("unchecked")
        List<Relationships> results = query.getResultList();
        return results;
    }

    @Override
    public Relationships findActive(Integer id) {
        Query query = entityManager.createQuery("FROM ?1 r WHERE r.id = ?2 AND (r.deleted IS NULL OR r.deleted = '0')");
        query.setParameter(1, modelClass.getSimpleName());
        query.setParameter(2, id);
        return getSingleResultOrNull(query);
    }

    @Override
    public List<Relationships> findByDemographicNumber(Integer demographicNumber) {
        Query query = entityManager.createQuery("FROM ?1 r WHERE r.demographicNo = ?2 AND (r.deleted IS NULL OR r.deleted = '0')");
        query.setParameter(1, modelClass.getSimpleName());
        query.setParameter(2, demographicNumber);
        return query.getResultList();
    }

    @Override
    public List<Relationships> findActiveSubDecisionMaker(Integer demographicNumber) {
        Query query = entityManager.createQuery("FROM ?1 r WHERE r.demographicNo = ?2 AND r.subDecisionMaker = ?3 AND (r.deleted IS NULL OR r.deleted = '0')");
        query.setParameter(1, modelClass.getSimpleName());
        query.setParameter(2, demographicNumber);
        query.setParameter(3, ConversionUtils.toBoolString(Boolean.TRUE));
        return query.getResultList();
    }

    @Override
    public List<Relationships> findActiveByDemographicNumberAndFacility(Integer demographicNumber, Integer facilityId) {
        Query query = entityManager.createQuery("FROM ?1 r WHERE r.demographicNo = ?2 AND r.facilityId = ?3 AND (r.deleted IS NULL OR r.deleted = '0')");
        query.setParameter(1, modelClass.getSimpleName());
        query.setParameter(2, demographicNumber);
        query.setParameter(3, facilityId);
        return query.getResultList();
    }
}
