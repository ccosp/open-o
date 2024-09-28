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


package ca.openosp.openo.oscarPrevention.reports;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Logger;
import ca.openosp.openo.common.dao.DemographicArchiveDao;
import ca.openosp.openo.common.model.Demographic;
import ca.openosp.openo.common.model.DemographicArchive;
import ca.openosp.openo.managers.DemographicManager;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

public final class PreventionReportUtil {
    private static Logger logger = MiscUtils.getLogger();

    public static DemographicManager demographicManager = SpringUtils.getBean(DemographicManager.class);
    public static DemographicArchiveDao demographicArchiveDao = (DemographicArchiveDao) SpringUtils.getBean(DemographicArchiveDao.class);

    public static boolean wasRostered(LoggedInInfo loggedInInfo, Integer demographicId, Date onThisDate) {
        logger.debug("Checking rosterd:" + demographicId);
        Demographic demographic = demographicManager.getDemographic(loggedInInfo, demographicId);

        if (rosteredDuringThisTimeDemographic(onThisDate, demographic.getRosterDate(), demographic.getRosterTerminationDate()))
            return (true);

        List<DemographicArchive> archives = demographicArchiveDao.findByDemographicNo(demographicId);
        for (DemographicArchive archive : archives) {
            if (rosteredDuringThisTimeDemographicArchive(onThisDate, archive.getRosterDate(), archive.getRosterTerminationDate()))
                return (true);
        }

        return (false);
    }


    public static boolean wasEnrolledToThisProvider(LoggedInInfo loggedInInfo, Integer demographicId, Date onThisDate, String providerNo) {
        logger.debug("Checking rosterd:" + demographicId + " for this date " + onThisDate + " for this providerNo " + providerNo);
        if (providerNo == null) {
            return false;
        }


        Demographic demographic = demographicManager.getDemographic(loggedInInfo, demographicId);

        if (rosteredDuringThisTimeDemographic(onThisDate, demographic.getRosterDate(), demographic.getRosterTerminationDate()) && providerNo.equals(demographic.getRosterEnrolledTo()))
            return (true);

        List<DemographicArchive> archives = demographicArchiveDao.findByDemographicNo(demographicId);
        for (DemographicArchive archive : archives) {
            if (rosteredDuringThisTimeDemographicArchive(onThisDate, archive.getRosterDate(), archive.getRosterTerminationDate()) && providerNo.equals(demographic.getRosterEnrolledTo()))
                return (true);
        }

        return (false);
    }

    public static boolean wasRosteredToThisProvider(LoggedInInfo loggedInInfo, Integer demographicId, Date onThisDate, String providerNo) {
        logger.debug("Checking rosterd:" + demographicId + " for this date " + onThisDate + " for this providerNo " + providerNo);
        if (providerNo == null) {
            return false;
        }


        Demographic demographic = demographicManager.getDemographic(loggedInInfo, demographicId);

        if (rosteredDuringThisTimeDemographic(onThisDate, demographic.getRosterDate(), demographic.getRosterTerminationDate()) && providerNo.equals(demographic.getProviderNo()))
            return (true);

        List<DemographicArchive> archives = demographicArchiveDao.findByDemographicNo(demographicId);
        for (DemographicArchive archive : archives) {
            if (rosteredDuringThisTimeDemographicArchive(onThisDate, archive.getRosterDate(), archive.getRosterTerminationDate()) && providerNo.equals(demographic.getProviderNo()))
                return (true);
        }

        return (false);
    }

    private static boolean rosteredDuringThisTimeDemographic(Date onThisDate, Date rosterStart, Date rosterEnd) {

        if (rosterStart != null) {
            if (rosterStart.before(onThisDate)) {
                if (rosterEnd == null || rosterEnd.after(onThisDate)) {
                    logger.debug("true:" + onThisDate + ", " + rosterStart + ", " + rosterEnd);
                    return (true);
                }
            }
        }

        logger.debug("false:" + onThisDate + ", " + rosterStart + ", " + rosterEnd);
        return (false);
    }

    private static boolean rosteredDuringThisTimeDemographicArchive(Date onThisDate, Date rosterStart, Date rosterEnd) {
        // algorithm for demographic archive must only look at archiv erecords with end dates as the archive is populated upon every change not just people being unrostered.
        if (rosterStart != null && rosterEnd != null) {
            if (rosterStart.before(onThisDate) && rosterEnd.after(onThisDate)) {
                logger.debug("true:" + onThisDate + ", " + rosterStart + ", " + rosterEnd);
                return (true);
            }
        }

        logger.debug("false:" + onThisDate + ", " + rosterStart + ", " + rosterEnd);
        return (false);
    }
}
