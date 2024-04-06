package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.AbstractCodeSystemModel;
import org.oscarehr.common.model.DiagnosticCode;

public interface DiagnosticCodeDao extends AbstractDao<DiagnosticCode> {
    List<DiagnosticCode> findByDiagnosticCode(String diagnosticCode);
    List<DiagnosticCode> findByDiagnosticCodeAndRegion(String diagnosticCode, String region);
    List<DiagnosticCode> search(String searchString);
    List<DiagnosticCode> newSearch(String a, String b, String c, String d, String e, String f);
    List<DiagnosticCode> searchCode(String code);
    List<DiagnosticCode> searchText(String description);
    List<DiagnosticCode> getByDxCode(String dxCode);
    List<DiagnosticCode> findByRegionAndType(String billRegion, String serviceType);
    List<Object[]> findDiagnosictsAndCtlDiagCodesByServiceType(String serviceType);
    DiagnosticCode findByCode(String code);
    AbstractCodeSystemModel<?> findByCodingSystem(String codingSystem);
}
