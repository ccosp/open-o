package org.oscarehr.common.dao;

import java.util.List;

import org.oscarehr.common.model.EncounterTemplate;

public interface EncounterTemplateDao extends AbstractDao<EncounterTemplate> {

    List<EncounterTemplate> findAll();

    List<EncounterTemplate> findByName(String name);

    List<EncounterTemplate> findByName(String name, Integer startIndex, Integer itemsToReturn);
}
