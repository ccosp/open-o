//CHECKSTYLE:OFF
/**
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
 */
package ca.openosp.openo.PMmodule.caisi_integrator;

import java.util.List;

import ca.openosp.openo.caisi_integrator.ws.FacilityWs;
import ca.openosp.openo.caisi_integrator.ws.ImportLog;
import ca.openosp.openo.common.dao.FacilityDao;
import ca.openosp.openo.common.dao.IntegratorFileLogDao;
import ca.openosp.openo.common.jobs.OscarRunnable;
import ca.openosp.openo.common.model.IntegratorFileLog;
import ca.openosp.openo.common.model.Provider;
import ca.openosp.openo.common.model.Security;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

public class IntegratorFileLogUpdateJob implements OscarRunnable {

    private Provider provider;
    private Security security;


    @Override
    public void run() {

        FacilityDao facilityDao = SpringUtils.getBean(FacilityDao.class);
        IntegratorFileLogDao integratorFileLogDao = SpringUtils.getBean(IntegratorFileLogDao.class);


        MiscUtils.getLogger().info("Running IntegratorFileLogUpdateJob");
        LoggedInInfo x = new LoggedInInfo();
        x.setLoggedInProvider(provider);
        x.setLoggedInSecurity(security);
        try {
            FacilityWs service = CaisiIntegratorManager.getFacilityWs(x, facilityDao.findAll(true).get(0));


            List<IntegratorFileLog> ourLogs = integratorFileLogDao.findAllWithNoCompletedIntegratorStatus();
            for (IntegratorFileLog ourLog : ourLogs) {
                List<ImportLog> theirLogs = service.getImportLogByFilenameAndChecksum(ourLog.getFilename(), ourLog.getChecksum());
                String bestStatus = null;
                for (ImportLog theirLog : theirLogs) {
                    if (bestStatus == null) {
                        bestStatus = theirLog.getStatus();
                        continue;
                    }
                    if (theirLog.getStatus().equals("ERROR") && bestStatus.equals("PROCESSING")) {
                        bestStatus = theirLog.getStatus();
                    }

                    if (theirLog.getStatus().equals("COMPLETED")) {
                        bestStatus = theirLog.getStatus();
                    }
                }
                ourLog.setIntegratorStatus(bestStatus);
                integratorFileLogDao.merge(ourLog);
                MiscUtils.getLogger().info("Updated " + ourLog.getFilename() + " with status " + ourLog.getIntegratorStatus());
            }


        } catch (Exception e) {
            MiscUtils.getLogger().error("Error getting file statuses", e);
        }

    }

    public void setLoggedInProvider(Provider provider) {
        this.provider = provider;
    }

    public void setLoggedInSecurity(Security security) {
        this.security = security;
    }

    @Override
    public void setConfig(String string) {
    }

}
