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

package com.quatro.dao.security;

import com.quatro.model.security.Secobjprivilege;

import java.util.List;

public interface SecobjprivilegeDao {

    public void save(Secobjprivilege secobjprivilege);

    public void saveAll(List list);

    public int update(Secobjprivilege instance);

    public int deleteByRoleName(String roleName);

    public void delete(Secobjprivilege persistentInstance);

    public String getFunctionDesc(String function_code);

    public String getAccessDesc(String accessType_code);

    public List getFunctions(String roleName);

    public List findByProperty(String propertyName, Object value);

    public List<Secobjprivilege> getByObjectNameAndRoles(String o, List<String> roles);

    public List<Secobjprivilege> getByRoles(List<String> roles);
}
