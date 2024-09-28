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

import javax.persistence.Query;

import ca.openosp.openo.common.model.HnrDataValidation;
import org.springframework.stereotype.Repository;

@Repository
public class HnrDataValidationDaoImpl extends AbstractDaoImpl<HnrDataValidation> implements HnrDataValidationDao {

    public HnrDataValidationDaoImpl() {
        super(HnrDataValidation.class);
    }

    @Override
    public HnrDataValidation findMostCurrentByFacilityIdClientIdType(Integer facilityId, Integer clientId, HnrDataValidation.Type type) {
        // build sql string
        String sqlCommand = "select * from HnrDataValidation where facilityId=?1 and clientId=?2 and validationType=?3 order by created desc";

        // set parameters
        Query query = entityManager.createNativeQuery(sqlCommand, modelClass);
        query.setParameter(1, facilityId);
        query.setParameter(2, clientId);
        query.setParameter(3, type.name());

        // run query
        return (getSingleResultOrNull(query));
    }
}
