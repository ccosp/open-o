package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.AbstractCodeSystemModel;
import org.oscarehr.common.model.OscarCode;

public interface OscarCodeDao extends AbstractDao<OscarCode> {
    List<OscarCode> getIcd9Code(String icdCode);
    List<OscarCode> getOscarCode(String query);
    OscarCode findByCode(String code);
    AbstractCodeSystemModel<?> findByCodingSystem(String codingSystem);
}
