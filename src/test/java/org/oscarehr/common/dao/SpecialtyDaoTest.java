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
import ca.openosp.openo.common.dao.utils.SchemaUtils;
import ca.openosp.openo.common.model.Specialty;
import ca.openosp.openo.common.model.SpecialtyPK;
import ca.openosp.openo.ehrutil.SpringUtils;

public class SpecialtyDaoTest extends DaoTestFixtures {

    protected SpecialtyDao dao = SpringUtils.getBean(SpecialtyDao.class);

    public SpecialtyDaoTest() {
    }


    @Before
    public void before() throws Exception {
        SchemaUtils.restoreTable("specialty");
    }

    @Test
    public void testCreate() {
        Specialty entity = new Specialty();
        entity.setId(new SpecialtyPK("TA", "00"));
        entity.setSpecialtydesc("desc");
        dao.persist(entity);

        assertNotNull(dao.find(new SpecialtyPK("TA", "00")));
    }

}

