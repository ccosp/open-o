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

import java.util.List;
import javax.persistence.Query;

import ca.openosp.openo.common.model.Favorite;
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
        Query query = entityManager.createQuery("FROM " + modelClass.getSimpleName() + " f WHERE f.providerNo = :providerNo AND f.name = :favoritename AND f.bn = :brandName AND f.gcnSeqno = :gcnSeqNo AND f.customName = :customName AND f.takeMin = :takemin AND f.takeMax = :takemax AND f.frequencyCode = :freqcode AND f.duration = :duration "
                + "AND f.durationUnit = :durunit AND f.quantity = :quantity AND f.repeat = :repeat AND f.nosubs = :nosubs AND f.prn = :prn AND f.special = :special AND f.gn = :gn AND f.unitName = :unitName AND " + "f.customInstructions = :customInstructions");

        query.setParameter("providerNo", providerNo);
        query.setParameter("favoritename", favoriteName);
        query.setParameter("brandName", bn);
        query.setParameter("gcnSeqNo", (double) gcn_SEQNO);
        query.setParameter("customName", customName);
        query.setParameter("takemin", takeMin);
        query.setParameter("takemax", takeMax);
        query.setParameter("freqcode", frequencyCode);
        query.setParameter("duration", duration);
        query.setParameter("durunit", durationUnit);
        query.setParameter("quantity", quantity);
        query.setParameter("repeat", repeat);
        query.setParameter("nosubs", nosubsInt);
        query.setParameter("prn", prnInt);
        query.setParameter("special", parsedSpecial);
        query.setParameter("gn", gn);
        query.setParameter("unitName", unitName);
        query.setParameter("customInstructions", customInstr);

        return getSingleResultOrNull(query);
    }
}
