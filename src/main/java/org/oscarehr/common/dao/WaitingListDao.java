package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.WaitingList;

public interface WaitingListDao extends AbstractDao<WaitingList> {
    List<Object[]> findByDemographic(Integer demographicNo);
    List<Object[]> findWaitingListsAndDemographics(Integer listId);
    List<WaitingList> findByWaitingListId(Integer listId);
    List<Appointment> findAppointmentFor(WaitingList waitingList);
    List<WaitingList> findByWaitingListIdAndDemographicId(Integer waitingListId, Integer demographicId);
    Integer getMaxPosition(Integer listId);
    List<WaitingList> search_wlstatus(Integer demographicId);
}
