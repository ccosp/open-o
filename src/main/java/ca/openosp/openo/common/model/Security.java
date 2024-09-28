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

import org.apache.logging.log4j.Logger;
import ca.openosp.openo.ehrutil.EncryptionUtils;
import ca.openosp.openo.ehrutil.MiscUtils;


@Entity
@Table(name = "security")
public class Security extends AbstractModel<Integer> {
    private static Logger logger = MiscUtils.getLogger();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "security_no")
    private Integer id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "provider_no")
    private String providerNo;

    @Column(name = "pin")
    private String pin;

    @Column(name = "b_ExpireSet")
    private Integer BExpireset;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_ExpireDate")
    private Date dateExpiredate;

    @Column(name = "b_LocalLockSet")
    private Integer BLocallockset;

    @Column(name = "b_RemoteLockSet")
    private Integer BRemotelockset;


    @Column(name = "forcePasswordReset")
    private Boolean forcePasswordReset = true;

    @Column(name = "oneIdKey")
    private String oneIdKey = "";

    @Column(name = "oneIdEmail")
    private String oneIdEmail = "";

    @Column(name = "delegateOneIdEmail")
    private String delagateOneIdEmail = "";


    @Temporal(TemporalType.TIMESTAMP)
    private Date passwordUpdateDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date pinUpdateDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateDate;

    private String lastUpdateUser;


    /**
     * default constructor
     */
    public Security() {
    }

    public Security(Security security) {
        setSecurityNo(security.getSecurityNo());
        setUserName(security.getUserName());
        setPassword(security.getPassword());
        setProviderNo(security.getProviderNo());
        setPin(security.getPin());
        setBRemotelockset(security.getBRemotelockset());
        setBLocallockset(security.getBLocallockset());
        setDateExpiredate(security.getDateExpiredate());
        setBExpireset(security.getBExpireset());
        setForcePasswordReset(security.isForcePasswordReset());
    }

    /**
     * full constructor
     */
    public Security(String userName, String password, String providerNo, String pin, Integer BRemotelockset, Integer BLocallockset, Date dateExpiredate, Integer BExpireset, Boolean forcePasswordReset) {
        this.userName = userName;
        this.password = password;
        this.providerNo = providerNo;
        this.pin = pin;
        this.BRemotelockset = BRemotelockset;
        this.BLocallockset = BLocallockset;
        this.dateExpiredate = dateExpiredate;
        this.BExpireset = BExpireset;
        this.forcePasswordReset = forcePasswordReset;
    }


    @Override
    public Integer getId() {
        return id;
    }

    public Integer getSecurityNo() {
        return id;
    }

    public void setSecurityNo(Integer securityNo) {
        this.id = securityNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProviderNo() {
        return providerNo;
    }

    public void setProviderNo(String providerNo) {
        this.providerNo = providerNo;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Integer getBExpireset() {
        return BExpireset;
    }

    public void setBExpireset(Integer bExpireset) {
        BExpireset = bExpireset;
    }

    public Date getDateExpiredate() {
        return dateExpiredate;
    }

    public void setDateExpiredate(Date dateExpiredate) {
        this.dateExpiredate = dateExpiredate;
    }

    public Integer getBLocallockset() {
        return BLocallockset;
    }

    public void setBLocallockset(Integer bLocallockset) {
        BLocallockset = bLocallockset;
    }

    public Integer getBRemotelockset() {
        return BRemotelockset;
    }

    public void setBRemotelockset(Integer bRemotelockset) {
        BRemotelockset = bRemotelockset;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    /**
     * @return true if inputed password equals password in the DB, false otherwise.
     */
    public boolean checkPassword(String inputedPassword) {
        if (password == null) return (false);

        byte[] sha1Bytes = EncryptionUtils.getSha1(inputedPassword);
        StringBuilder sb = new StringBuilder();
        for (byte b : sha1Bytes) {
            sb.append(b);
        }

        if (password.equals(sb.toString())) {
            return (true);
        } else {
            throttleOnFailedLogin();
            return (false);
        }
    }

    protected void throttleOnFailedLogin() {
        try {
            // sleep to throttle anyone trying to brute force hack passwords
            Thread.sleep(250);
        } catch (InterruptedException e) {
            logger.error("Error", e);
        }
    }

    public Boolean isForcePasswordReset() {
        return forcePasswordReset;
    }

    public void setForcePasswordReset(Boolean forcePasswordReset) {
        this.forcePasswordReset = forcePasswordReset;
    }

    public Date getPasswordUpdateDate() {
        return passwordUpdateDate;
    }

    public void setPasswordUpdateDate(Date passwordUpdateDate) {
        this.passwordUpdateDate = passwordUpdateDate;
    }

    public Date getPinUpdateDate() {
        return pinUpdateDate;
    }

    public void setPinUpdateDate(Date pinUpdateDate) {
        this.pinUpdateDate = pinUpdateDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    public String getOneIdKey() {
        return oneIdKey;
    }

    public void setOneIdKey(String oneIdKey) {
        this.oneIdKey = oneIdKey;
    }

    public String getOneIdEmail() {
        return oneIdEmail;
    }

    public void setOneIdEmail(String oneIdEmail) {
        this.oneIdEmail = oneIdEmail;
    }

    public String getDelagateOneIdEmail() {
        return delagateOneIdEmail;
    }

    public void setDelagateOneIdEmail(String delagateOneIdEmail) {
        this.delagateOneIdEmail = delagateOneIdEmail;
    }
}
