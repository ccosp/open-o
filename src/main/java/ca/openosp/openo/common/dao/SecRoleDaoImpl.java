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

import java.util.List;

import javax.persistence.Query;

import ca.openosp.openo.common.model.SecRole;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SecRoleDaoImpl extends AbstractDaoImpl<SecRole> implements SecRoleDao {

    public SecRoleDaoImpl() {
        super(SecRole.class);
    }

    @Override
    public List<SecRole> findAll() {
        StringBuilder sb = new StringBuilder();
        sb.append("select x from SecRole x");

        sb.append(" order by x.name");

        Query query = entityManager.createQuery(sb.toString());

        @SuppressWarnings("unchecked")
        List<SecRole> results = query.getResultList();

        return results;
    }

    @Override
    public List<String> findAllNames() {
        StringBuilder sb = new StringBuilder();
        sb.append("select x.name from SecRole x");

        sb.append(" order by x.name");

        Query query = entityManager.createQuery(sb.toString());

        @SuppressWarnings("unchecked")
        List<String> results = query.getResultList();

        return results;
    }

    @Override
    public SecRole findByName(String name) {
        Query q = entityManager.createQuery("select x from SecRole x where x.name=?");

        q.setParameter(0, name);

        return this.getSingleResultOrNull(q);
    }

    @Override
    public List<SecRole> findAllOrderByRole() {
        Query query = entityManager.createQuery("select x from SecRole x order by x.name");

        @SuppressWarnings("unchecked")
        List<SecRole> results = query.getResultList();

        return results;
    }
}
