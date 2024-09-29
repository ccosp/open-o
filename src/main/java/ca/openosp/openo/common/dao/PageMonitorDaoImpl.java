//CHECKSTYLE:OFF
/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * <p>
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 * <p>
 * Modifications made by Magenta Health in 2024.
 */
package ca.openosp.openo.common.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

import ca.openosp.openo.common.model.PageMonitor;
import org.springframework.stereotype.Repository;

@Repository
public class PageMonitorDaoImpl extends AbstractDaoImpl<PageMonitor> implements PageMonitorDao {

    public PageMonitorDaoImpl() {
        super(PageMonitor.class);
    }

    @Override
    public List<PageMonitor> findByPage(String pageName, String pageId) {
        Query query = entityManager.createQuery("SELECT e FROM PageMonitor e WHERE e.pageName=? and e.pageId=? order by e.updateDate desc");
        query.setParameter(0, pageName);
        query.setParameter(1, pageId);
        @SuppressWarnings("unchecked")
        List<PageMonitor> results = query.getResultList();
        return results;
    }

    @Override
    public List<PageMonitor> findByPageName(String pageName) {
        Query query = entityManager.createQuery("SELECT e FROM PageMonitor e WHERE e.pageName=? order by e.updateDate desc");
        query.setParameter(0, pageName);
        @SuppressWarnings("unchecked")
        List<PageMonitor> results = query.getResultList();
        return results;
    }

    @Override
    public void updatePage(String pageName, String pageId) {
        Query query = entityManager.createQuery("SELECT e FROM PageMonitor e WHERE e.pageName=? and e.pageId=? order by e.updateDate desc");
        query.setParameter(0, pageName);
        query.setParameter(1, pageId);
        @SuppressWarnings("unchecked")
        List<PageMonitor> results = query.getResultList();
        for (PageMonitor result : results) {
            Date now = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(result.getUpdateDate());
            c.add(Calendar.SECOND, result.getTimeout());
            if (c.getTime().before(now)) {
                this.remove(result.getId());
            }
        }
    }

    @Override
    public void removePageNameKeepPageIdForProvider(String pageName, String excludePageId, String providerNo) {
        Query query = entityManager.createQuery("SELECT e FROM PageMonitor e WHERE e.pageName=? and e.pageId!=? and e.providerNo=?");
        query.setParameter(0, pageName);
        query.setParameter(1, excludePageId);
        query.setParameter(2, providerNo);
        @SuppressWarnings("unchecked")
        List<PageMonitor> results = query.getResultList();
        for (PageMonitor result : results) {
            this.remove(result.getId());
        }
    }

    @Override
    public void cancelPageIdForProvider(String pageName, String cancelPageId, String providerNo) {
        Query query = entityManager.createQuery("SELECT e FROM PageMonitor e WHERE e.pageName=? and e.pageId=? and  e.providerNo=?");
        query.setParameter(0, pageName);
        query.setParameter(1, cancelPageId);
        query.setParameter(2, providerNo);
        @SuppressWarnings("unchecked")
        List<PageMonitor> results = query.getResultList();
        for (PageMonitor result : results) {
            this.remove(result.getId());
        }
    }
}
