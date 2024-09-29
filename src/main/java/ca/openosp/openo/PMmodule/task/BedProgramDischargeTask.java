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

package ca.openosp.openo.PMmodule.task;

import java.util.Date;
import java.util.TimerTask;

import org.apache.logging.log4j.Logger;
import ca.openosp.openo.PMmodule.exception.AdmissionException;
import ca.openosp.openo.PMmodule.model.Program;
import ca.openosp.openo.PMmodule.service.AdmissionManager;
import ca.openosp.openo.PMmodule.service.ProgramManager;
import ca.openosp.openo.PMmodule.utility.DateTimeFormatUtils;
import ca.openosp.openo.common.model.Bed;
import ca.openosp.openo.common.model.BedDemographic;
import ca.openosp.openo.common.model.Provider;
import ca.openosp.openo.managers.BedDemographicManager;
import ca.openosp.openo.managers.BedManager;
import ca.openosp.openo.ehrutil.DbConnectionFilter;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.ShutdownException;

public class BedProgramDischargeTask extends TimerTask {

    private static final Logger log = MiscUtils.getLogger();

    // TODO IC Bedlog bedProgram.getDischargeTime();
    private static final String DISCHARGE_TIME = "8:00 AM";
    private static final long PERIOD = 3600000;

    private ProgramManager programManager;
    private BedManager bedManager;
    private BedDemographicManager bedDemographicManager;
    private AdmissionManager admissionManager;

    public void setProgramManager(ProgramManager programManager) {
        this.programManager = programManager;
    }

    public void setBedManager(BedManager bedManager) {
        this.bedManager = bedManager;
    }

    public void setBedDemographicManager(BedDemographicManager bedDemographicManager) {
        this.bedDemographicManager = bedDemographicManager;
    }

    @Override
    public void run() {
        try {
            log.debug("start bed program discharge task");

            Program[] bedPrograms = programManager.getBedPrograms();

            if (bedPrograms == null) {
                log.error("getBedPrograms returned null");
                return;
            }

            for (Program bedProgram : bedPrograms) {
                MiscUtils.checkShutdownSignaled();

                Date dischargeTime = DateTimeFormatUtils.getTimeFromString(DISCHARGE_TIME);
                Date previousExecutionTime = DateTimeFormatUtils.getTimeFromLong(scheduledExecutionTime() - PERIOD);
                Date currentExecutionTime = DateTimeFormatUtils.getTimeFromLong(scheduledExecutionTime());

                // previousExecutionTime < dischargeTime <= currentExecutionTime
                if (previousExecutionTime.before(dischargeTime) && (dischargeTime.equals(currentExecutionTime) || dischargeTime.before(currentExecutionTime))) {
                    Bed[] reservedBeds = bedManager.getBedsByProgram(bedProgram.getId(), true);

                    if (reservedBeds == null) {
                        log.error("getBedsByProgram returned null for bed program with id: " + bedProgram.getId());
                        continue;
                    }

                    for (Bed reservedBed : reservedBeds) {
                        MiscUtils.checkShutdownSignaled();

                        BedDemographic bedDemographic = bedDemographicManager.getBedDemographicByBed(reservedBed.getId());

                        if (bedDemographic != null && bedDemographic.getId() != null && bedDemographic.isExpired()) {
                            try {
                                admissionManager.processDischargeToCommunity(Program.DEFAULT_COMMUNITY_PROGRAM_ID, bedDemographic.getId().getDemographicNo(), Provider.SYSTEM_PROVIDER_NO, "bed reservation ended - automatically discharged", "0", null);
                            } catch (AdmissionException e) {
                                log.error("Error discharging to community", e);
                            }
                        }
                    }
                }
            }

            log.debug("finish bed program discharge task");
        } catch (ShutdownException e) {
            log.debug("BedProgramDischargeTask noticed shutdown signaled.");
        } finally {
            DbConnectionFilter.releaseAllThreadDbResources();
        }
    }

}
