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

import ca.openosp.openo.common.model.ScheduleTemplateCode;
import org.springframework.stereotype.Repository;

@Repository
public class ScheduleTemplateCodeDaoImpl extends AbstractDaoImpl<ScheduleTemplateCode> implements ScheduleTemplateCodeDao {

    public ScheduleTemplateCodeDaoImpl() {
        super(ScheduleTemplateCode.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ScheduleTemplateCode> findAll() {
        Query query = createQuery("x", null);
        return query.getResultList();
    }

    @Override
    public ScheduleTemplateCode getByCode(char code) {
        //Query query = entityManager.createQuery("FROM " + modelClass.getSimpleName() + " bst WHERE bst.id IN (:typeCodes)");
        Query query = entityManager.createQuery("select s from ScheduleTemplateCode s where s.code=?");
        query.setParameter(0, code);

        @SuppressWarnings("unchecked")
        List<ScheduleTemplateCode> results = query.getResultList();
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public List<ScheduleTemplateCode> findTemplateCodes() {
        Query query = entityManager.createQuery("select s from ScheduleTemplateCode s where s.bookinglimit > 0 and s.duration <>''");

        @SuppressWarnings("unchecked")
        List<ScheduleTemplateCode> results = query.getResultList();

        return results;
    }

    @Override
    public ScheduleTemplateCode findByCode(String code) {
        char codeChar = code.charAt(0);
        Query query = entityManager.createQuery("select s from ScheduleTemplateCode s where s.code like :code");
        query.setParameter("code", codeChar);

        @SuppressWarnings("unchecked")
        List<ScheduleTemplateCode> results = query.getResultList();
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }
}
