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
package ca.openosp.openo.billing.CA.BC.dao;

import static org.junit.Assert.assertNotNull;

import ca.openosp.openo.billing.CA.BC.dao.Hl7PidDao;
import org.junit.Before;
import org.junit.Test;
import ca.openosp.openo.billing.CA.BC.model.Hl7Pid;
import ca.openosp.openo.common.dao.DaoTestFixtures;
import ca.openosp.openo.common.dao.utils.EntityDataGenerator;
import ca.openosp.openo.common.dao.utils.SchemaUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

public class Hl7PidDaoTest extends DaoTestFixtures {

    public Hl7PidDao dao = SpringUtils.getBean(Hl7PidDao.class);

    @Before
    public void before() throws Exception {
        SchemaUtils.restoreTable("hl7_pid", "hl7_link", "hl7_msh", "hl7_obr", "hl7_obx", "provider", "hl7_message");
    }

    @Test
    public void testCreate() throws Exception {
        Hl7Pid entity = new Hl7Pid();
        EntityDataGenerator.generateTestDataForModelClass(entity);
        dao.persist(entity);
        assertNotNull(entity.getId());
    }

    @Test
    public void testFindByMessageId() {
        assertNotNull(dao.findByMessageId(100));
    }

    @Test
    public void testFindPidsByStatus() {
        assertNotNull(dao.findPidsByStatus("F"));
    }

    @Test
    public void testFindPidsAndMshByMessageId() {
        assertNotNull(dao.findPidsAndMshByMessageId(100));
    }

}
