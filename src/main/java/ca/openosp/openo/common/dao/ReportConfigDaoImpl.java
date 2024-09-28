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

import java.util.List;
import javax.persistence.Query;

import ca.openosp.openo.common.model.ReportConfig;
import org.springframework.stereotype.Repository;

@Repository
public class ReportConfigDaoImpl extends AbstractDaoImpl<ReportConfig> implements ReportConfigDao {

    public ReportConfigDaoImpl() {
        super(ReportConfig.class);
    }

    @Override
    public List<ReportConfig> findByReportIdAndNameAndCaptionAndTableNameAndSave(int reportId, String name, String caption, String tableName, String save) {
        Query q = entityManager.createQuery("select x from ReportConfig x where x.reportId=? and x.name=? and x.caption=? and x.tableName=? and x.save=?");
        q.setParameter(0, reportId);
        q.setParameter(1, name);
        q.setParameter(2, caption);
        q.setParameter(3, tableName);
        q.setParameter(4, save);

        @SuppressWarnings("unchecked")
        List<ReportConfig> results = q.getResultList();

        return results;
    }

    @Override
    public List<ReportConfig> findByReportIdAndSaveAndGtOrderNo(int reportId, String save, int orderNo) {
        Query q = entityManager.createQuery("select x from ReportConfig x where x.reportId=? and x.save=? and x.orderNo >= ? order by x.orderNo DESC");
        q.setParameter(0, reportId);
        q.setParameter(1, save);
        q.setParameter(2, orderNo);


        @SuppressWarnings("unchecked")
        List<ReportConfig> results = q.getResultList();

        return results;
    }
}
