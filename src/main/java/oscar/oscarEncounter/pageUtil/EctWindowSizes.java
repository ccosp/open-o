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

/*
 * EctWindowSizes.java
 *
 * Created on April 2, 2004, 2:47 PM
 */

package oscar.oscarEncounter.pageUtil;

import java.util.Properties;

import org.oscarehr.common.dao.EncounterWindowDao;
import org.oscarehr.common.model.EncounterWindow;
import org.oscarehr.util.SpringUtils;

/**
 * @author root
 */
public class EctWindowSizes {

    /**
     * Creates a new instance of EctWindowSizes
     */
    public EctWindowSizes() {
    }

    public static Properties getWindowSizes(String provNo) {
        Properties props = new Properties();

        EncounterWindowDao dao = SpringUtils.getBean(EncounterWindowDao.class);
        EncounterWindow ectWindow = dao.findByProvider(provNo);

        if (ectWindow == null) {
            props.setProperty("rowOneSize", "60");
            props.setProperty("rowTwoSize", "60");
            props.setProperty("rowThreeSize", "378");
            props.setProperty("presBoxSize", "30");
        } else {
            props.setProperty("rowOneSize", "" + ectWindow.getRowOneSize());
            props.setProperty("rowTwoSize", "" + ectWindow.getRowTwoSize());
            props.setProperty("rowThreeSize", "" + ectWindow.getRowThreeSize());
            props.setProperty("presBoxSize", "" + ectWindow.getPresBoxSize());
        }

        return props;
    }
}