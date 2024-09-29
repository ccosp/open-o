//CHECKSTYLE:OFF
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

package ca.openosp.openo.common.dao;

import java.util.*;

import javax.persistence.Query;

import ca.openosp.openo.common.model.DemographicExt;
import ca.openosp.openo.common.model.enumerator.DemographicExtKey;
import org.springframework.stereotype.Repository;

public interface DemographicExtDao extends AbstractDao<DemographicExt> {

    public DemographicExt getDemographicExt(Integer id);

    public List<DemographicExt> getDemographicExtByDemographicNo(Integer demographicNo);

    public DemographicExt getDemographicExt(Integer demographicNo, DemographicExtKey demographicExtKey);

    public DemographicExt getDemographicExt(Integer demographicNo, String key);

    public List<DemographicExt> getDemographicExtByKeyAndValue(DemographicExtKey demographicExtKey, String value);

    public List<DemographicExt> getDemographicExtByKeyAndValue(String key, String value);

    public DemographicExt getLatestDemographicExt(Integer demographicNo, String key);

    public void updateDemographicExt(DemographicExt de);

    public void saveDemographicExt(Integer demographicNo, String key, String value);

    public void removeDemographicExt(Integer id);

    public void removeDemographicExt(Integer demographicNo, String key);

    public Map<String, String> getAllValuesForDemo(Integer demo);

    public void addKey(String providerNo, Integer demo, String key, String value);

    public void addKey(String providerNo, Integer demo, String key, String newValue, String oldValue);

    public List<String[]> getListOfValuesForDemo(Integer demo);

    public String getValueForDemoKey(Integer demo, String key);

    public List<Integer> findDemographicIdsByKeyVal(DemographicExtKey demographicExtKey, String val);

    public List<Integer> findDemographicIdsByKeyVal(String key, String val);

    public List<DemographicExt> getMultipleDemographicExtKeyForDemographicNumbersByProviderNumber(
            final DemographicExtKey demographicExtKey,
            final Collection<Integer> demographicNumbers,
            final String midwifeNumber);

    public List<Integer> getDemographicNumbersByDemographicExtKeyAndProviderNumberAndDemographicLastNameRegex(
            final DemographicExtKey key,
            final String providerNumber,
            final String lastNameRegex);
}
