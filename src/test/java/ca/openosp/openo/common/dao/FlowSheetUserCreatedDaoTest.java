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
package ca.openosp.openo.common.dao;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import ca.openosp.openo.common.dao.utils.EntityDataGenerator;
import ca.openosp.openo.common.dao.utils.SchemaUtils;
import ca.openosp.openo.common.model.FlowSheetUserCreated;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

public class FlowSheetUserCreatedDaoTest extends DaoTestFixtures {

    protected FlowSheetUserCreatedDao dao = SpringUtils.getBean(FlowSheetUserCreatedDao.class);


    @Before
    public void before() throws Exception {
        SchemaUtils.restoreTable("FlowSheetUserCreated");
    }

    @Test
    public void testCreate() throws Exception {
        FlowSheetUserCreated entity = new FlowSheetUserCreated();
        EntityDataGenerator.generateTestDataForModelClass(entity);
        dao.persist(entity);
        assertNotNull(entity.getId());
    }

    @Test
    public void testGetAllUserCreatedFlowSheets() throws Exception {

        boolean isArchived = true;

        FlowSheetUserCreated flowSheetUserCreated1 = new FlowSheetUserCreated();
        EntityDataGenerator.generateTestDataForModelClass(flowSheetUserCreated1);
        flowSheetUserCreated1.setArchived(!isArchived);
        dao.persist(flowSheetUserCreated1);

        FlowSheetUserCreated flowSheetUserCreated2 = new FlowSheetUserCreated();
        EntityDataGenerator.generateTestDataForModelClass(flowSheetUserCreated2);
        flowSheetUserCreated2.setArchived(isArchived);
        dao.persist(flowSheetUserCreated2);

        FlowSheetUserCreated flowSheetUserCreated3 = new FlowSheetUserCreated();
        EntityDataGenerator.generateTestDataForModelClass(flowSheetUserCreated3);
        flowSheetUserCreated3.setArchived(!isArchived);
        dao.persist(flowSheetUserCreated3);

        List<FlowSheetUserCreated> expectedResult = new ArrayList<FlowSheetUserCreated>(Arrays.asList(flowSheetUserCreated1, flowSheetUserCreated3));
        List<FlowSheetUserCreated> result = dao.getAllUserCreatedFlowSheets();

        Logger logger = MiscUtils.getLogger();

        if (result.size() != expectedResult.size()) {
            logger.warn("Array sizes do not match.");
            fail("Array sizes do not match.");
        }
        for (int i = 0; i < expectedResult.size(); i++) {
            if (!expectedResult.get(i).equals(result.get(i))) {
                logger.warn("Items  do not match.");
                fail("Items  do not match.");
            }
        }
        assertTrue(true);
    }
}
