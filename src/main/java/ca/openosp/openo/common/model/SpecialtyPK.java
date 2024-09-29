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

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class SpecialtyPK implements Serializable {

    private String region;
    private String specialty;

    public SpecialtyPK() {

    }

    public SpecialtyPK(String region, String specialty) {
        setRegion(region);
        setSpecialty(specialty);
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    @Override
    public String toString() {
        return getRegion() + getSpecialty();
    }

    @Override
    public int hashCode() {
        return (toString().hashCode());
    }

    @Override
    public boolean equals(Object o) {
        try {
            SpecialtyPK o1 = (SpecialtyPK) o;
            return ((region.equals(o1.region)) && (specialty.equals(o1.specialty)));
        } catch (RuntimeException e) {
            return (false);
        }
    }
}

	