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

package ca.openosp.openo.oscarWaitingList.bean;

import java.util.ArrayList;
import java.util.List;

import ca.openosp.openo.common.dao.WaitingListDao;
import ca.openosp.openo.common.model.WaitingList;
import ca.openosp.openo.common.model.WaitingListName;
import ca.openosp.openo.ehrutil.SpringUtils;

import ca.openosp.openo.util.ConversionUtils;

public class WLPatientWaitingListBeanHandler {

    List<WLPatientWaitingListBean> patientWaitingList = new ArrayList<WLPatientWaitingListBean>();

    public WLPatientWaitingListBeanHandler(String demographicNo) {
        init(demographicNo);
    }

    public boolean init(String demographicNo) {
        WaitingListDao dao = SpringUtils.getBean(WaitingListDao.class);
        List<Object[]> lists = dao.findByDemographic(ConversionUtils.fromIntString(demographicNo));

        boolean verdict = true;
        for (Object[] l : lists) {
            WaitingListName name = (WaitingListName) l[0];
            WaitingList list = (WaitingList) l[1];

            WLPatientWaitingListBean wLBean = new WLPatientWaitingListBean(demographicNo, name.getId().toString(), name.getName(), String.valueOf(list.getPosition()), list.getNote(), ConversionUtils.toDateString(list.getOnListSince()));
            patientWaitingList.add(wLBean);
        }

        return verdict;
    }

    public List<WLPatientWaitingListBean> getPatientWaitingList() {
        return patientWaitingList;
    }
}
