//CHECKSTYLE:OFF
/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * Copyright (C) 2007  Heart & Stroke Foundation
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
package org.oscarehr.common.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import openo.util.DateUtils;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.Hsfo2Patient;
import org.oscarehr.common.model.Hsfo2RecommitSchedule;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.stereotype.Repository;

import openo.form.study.hsfo2.HSFODAO;
import openo.oscarDemographic.data.DemographicData;

@Repository
public class Hsfo2RecommitScheduleDaoImpl extends AbstractDaoImpl<Hsfo2RecommitSchedule> implements Hsfo2RecommitScheduleDao {
    public Hsfo2RecommitScheduleDaoImpl() {
        super(Hsfo2RecommitSchedule.class);
    }

    public Hsfo2RecommitSchedule getLastSchedule(boolean statusFlag) {
        String sqlCommand = "select * from hsfo_recommit_schedule  ";
        if (statusFlag) sqlCommand += "where status='D' ";
        sqlCommand += "order by id desc";

        Query query = entityManager.createNativeQuery(sqlCommand, modelClass);
        return getSingleResultOrNull(query);
    }

    public void updateLastSchedule(Hsfo2RecommitSchedule rd) {
        this.merge(rd);
    }

    public void insertchedule(Hsfo2RecommitSchedule rd) {
        this.persist(rd);
    }


    public boolean isLastActivExpire() {
        boolean exp = false;
        Hsfo2RecommitSchedule rd = getLastSchedule(false);
        if (rd != null && !"D".equalsIgnoreCase(rd.getStatus())) {
            if (rd.getSchedule_time().before(new Date())) exp = true;
        }
        return exp;
    }

    public void deActiveLast() {
        Hsfo2RecommitSchedule rd = getLastSchedule(false);
        if (rd != null && !"D".equalsIgnoreCase(rd.getStatus())) {
            rd.setStatus("D");
            updateLastSchedule(rd);
        }
    }

    public String SynchronizeDemoInfo(LoggedInInfo loggedInInfo) {
        HSFODAO hsfoDao = new HSFODAO();
        List idList = hsfoDao.getAllPatientId();
        Iterator itr = idList.iterator();
        DemographicData demoData = new DemographicData();
        while (itr.hasNext()) {
            String pid = (String) itr.next();
            Hsfo2Patient pd = hsfoDao.retrievePatientRecord(pid);
            Demographic demo = demoData.getDemographic(loggedInInfo, pid);
            if (demo != null) {
                String internalId = demo.getProviderNo();
                if (internalId == null || internalId.length() == 0) {
                    return demo.getLastName() + "," + demo.getFirstName();
                }
                pd.setBirthDate(DateUtils.toDate(demo.getFormattedDob()));
                pd.setSex(demo.getSex().toLowerCase());
                if (demo.getLastName() != null
                        && demo.getLastName().trim().length() > 0)
                    pd.setLName(demo.getLastName());
                if (demo.getFirstName() != null
                        && demo.getFirstName().trim().length() > 0)
                    pd.setFName(demo.getFirstName());

                String pcode = demo.getPostal().trim();
                if (pcode != null && pcode.length() >= 3)
                    pd.setPostalCode(pcode.substring(0, 3));
                hsfoDao.updatePatient(pd);
            }
        }
        return null;
    }

    public String checkProvider(LoggedInInfo loggedInInfo) {
        HSFODAO hsfoDao = new HSFODAO();
        List idList = hsfoDao.getAllPatientId();
        Iterator itr = idList.iterator();
        DemographicData demoData = new DemographicData();
        while (itr.hasNext()) {
            String pid = (String) itr.next();
            Demographic demo = demoData.getDemographic(loggedInInfo, pid);
            String internalId = demo.getProviderNo();
            if (internalId == null || internalId.length() == 0) {
                return demo.getLastName() + "," + demo.getFirstName();
            }

        }
        return null;
    }
}
