package org.oscarehr.common.dao;

import org.oscarehr.common.model.DrugDispensingMapping;

public interface DrugDispensingMappingDao extends AbstractDao<DrugDispensingMapping> {
    DrugDispensingMapping findMappingByDin(String din);
    DrugDispensingMapping findMapping(String din, String duration, String durUnit, String freqCode, String quantity, Float takeMin, Float takeMax);
}
