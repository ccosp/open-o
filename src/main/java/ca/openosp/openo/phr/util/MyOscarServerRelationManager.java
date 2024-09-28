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
package ca.openosp.openo.phr.util;

import java.util.List;

import org.apache.logging.log4j.Logger;
import ca.openosp.openo.myoscar.client.ws_manager.MyOscarServerWebServicesManager;
import ca.openosp.openo.myOscar.utils.MyOscarLoggedInInfo;
import ca.openosp.openo.myoscar_server.ws.AccountWs;
import ca.openosp.openo.myoscar_server.ws.RelationshipTransfer4;
import ca.openosp.openo.ehrutil.MiscUtils;

/**
 * @deprecated 2012-11-22 use AccountManager instead, it should have all the account relationship calls needed.
 */
public class MyOscarServerRelationManager {
    private static final Logger logger = MiscUtils.getLogger();

    private static List<RelationshipTransfer4> getRelationShipTransferFromServer(MyOscarLoggedInInfo myOscarLoggedInInfo, Long targetMyOscarUserId) {
        AccountWs accountWs = MyOscarServerWebServicesManager.getAccountWs(myOscarLoggedInInfo);
        final int REASONABLE_RELATIONSHIP_LIMIT = 1024;
        List<RelationshipTransfer4> relationList = accountWs.getRelationshipsByPersonId2(targetMyOscarUserId, 0, REASONABLE_RELATIONSHIP_LIMIT);
        if (relationList.size() >= REASONABLE_RELATIONSHIP_LIMIT)
            logger.error("Error, we hit a hard coded limit. targetMyOscarUserId=" + targetMyOscarUserId);
        return relationList;
    }

    public static List<RelationshipTransfer4> getRelationData(MyOscarLoggedInInfo myOscarLoggedInInfo, Long targetMyOscarUserId) {
        List<RelationshipTransfer4> relationList = getRelationShipTransferFromServer(myOscarLoggedInInfo, targetMyOscarUserId);
        return relationList;
    }

    public static boolean hasPatientRelationship(MyOscarLoggedInInfo myOscarLoggedInInfo, Long targetMyOscarUserId) {

        List<RelationshipTransfer4> relationList = getRelationData(myOscarLoggedInInfo, targetMyOscarUserId);
        if (relationList != null) {
            for (RelationshipTransfer4 rt : relationList) {
                if (rt.getRelation().equals("PatientPrimaryCareProvider")) {
                    if (rt.getPerson2().getPersonId().equals(myOscarLoggedInInfo.getLoggedInPersonId()) && rt.getPerson2VerificationDate() != null) {
                        return (true);
                    }
                }
            }
        }
        return false;
    }
}
