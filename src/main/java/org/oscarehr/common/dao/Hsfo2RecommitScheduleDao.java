package org.oscarehr.common.dao;

import org.oscarehr.common.model.Hsfo2RecommitSchedule;
import org.oscarehr.util.LoggedInInfo;

public interface Hsfo2RecommitScheduleDao extends AbstractDao<Hsfo2RecommitSchedule> {
    Hsfo2RecommitSchedule getLastSchedule(boolean statusFlag);
    void updateLastSchedule(Hsfo2RecommitSchedule rd);
    void insertchedule(Hsfo2RecommitSchedule rd);
    boolean isLastActivExpire();
    void deActiveLast();
    String SynchronizeDemoInfo(LoggedInInfo loggedInInfo);
    String checkProvider(LoggedInInfo loggedInInfo);
}
