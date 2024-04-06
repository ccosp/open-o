package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.CtlDiagCode;

public interface CtlDiagCodeDao extends AbstractDao<CtlDiagCode> {
    List<Object[]> getDiagnostics(String billRegion, String serviceType);
    List<CtlDiagCode> findByServiceType(String serviceType);
}
