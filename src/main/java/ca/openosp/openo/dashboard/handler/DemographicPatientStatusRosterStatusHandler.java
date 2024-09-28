//CHECKSTYLE:OFF
/**
 * Copyright (c) 2013-2015. Department of Computer Science, University of Victoria. All Rights Reserved.
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
 * Department of Computer Science
 * LeadLab
 * University of Victoria
 * Victoria, Canada
 */

package ca.openosp.openo.dashboard.handler;

//import java.ehrutil.List;

import org.apache.logging.log4j.Logger;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

import net.sf.json.JSONArray;

import ca.openosp.openo.common.model.Demographic;
import ca.openosp.openo.common.dao.DemographicDao;
import ca.openosp.openo.common.dao.DemographicArchiveDao;
//import ca.openosp.openo.common.model.DemographicArchive; 
import ca.openosp.openo.common.dao.DemographicExtDao;
import ca.openosp.openo.common.dao.DemographicExtArchiveDao;
//import ca.openosp.openo.common.model.DemographicExt; 
//import ca.openosp.openo.common.model.DemographicExtArchive; 
import ca.openosp.openo.ehrutil.LoggedInInfo;

public class DemographicPatientStatusRosterStatusHandler {

    private static Logger logger = MiscUtils.getLogger();
    private LoggedInInfo loggedInInfo;
    static DemographicDao demographicDao = SpringUtils.getBean(DemographicDao.class);
    DemographicArchiveDao demographicArchiveDao = SpringUtils.getBean(DemographicArchiveDao.class);
    DemographicExtDao demographicExtDao = SpringUtils.getBean(DemographicExtDao.class);
    DemographicExtArchiveDao demographicExtArchiveDao = SpringUtils.getBean(DemographicExtArchiveDao.class);

    public Boolean setPatientStatusInactiveJson(String jsonString) {
        Boolean result = false;
        if (jsonString == null || jsonString.isEmpty()) return false;
        String providerNo = getProviderNo();
        if (providerNo == null || providerNo.isEmpty()) return false;

        if (!jsonString.startsWith("[")) {
            jsonString = "[" + jsonString;
        }
        if (!jsonString.endsWith("]")) {
            jsonString = jsonString + "]";
        }
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        Integer arraySize = jsonArray.size();
        for (int i = 0; i < arraySize; i++) {
            result = setPatientStatusInactive(jsonArray.getString(i));
            if (!result) return false;
        }
        return true;
    }

    public Boolean setPatientStatusInactive(String demographicNo) {
        Demographic demographic = demographicDao.getDemographic(demographicNo);
        if (demographic != null) {
            demographic.setPatientStatus("IN");
            demographic.setPatientStatusDate(null);
            String providerNo = getProviderNo();
            if (providerNo != null && !providerNo.isEmpty()) {
                demographic.setLastUpdateUser(providerNo);
            } else {
                return false;
            }
            demographic.setLastUpdateDate(new java.util.Date());
            demographicDao.save(demographic);
            logger.info("demographic_no=" + demographicNo + " set INACTIVE by " + providerNo);
            return true;
        }
        return false;
    }

    public Boolean setRosterStatus(String demographicNo, String rosterStatus) {
        Demographic demographic = demographicDao.getDemographic(demographicNo);
        if (demographic != null) {
            demographic.setRosterStatus(rosterStatus);
            demographic.setRosterDate(null);
            //TODO: Determine whether we need to do more here to save roster history
            String providerNo = getProviderNo();
            if (providerNo != null && !providerNo.isEmpty()) {
                demographic.setLastUpdateUser(providerNo);
            } else {
                return false;
            }
            demographic.setLastUpdateDate(new java.util.Date());
            demographicDao.save(demographic);
            return true;
        }
        return false;
    }

    public LoggedInInfo getLoggedinInfo() {
        return loggedInInfo;
    }

    public void setLoggedinInfo(LoggedInInfo loggedInInfo) {
        this.loggedInInfo = loggedInInfo;
    }

    private String getProviderNo() {
        String providerNo = null;
        if (loggedInInfo != null) {
            providerNo = getLoggedinInfo().getLoggedInProviderNo();
        }
        return providerNo;
    }

    //Do we need to do anything with these tables?
    //DemographicExt
    //DemographicArchive
    //DemographicArchiveExt
    //Log

}
    
