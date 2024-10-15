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
package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.NativeSql;
import org.oscarehr.common.model.Prevention;

public class PreventionDaoImpl extends AbstractDaoImpl<Prevention> implements PreventionDao {

    public PreventionDaoImpl() {
        super(Prevention.class);
    }

    @Override
    public List<Prevention> findByDemographicId(Integer demographicId) {
        Query query = entityManager.createQuery("select x from ?1 x where demographicId=?2");
        query.setParameter(1, modelClass.getSimpleName());
        query.setParameter(2, demographicId);

        List<Prevention> results = query.getResultList();

        return (results);
    }

    /**
     * @return results ordered by lastUpdateDate
     */
    @Override
    public List<Prevention> findByUpdateDate(Date updatedAfterThisDateExclusive, int itemsToReturn) {
        String sqlCommand = "select x from ?1 x where x.lastUpdateDate>?2 order by x.lastUpdateDate";

        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, modelClass.getSimpleName());
        query.setParameter(2, updatedAfterThisDateExclusive);
        setLimit(query, itemsToReturn);

        @SuppressWarnings("unchecked")
        List<Prevention> results = query.getResultList();
        return (results);
    }

    @Override
    public List<Prevention> findByDemographicIdAfterDatetime(Integer demographicId, Date dateTime) {
        Query query = entityManager.createQuery("select x from Prevention x where demographicId=?1 and lastUpdateDate>=?2 and deleted='0'");
        query.setParameter(1, demographicId);
        query.setParameter(2, dateTime);

        @SuppressWarnings("unchecked")
        List<Prevention> results = query.getResultList();

        return (results);
    }

    @Override
    public List<Prevention> findByDemographicIdAfterDatetimeExclusive(Integer demographicId, Date dateTime) {
        Query query = entityManager.createQuery("select x from Prevention x where demographicId=?1 and lastUpdateDate>?2 and deleted='0'");
        query.setParameter(1, demographicId);
        query.setParameter(2, dateTime);

        @SuppressWarnings("unchecked")
        List<Prevention> results = query.getResultList();

        return (results);
    }

    /*
     * for integrator
     */
    @Override
    public List<Integer> findDemographicIdsAfterDatetime(Date dateTime) {
        Query query = entityManager.createQuery("select x.demographicId from Prevention x where x.lastUpdateDate > ?1");
        query.setParameter(1, dateTime);

        @SuppressWarnings("unchecked")
        List<Integer> results = query.getResultList();

        return (results);
    }

    @Override
    public List<Prevention> findByProviderDemographicLastUpdateDate(String providerNo, Integer demographicId, Date updatedAfterThisDateExclusive, int itemsToReturn) {
        String sqlCommand = "select x from ?1 x where x.demographicId=?2 and x.providerNo=?3 and x.lastUpdateDate>?4 order by x.lastUpdateDate";

        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(1, modelClass.getSimpleName());
        query.setParameter(2, demographicId);
        query.setParameter(3, providerNo);
        query.setParameter(4, updatedAfterThisDateExclusive);
        setLimit(query, itemsToReturn);

        @SuppressWarnings("unchecked")
        List<Prevention> results = query.getResultList();
        return (results);
    }

    @Override
    public List<Prevention> findNotDeletedByDemographicIdAfterDatetime(Integer demographicId, Date dateTime) {
        Query query = entityManager.createQuery("select x from Prevention x where demographicId=?1 and lastUpdateDate> ?2");
        query.setParameter(1, demographicId);
        query.setParameter(2, dateTime);

        @SuppressWarnings("unchecked")
        List<Prevention> results = query.getResultList();

        return (results);
    }

    @Override
    public List<Integer> findNonDeletedIdsByDemographic(Integer demographicId) {
        Query query = entityManager.createQuery("select x.id from Prevention x where demographicId=?1 and deleted='0'");
        query.setParameter(1, demographicId);

        @SuppressWarnings("unchecked")
        List<Integer> results = query.getResultList();

        return (results);
    }

    @Override
    public List<Prevention> findNotDeletedByDemographicId(Integer demographicId) {
        Query query = entityManager.createQuery("select x from ?1 x where demographicId=?2 and deleted=?3");
        query.setParameter(1, modelClass.getSimpleName());
        query.setParameter(2, demographicId);
        query.setParameter(3, '0');

        @SuppressWarnings("unchecked")
        List<Prevention> results = query.getResultList();

        return (results);
    }

    @Override
    public List<Prevention> findByTypeAndDate(String preventionType, Date startDate, Date endDate) {
        Query query = entityManager.createQuery("select x from ?1 x where preventionType=?2 and preventionDate>=?3 and preventionDate<=?4 and deleted='0' and refused='0' order by preventionDate");
        query.setParameter(1, modelClass.getSimpleName());
        query.setParameter(2, preventionType);
        query.setParameter(3, startDate);
        query.setParameter(4, endDate);

        @SuppressWarnings("unchecked")
        List<Prevention> results = query.getResultList();

        return (results);
    }

    @Override
    public List<Prevention> findByTypeAndDemoNo(String preventionType, Integer demoNo) {
        Query query = entityManager.createQuery("select x from ?1 x where preventionType=?2 and demographicId=?3 and deleted='0' order by preventionDate");
        query.setParameter(1, modelClass.getSimpleName());
        query.setParameter(2, preventionType);
        query.setParameter(3, demoNo);

        @SuppressWarnings("unchecked")
        List<Prevention> results = query.getResultList();
        return (results);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Prevention> findActiveByDemoId(Integer demoId) {
        Query query = createQuery("p", "p.demographicId = ?1 and p.deleted <> '1' ORDER BY p.preventionType, p.preventionDate");
        query.setParameter(1, demoId);
        return query.getResultList();
    }

    @Override
    public List<Prevention> findUniqueByDemographicId(Integer demographicId) {
        Query query = entityManager.createQuery("select x from ?1 x where demographicId=?2 and deleted='0' GROUP BY preventionType ORDER BY preventionDate DESC");
        query.setParameter(1, modelClass.getSimpleName());
        query.setParameter(2, demographicId);

        @SuppressWarnings("unchecked")
        List<Prevention> results = query.getResultList();

        return (results);
    }


    @NativeSql("preventions")
    @Override
    public List<Integer> findNewPreventionsSinceDemoKey(String keyName) {

        String sql = "select distinct dr.demographic_no from preventions dr,demographic d,demographicExt e " +
        "where dr.demographic_no = d.demographic_no and d.demographic_no = e.demographic_no and e.key_val=?1 and dr.lastUpdateDate > e.value";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, keyName);
        return query.getResultList();
    }


}
