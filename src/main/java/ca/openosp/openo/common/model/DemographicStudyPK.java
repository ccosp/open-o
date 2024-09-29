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

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class DemographicStudyPK implements Serializable {

    @Column(name = "demographic_no")
    private Integer demographicNo;
    @Column(name = "study_no")
    private Integer studyNo;


    public Integer getDemographicNo() {
        return demographicNo;
    }

    public void setDemographicNo(Integer demographicNo) {
        this.demographicNo = demographicNo;
    }

    public Integer getStudyNo() {
        return studyNo;
    }

    public void setStudyNo(Integer studyNo) {
        this.studyNo = studyNo;
    }

    @Override
    public String toString() {
        return ("demographicNo=" + demographicNo + ", studyNo=" + studyNo);
    }

    @Override
    public int hashCode() {
        return (demographicNo);
    }

    @Override
    public boolean equals(Object o) {
        try {
            DemographicStudyPK o1 = (DemographicStudyPK) o;
            return ((demographicNo.equals(o1.demographicNo)) && (studyNo.equals(o1.studyNo)));
        } catch (RuntimeException e) {
            return (false);
        }
    }

}
