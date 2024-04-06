package org.oscarehr.common.dao;

import org.oscarehr.common.model.ISO36612;

public interface ISO36612Dao extends AbstractDao<ISO36612> {
    ISO36612 findByCode(String code);
    String findProvinceByCode(String code);
    String findCountryByCode(String code);
    boolean reloadTable();
}
