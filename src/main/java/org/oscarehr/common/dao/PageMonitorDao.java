package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.PageMonitor;

public interface PageMonitorDao extends AbstractDao<PageMonitor> {
    List<PageMonitor> findByPage(String pageName, String pageId);
    List<PageMonitor> findByPageName(String pageName);
    void updatePage(String pageName, String pageId);
    void removePageNameKeepPageIdForProvider(String pageName, String excludePageId, String providerNo);
    void cancelPageIdForProvider (String pageName, String cancelPageId, String providerNo);
}
