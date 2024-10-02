//CHECKSTYLE:OFF
/**
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
 */
package org.oscarehr.billing.CA.BC.dao;

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.billing.CA.BC.model.WcbNoiCode;
import org.oscarehr.common.dao.AbstractDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class WcbNoiCodeDao extends AbstractDaoImpl<WcbNoiCode> {

    public WcbNoiCodeDao() {
        super(WcbNoiCode.class);
    }

    public List<WcbNoiCode> findByCodeOrLevel(String search) {
        Query q = createQuery("w", "w.code like ?1 OR w.level1 like ?1 OR w.level2 like ?1 OR w.level3 like ?1 ORDER BY w.level1, w.level2, w.level3");
        q.setParameter(1, search);
        return q.getResultList();
    }
}
