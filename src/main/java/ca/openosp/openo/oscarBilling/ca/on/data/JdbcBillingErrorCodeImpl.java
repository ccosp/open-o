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

import org.oscarehr.common.dao.BillingONErrorCodeDao;
import org.oscarehr.common.model.BillingONErrorCode;
import org.oscarehr.util.SpringUtils;

public class JdbcBillingErrorCodeImpl {

    public String getCodeDesc(String strServiceCode) {
        BillingONErrorCodeDao dao = SpringUtils.getBean(BillingONErrorCodeDao.class);
        BillingONErrorCode code = dao.find(strServiceCode);
        if (code != null) {
            return code.getDescription();
        }
        return null;
    }

}
