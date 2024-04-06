package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.decisionSupport.model.DSGuideline;

public interface DSGuidelineDao extends AbstractDao<DSGuideline> {
    DSGuideline findByUUID(String uuid);
    List<DSGuideline> getDSGuidelinesByProvider(String providerNo);
}
