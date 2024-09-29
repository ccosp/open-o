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
package ca.openosp.openo.renal;

import java.util.TimerTask;

import org.apache.logging.log4j.Logger;
import ca.openosp.openo.common.model.Provider;
import ca.openosp.openo.common.model.Security;
import ca.openosp.openo.ehrutil.DbConnectionFilter;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;

import ca.openosp.openo.OscarProperties;

public class CkdScreenerSchedulerJob extends TimerTask {

    private Logger logger = MiscUtils.getLogger();

    @Override
    public void run() {
        if (!OscarProperties.getInstance().getProperty("ORN_PILOT", "no").equalsIgnoreCase("yes")) {
            return;
        }

        Provider provider = new Provider();
        provider.setProviderNo(OscarProperties.getInstance().getProperty("ORN_PILOT_USER", "999998"));
        Security security = new Security();
        security.setId(0);

        LoggedInInfo loggedInInfo = new LoggedInInfo();
        loggedInInfo.setLoggedInProvider(provider);
        loggedInInfo.setLoggedInSecurity(security);

        try {
            logger.info("starting renal background ckd screening job");

            CkdScreener screener = new CkdScreener();
            screener.screenPopulation(loggedInInfo);

            logger.info("done renal background ckd screening job");

        } catch (Throwable e) {
            logger.error("Error", e);
        } finally {
            DbConnectionFilter.releaseAllThreadDbResources();
        }
    }

}
