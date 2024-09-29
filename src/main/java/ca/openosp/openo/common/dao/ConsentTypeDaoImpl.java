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

import javax.persistence.Query;

import ca.openosp.openo.common.model.ConsentType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Collections;

@Repository
public class ConsentTypeDaoImpl extends AbstractDaoImpl<ConsentType> implements ConsentTypeDao {

    protected ConsentTypeDaoImpl() {
        super(ConsentType.class);
    }

    @Override
    public ConsentType findConsentType(String type) {
        String sql = "select x from " + modelClass.getSimpleName() + " x where x.type=?1 and x.active=1";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, type);

        ConsentType consentType = getSingleResultOrNull(query);

        return consentType;
    }

    @Override
    public ConsentType findConsentTypeForProvider(String type, String providerNo) {
        String sql = "select x from " + modelClass.getSimpleName()
                + " x where x.type=?1 and x.active=1 and x.providerNo = ?2";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, type);
        query.setParameter(2, providerNo);
        ConsentType consentType = getSingleResultOrNull(query);

        return consentType;
    }

    @Override
    public List<ConsentType> findAllActive() {
        Query q = entityManager.createQuery("select ct from ConsentType ct where ct.active=?1  order by ct.type asc");
        q.setParameter(1, true);

        @SuppressWarnings("unchecked")
        List<ConsentType> result = q.getResultList();

        if (result == null) {
            result = Collections.emptyList();
        }

        return result;
    }
}
