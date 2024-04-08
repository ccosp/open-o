package org.oscarehr.common.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.PageMonitor;
import org.springframework.stereotype.Repository;

@Repository
public class PageMonitorDaoImpl extends AbstractDaoImpl<PageMonitor> implements PageMonitorDao {

    public PageMonitorDaoImpl() {
        super(PageMonitor.class);
    }

    @Override
    public List<PageMonitor> findByPage(String pageName, String pageId) {
        Query query = entityManager.createQuery("SELECT e FROM PageMonitor e WHERE e.pageName=? and e.pageId=? order by e.updateDate desc");
        query.setParameter(1,pageName);
        query.setParameter(2,pageId);
        @SuppressWarnings("unchecked")
        List<PageMonitor> results = query.getResultList();
        return results;
    }

    @Override
    public List<PageMonitor> findByPageName(String pageName) {
        Query query = entityManager.createQuery("SELECT e FROM PageMonitor e WHERE e.pageName=? order by e.updateDate desc");
        query.setParameter(1,pageName);
        @SuppressWarnings("unchecked")
        List<PageMonitor> results = query.getResultList();
        return results;
    }

    @Override
    public void updatePage(String pageName, String pageId) {
        Query query = entityManager.createQuery("SELECT e FROM PageMonitor e WHERE e.pageName=? and e.pageId=? order by e.updateDate desc");
        query.setParameter(1,pageName);
        query.setParameter(2,pageId);
        @SuppressWarnings("unchecked")
        List<PageMonitor> results = query.getResultList();
        for(PageMonitor result:results) {
            Date now = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(result.getUpdateDate());
            c.add(Calendar.SECOND,result.getTimeout());
            if(c.getTime().before(now)) {
                this.remove(result.getId());
            }
        }
    }

    @Override
    public void removePageNameKeepPageIdForProvider(String pageName, String excludePageId, String providerNo) {
        Query query = entityManager.createQuery("SELECT e FROM PageMonitor e WHERE e.pageName=? and e.pageId!=? and e.providerNo=?");
        query.setParameter(1,pageName);
        query.setParameter(2,excludePageId);
        query.setParameter(3,providerNo);
        @SuppressWarnings("unchecked")
        List<PageMonitor> results = query.getResultList();
        for(PageMonitor result:results) {
            this.remove(result.getId());
        }
    }

    @Override
    public void cancelPageIdForProvider (String pageName, String cancelPageId, String providerNo) {
        Query query = entityManager.createQuery("SELECT e FROM PageMonitor e WHERE e.pageName=? and e.pageId=? and  e.providerNo=?");
        query.setParameter(1,pageName);
        query.setParameter(2,cancelPageId);
        query.setParameter(3,providerNo);
        @SuppressWarnings("unchecked")
        List<PageMonitor> results = query.getResultList();
        for(PageMonitor result:results) {
            this.remove(result.getId());
        }
    }
}
