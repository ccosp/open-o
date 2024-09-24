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
package org.oscarehr.common.dao;

import org.oscarehr.common.model.Room;

public interface RoomDao extends AbstractDao<Room> {

    public boolean roomExists(Integer roomId);

    public Room getRoom(Integer roomId);

    public Room[] getRooms(Integer facilityId, Integer programId, Boolean active);

    public Room[] getAssignedBedRooms(Integer facilityId, Integer programId, Boolean active);

    public Room[] getAvailableRooms(Integer facilityId, Integer programId, Boolean active);

    public void saveRoom(Room room);

    public void deleteRoom(Room room);

    public String getRoomsQueryString(Integer facilityId, Integer programId, Boolean active);

    public String getAssignedBedRoomsQueryString(Integer facilityId, Integer programId, Boolean active);

    public Object[] getRoomsValues(Integer facilityId, Integer programId, Boolean active);

    public void updateHistory(Room room);

}
