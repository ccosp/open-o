package ca.openosp.openo.integration.fhir.model;
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

import ca.openosp.openo.integration.fhir.model.Practitioner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ca.openosp.openo.common.model.Provider;

public class PractitionerTest {

    private static Practitioner practitioner;

    @BeforeClass
    public static void setUpBeforeClass() {
        Provider provider = new Provider();
        provider.setProviderNo("8879");
        provider.setFirstName("Doug");
        provider.setLastName("Ross");
        provider.setHsoNo("12342");
        provider.setOhipNo("12342");

        practitioner = new Practitioner(provider);
    }

    @AfterClass
    public static void tearDownAfterClass() {
        practitioner = null;
    }


    @Test
    public void testGetFhirJSON() {
        System.out.println(practitioner.getFhirJSON());
    }

}
