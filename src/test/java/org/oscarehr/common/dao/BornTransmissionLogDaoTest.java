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

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.BornTransmissionLog;
import ca.openosp.openo.ehrutil.SpringUtils;

public class BornTransmissionLogDaoTest extends DaoTestFixtures {

    protected BornTransmissionLogDao dao = SpringUtils.getBean(BornTransmissionLogDao.class);

    public BornTransmissionLogDaoTest() {
    }

    @Before
    public void before() throws Exception {
        SchemaUtils.restoreTable("BornTransmissionLog");
    }

    @Test
    public void testCreate() throws Exception {
        BornTransmissionLog entity = new BornTransmissionLog();
        EntityDataGenerator.generateTestDataForModelClass(entity);

        entity.setFilename("test01.xml");
        entity.setSubmitDateTime(new Date());
        entity.setSuccess(true);
        dao.persist(entity);

        Long expected = 2L;
        assertEquals(expected, dao.getSeqNoToday("test", 2));
        expected = 1L;
        assertEquals(expected, dao.getSeqNoToday("noname", 2));
    }
}
