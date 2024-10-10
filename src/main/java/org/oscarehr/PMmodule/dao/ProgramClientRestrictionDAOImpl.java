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

package org.oscarehr.PMmodule.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.oscarehr.PMmodule.model.ProgramClientRestriction;
import org.oscarehr.common.dao.DemographicDao;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

/**
 *
 */
public class ProgramClientRestrictionDAOImpl extends HibernateDaoSupport implements ProgramClientRestrictionDAO {
    private DemographicDao demographicDao;
    private ProgramDao programDao;
    private ProviderDao providerDao;

    public Collection<ProgramClientRestriction> find(int programId, int demographicNo) {

        String sSQL = "from ProgramClientRestriction pcr where pcr.enabled = true and pcr.programId = ?0 and pcr.demographicNo = ?1 order by pcr.startDate";
        List<ProgramClientRestriction> pcrs = (List<ProgramClientRestriction>) getHibernateTemplate().find(sSQL, new Object[]{programId, demographicNo});
        for (ProgramClientRestriction pcr : pcrs) {
            setRelationships(pcr);
        }
        return pcrs;
    }

    public void save(ProgramClientRestriction restriction) {
        getHibernateTemplate().saveOrUpdate(restriction);
    }

    public ProgramClientRestriction find(int restrictionId) {
        return setRelationships(getHibernateTemplate().get(ProgramClientRestriction.class, restrictionId));
    }

    public Collection<ProgramClientRestriction> findForProgram(int programId) {
        String sSQL = "from ProgramClientRestriction pcr where pcr.enabled = true and pcr.programId = ?0 order by pcr.demographicNo";
        Collection<ProgramClientRestriction> pcrs = (Collection<ProgramClientRestriction>) getHibernateTemplate().find(sSQL, programId);
        for (ProgramClientRestriction pcr : pcrs) {
            setRelationships(pcr);
        }
        return pcrs;
    }

    public Collection<ProgramClientRestriction> findDisabledForProgram(int programId) {
        String sSQL = "from ProgramClientRestriction pcr where pcr.enabled = false and pcr.programId = ?0 order by pcr.demographicNo";
        Collection<ProgramClientRestriction> pcrs = (Collection<ProgramClientRestriction>) getHibernateTemplate().find(sSQL, programId);
        for (ProgramClientRestriction pcr : pcrs) {
            setRelationships(pcr);
        }
        return pcrs;
    }

    public Collection<ProgramClientRestriction> findForClient(int demographicNo) {
        String sSQL = "from ProgramClientRestriction pcr where pcr.enabled = true and pcr.demographicNo = ?0 order by pcr.programId";
        Collection<ProgramClientRestriction> pcrs = (Collection<ProgramClientRestriction>) getHibernateTemplate().find(sSQL, demographicNo);
        for (ProgramClientRestriction pcr : pcrs) {
            setRelationships(pcr);
        }
        return pcrs;
    }

    public Collection<ProgramClientRestriction> findForClient(int demographicNo, int facilityId) {
        String sSQL = "from ProgramClientRestriction pcr where pcr.enabled = true and pcr.demographicNo = ?0" +
        " and pcr.programId in (select s.id from Program s where s.facilityId = ?1 or s.facilityId is null) order by pcr.programId";
        Object params[] = new Object[]{Integer.valueOf(demographicNo), facilityId};
        Collection<ProgramClientRestriction> pcrs = (Collection<ProgramClientRestriction>) getHibernateTemplate().find(sSQL, params);
        for (ProgramClientRestriction pcr : pcrs) {
            setRelationships(pcr);
        }
        return pcrs;
    }

    public Collection<ProgramClientRestriction> findDisabledForClient(int demographicNo) {
        String sSQL = "from ProgramClientRestriction pcr where pcr.enabled = false and pcr.demographicNo = ?0 order by pcr.programId";
        Collection<ProgramClientRestriction> pcrs = (Collection<ProgramClientRestriction>) getHibernateTemplate().find(sSQL, demographicNo);
        for (ProgramClientRestriction pcr : pcrs) {
            setRelationships(pcr);
        }
        return pcrs;
    }

    private ProgramClientRestriction setRelationships(ProgramClientRestriction pcr) {
        pcr.setClient(demographicDao.getDemographic("" + pcr.getDemographicNo()));
        pcr.setProgram(programDao.getProgram(pcr.getProgramId()));
        pcr.setProvider(providerDao.getProvider(pcr.getProviderNo()));

        return pcr;
    }

    @Required
    public void setDemographicDao(DemographicDao demographicDao) {
        this.demographicDao = demographicDao;
    }

    @Required
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }

    @Required
    public void setProviderDao(ProviderDao providerDao) {
        this.providerDao = providerDao;
    }

}
