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
package ca.openosp.openo.billing.CA.BC.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ca.openosp.openo.common.model.AbstractModel;

@Entity
@Table(name = "billingnote")
public class BillingNotes extends AbstractModel<Integer> {

    /**
     * Default note type for BC billing
     */
    public static final Integer DEFAULT_NOTE_TYPE = 2;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "billingnote_no", unique = true, nullable = false)
    private Integer id;
    @Column(name = "billingmaster_no", nullable = false)
    private int billingmasterNo;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdate", length = 19)
    private Date createdate;
    @Column(name = "provider_no", nullable = false, length = 6)
    private String providerNo;
    @Column(name = "note", length = 65535)
    private String note = "";
    @Column(name = "note_type")
    private Integer noteType;

    public BillingNotes() {
    }

    public BillingNotes(int billingmasterNo, String providerNo) {
        this.billingmasterNo = billingmasterNo;
        this.providerNo = providerNo;
    }

    public BillingNotes(int billingmasterNo, Date createdate, String providerNo,
                        String note, Integer noteType) {
        this.billingmasterNo = billingmasterNo;
        this.createdate = createdate;
        this.providerNo = providerNo;
        this.note = note;
        this.noteType = noteType;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getBillingmasterNo() {
        return this.billingmasterNo;
    }

    public void setBillingmasterNo(int billingmasterNo) {
        this.billingmasterNo = billingmasterNo;
    }

    public Date getCreatedate() {
        return this.createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public String getProviderNo() {
        return this.providerNo;
    }

    public void setProviderNo(String providerNo) {
        this.providerNo = providerNo;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getNoteType() {
        return this.noteType;
    }

    public void setNoteType(Integer noteType) {
        this.noteType = noteType;
    }

}
