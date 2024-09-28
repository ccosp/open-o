//CHECKSTYLE:OFF
/**
 * Copyright (c) 2008-2012 Indivica Inc.
 * <p>
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package ca.openosp.openo.hospitalReportManager.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import ca.openosp.openo.common.model.AbstractModel;

@Entity
public class HRMSubClass extends AbstractModel<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String className;
    private String subClassName;
    private String subClassMnemonic;
    private String subClassDescription;
    private String sendingFacilityId;

    @ManyToOne
    @JoinColumn(name = "hrmCategoryId")
    private HRMCategory hrmCategory;

    public HRMSubClass() {
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubClassName() {
        return subClassName;
    }

    public void setSubClassName(String subClassName) {
        this.subClassName = subClassName;
    }

    public HRMCategory getHrmCategory() {
        return hrmCategory;
    }

    public void setHrmCategory(HRMCategory hrmCategory) {
        this.hrmCategory = hrmCategory;
    }

    public String getSubClassMnemonic() {
        return subClassMnemonic;
    }

    public void setSubClassMnemonic(String subClassMnemonic) {
        this.subClassMnemonic = subClassMnemonic;
    }

    public String getSubClassDescription() {
        return subClassDescription;
    }

    public void setSubClassDescription(String subClassDescription) {
        this.subClassDescription = subClassDescription;
    }

    public String getSendingFacilityId() {
        return sendingFacilityId;
    }

    public void setSendingFacilityId(String sendingFacilityId) {
        this.sendingFacilityId = sendingFacilityId;
    }


}
