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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.DemographicExt;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.common.dao.DemographicDao;

public class DemographicExtDaoTest extends DaoTestFixtures {

	protected DemographicExtDao dao = SpringUtils.getBean(DemographicExtDao.class);
	protected DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean(DemographicDao.class);

	public DemographicExtDaoTest() {

	}

	@Before
	public void before() throws Exception {
		//SchemaUtils.restoreTable("demographicExt","lst_gender","demographic_merged","admission");
		SchemaUtils.restoreTable("demographic", "lst_gender", "admission", "demographic_merged", "program", 
                                "health_safety", "provider", "providersite", "site", "program_team","log", "Facility","demographicExt");

		
		for(int i=0; i<20; i++){
		Demographic entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		demographicDao.save(entity);
		}
		
	}

	@Test
	public void testCreate() throws Exception {
		DemographicExt entity = new DemographicExt();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		dao.persist(entity);
		assertNotNull(entity.getId());
	}

	// Fail
	@Test
	public void testGetDemographicExt() throws Exception {
		DemographicExt entity = new DemographicExt();
		entity = (DemographicExt)EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(1);
		dao.persist(entity);
		DemographicExt foundEntity = dao.getDemographicExt(entity.getId());
		assertEquals(entity, foundEntity);
	}
	@Test
	public void getDemographicExtByDemographicNo() throws Exception {
		DemographicExt item1 = new DemographicExt();
		EntityDataGenerator.generateTestDataForModelClass(item1);
		item1.setDemographicNo(20);
		DemographicExt item2 = new DemographicExt();
		EntityDataGenerator.generateTestDataForModelClass(item2);
		item2.setDemographicNo(20);
		DemographicExt item3 = new DemographicExt();
		EntityDataGenerator.generateTestDataForModelClass(item3);
		item3.setDemographicNo(20);
		dao.persist(item1);
		dao.persist(item2);
		dao.persist(item3);
		List<DemographicExt> found = dao.getDemographicExtByDemographicNo(20);
		List<DemographicExt> items = new ArrayList<DemographicExt>();
		items.add(item1);
		items.add(item2);
		items.add(item3);
		assertTrue(found.containsAll(items));
	}

	@Test
	public void testGetDemographicExtWithKeyAndDemographicNo() throws Exception {
		DemographicExt entity = new DemographicExt();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setKey("Test");
		entity.setDemographicNo(20);
		dao.persist(entity);
		DemographicExt foundEntity = dao.getDemographicExt(20, "Test");
		assertEquals(entity, foundEntity);
	}

	@Test
	public void testGetLatestDemographic() throws Exception {
		DemographicExt entity = new DemographicExt();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setKey("Test");
		entity.setDemographicNo(20);
		entity.setDateCreated(new Date(111111));
		dao.persist(entity);
		DemographicExt foundEntity = dao.getLatestDemographicExt(20, "Test");
		assertEquals(entity.getId(), foundEntity.getId());
	}

	@Test
	public void testUpdateDemographicExt() throws Exception {
		DemographicExt entity = new DemographicExt();
		
		entity = (DemographicExt)EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(1);
		dao.persist(entity);
		entity.setDemographicNo(20);
		dao.updateDemographicExt(entity);
		
		assertTrue(dao.getDemographicExt(1).getDemographicNo() == 20);
	}

	@Test
	public void testUpdateDemographicExtExceptionWithNull() throws Exception {
		DemographicExt entity = new DemographicExt();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		dao.persist(entity);
		entity = null;
		try{
			dao.updateDemographicExt(entity);
		}catch(IllegalArgumentException ex){
			assert(true);
		}
	}

	@Test
	public void testSaveDemographicExt() {
		dao.saveDemographicExt(20, "Test", "TestVal");
		DemographicExt result = dao.getDemographicExt(20, "Test");
		assert("TestVal".equals(result.getValue()));
	}
}
