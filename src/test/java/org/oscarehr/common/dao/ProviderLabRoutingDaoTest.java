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
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.ProviderLabRoutingModel;
import ca.openosp.openo.ehrutil.SpringUtils;

public class ProviderLabRoutingDaoTest extends DaoTestFixtures {

    protected ProviderLabRoutingDao dao = SpringUtils.getBean(ProviderLabRoutingDao.class);

    public ProviderLabRoutingDaoTest() {
    }

    @Before
    public void before() throws Exception {
        SchemaUtils.restoreTable("provider", "providerLabRouting", "patientLabRouting", "mdsMSH", "mdsPID", "mdsPV1", "mdsZFR", "mdsOBR", "mdsZRG", "document");
    }

    @Test
    public void testCreate() throws Exception {
        ProviderLabRoutingModel ql = new ProviderLabRoutingModel();
        EntityDataGenerator.generateTestDataForModelClass(ql);
        dao.persist(ql);
        assertNotNull(ql.getId());
    }

    @Test
    public void testGetProviderLabRoutings() {
        assertNotNull(dao.getProviderLabRoutings(1, "HL7"));
    }

    @Test
    public void testFindByStatusANDLabNoType() {
        assertNotNull(dao.findByStatusANDLabNoType(100, "HL7", "A"));
    }

    @Test
    public void testFindByProviderNo() {
        assertNotNull(dao.findByProviderNo("100", "N"));
    }

    @Test
    public void testFindByLabNoTypeAndStatus() {
        assertNotNull(dao.findByLabNoTypeAndStatus(100, "BCP", "STS"));
    }

}
