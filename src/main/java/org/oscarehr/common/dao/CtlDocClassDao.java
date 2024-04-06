package org.oscarehr.common.dao;

import java.util.List;

public interface CtlDocClassDao extends AbstractDao<CtlDocClass> {
    List<String> findUniqueReportClasses();
    List<String> findSubClassesByReportClass(String reportClass);
}
