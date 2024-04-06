/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.EyeformMacro;

public interface EyeformMacroDao extends AbstractDao<EyeformMacro> {
	List<EyeformMacro> getMacros();
}
