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

package ca.openosp.openo.oscarEncounter.data;

import java.util.ArrayList;

import org.oscarehr.common.dao.OscarCommLocationsDao;
import org.oscarehr.common.dao.RemoteAttachmentsDao;
import org.oscarehr.common.model.RemoteAttachments;
import org.oscarehr.util.SpringUtils;

import ca.openosp.openo.util.ConversionUtils;

public class EctRemoteAttachments {

    String demoNo;
    public ArrayList<String> messageIds;
    public ArrayList<String> savedBys;
    public ArrayList<String> dates;

    public EctRemoteAttachments() {
        demoNo = null;
        messageIds = null;
        savedBys = null;
        dates = null;
    }

    public void estMessageIds(String demo) {
        demoNo = demo;
        messageIds = new ArrayList<String>();
        savedBys = new ArrayList<String>();
        dates = new ArrayList<String>();

        RemoteAttachmentsDao dao = SpringUtils.getBean(RemoteAttachmentsDao.class);
        for (RemoteAttachments ra : dao.findByDemoNo(ConversionUtils.fromIntString(demoNo))) {
            dates.add(ConversionUtils.toDateString(ra.getDate()));
            messageIds.add("" + ra.getMessageId());
            savedBys.add(ra.getSavedBy());
        }
    }

    public ArrayList<String> getFromLocation(String messId) {
        ArrayList<String> retval = new ArrayList<String>();

        OscarCommLocationsDao dao = SpringUtils.getBean(OscarCommLocationsDao.class);
        for (Object[] o : dao.findFormLocationByMesssageId(messId)) {
            String locationDesc = String.valueOf(o[0]);
            String thesubject = String.valueOf(o[1]);

            retval.add(thesubject);
            retval.add(locationDesc);
        }

        return retval;
    }
}
