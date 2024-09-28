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
package org.oscarehr.billing.CA.BC.dao;

import static org.junit.Assert.assertNotNull;

import ca.openosp.openo.billing.CA.BC.dao.Hl7ObrDao;
import org.junit.Before;
import org.junit.Test;
import ca.openosp.openo.billing.CA.BC.model.Hl7Obr;
import ca.openosp.openo.common.dao.DaoTestFixtures;
import ca.openosp.openo.common.dao.utils.EntityDataGenerator;
import ca.openosp.openo.common.dao.utils.SchemaUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

public class Hl7ObrDaoTest extends DaoTestFixtures {

    public Hl7ObrDao dao = SpringUtils.getBean(Hl7ObrDao.class);


    @Before
    public void before() throws Exception {
        SchemaUtils.restoreTable("hl7_obr", "hl7_pid", "hl7_link", "hl7_msh", "hl7_obx");
    }

    @Test
    public void testCreate() throws Exception {
        Hl7Obr entity = new Hl7Obr();
        EntityDataGenerator.generateTestDataForModelClass(entity);
        dao.persist(entity);
        assertNotNull(entity.getId());
    }

    @Test
    public void testFindByPid() {
        assertNotNull(dao.findByPid(100));
    }
}
