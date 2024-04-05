package org.oscarehr.common.dao;

import java.util.List;

import org.oscarehr.common.model.PreventionReport;

public interface PreventionReportDao extends AbstractDao<PreventionReport> {

    List<PreventionReport> getPreventionReports();
}
