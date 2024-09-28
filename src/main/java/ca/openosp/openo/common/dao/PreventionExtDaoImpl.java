//CHECKSTYLE:OFF
/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * <p>
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * This software was written for
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 * <p>
 * Modifications made by Magenta Health in 2024.
 */

package ca.openosp.openo.common.dao;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import ca.openosp.openo.common.model.PreventionExt;
import org.springframework.stereotype.Repository;

@Repository
public class PreventionExtDaoImpl extends AbstractDaoImpl<PreventionExt> implements PreventionExtDao {

    public PreventionExtDaoImpl() {
        super(PreventionExt.class);
    }

    @Override
    public List<PreventionExt> findByPreventionId(Integer preventionId) {
        Query query = entityManager.createQuery("select x from PreventionExt x where preventionId=?1");
        query.setParameter(1, preventionId);

        @SuppressWarnings("unchecked")
        List<PreventionExt> results = query.getResultList();

        return (results);
    }

    @Override
    public List<PreventionExt> findByKeyAndValue(String key, String value) {
        Query query = entityManager.createQuery("select x from PreventionExt x where keyval=?1 and val=?2");
        query.setParameter(1, key);
        query.setParameter(2, value);

        @SuppressWarnings("unchecked")
        List<PreventionExt> results = query.getResultList();

        return (results);
    }

    @Override
    public List<PreventionExt> findByPreventionIdAndKey(Integer preventionId, String key) {
        Query query = entityManager.createQuery("select x from PreventionExt x where preventionId=?1 and keyval=?2");
        query.setParameter(1, preventionId);
        query.setParameter(2, key);

        @SuppressWarnings("unchecked")
        List<PreventionExt> results = query.getResultList();

        return (results);
    }

    @Override
    public HashMap<String, String> getPreventionExt(Integer preventionId) {
        HashMap<String, String> results = new HashMap<String, String>();

        List<PreventionExt> preventionExts = findByPreventionId(preventionId);
        for (PreventionExt preventionExt : preventionExts) {
            results.put(preventionExt.getkeyval(), preventionExt.getVal());
        }

        return results;
    }
}
