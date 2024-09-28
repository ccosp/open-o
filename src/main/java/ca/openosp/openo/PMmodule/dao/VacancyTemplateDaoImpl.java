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

package ca.openosp.openo.PMmodule.dao;

import java.util.List;

import javax.persistence.Query;

import ca.openosp.openo.PMmodule.model.VacancyTemplate;
import ca.openosp.openo.common.dao.AbstractDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class VacancyTemplateDaoImpl extends AbstractDaoImpl<VacancyTemplate> implements VacancyTemplateDao {

    public VacancyTemplateDaoImpl() {
        super(VacancyTemplate.class);
    }

    @Override
    public void saveVacancyTemplate(VacancyTemplate obj) {
        persist(obj);
    }

    @Override
    public void mergeVacancyTemplate(VacancyTemplate obj) {
        merge(obj);
    }

    @Override
    public VacancyTemplate getVacancyTemplate(Integer templateId) {
        return find(templateId);
    }

    @Override
    public List<VacancyTemplate> getVacancyTemplateByWlProgramId(Integer wlProgramId) {
        Query query = entityManager.createQuery("select x from VacancyTemplate x where x.wlProgramId=?");
        query.setParameter(0, wlProgramId);

        @SuppressWarnings("unchecked")
        List<VacancyTemplate> results = query.getResultList();

        return results;
    }

    @Override
    public List<VacancyTemplate> getActiveVacancyTemplatesByWlProgramId(Integer wlProgramId) {
        Query query = entityManager.createQuery("select x from VacancyTemplate x where x.wlProgramId=? and x.active=?");
        query.setParameter(0, wlProgramId);
        query.setParameter(1, true);

        @SuppressWarnings("unchecked")
        List<VacancyTemplate> results = query.getResultList();

        return results;
    }
}
