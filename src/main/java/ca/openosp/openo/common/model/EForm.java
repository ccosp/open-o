//CHECKSTYLE:OFF
/**
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * This software was written for
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package ca.openosp.openo.common.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

@Entity
@Table(name = "eform")
public class EForm extends AbstractModel<Integer> implements Serializable {

    /**
     * This comparator sorts EForm ascending based on the formName
     */
    public static final Comparator<EForm> FORM_NAME_COMPARATOR = new Comparator<EForm>() {
        public int compare(EForm o1, EForm o2) {
            return (o1.formName.compareTo(o2.formName));
        }
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fid")
    private Integer id;

    @Column(name = "form_name")
    private String formName;

    @Column(name = "file_name")
    private String fileName;

    private String subject;

    @Column(name = "status")
    private boolean current;

    @Column(name = "form_date")
    @Temporal(TemporalType.DATE)
    private Date formDate = new Date();

    @Column(name = "form_time")
    @Temporal(TemporalType.TIME)
    private Date formTime = formDate;

    @Column(name = "form_creator")
    private String creator;

    @Column(name = "form_html")
    private String formHtml;

    private boolean showLatestFormOnly;

    @Column(name = "patient_independent")
    private boolean patientIndependent;

    private String roleType;

    private boolean stable;

    private String errorLog;

    @Override
    public Integer getId() {
        return (id);
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public Date getFormDate() {
        return formDate;
    }

    public void setFormDate(Date formDate) {
        this.formDate = formDate;
    }

    public Date getFormTime() {
        return formTime;
    }

    public void setFormTime(Date formTime) {
        this.formTime = formTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getFormHtml() {
        return formHtml;
    }

    public void setFormHtml(String formHtml) {
        this.formHtml = formHtml;
    }

    public boolean isShowLatestFormOnly() {
        return showLatestFormOnly;
    }

    public void setShowLatestFormOnly(boolean showLatestFormOnly) {
        this.showLatestFormOnly = showLatestFormOnly;
    }

    public boolean isPatientIndependent() {
        return patientIndependent;
    }

    public void setPatientIndependent(boolean patientIndependent) {
        this.patientIndependent = patientIndependent;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public boolean isStable() {
        return stable;
    }

    public void setStable(boolean stable) {
        this.stable = stable;
    }

    public String getErrorLog() {
        return errorLog;
    }

    public void setErrorLog(String errorLog) {
        this.errorLog = errorLog;
    }

    @PreRemove
    protected void jpa_preventDelete() {
        throw (new UnsupportedOperationException("Remove is not allowed for this type of item."));
    }
}
