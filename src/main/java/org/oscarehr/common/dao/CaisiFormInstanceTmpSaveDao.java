package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.CaisiFormInstance;
import org.oscarehr.common.model.CaisiFormInstanceTmpSave;

public interface CaisiFormInstanceTmpSaveDao extends AbstractDao<CaisiFormInstanceTmpSave> {
    List<CaisiFormInstance> getTmpForms(Integer instanceId, Integer formId, Integer clientId, Integer providerNo);
}
