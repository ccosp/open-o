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

import ca.openosp.openo.billing.CA.BC.dao.TeleplanC12Dao;
import org.junit.Before;
import org.junit.Test;
import ca.openosp.openo.billing.CA.BC.model.TeleplanC12;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

public class TeleplanC12DaoTest extends DaoTestFixtures {

    public TeleplanC12Dao dao = SpringUtils.getBean(TeleplanC12Dao.class);

    public TeleplanC12DaoTest() {
    }

    @Before
    public void before() throws Exception {
        SchemaUtils.restoreTable("teleplanC12", "teleplanS21");
    }

    @Test
    public void testCreate() throws Exception {
        TeleplanC12 entity = new TeleplanC12();
        EntityDataGenerator.generateTestDataForModelClass(entity);
        dao.persist(entity);
        assertNotNull(entity.getId());
    }

    @Test
    public void testFindCurrent() {
        assertNotNull(dao.findCurrent());
    }

    @Test
    public void testFindByOfficeClaimNo() {
        assertNotNull(dao.findByOfficeClaimNo("100"));
    }

    @Test
    public void testFindRejected() {
        assertNotNull(dao.findRejected());
    }
}
