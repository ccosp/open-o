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

import ca.openosp.openo.common.model.Desaprisk;
import org.springframework.stereotype.Repository;

@Repository
public class DesapriskDaoImpl extends AbstractDaoImpl<Desaprisk> implements DesapriskDao {

    public DesapriskDaoImpl() {
        super(Desaprisk.class);
    }

    @Override
    public Desaprisk search(Integer formNo, Integer demographicNo) {

        String sqlCommand = "select x from Desaprisk x where x.formNo <= ? and x.demographicNo=? order by x.formNo DESC, x.desapriskDate DESC, x.desapriskTime DESC";

        Query query = entityManager.createQuery(sqlCommand);
        query.setParameter(0, formNo);
        query.setParameter(1, demographicNo);

        @SuppressWarnings("unchecked")
        List<Desaprisk> results = query.getResultList();

        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }
}
