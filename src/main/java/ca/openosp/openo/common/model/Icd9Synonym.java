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
package ca.openosp.openo.common.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/*
 * Author: Dennis Warren
 * Company: Colcamex Resources
 * Date: November 2014
 * For: UBC Pharmacy Clinic and McMaster Department of Family Medicine
 */
@Entity
@Table(name = "Icd9Synonym")
public class Icd9Synonym extends AbstractModel<Integer> {

    private Integer id;
    private String dxCode;
    private String patientFriendly;

    @Id
    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDxCode() {
        return dxCode;
    }

    public void setDxCode(String dxCode) {
        this.dxCode = dxCode;
    }

    public String getPatientFriendly() {
        return patientFriendly;
    }

    public void setPatientFriendly(String patientFriendly) {
        this.patientFriendly = patientFriendly;
    }

}
