/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version. 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


package org.oscarehr.common.dao;

import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.GroupMembers;
import org.springframework.stereotype.Repository;

@Repository
public class GroupMembersDao extends AbstractDaoImpl<GroupMembers>{

	public GroupMembersDao() {
		super(GroupMembers.class);
	}
	
	/**
	 * Only group members with an integrated facility ID that is remote - greater than zero.
	 * @param groupId
	 * @return
	 */
	public List<GroupMembers> findRemoteByGroupId(int groupId) {
		Query q = entityManager.createQuery("SELECT x FROM GroupMembers x WHERE x.facilityId > 0 AND x.groupId=?");
		q.setParameter(1, groupId);
		
		@SuppressWarnings("unchecked")
		List<GroupMembers> results = q.getResultList();
		
		return results;
	}
	
	/**
	 * Only group members that have a facility id of 0 - for local. 
	 * @param groupId
	 * @return
	 */
	public List<GroupMembers> findLocalByGroupId(int groupId) {
		Query q = entityManager.createQuery("SELECT x FROM GroupMembers x WHERE x.facilityId = 0 AND x.groupId=?");
		q.setParameter(1, groupId);
		
		@SuppressWarnings("unchecked")
		List<GroupMembers> results = q.getResultList();
		
		return results;
	}
	
	public List<GroupMembers> findByGroupId(int groupId) {
		Query q = entityManager.createQuery("SELECT x FROM GroupMembers x WHERE x.groupId=?");
		q.setParameter(1, groupId);
		
		@SuppressWarnings("unchecked")
		List<GroupMembers> results = q.getResultList();
		
		return results;
	}

	@SuppressWarnings("unchecked")
    public List<Object[]> findMembersByGroupId(int groupId) {
        String sql = "FROM GroupMembers g, Provider p " 
        		+ "WHERE g.providerNo = p.ProviderNo "
                + "AND g.groupId = :id " 
        		+ "ORDER BY p.LastName, p.FirstName";
        Query query = entityManager.createQuery(sql);
        query.setParameter("id", groupId);
        return query.getResultList();
    }
	
    public List<GroupMembers> findByProviderNumberAndFacilityId(String providerNo, Integer facilityId) {
		Query query = entityManager.createQuery("SELECT x FROM GroupMembers x WHERE x.providerNo LIKE ? AND x.facilityId=?");
		query.setParameter(1, providerNo);
		query.setParameter(2, facilityId);
		
		@SuppressWarnings("unchecked")
		List<GroupMembers> results = query.getResultList();
		
		if(results == null) {
			results = Collections.emptyList();
		}
		
		return results;
    }
    
    public List<GroupMembers> findByFacilityId(Integer facilityId) {
		Query query = entityManager.createQuery("SELECT x FROM GroupMembers x WHERE x.facilityId=?");
		query.setParameter(1, facilityId);
		
		@SuppressWarnings("unchecked")
		List<GroupMembers> results = query.getResultList();
		
		if(results == null) {
			results = Collections.emptyList();
		}
		
		return results;
    }
	
}
