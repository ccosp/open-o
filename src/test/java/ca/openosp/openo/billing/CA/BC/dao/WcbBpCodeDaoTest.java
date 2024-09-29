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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import ca.openosp.openo.billing.CA.BC.model.WcbBpCode;
import ca.openosp.openo.billing.CA.dao.WcbBpCodeDao;
import ca.openosp.openo.common.dao.DaoTestFixtures;
import ca.openosp.openo.common.dao.utils.EntityDataGenerator;
import ca.openosp.openo.common.dao.utils.SchemaUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

public class WcbBpCodeDaoTest extends DaoTestFixtures {

    public WcbBpCodeDao dao = SpringUtils.getBean(WcbBpCodeDao.class);


    @Before
    public void before() throws Exception {
        SchemaUtils.restoreTable("wcb_bp_code");
    }

    @Test
    public void testCreate() throws Exception {
        WcbBpCode entity = new WcbBpCode();
        EntityDataGenerator.generateTestDataForModelClass(entity);
        entity.setCode("code");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append(i);
        }
        entity.setUsagenote(sb.toString());

        dao.persist(entity);
        assertNotNull(entity.getId());

        List<WcbBpCode> entityChecks = dao.findByCodeOrAnyLevel(entity.getCode());
        assertNotNull(entityChecks);
        assertFalse(entityChecks.isEmpty());
    }

}
