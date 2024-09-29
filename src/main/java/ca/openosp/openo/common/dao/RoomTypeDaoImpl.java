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
package ca.openosp.openo.common.dao;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.Logger;
import ca.openosp.openo.common.model.RoomType;
import ca.openosp.openo.ehrutil.MiscUtils;
import org.springframework.stereotype.Repository;

@Repository
public class RoomTypeDaoImpl extends AbstractDaoImpl<RoomType> implements RoomTypeDao {

    private Logger log = MiscUtils.getLogger();

    public RoomTypeDaoImpl() {
        super(RoomType.class);
    }

    /**
     * Does room type with id exist
     *
     * @param roomTypeId id
     * @return true if room type exists
     */
    @Override
    public boolean roomTypeExists(Integer roomTypeId) {
        Query query = entityManager.createQuery("select count(*) from RoomType r where r.id = ?");
        query.setParameter(0, roomTypeId);

        Long result = (Long) query.getSingleResult();

        return (result.intValue() == 1);
    }


    /**
     * Get room type by id
     *
     * @param roomTypeId id
     * @return room type
     */
    @Override
    public RoomType getRoomType(Integer roomTypeId) {
        RoomType roomType = find(roomTypeId);
        log.debug("getRoom: id: " + roomTypeId);

        return roomType;
    }

    /**
     * Get room types
     */
    @SuppressWarnings("unchecked")
    @Override
    public RoomType[] getRoomTypes() {
        Query query = entityManager.createQuery("select r from RoomType r");

        List<RoomType> roomTypes = query.getResultList();
        log.debug("getRooms: size: " + roomTypes.size());

        return roomTypes.toArray(new RoomType[roomTypes.size()]);
    }
}
