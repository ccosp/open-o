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

package openo.oscarEncounter.data;

import java.util.Vector;

import org.oscarehr.common.dao.EChartDao;
import org.oscarehr.common.model.EChart;
import org.oscarehr.util.SpringUtils;

import openo.util.ConversionUtils;

/**
 * @author Jay Gallagher
 */
public class EctSplitChart {

    public Vector<String[]> getSplitCharts(String demographicNo) {
        EChartDao dao = SpringUtils.getBean(EChartDao.class);
        Vector<String[]> vec = new Vector<String[]>();

        for (EChart ec : dao.findByDemoIdAndSubject(ConversionUtils.fromIntString(demographicNo), "SPLIT CHART")) {
            String[] s = new String[2];
            s[0] = ec.getId().toString();
            s[1] = ConversionUtils.toDateString(ec.getTimestamp());

            vec.add(s);
        }
        return vec;
    }
}
