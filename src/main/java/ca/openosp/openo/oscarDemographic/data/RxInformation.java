//CHECKSTYLE:OFF
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


package ca.openosp.openo.oscarDemographic.data;

import ca.openosp.openo.oscarRx.data.RxPrescriptionData;
import ca.openosp.openo.common.model.Allergy;
import ca.openosp.openo.ehrutil.LoggedInInfo;

import ca.openosp.openo.oscarRx.data.RxPatientData;

public class RxInformation {
    private String currentMedication;
    private String allergies;

    public String getCurrentMedication(String demographic_no) {
        RxPrescriptionData prescriptData = new RxPrescriptionData();
        RxPrescriptionData.Prescription[] arr = {};
        arr = prescriptData.getUniquePrescriptionsByPatient(Integer.parseInt(demographic_no));
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].isCurrent()) {

                stringBuffer.append(arr[i].getFullOutLine().replaceAll(";", " ") + "\n");
                // stringBuffer.append(arr[i].getRxDisplay()+"\n");
            }
        }
        this.currentMedication = stringBuffer.toString();
        return this.currentMedication;
    }

    public String getAllergies(LoggedInInfo loggedInInfo, String demographic_no) {
        RxPatientData.Patient patient = RxPatientData.getPatient(loggedInInfo, Integer.parseInt(demographic_no));
        Allergy[] allergies = {};
        allergies = patient.getActiveAllergies();
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < allergies.length; i++) {
            Allergy allerg = allergies[i];
            stringBuffer.append(allerg.getDescription() + "  " + Allergy.getTypeDesc(allerg.getTypeCode()) + " \n");
        }
        this.allergies = stringBuffer.toString();

        return this.allergies;
    }
}
