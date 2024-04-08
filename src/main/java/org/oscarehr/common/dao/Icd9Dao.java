package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.AbstractCodeSystemModel;
import org.oscarehr.common.model.Icd9;

public interface Icd9Dao extends AbstractDao<Icd9> {
    List<Icd9> getIcd9Code(String icdCode);
    List<Icd9> getIcd9(String query);
    Icd9 findByCode(String code);
    List<Icd9> searchCode(String term);
    AbstractCodeSystemModel<?> findByCodingSystem(String codingSystem);
}
