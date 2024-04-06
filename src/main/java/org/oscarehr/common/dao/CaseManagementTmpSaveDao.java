package org.oscarehr.common.dao;

import java.util.Date;
import org.oscarehr.common.model.CaseManagementTmpSave;

public interface CaseManagementTmpSaveDao extends AbstractDao<CaseManagementTmpSave> {
    void remove(String providerNo, Integer demographicNo, Integer programId);
    CaseManagementTmpSave find(String providerNo, Integer demographicNo, Integer programId);
    CaseManagementTmpSave find(String providerNo, Integer demographicNo, Integer programId, Date date);
    boolean noteHasContent(Integer id);
}
