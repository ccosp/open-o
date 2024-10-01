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
package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;

import org.oscarehr.common.model.Favorite;
import org.springframework.stereotype.Repository;

@Repository
public class FavoriteDaoImpl extends AbstractDaoImpl<Favorite> implements FavoriteDao {

    public FavoriteDaoImpl() {
        super(Favorite.class);
    }

    @SuppressWarnings("unchecked")
    public List<Favorite> findByProviderNo(String providerNo) {
        Query query = entityManager.createQuery("FROM " + modelClass.getSimpleName() + " f WHERE f.providerNo = :providerNo ORDER BY f.name");
        query.setParameter("providerNo", providerNo);
        return query.getResultList();
    }

    public Favorite findByEverything(String providerNo, String favoriteName, String bn, int gcn_SEQNO, String customName, float takeMin, float takeMax, String frequencyCode, String duration, String durationUnit, String quantity, int repeat, boolean nosubsInt, boolean prnInt, String parsedSpecial, String gn, String unitName, boolean customInstr) {
        Query query = entityManager.createQuery("FROM " + modelClass.getSimpleName() + " f WHERE f.providerNo = ?1 AND f.name = ?2 AND f.bn = ?3 AND f.gcnSeqno = ?4 AND f.customName = ?5 AND f.takeMin = ?6 AND f.takeMax = ?7 AND f.frequencyCode = ?8 AND f.duration = ?9 "
                + "AND f.durationUnit = ?10 AND f.quantity = ?11 AND f.repeat = ?12 AND f.nosubs = ?13 AND f.prn = ?14 AND f.special = ?15 AND f.gn = ?16 AND f.unitName = ?17 AND f.customInstructions = ?18");

        query.setParameter(1, providerNo);
        query.setParameter(2, favoriteName);
        query.setParameter(3, bn);
        query.setParameter(4, gcn_SEQNO);
        query.setParameter(5, customName);
        query.setParameter(6, takeMin);
        query.setParameter(7, takeMax);
        query.setParameter(8, frequencyCode);
        query.setParameter(9, duration);
        query.setParameter(10, durationUnit);
        query.setParameter(11, quantity);
        query.setParameter(12, repeat);
        query.setParameter(13, nosubsInt);
        query.setParameter(14, prnInt);
        query.setParameter(15, parsedSpecial);
        query.setParameter(16, gn);
        query.setParameter(17, unitName);
        query.setParameter(18, customInstr);

        return getSingleResultOrNull(query);
    }
}
