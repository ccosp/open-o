package org.oscarehr.PMmodule.service;

import java.util.List;
import org.oscarehr.PMmodule.model.ProgramQueue;

public interface ProgramQueueManager {
    void setVacancyDao(VacancyDao vacancyDao);
    void setVacancyTemplateDao(VacancyTemplateDao vacancyTemplateDao);
    void setProgramQueueDao(ProgramQueueDao dao);
    void setClientReferralDAO(ClientReferralDAO dao);
    ProgramQueue getProgramQueue(String queueId);
    List<ProgramQueue> getProgramQueuesByProgramId(Long programId);
    void saveProgramQueue(ProgramQueue programQueue);
    List<ProgramQueue> getActiveProgramQueuesByProgramId(Long programId);
    ProgramQueue getActiveProgramQueue(String programId, String demographicNo);
    void rejectQueue(String programId, String clientId,String notes, String rejectionReason);
}
