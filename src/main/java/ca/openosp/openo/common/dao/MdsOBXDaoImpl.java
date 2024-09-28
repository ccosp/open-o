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

import ca.openosp.openo.common.model.MdsOBX;
import org.springframework.stereotype.Repository;

import ca.openosp.openo.util.ParamAppender;

@Repository
public class MdsOBXDaoImpl extends AbstractDaoImpl<MdsOBX> implements MdsOBXDao {

    public MdsOBXDaoImpl() {
        super(MdsOBX.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MdsOBX> findByIdObrAndCodes(Integer id, String associatedOBR, List<String> codes) {
        ParamAppender pa = getAppender("obx");
        pa.and("obx.id = :id", "id", id);
        pa.and("obx.associatedOBR = :associatedOBR", "associatedOBR", associatedOBR);

        if (!codes.isEmpty()) {
            ParamAppender codesPa = new ParamAppender();
            for (int i = 0; i < codes.size(); i++) {
                String paramName = "observationSubId" + i;
                codesPa.or("obx.observationSubId like :" + paramName, paramName, "%" + codes.get(i) + "%");
            }
            pa.and(codesPa);
        }

        Query query = entityManager.createQuery(pa.getQuery());
        pa.setParams(query);
        return query.getResultList();
    }
}
