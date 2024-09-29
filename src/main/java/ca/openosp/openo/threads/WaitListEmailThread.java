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

package ca.openosp.openo.threads;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.Logger;
import ca.openosp.openo.ehrutil.MiscUtils;

import ca.openosp.openo.OscarProperties;

public class WaitListEmailThread extends TimerTask {
    private static final Logger logger = MiscUtils.getLogger();

    private static Timer timer = new Timer("WaitListEmailThread Timer", true);

    private static TimerTask timerTask = null;

    public static synchronized void startTaskIfEnabled() {
        boolean enableEmailNotifications = Boolean.parseBoolean(OscarProperties.getInstance().getProperty("enable_wait_list_email_notifications"));
        long waitListNotificationPeriod = Long.parseLong(OscarProperties.getInstance().getProperty("wait_list_email_notification_period"));

        logger.info("WaitListEmailThread enabled=" + enableEmailNotifications + ", period=" + waitListNotificationPeriod);

        if (!enableEmailNotifications) return;

        if (timerTask == null) {
            timerTask = new WaitListEmailThread();
            // run 4 times per period so if it's behind it can slowly catch up
            timer.schedule(timerTask, 10000, waitListNotificationPeriod / 4);
        } else {
            logger.error("Start was called twice on this timer task object.", new Exception());
        }
    }

    public static synchronized void stopTask() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;

            logger.info("WaitListEmailThread has been unscheduled.");
        }
    }

    @Override
    public void run() {
        logger.debug("WaitListEmailThread start");

        try {
            notifyProgramAdmissions();
        } catch (Exception e) {
            logger.error("Unexpected Error", e);
        }

        logger.debug("WaitListEmailThread end");
    }

    /*
     * This method is no longer supported as it relies on WaitListManager's checkAndSendAdmissionIntervalNotification method.
     * For more details, please refer to the checkAndSendAdmissionIntervalNotification method.
     */
    private void notifyProgramAdmissions() {
        throw new UnsupportedOperationException("This method is no longer supported.");
        // String programIdsString=StringUtils.trimToNull(OscarProperties.getInstance().getProperty("wait_list_email_notification_program_ids"));

        // if (programIdsString==null) return;

        // String[] programIdsStringSplit=programIdsString.split(",");

        // ProgramDao programDao=(ProgramDao) SpringUtils.getBean(ProgramDao.class);
        // WaitListManager waitListManager=(WaitListManager) SpringUtils.getBean(WaitListManager.class);

        // for (String programIdString : programIdsStringSplit)
        // {
        // 	try {
        //         Integer programId=new Integer(programIdString);
        //         Program program=programDao.getProgram(programId);
        //         waitListManager.checkAndSendAdmissionIntervalNotification(program);
        //     } catch (Exception e) {
        //      logger.error("Unexpected error processing programId="+programIdString, e);
        //     }
        // }
    }
}