package org.oscarehr.common.dao;

import org.oscarehr.decisionSupport.model.DSGuidelineProviderMapping;
import java.util.List;

public interface DSGuidelineProviderMappingDao extends AbstractDao<DSGuidelineProviderMapping> {
    List<DSGuidelineProviderMapping> getMappingsByProvider(String providerNo);
    boolean mappingExists(DSGuidelineProviderMapping dsGuidelineProviderMapping);
}
