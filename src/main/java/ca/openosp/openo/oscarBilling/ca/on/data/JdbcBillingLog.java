//CHECKSTYLE:OFF
/**
 * Copyright (c) 2006-. OSCARservice, OpenSoft System. All Rights Reserved.
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
 */

package ca.openosp.openo.oscarBilling.ca.on.data;

import java.util.Date;

import org.oscarehr.billing.CA.ON.dao.BillingONProcDao;
import org.oscarehr.billing.CA.ON.model.BillingONProc;
import org.oscarehr.util.SpringUtils;

public class JdbcBillingLog {

    private BillingONProcDao dao = SpringUtils.getBean(BillingONProcDao.class);

    public boolean addBillingLog(String providerNo, String action, String comment, String object) {
        BillingONProc b = new BillingONProc();
        b.setCreator(providerNo);
        b.setAction(action);
        b.setComment(comment);
        b.setObject(object);
        b.setCreateDateTime(new Date());
        dao.persist(b);
        return true;
    }

}
