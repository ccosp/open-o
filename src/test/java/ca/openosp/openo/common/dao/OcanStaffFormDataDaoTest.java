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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import ca.openosp.openo.common.dao.utils.EntityDataGenerator;
import ca.openosp.openo.common.dao.utils.SchemaUtils;
import ca.openosp.openo.common.model.OcanStaffFormData;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

public class OcanStaffFormDataDaoTest extends DaoTestFixtures {

    protected OcanStaffFormDataDao dao = SpringUtils.getBean(OcanStaffFormDataDao.class);


    @Before
    public void before() throws Exception {
        SchemaUtils.restoreTable("OcanStaffFormData");
    }

    @Test
    public void testFindByQuestion() throws Exception {

        int id1 = 111;
        int id2 = 222;

        String question1 = "Some Text";
        String question2 = "More Text";

        OcanStaffFormData ocanStaffFormData1 = new OcanStaffFormData();
        EntityDataGenerator.generateTestDataForModelClass(ocanStaffFormData1);
        ocanStaffFormData1.setOcanStaffFormId(id1);
        ocanStaffFormData1.setQuestion(question1);
        dao.persist(ocanStaffFormData1);

        OcanStaffFormData ocanStaffFormData2 = new OcanStaffFormData();
        EntityDataGenerator.generateTestDataForModelClass(ocanStaffFormData2);
        ocanStaffFormData2.setOcanStaffFormId(id2);
        ocanStaffFormData2.setQuestion(question2);
        dao.persist(ocanStaffFormData2);

        OcanStaffFormData ocanStaffFormData3 = new OcanStaffFormData();
        EntityDataGenerator.generateTestDataForModelClass(ocanStaffFormData3);
        ocanStaffFormData3.setOcanStaffFormId(id1);
        ocanStaffFormData3.setQuestion(question1);
        dao.persist(ocanStaffFormData3);

        List<OcanStaffFormData> expectedResult = new ArrayList<OcanStaffFormData>(Arrays.asList(ocanStaffFormData1, ocanStaffFormData3));
        List<OcanStaffFormData> result = dao.findByQuestion(id1, question1);

        Logger logger = MiscUtils.getLogger();

        if (result.size() != expectedResult.size()) {
            logger.warn("Array sizes do not match. RESULT: " + result.size());
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

    @Test
    public void testFindLatestByQuestion() throws Exception {

        int id1 = 100;
        int id2 = 200;
        int id3 = 300;

        String question1 = "Some Text";
        String question2 = "More Text";

        OcanStaffFormData ocanStaffFormData1 = new OcanStaffFormData();
        EntityDataGenerator.generateTestDataForModelClass(ocanStaffFormData1);
        ocanStaffFormData1.setOcanStaffFormId(id1);
        ocanStaffFormData1.setQuestion(question1);
        dao.persist(ocanStaffFormData1);

        OcanStaffFormData ocanStaffFormData2 = new OcanStaffFormData();
        EntityDataGenerator.generateTestDataForModelClass(ocanStaffFormData2);
        ocanStaffFormData2.setOcanStaffFormId(id2);
        ocanStaffFormData2.setQuestion(question2);
        dao.persist(ocanStaffFormData2);

        OcanStaffFormData ocanStaffFormData3 = new OcanStaffFormData();
        EntityDataGenerator.generateTestDataForModelClass(ocanStaffFormData3);
        ocanStaffFormData3.setOcanStaffFormId(id3);
        ocanStaffFormData3.setQuestion(question1);
        dao.persist(ocanStaffFormData3);

        OcanStaffFormData expectedResult = ocanStaffFormData3;
        OcanStaffFormData result = dao.findLatestByQuestion(id3, question1);

        assertEquals(expectedResult, result);
    }

    @Test
    public void testFindByForm() throws Exception {

        int id1 = 100;
        int id2 = 200;

        OcanStaffFormData ocanStaffFormData1 = new OcanStaffFormData();
        EntityDataGenerator.generateTestDataForModelClass(ocanStaffFormData1);
        ocanStaffFormData1.setOcanStaffFormId(id1);
        dao.persist(ocanStaffFormData1);

        OcanStaffFormData ocanStaffFormData2 = new OcanStaffFormData();
        EntityDataGenerator.generateTestDataForModelClass(ocanStaffFormData2);
        ocanStaffFormData2.setOcanStaffFormId(id2);
        dao.persist(ocanStaffFormData2);

        OcanStaffFormData ocanStaffFormData3 = new OcanStaffFormData();
        EntityDataGenerator.generateTestDataForModelClass(ocanStaffFormData3);
        ocanStaffFormData3.setOcanStaffFormId(id1);
        dao.persist(ocanStaffFormData3);

        List<OcanStaffFormData> expectedResult = new ArrayList<OcanStaffFormData>(Arrays.asList(ocanStaffFormData1, ocanStaffFormData3));
        List<OcanStaffFormData> result = dao.findByForm(id1);

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

    @Test
    public void testFindByAnswer() throws Exception {

        int id1 = 100;
        int id2 = 200;

        String answer1 = "Some answer";
        String answer2 = "More answer";

        OcanStaffFormData ocanStaffFormData1 = new OcanStaffFormData();
        EntityDataGenerator.generateTestDataForModelClass(ocanStaffFormData1);
        ocanStaffFormData1.setOcanStaffFormId(id1);
        ocanStaffFormData1.setAnswer(answer1);
        dao.persist(ocanStaffFormData1);

        OcanStaffFormData ocanStaffFormData2 = new OcanStaffFormData();
        EntityDataGenerator.generateTestDataForModelClass(ocanStaffFormData2);
        ocanStaffFormData2.setOcanStaffFormId(id2);
        ocanStaffFormData2.setAnswer(answer2);
        dao.persist(ocanStaffFormData2);

        OcanStaffFormData ocanStaffFormData3 = new OcanStaffFormData();
        EntityDataGenerator.generateTestDataForModelClass(ocanStaffFormData3);
        ocanStaffFormData3.setOcanStaffFormId(id1);
        ocanStaffFormData3.setAnswer(answer1);
        dao.persist(ocanStaffFormData3);

        OcanStaffFormData expectedResult = ocanStaffFormData2;
        OcanStaffFormData result = dao.findByAnswer(id2, answer2);

        assertEquals(expectedResult, result);
    }
}
