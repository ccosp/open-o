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

import org.oscarehr.common.model.ServiceClient;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

@Repository
public class ServiceClientDaoImpl extends AbstractDaoImpl<ServiceClient> implements ServiceClientDao {

    public ServiceClientDaoImpl() {
        super(ServiceClient.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ServiceClient> findAll() {
        Query query = createQuery("x", null);
        return query.getResultList();
    }

    @Override
    public ServiceClient findByName(String name) {
        Query query = entityManager.createQuery("SELECT x FROM ServiceClient x WHERE x.name=?1");
        query.setParameter(1, name);
        return this.getSingleResultOrNull(query);
    }

    @Override
    public ServiceClient findByKey(String key) {
        Query query = entityManager.createQuery("SELECT x FROM ServiceClient x WHERE x.key=?1");
        query.setParameter(1, key);
        return this.getSingleResultOrNull(query);
    }

    @Override
    public ServiceClient find(Integer id) {
        return this.entityManager.find(ServiceClient.class, id);
    }
}
