package org.oscarehr.PMmodule.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.oscarehr.PMmodule.dao.ClientReferralDAO;
import org.oscarehr.PMmodule.dao.ProgramQueueDao;
import org.oscarehr.PMmodule.dao.VacancyDao;
import org.oscarehr.PMmodule.dao.VacancyTemplateDao;
import org.oscarehr.PMmodule.model.ClientReferral;
import org.oscarehr.PMmodule.model.ProgramQueue;
import org.oscarehr.PMmodule.model.Vacancy;
import org.oscarehr.PMmodule.model.VacancyTemplate;
import org.springframework.transaction.annotation.Transactional;

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
