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
package ca.openosp.openo.ws.rest.to;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import ca.openosp.openo.ws.rest.to.model.ConsultationServiceTo1;
import ca.openosp.openo.ws.rest.to.model.ProfessionalSpecialistTo1;

@XmlRootElement
public class ReferralResponse {
    private List<ProfessionalSpecialistTo1> specialists = new ArrayList<ProfessionalSpecialistTo1>();

    private List<ConsultationServiceTo1> services = new ArrayList<ConsultationServiceTo1>();

    public List<ProfessionalSpecialistTo1> getSpecialists() {
        return specialists;
    }

    public void setSpecialists(List<ProfessionalSpecialistTo1> specialists) {
        this.specialists = specialists;
    }

    public List<ConsultationServiceTo1> getServices() {
        return services;
    }

    public void setServices(List<ConsultationServiceTo1> services) {
        this.services = services;
    }


}
