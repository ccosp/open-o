package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.apache.logging.log4j.Logger;
import org.oscarehr.common.model.RoomBed;
import org.oscarehr.common.model.RoomBedPK;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

@Repository
public class RoomBedDaoImpl extends AbstractDaoImpl<RoomBed> implements RoomBedDao {

    @Override
    public boolean bedExists(Integer roomId) {
        // Implement the method here
    }

    @Override
    public boolean roomExists(Integer bedId) {
        // Implement the method here
    }

    @Override
    public RoomBed getRoomBedByRoom(Integer roomId) {
        // Implement the method here
    }

    @Override
    public RoomBed getRoomBedByBed(Integer bedId) {
        // Implement the method here
    }

    @Override
    public void saveRoomBed(RoomBed roomBed) {
        // Implement the method here
    }

    @Override
    public void deleteRoomBed(RoomBed roomBed) {
        // Implement the method here
    }

    @Override
    public boolean roomBedExists(RoomBedPK id) {
        // Implement the method here
    }

    @Override
    public void updateHistory(RoomBed roomBed) {
        // Implement the method here
    }
}
