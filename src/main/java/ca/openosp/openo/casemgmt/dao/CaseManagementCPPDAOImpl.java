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

package ca.openosp.openo.casemgmt.dao;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Logger;
import ca.openosp.openo.casemgmt.model.CaseManagementCPP;
import ca.openosp.openo.ehrutil.MiscUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

/*
 * Updated by Eugene Petruhin on 09 jan 2009 while fixing #2482832 & #2494061
 */
public class CaseManagementCPPDAOImpl extends HibernateDaoSupport implements CaseManagementCPPDAO {

    private Logger log = MiscUtils.getLogger();

    @Override
    public CaseManagementCPP getCPP(String demographic_no) {
        List results = this.getHibernateTemplate().find(
                "from CaseManagementCPP cpp where cpp.demographic_no = ? order by update_date desc",
                new Object[]{demographic_no});
        return (results.size() != 0) ? (CaseManagementCPP) results.get(0) : null;
    }

    @Override
    public void saveCPP(CaseManagementCPP cpp) {

        String fhist = cpp.getFamilyHistory() == null ? "" : cpp.getFamilyHistory();
        String mhist = cpp.getMedicalHistory() == null ? "" : cpp.getMedicalHistory();
        String ongoing = cpp.getOngoingConcerns() == null ? "" : cpp.getOngoingConcerns();
        String rem = cpp.getReminders() == null ? "" : cpp.getReminders();
        String shist = cpp.getSocialHistory() == null ? "" : cpp.getSocialHistory();
        String ofnum = cpp.getOtherFileNumber() == null ? "" : cpp.getOtherFileNumber();
        String ossystem = cpp.getOtherSupportSystems() == null ? "" : cpp.getOtherSupportSystems();
        String pm = cpp.getPastMedications() == null ? "" : cpp.getPastMedications();

        cpp.setFamilyHistory(fhist);
        cpp.setMedicalHistory(mhist);
        cpp.setOngoingConcerns(ongoing);
        cpp.setReminders(rem);
        cpp.setSocialHistory(shist);
        cpp.setUpdate_date(new Date());
        cpp.setOtherFileNumber(ofnum);
        cpp.setOtherSupportSystems(ossystem);
        cpp.setPastMedications(pm);

        if (log.isDebugEnabled()) {
            log.debug("Saving or updating a CPP: " + cpp);
        }

        this.getHibernateTemplate().saveOrUpdate(cpp);

    }
}
