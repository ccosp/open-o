/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
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
 * <p>
 * Modifications made by Magenta Health in 2024.
 */

package org.oscarehr.common.dao;

import java.util.Collection;
import java.util.List;

import org.oscarehr.common.model.DemographicCust;

public interface DemographicCustDao extends AbstractDao<DemographicCust> {

    public List<DemographicCust> findMultipleMidwife(Collection<Integer> demographicNos, String oldMidwife);

    public List<DemographicCust> findMultipleResident(Collection<Integer> demographicNos, String oldResident);

    public List<DemographicCust> findMultipleNurse(Collection<Integer> demographicNos, String oldNurse);

    public List<DemographicCust> findByResident(String resident);

    public Integer select_demoname(String resident, String lastNameRegExp);

    public Integer select_demoname1(String nurse, String lastNameRegExp);

    public Integer select_demoname2(String midwife, String lastNameRegExp);

    public List<DemographicCust> findAllByDemographicNumber(int demographic_no);

}
