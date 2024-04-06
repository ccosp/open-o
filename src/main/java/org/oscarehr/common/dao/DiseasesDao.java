package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.Diseases;

public interface DiseasesDao extends AbstractDao<Diseases> {
    List<Diseases> findByDemographicNo(int demographicNo);
    List<Diseases> findByIcd9(String icd9);
}
