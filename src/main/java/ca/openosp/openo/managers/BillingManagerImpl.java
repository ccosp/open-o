//CHECKSTYLE:OFF
/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
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
 * <p>
 * Modifications made by Magenta Health in 2024.
 */
package ca.openosp.openo.managers;

import java.util.ArrayList;
import java.util.List;

import ca.openosp.openo.common.dao.CtlBillingServiceDao;
import ca.openosp.openo.common.dao.CtlBillingServiceDaoImpl;
import ca.openosp.openo.managers.model.ServiceType;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillingManagerImpl implements BillingManager {

    @Autowired
    CtlBillingServiceDao ctlBillingServiceDao;

    /*
     * I'm only doing that conversion in the manager because I don't have time to
     * refactor the DAO method..but given more time..that's where I would do it.
     * Regardless those other calls should be moved over to calling this one.
     */
    @Override
    public List<ServiceType> getUniqueServiceTypes(LoggedInInfo loggedInInfo) {
        return getUniqueServiceTypes(loggedInInfo, CtlBillingServiceDaoImpl.DEFAULT_STATUS);
    }

    /*
     * I'm only doing that conversion in the manager because I don't have time to
     * refactor the DAO method..but given more time..that's where I would do it.
     * Regardless those other calls should be moved over to calling this one.
     */
    @Override
    public List<ServiceType> getUniqueServiceTypes(LoggedInInfo loggedInInfo, String type) {
        List<ServiceType> result = new ArrayList<ServiceType>();

        for (Object[] r : ctlBillingServiceDao.getUniqueServiceTypes(type)) {
            result.add(new ServiceType((String) r[0], (String) r[1]));
        }

        return result;
    }
}
