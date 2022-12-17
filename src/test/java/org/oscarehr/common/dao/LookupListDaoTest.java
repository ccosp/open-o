/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.common.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.LookupList;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

public class LookupListDaoTest extends DaoTestFixtures {

	protected LookupListDao dao = SpringUtils.getBean(LookupListDao.class);

	@Before
	public void before() throws Exception {
		SchemaUtils.restoreTable("LookupList","LookupListItem");
	}

	@Test
	public void testCreate() throws Exception {
		LookupList entity = new LookupList();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		dao.persist(entity);
		assertNotNull(entity.getId());
	}
	
	@Test
	public void testFindAllActive() throws Exception {
		
		String name1 = "alpha";
		String name2 = "bravo";
		String name3 = "charlie";
		
		boolean isActive = true;
		
		LookupList lookupList1 = new LookupList();
		EntityDataGenerator.generateTestDataForModelClass(lookupList1);
		lookupList1.setActive(isActive);
		lookupList1.setName(name2);
		dao.persist(lookupList1);
		
		LookupList lookupList2 = new LookupList();
		EntityDataGenerator.generateTestDataForModelClass(lookupList2);
		lookupList2.setActive(!isActive);
		lookupList2.setName(name2);
		dao.persist(lookupList2);
		
		LookupList lookupList3 = new LookupList();
		EntityDataGenerator.generateTestDataForModelClass(lookupList3);
		lookupList3.setActive(isActive);
		lookupList3.setName(name3);
		dao.persist(lookupList3);
		
		LookupList lookupList4 = new LookupList();
		EntityDataGenerator.generateTestDataForModelClass(lookupList4);
		lookupList4.setActive(isActive);
		lookupList4.setName(name1);
		dao.persist(lookupList4);
		
		List<LookupList> expectedResult = new ArrayList<LookupList>(Arrays.asList(lookupList4, lookupList1, lookupList3));
		List<LookupList> result = dao.findAllActive().subList(0, 3);

		Logger logger = MiscUtils.getLogger();
		
		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match.");
			fail("Array sizes do not match.");
		}
		for (int i = 0; i < expectedResult.size(); i++) {
			if (!expectedResult.get(i).equals(result.get(i))){
				logger.warn("Items  do not match.");
				fail("Items  do not match.");
			}
		}
		assertTrue(true);
	}
	
	@Test
	public void testFindByName() throws Exception {
		
		String name1 = "alpha";
		String name2 = "bravo";
		String name3 = "charlie";
		
		LookupList lookupList1 = new LookupList();
		EntityDataGenerator.generateTestDataForModelClass(lookupList1);
		lookupList1.setName(name2);
		dao.persist(lookupList1);
		
		LookupList lookupList2 = new LookupList();
		EntityDataGenerator.generateTestDataForModelClass(lookupList2);
		lookupList2.setName(name1);
		dao.persist(lookupList2);
		
		LookupList lookupList3 = new LookupList();
		EntityDataGenerator.generateTestDataForModelClass(lookupList3);
		lookupList3.setName(name3);
		dao.persist(lookupList3);
		
		LookupList expectedResult = lookupList2;
		LookupList result = dao.findByName(name1);
		
		assertEquals(expectedResult, result);
	}
}