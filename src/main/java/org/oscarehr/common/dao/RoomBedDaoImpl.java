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

    private Logger log = MiscUtils.getLogger();

    public RoomBedDaoImpl() {
		super(RoomBed.class);
	}

    @Override
    public boolean bedExists(Integer roomId) {
        Query query = entityManager.createQuery("select count(*) from RoomBed rb where rb.id.roomId = ?");
		query.setParameter(1, roomId);
		
		Long result = (Long)query.getSingleResult();
		
		return (result.intValue() == 1);
    }

    @Override
    public boolean roomExists(Integer bedId) {
        Query query = entityManager.createQuery("select count(*) from RoomBed rb where rb.id.bedId = ?");
		query.setParameter(1, bedId);
		
		Long result = (Long)query.getSingleResult();
		
		return (result.intValue() == 1);
    }

    @Override
    public RoomBed getRoomBedByRoom(Integer roomId) {
        Query query = entityManager.createQuery("select bd from RoomBed bd where bd.id.roomId = ?");
    	query.setParameter(1, roomId);
    	
    	@SuppressWarnings("unchecked")
        List<RoomBed> roomBeds =query.getResultList();

        if (roomBeds.size() > 1) {
            throw new IllegalStateException("Room is assigned to more than one client");
        }

        RoomBed roomBed = ((roomBeds.size() == 1)?roomBeds.get(0):null);

        log.debug("getRoomBedByRoom: " + roomId);

        return roomBed;
    }

    @Override
    public RoomBed getRoomBedByBed(Integer bedId) {
        Query query = entityManager.createQuery("select bd from RoomBed bd where bd.id.bedId = ?");
    	query.setParameter(1, bedId);
    	
    	@SuppressWarnings("unchecked")
        List<RoomBed> roomBeds =query.getResultList();
    	
    	
        if (roomBeds.size() > 1) {
            throw new IllegalStateException("Client is assigned to more than one room");
        }

        RoomBed roomBed = ((roomBeds.size() == 1)?roomBeds.get(0):null);

        log.debug("getRoomBedByBed: " + bedId);

        return roomBed;
    }

    @Override
    public void saveRoomBed(RoomBed roomBed) {
        updateHistory(roomBed);
        
        if(roomBed == null)
        	return;
        
        if(roomBed.getId() == null || roomBed.getId().getBedId() == null || roomBed.getId().getRoomId() == null)
        	persist(roomBed);
        else
        	merge(roomBed);


        log.debug("saveRoomBed: " + roomBed);
    }

    @Override
    public void deleteRoomBed(RoomBed roomBed) {
        if(roomBed != null)
    		remove(roomBed.getId());
    }

    @Override
    public boolean roomBedExists(RoomBedPK id) {
        Query query = entityManager.createQuery("select count(*) from RoomBed rb where rb.id.roomId = ? and rb.id.bedId = ?");
		query.setParameter(1, id.getRoomId());
		query.setParameter(1, id.getBedId());
		
		Long result = (Long)query.getSingleResult();
		
		return (result.intValue() == 1);
    }

    @Override
    public void updateHistory(RoomBed roomBed) {
        RoomBed existing = null;

        RoomBedPK id = roomBed.getId();

        if (!roomBedExists(id)) {
            Integer bedId = id.getBedId();
            Integer roomId = id.getRoomId();

            if (roomExists(bedId)) {
                existing = getRoomBedByBed(bedId);
            }
            else if (bedExists(roomId)) {
                existing = getRoomBedByRoom(roomId);
            }
        }

        if (existing != null) {
            deleteRoomBed(existing);
        }
    }
}
