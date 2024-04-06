package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.Dashboard;

public interface DashboardDao extends AbstractDao<Dashboard> {
    List<Dashboard> getActiveDashboards();
    List<Dashboard> getDashboardsByStatus(boolean status);
    List<Dashboard> getDashboards();
}
