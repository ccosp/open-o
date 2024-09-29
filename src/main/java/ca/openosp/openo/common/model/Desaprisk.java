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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "desaprisk")
public class Desaprisk extends AbstractModel<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "desaprisk_no")
    private Integer id;

    @Column(name = "desaprisk_date")
    @Temporal(TemporalType.DATE)
    private Date desapriskDate;

    @Column(name = "desaprisk_time")
    @Temporal(TemporalType.TIME)
    private Date desapriskTime;

    @Column(name = "demographic_no")
    private int demographicNo;

    @Column(name = "form_no")
    private int formNo;

    @Column(name = "provider_no")
    private String providerNo;

    @Column(name = "risk_content")
    private String riskContent;

    @Column(name = "checklist_content")
    private String checklistContent;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDesapriskDate() {
        return desapriskDate;
    }

    public void setDesapriskDate(Date desapriskDate) {
        this.desapriskDate = desapriskDate;
    }

    public Date getDesapriskTime() {
        return desapriskTime;
    }

    public void setDesapriskTime(Date desapriskTime) {
        this.desapriskTime = desapriskTime;
    }

    public int getDemographicNo() {
        return demographicNo;
    }

    public void setDemographicNo(int demographicNo) {
        this.demographicNo = demographicNo;
    }

    public int getFormNo() {
        return formNo;
    }

    public void setFormNo(int formNo) {
        this.formNo = formNo;
    }

    public String getProviderNo() {
        return providerNo;
    }

    public void setProviderNo(String providerNo) {
        this.providerNo = providerNo;
    }

    public String getRiskContent() {
        return riskContent;
    }

    public void setRiskContent(String riskContent) {
        this.riskContent = riskContent;
    }

    public String getChecklistContent() {
        return checklistContent;
    }

    public void setChecklistContent(String checklistContent) {
        this.checklistContent = checklistContent;
    }


}
