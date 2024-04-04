/**
 * Copyright (c) 2006-. OSCARservice, OpenSoft System. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.oscarehr.common.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.Logger;
import org.oscarehr.common.NativeSql;
import org.oscarehr.common.model.AbstractCodeSystemModel;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.DxRegistedPTInfo;
import org.oscarehr.common.model.Dxresearch;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.stereotype.Repository;

import oscar.oscarResearch.oscarDxResearch.bean.dxCodeSearchBean;
import oscar.oscarResearch.oscarDxResearch.bean.dxQuickListItemsHandler;

/**
 *
 * @author toby
 */

public interface DxresearchDAO extends AbstractDao<Dxresearch> {

    public List<DxRegistedPTInfo> getPatientRegisted(List<Dxresearch> dList, List<String> doctorList);

    public List<DxRegistedPTInfo> patientRegistedDistincted(List<dxCodeSearchBean> searchItems,
            List<String> doctorList);

    public List<DxRegistedPTInfo> patientRegistedAll(List<dxCodeSearchBean> searchItems, List<String> doctorList);

    public List<DxRegistedPTInfo> patientRegistedActive(List<dxCodeSearchBean> searchItems, List<String> doctorList);

    public List<DxRegistedPTInfo> patientRegistedResolve(List<dxCodeSearchBean> searchItems, List<String> doctorList);

    public List<DxRegistedPTInfo> patientRegistedDeleted(List<dxCodeSearchBean> searchItems, List<String> doctorList);

    public List<DxRegistedPTInfo> patientRegistedStatus(String status, List<dxCodeSearchBean> searchItems,
            List<String> doctorList);

    public List<dxCodeSearchBean> getQuickListItems(String quickListName);

    public List<Dxresearch> getDxResearchItemsByPatient(Integer demographicNo);

    public void save(Dxresearch d);

    public List<Dxresearch> getByDemographicNo(int demographicNo);

    public List<Dxresearch> find(int demographicNo, String codeType, String code);

    public List<Dxresearch> findActive(String codeType, String code);

    public boolean entryExists(int demographicNo, String codeType, String code);

    public boolean activeEntryExists(int demographicNo, String codeType, String code);

    public void removeAllAssociationEntries();

    public List<Dxresearch> findByDemographicNoResearchCodeAndCodingSystem(Integer demographicNo, String dxresearchCode,
            String codingSystem);

    public List<Object[]> getDataForInrReport(Date fromDate, Date toDate);

    public Integer countResearches(String researchCode, Date sdate, Date edate);

    public Integer countBillingResearches(String researchCode, String diagCode, String creator, Date sdate, Date edate);

    public List<Object[]> findResearchAndCodingSystemByDemographicAndCondingSystem(String codingSystem,
            String demographicNo);

    public List<Dxresearch> findCurrentByCodeTypeAndCode(String codeType, String code);

    public List<Dxresearch> getByDemographicNoSince(int demographicNo, Date lastUpdateDate);

    public List<Integer> getByDemographicNoSince(Date lastUpdateDate);

    public List<Dxresearch> findNonDeletedByDemographicNo(Integer demographicNo);

    public List<Integer> findNewProblemsSinceDemokey(String keyName);

    public String getDescription(String codingSystem, String code);
}
