package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.AbstractCodeSystemModel;
import org.oscarehr.common.model.SnomedCore;

public interface SnomedCoreDao extends AbstractCodeSystemDao<SnomedCore>{
    List<SnomedCore> getSnomedCoreCode(String snomedCoreCode);
    List<SnomedCore> getSnomedCore(String query);
    List<SnomedCore> searchCode(String term);
    SnomedCore findByCode(String code);
    AbstractCodeSystemModel<?> findByCodingSystem(String codingSystem);
}
