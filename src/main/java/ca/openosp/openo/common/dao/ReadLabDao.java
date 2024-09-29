//CHECKSTYLE:OFF
/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * Copyright (c) 2008-2012 Indivica Inc.
 * <p>
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 * <p>
 * Modifications made by Magenta Health in 2024.
 */
package ca.openosp.openo.common.dao;

import ca.openosp.openo.common.model.ReadLab;

public interface ReadLabDao extends AbstractDao<ReadLab> {
    void markAsRead(String providerNo, String labType, Integer labId);

    boolean isRead(String providerNo, String labType, Integer labId);

    ReadLab getByProviderNoAndLabTypeAndLabId(String providerNo, String labType, Integer labId);
}
