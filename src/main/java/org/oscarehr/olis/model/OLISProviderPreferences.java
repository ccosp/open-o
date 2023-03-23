/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package org.oscarehr.olis.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.oscarehr.common.model.AbstractModel;

@Entity
public class OLISProviderPreferences extends AbstractModel<String> {
	@Id
	private String providerId;

	private String startTime;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastRun;
	
	@Override
	public String getId() {
		return providerId;
	}
	
    public String getProviderId() {
	    return providerId;
    }
	
	public void setProviderId(String providerNo) {
		this.providerId = providerNo;
    }	

	public String getStartTime() {
    	return startTime;
    }

	public void setStartTime(String startTime) {
    	this.startTime = startTime;
    }

	public Date getLastRun() {
		return lastRun;
	}

	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}

	public OLISProviderPreferences(){
		super();
	}
}
