package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.ORNPreImplementationReportLog;

public interface ORNPreImplementationReportLogDao extends AbstractDao<ORNPreImplementationReportLog> {
    List<ORNPreImplementationReportLog> getAllReports();
}
