//CHECKSTYLE:OFF
/**
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
 */

package ca.openosp.openo.PMmodule.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Logger;
import ca.openosp.openo.common.dao.DemographicDao;
import ca.openosp.openo.PMmodule.model.Intake;
import ca.openosp.openo.PMmodule.service.GenericIntakeManager;
import ca.openosp.openo.PMmodule.service.StreetHealthReportManager;
import ca.openosp.openo.common.model.Demographic;
import ca.openosp.openo.ehrutil.MiscUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class StreetHealthReportManagerImpl implements StreetHealthReportManager {

    private Logger log = MiscUtils.getLogger();

    private DemographicDao demographicDao;
    private GenericIntakeManager intakeMgr;

    public void setDemographicDao(DemographicDao dao) {
        this.demographicDao = dao;
    }

    public void setGenericIntakeManager(GenericIntakeManager mgr) {
        this.intakeMgr = mgr;
    }

    public List getCohort(Date beginDate, Date endDate, int facilityId) {
        List<Demographic> clients = demographicDao.getClients();
//		//return streetHealthDao.getCohort(beginDate, endDate, demographicDao.getClients());
        if (beginDate == null && endDate == null) {
            return new ArrayList();
        }

        List results = new ArrayList();

        if (log.isDebugEnabled()) {
            log.debug("Getting Cohort: " + beginDate + " to " + endDate);
        }

        for (int x = 0; x < clients.size(); x++) {
            Demographic client = clients.get(x);
            if (client.getPatientStatus().equals("AC")) {
                // get current intake

                Intake intake = this.intakeMgr.getMostRecentQuickIntake(client.getDemographicNo(), facilityId);

                if (intake == null) {
                    continue;
                }

                // parse date
                Date admissionDate = null;
                try {
                    //admissionDate = formatter.parse(intake.getAnswerKeyValues().get("Admission Date"));
                    admissionDate = intake.getCreatedOn().getTime();
                } catch (Exception e) {
                }
                if (admissionDate == null) {
                    log.warn("invalid admission date for client #" + client.getDemographicNo());
                    continue;
                }
                // does it belong in this cohort?
                if (beginDate != null && endDate != null) {
                    if (admissionDate.after(beginDate) && admissionDate.before(endDate)) {
                        log.debug("admissionDate=" + admissionDate);
                        // ok, add this client
                        Object[] ar = new Object[2];
                        ar[0] = intake;
                        ar[1] = client;
                        results.add(ar);
                    }
                }
                if (beginDate == null && admissionDate.before(endDate)) {
                    log.debug("admissionDate=" + admissionDate);
                    // ok, add this client
                    Object[] ar = new Object[2];
                    ar[0] = intake;
                    ar[1] = client;
                    results.add(ar);
                }
            }
        }

        log.info("getCohort: found " + results.size() + " results. (" + beginDate + " - " + endDate + ")");

        return results;
    }


}
