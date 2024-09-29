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
package ca.openosp.openo.PMmodule.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.openosp.openo.PMmodule.dao.OcanSubmissionLogDao;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import ca.openosp.openo.PMmodule.model.OcanSubmissionLog;
import ca.openosp.openo.common.dao.DaoTestFixtures;
import ca.openosp.openo.common.dao.utils.EntityDataGenerator;
import ca.openosp.openo.common.dao.utils.SchemaUtils;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

public class OcanSubmissionLogDaoTest extends DaoTestFixtures {

    public OcanSubmissionLogDao dao = SpringUtils.getBean(OcanSubmissionLogDao.class);


    @Before
    public void before() throws Exception {
        SchemaUtils.restoreTable("OcanSubmissionLog");
    }

    @Test
    public void testCreate() throws Exception {
        OcanSubmissionLog entity = new OcanSubmissionLog();
        EntityDataGenerator.generateTestDataForModelClass(entity);
        dao.persist(entity);
        assertNotNull(entity.getId());
    }

    @Test
    public void testFindAll() throws Exception {
        String type = "OCAN";
        OcanSubmissionLog oSL1 = new OcanSubmissionLog();
        EntityDataGenerator.generateTestDataForModelClass(oSL1);
        oSL1.setSubmissionType(type);
        dao.persist(oSL1);

        OcanSubmissionLog oSL2 = new OcanSubmissionLog();
        EntityDataGenerator.generateTestDataForModelClass(oSL2);
        oSL2.setSubmissionType(type);
        dao.persist(oSL2);

        OcanSubmissionLog oSL3 = new OcanSubmissionLog();
        EntityDataGenerator.generateTestDataForModelClass(oSL3);
        oSL3.setSubmissionType(type);
        dao.persist(oSL3);

        OcanSubmissionLog oSL4 = new OcanSubmissionLog();
        EntityDataGenerator.generateTestDataForModelClass(oSL4);
        oSL4.setSubmissionType(type);
        dao.persist(oSL4);

        List<OcanSubmissionLog> expectedResult = new ArrayList<OcanSubmissionLog>(Arrays.asList(oSL1, oSL2, oSL3, oSL4));
        List<OcanSubmissionLog> result = dao.findAllByType(type);

        Logger logger = MiscUtils.getLogger();

        if (result.size() != expectedResult.size()) {
            logger.warn("Array sizes do not match.");
            fail("Array sizes do not match.");
        }
		/*
		for (int i = 0; i < expectedResult.size(); i++) {
			if (!expectedResult.get(i).equals(result.get(i))){
				logger.warn("Items  do not match.");
				fail("Items  do not match.");
			}
		}
		*/
        assertTrue(true);
    }
}
