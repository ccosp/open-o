/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.common.dao;

import java.util.ArrayList;

import org.oscarehr.common.model.ClinicNbr;

public interface ClinicNbrDao extends AbstractDao<ClinicNbr> {
	
	ArrayList<ClinicNbr> findAll();
	
	Integer removeEntry(Integer id);
	
	int addEntry(String nbrValue, String nbrString);
}
