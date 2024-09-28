/**
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
 */
package org.oscarehr.common.dao;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import ca.openosp.openo.common.dao.utils.EntityDataGenerator;
import ca.openosp.openo.common.dao.utils.SchemaUtils;
import ca.openosp.openo.common.model.LabPatientPhysicianInfo;
import ca.openosp.openo.ehrutil.SpringUtils;

public class LabPatientPhysicianInfoDaoTest extends DaoTestFixtures {

    protected LabPatientPhysicianInfoDao dao = SpringUtils.getBean(LabPatientPhysicianInfoDao.class);

    public LabPatientPhysicianInfoDaoTest() {
    }

    @Before
    public void before() throws Exception {
        SchemaUtils.restoreTable("labPatientPhysicianInfo", "patientLabRouting", "labPatientPhysicianInfo", "providerLabRouting", "labReportInformation");
    }

    @Test
    public void testCreate() throws Exception {
        LabPatientPhysicianInfo entity = new LabPatientPhysicianInfo();
        EntityDataGenerator.generateTestDataForModelClass(entity);
        dao.persist(entity);

        assertNotNull(entity.getId());
    }

    @Test
    public void testFindRoutings() {
        assertNotNull(dao.findRoutings(100, "T"));
    }

    @Test
    public void testFindByPatientName() {
        assertNotNull(dao.findByPatientName("ST", "LAB", "100", "LNAME", "FNAME", "HIN"));
    }

    @Test
    public void testFindByDemographic() {
        assertNotNull(dao.findByDemographic(199, "D"));
    }
}
