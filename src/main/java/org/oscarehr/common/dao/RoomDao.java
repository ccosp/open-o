/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.common.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.Logger;
import org.oscarehr.common.model.Room;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

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
