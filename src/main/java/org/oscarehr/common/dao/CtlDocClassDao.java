package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.CtlDocClass;

public interface CtlDocClassDao extends AbstractDao<CtlDocClass> {
    List<String> findUniqueReportClasses();
    List<String> findSubClassesByReportClass(String reportClass);
}
