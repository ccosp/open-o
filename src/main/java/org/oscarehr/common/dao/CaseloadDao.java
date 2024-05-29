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

import org.oscarehr.caseload.CaseloadCategory;

import java.util.List;
import java.util.Map;

public interface CaseloadDao {
    List<Integer> getCaseloadDemographicSet(String searchQuery, String[] searchParams, String[] sortParams, CaseloadCategory category, String sortDir, int page, int pageSize);
    List<Map<String, Object>> getCaseloadDemographicData(String searchQuery, Object[] params);
    Integer getCaseloadDemographicSearchSize(String searchQuery, String[] searchParams);
}
