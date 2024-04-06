package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.BORNPathwayMapping;

public interface BORNPathwayMappingDao extends AbstractDao<BORNPathwayMapping> {
    List<BORNPathwayMapping> findAll();
    BORNPathwayMapping findRecord(String bornPathway, int serviceId);
    List<BORNPathwayMapping> findByBornPathway(String bornPathway);
}
