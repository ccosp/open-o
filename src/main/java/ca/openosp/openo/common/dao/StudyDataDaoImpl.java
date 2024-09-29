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

import ca.openosp.openo.common.model.AbstractModel;
import ca.openosp.openo.common.model.StudyData;
import org.springframework.stereotype.Repository;

@Repository
public class StudyDataDaoImpl extends AbstractDaoImpl<StudyData> implements StudyDataDao {

    public StudyDataDaoImpl() {
        super(StudyData.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<StudyData> findByContent(String content) {
        Query query = createQuery("s", "s.content LIKE :content AND s.deleted = false");
        query.setParameter("content", content);
        return query.getResultList();
    }

    @Override
    public StudyData findSingleByContent(String content) {
        Query query = createQuery("s", "s.content LIKE :content AND s.deleted = false");
        query.setParameter("content", content);
        return getSingleResultOrNull(query);
    }

    @Override
    public int removeByDemoAndStudy(Integer demographicNo, Integer studyId) {
        Query query = entityManager.createQuery("from StudyData s where s.demographicNo = :demoNo and s.studyNo = :studyId");
        query.setParameter("demoNo", demographicNo);
        query.setParameter("studyId", studyId);

        @SuppressWarnings("unchecked")
        List<StudyData> resultList = query.getResultList();

        int i = 0;
        for (StudyData data : resultList) {
            remove(data);
            i++;
        }
        return i;
    }

    @Override
    public List<StudyData> findByDemoAndStudy(Integer demographicNo, Integer studyId) {
        Query query = entityManager.createQuery("select s from StudyData s where s.demographicNo = :demoNo and s.studyNo = :studyId and s.deleted = false");

        query.setParameter("demoNo", demographicNo);
        query.setParameter("studyId", studyId);

        @SuppressWarnings("unchecked")
        List<StudyData> studyDataList = query.getResultList();

        return studyDataList;
    }

    @Override
    public void remove(StudyData o) {
        o.setDeleted(true);
        entityManager.merge(o);
    }
}
