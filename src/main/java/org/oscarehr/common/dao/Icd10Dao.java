package org.oscarehr.common.dao;

import org.oscarehr.common.model.AbstractCodeSystemModel;
import org.oscarehr.common.model.Icd10;
import java.util.List;

public interface Icd10Dao extends AbstractDao<Icd10> {
    List<Icd10> searchCode(String term);
    Icd10 findByCode(String code);
    AbstractCodeSystemModel<?> findByCodingSystem(String codingSystem);
    List<Icd10> searchText(String description);
}
