//CHECKSTYLE:OFF
/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * Copyright (C) 2007  Heart & Stroke Foundation
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
package ca.openosp.openo.common.dao;

import ca.openosp.openo.common.model.Hsfo2RecommitSchedule;
import ca.openosp.openo.ehrutil.LoggedInInfo;

public interface Hsfo2RecommitScheduleDao extends AbstractDao<Hsfo2RecommitSchedule> {
    Hsfo2RecommitSchedule getLastSchedule(boolean statusFlag);

    void updateLastSchedule(Hsfo2RecommitSchedule rd);

    void insertchedule(Hsfo2RecommitSchedule rd);

    boolean isLastActivExpire();

    void deActiveLast();

    String SynchronizeDemoInfo(LoggedInInfo loggedInInfo);

    String checkProvider(LoggedInInfo loggedInInfo);
}
