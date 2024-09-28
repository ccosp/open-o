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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.oscarehr.common.dao.WaitingListNameDao;
import org.oscarehr.common.model.WaitingListName;
import org.oscarehr.util.SpringUtils;

public class WLWaitingListNameBeanHandler {

    List<WLWaitingListNameBean> waitingListNameList = new ArrayList<WLWaitingListNameBean>();
    List<String> waitingListNames = new ArrayList<String>();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //  private MyGroupDao myGroupDao = SpringUtils.getBean(MyGroupDao.class);
    private WaitingListNameDao waitingListNameDao = SpringUtils.getBean(WaitingListNameDao.class);
    //  private ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);

    public WLWaitingListNameBeanHandler(String groupNo, String providerNo) {
        init(groupNo, providerNo);
    }

    public boolean init(String groupNo, String providerNo) {
        List<WaitingListName> wlNames = null;

        wlNames = waitingListNameDao.findCurrentByGroup(groupNo);

        for (WaitingListName tmp : wlNames) {
            WLWaitingListNameBean wLBean =
                    new WLWaitingListNameBean(String.valueOf(tmp.getId()), tmp.getName(), tmp.getGroupNo(), tmp.getProviderNo(), formatter.format(tmp.getCreateDate()));

            waitingListNameList.add(wLBean);
            waitingListNames.add(tmp.getName());
        }


        return true;
    }

    public List<WLWaitingListNameBean> getWaitingListNameList() {
        return waitingListNameList;
    }

    public List<String> getWaitingListNames() {
        return waitingListNames;
    }

}
