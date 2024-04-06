package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.LabRequestReportLink;

public interface LabRequestReportLinkDao extends AbstractDao<LabRequestReportLink> {
    List<LabRequestReportLink> findByReportTableAndReportId(String reportTable, int reportId);
    List<LabRequestReportLink> findByRequestTableAndRequestId(String requestTable, int requestId);
}
