/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 *
 * Modifications made by Magenta Health in 2024.
 */

package org.oscarehr.common.dao;

import org.oscarehr.common.model.HL7HandlerMSHMapping;

public interface HL7HandlerMSHMappingDao extends AbstractDao<HL7HandlerMSHMapping> {
    HL7HandlerMSHMapping findByFacility(String facility);
}
