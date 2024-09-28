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
package ca.openosp.openo.PMmodule.utility;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ca.openosp.openo.PMmodule.dao.ProgramAccessDAO;
import ca.openosp.openo.PMmodule.model.ProgramAccess;
import ca.openosp.openo.ehrutil.SpringUtils;

public class ProgramAccessCache {

    static ProgramAccessDAO programAccessDAO = (ProgramAccessDAO) SpringUtils.getBean(ProgramAccessDAO.class);
    static Map<String, Map> accessMaps = new HashMap<String, Map>();


    public static Map getAccessMap(long programId) {
        return accessMaps.get(programId + ":AccessMap");
    }

    public static void setAccessMap(long programId) {
        List programAccessList = programAccessDAO.getAccessListByProgramId(programId);
        @SuppressWarnings("unchecked")
        Map programAccessMap = convertProgramAccessListToMap(programAccessList);
        accessMaps.put(programId + ":AccessMap", programAccessMap);
    }

    private static Map<String, ProgramAccess> convertProgramAccessListToMap(List<ProgramAccess> paList) {
        Map<String, ProgramAccess> map = new HashMap<String, ProgramAccess>();
        if (paList == null) {
            return map;
        }
        for (Iterator<ProgramAccess> iter = paList.iterator(); iter.hasNext(); ) {
            ProgramAccess pa = iter.next();
            map.put(pa.getAccessType().getName().toLowerCase(), pa);
        }
        return map;
    }

}
