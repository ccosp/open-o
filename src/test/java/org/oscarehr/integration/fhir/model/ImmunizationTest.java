package org.oscarehr.integration.fhir.model;
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

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oscarehr.common.model.Prevention;
import org.oscarehr.util.MiscUtils;

public class ImmunizationTest {
	private static Logger logger = MiscUtils.getLogger();
	private static Prevention immunization;
	private static AbstractOscarFhirResource< org.hl7.fhir.dstu3.model.Immunization, Prevention> oscarFhirResource;
	private static final String testJSON = "";
	private static final String testXML = "";
	private static Date date = new Date(System.currentTimeMillis());
	// private static String fhirJson = "{\"resourceType\":\"Immunization\",\"status\":\"completed\",\"notGiven\":false,\"vaccineCode\":{\"coding\":[{\"system\":\"http://hl7.org/fhir/sid/cvx\",\"code\":\"HPV\",\"display\":\"Pfizer HPV Vaccine\"}]},\"patient\":{\"display\":\"null\"},\"date\":\"2017-09-17T18:21:26-07:00\",\"lotNumber\":\"123456\",\"site\":{\"coding\":[{\"system\":\"http://hl7.org/fhir/v3/ActSite\",\"code\":\"LD\",\"display\":\"LD\"}]},\"route\":{\"coding\":[{\"system\":\"http://hl7.org/fhir/v3/RouteOfAdministration\",\"code\":\"IM\",\"display\":\"IM\"}]},\"doseQuantity\":{\"value\":20.0,\"unit\":\"cc\"},\"note\":[{\"text\":\"Didnt want it.\"},{\"text\":\"This is a comment\"}]}";
	
	@BeforeClass
	public static void setUpBeforeClass() {
		immunization = new Prevention();
		immunization.setImmunizationDate(date);
		immunization.setImmunizationRefused(Boolean.FALSE);
		immunization.setImmunizationRefusedReason("Didnt want it.");
		immunization.setComment("This is a comment");
		immunization.setDose("20cc");
		immunization.setImmunizationType("HPV");
		immunization.setSite("LD");
		immunization.setRoute("IM");
		immunization.setLotNo("123456");
		immunization.setManufacture("Pfizer");
		immunization.setName("HPV Vaccine");
	
		oscarFhirResource = new org.oscarehr.integration.fhir.model.Immunization<Prevention>( immunization );		
	}

	@AfterClass
	public static void tearDownAfterClass() {
		immunization = null;
		oscarFhirResource = null;
	}
	
	@Test
	public void testGetPrevention() {
		logger.info( "testGetPrevention" );
		assertEquals( date, oscarFhirResource.getOscarResource().getPreventionDate() );
	}

	@Test
	public void testGetFhirImmunization() {
		logger.info( "testGetFhirImmunization" );
		assertEquals( date, oscarFhirResource.getFhirResource().getDate() );
	}

	// @Test
	public void testGetFhirImmunizationJSONDstu3() {
		logger.info( "testGetFhirImmunizationJSONDstu3" );
		logger.info( oscarFhirResource.getFhirJSON() );
		assertEquals( testJSON, oscarFhirResource.getFhirJSON() );		
	}


	// @Test
	public void testGetFhirImmunizationXML() {	
		logger.info( "testGetFhirImmunizationXML" );
		logger.info( oscarFhirResource.getFhirXML() );
		assertEquals( testXML, oscarFhirResource.getFhirXML() );		
	}

}
