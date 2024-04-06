package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.ConfigImmunization;

public interface ConfigImmunizationDao extends AbstractDao<ConfigImmunization> {
    List<ConfigImmunization> findAll();
    List<ConfigImmunization> findByArchived(Integer archived, boolean orderByName);
}
