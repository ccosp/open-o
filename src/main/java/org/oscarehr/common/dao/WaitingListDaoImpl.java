package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.WaitingList;
import org.springframework.stereotype.Repository;

@Repository
public class WaitingListDaoImpl extends AbstractDaoImpl<WaitingList> implements WaitingListDao {

    public WaitingListDaoImpl() {
        super(WaitingList.class);
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> findByDemographic(Integer demographicNo) {
        Query query = entityManager.createQuery("FROM WaitingListName wn, WaitingList w WHERE wn.id = w.listId AND w.demographicNo = :demoNo AND w.isHistory != 'Y'");
        query.setParameter("demoNo", demographicNo);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> findWaitingListsAndDemographics(Integer listId) {
        Query query = entityManager.createQuery("FROM WaitingList w, Demographic d WHERE w.demographicNo = d.DemographicNo AND  w.listId = :listId AND w.isHistory = 'N' ORDER BY w.position");
        query.setParameter("listId", listId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<WaitingList> findByWaitingListId(Integer listId) {
        Query query = entityManager.createQuery("FROM WaitingList w WHERE w.listId = :listId AND w.isHistory = 'N' ORDER BY w.onListSince");
        query.setParameter("listId", listId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Appointment> findAppointmentFor(WaitingList waitingList) {
        Query query = entityManager.createQuery("From Appointment a where a.appointmentDate >= :onListSince AND a.demographicNo = :demographicNo");
        query.setParameter("onListSince", waitingList.getOnListSince());
        query.setParameter("demographicNo", waitingList.getDemographicNo());
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<WaitingList> findByWaitingListIdAndDemographicId(Integer waitingListId, Integer demographicId) {
        Query query = createQuery("wl", "wl.demographicNo = :demoNo AND wl.listId = :listId");
        query.setParameter("demoNo", demographicId);
        query.setParameter("listId", waitingListId);
        return query.getResultList();
    }

    public Integer getMaxPosition(Integer listId) {
        Query query = entityManager.createQuery("select max(w.position) from WaitingList w where w.listId = :listId AND w.isHistory = 'N'");
        query.setParameter("listId", listId);
        Long result = (Long) query.getSingleResult();
        if (result == null) {
            return 0;
        } 
        return result.intValue();
    }

    @SuppressWarnings("unchecked")
    public List<WaitingList> search_wlstatus(Integer demographicId) {
        Query query = entityManager.createQuery("select wl from WaitingList wl where wl.demographicNo = :demoNo AND wl.isHistory = 'N' order BY wl.onListSince desc");
        query.setParameter("demoNo", demographicId);
        return query.getResultList();
    }
}
