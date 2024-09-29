/**
 * Copyright (c) 2013-2015. Department of Computer Science, University of Victoria. All Rights Reserved.
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
 * Department of Computer Science
 * LeadLab
 * University of Victoria
 * Victoria, Canada
 */

package ca.openosp.openo.dashboard.handler;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.openosp.openo.dashboard.handler.DiseaseRegistryHandler;
import org.junit.BeforeClass;
import org.junit.Test;
import ca.openosp.openo.common.dao.DemographicDao;
import ca.openosp.openo.common.dao.DxresearchDAO;
import ca.openosp.openo.common.dao.utils.EntityDataGenerator;
import ca.openosp.openo.common.dao.utils.SchemaUtils;
import ca.openosp.openo.common.model.Demographic;
import ca.openosp.openo.common.model.Dxresearch;
import ca.openosp.openo.common.model.Provider;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.SpringUtils;

public class DiseaseRegistryHandlerTest {

    //	private static Logger logger = MiscUtils.getLogger();
    private static DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean(DemographicDao.class);
    private DxresearchDAO dXdao = (DxresearchDAO) SpringUtils.getBean(DxresearchDAO.class);
    Date now = new java.util.Date();
    static Demographic demographic;
    static String providerNo = "999998";
    static List<Integer> demoNos = new ArrayList<Integer>();
    private static DiseaseRegistryHandler diseaseRegistryHandler;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        SchemaUtils.restoreTable("dxresearch", "demographic", "lst_gender", "admission", "demographic_merged",
                "program", "health_safety", "provider", "providersite", "site", "program_team",
                "log", "Facility", "demographicExt", "measurements", "measurementType", "measurementsExt",
                "quickList", "icd9", "ichppccode", "billing", "billingdetail");
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoAsCurrentClassAndMethod();
        Provider provider = new Provider();
        provider.setProviderNo(providerNo);
        for (int i = 0; i < 12; i++) {
            demographic = new Demographic();
            EntityDataGenerator.generateTestDataForModelClass(demographic);
            demographic.setDemographicNo(null);
            demographic.setProvider(provider);
            demographicDao.save(demographic);
            demoNos.add(demographic.getDemographicNo());
        }
        loggedInInfo.setLoggedInProvider(provider);
        diseaseRegistryHandler = new DiseaseRegistryHandler();
        diseaseRegistryHandler.setLoggedInInfo(loggedInInfo);
    }

    @Test
    public void addDx() {
        String icd9code = "338.2";
        String icd9codesys = "icd9";
        for (Integer demoNo : demoNos) {
            diseaseRegistryHandler.addToDiseaseRegistry(demoNo, icd9code);
        }
        for (Integer demoNo : demoNos) {
            List<Dxresearch> list = dXdao.findByDemographicNoResearchCodeAndCodingSystem(demoNo, icd9code, icd9codesys);
            assertNotNull(list);
        }
    }


}
