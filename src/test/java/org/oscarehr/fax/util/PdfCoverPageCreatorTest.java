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
package org.oscarehr.fax.util;


import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.oscarehr.util.MiscUtils;

import oscar.oscarClinic.ClinicData;

public class PdfCoverPageCreatorTest {


	public void test() {
		
		ClinicData clinic = new ClinicData();
		clinic.setClinic_name("Dennis Test Clinic");
		clinic.setClinic_address("125 - 1035 Pacific St.");
		clinic.setClinic_city("Victoria");
		clinic.setClinic_province("BC");
		clinic.setClinic_postal("V9R 2F2");
		clinic.setClinic_phone("250-393-2345");
		clinic.setClinic_fax("250-393-2345");
		
		PdfCoverPageCreator pdfCoverPageCreator = new PdfCoverPageCreator("this is a test coverpage", clinic);
		byte[] coverpage = pdfCoverPageCreator.createCoverPage();
		try {
			System.out.print("writing array " + coverpage.toString());
			FileUtils.writeByteArrayToFile(new File("./test_cover_page.pdf"), coverpage);
		} catch (IOException e) {
			MiscUtils.getLogger().error("Error PDF Cover Page Creator test", e);
		}
	}

}
