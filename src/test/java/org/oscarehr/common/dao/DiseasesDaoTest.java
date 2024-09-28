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
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.Diseases;
import ca.openosp.openo.ehrutil.SpringUtils;

public class DiseasesDaoTest extends DaoTestFixtures {

    protected DiseasesDao dao = SpringUtils.getBean(DiseasesDao.class);

    public DiseasesDaoTest() {
    }


    @Before
    public void before() throws Exception {
        SchemaUtils.restoreTable("diseases");
    }

    @Test
    public void testCreate() throws Exception {
        Diseases entity = new Diseases();
        EntityDataGenerator.generateTestDataForModelClass(entity);
        dao.persist(entity);

        assertNotNull(entity.getId());
    }

    @Test
    public void testFindByDemographicNo() throws Exception {
        Diseases entity = new Diseases();
        EntityDataGenerator.generateTestDataForModelClass(entity);
        entity.setDemographicNo(1);
        dao.persist(entity);

        assertEquals(1, dao.findByDemographicNo(1).size());
    }

    @Test
    public void testFindByIcd9() throws Exception {
        Diseases entity = new Diseases();
        EntityDataGenerator.generateTestDataForModelClass(entity);
        entity.setDemographicNo(1);
        entity.setIcd9Entry("250");
        dao.persist(entity);

        assertEquals(1, dao.findByIcd9("250").size());
    }
}
