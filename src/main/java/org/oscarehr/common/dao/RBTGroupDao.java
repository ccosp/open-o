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

import org.oscarehr.common.model.RBTGroup;
import org.springframework.stereotype.Repository;

@Repository
public class RBTGroupDao extends AbstractDao<RBTGroup> {

	public RBTGroupDao() {
		super(RBTGroup.class);
	}

	/**
	 * Deletes groups with the specified name and, optionally, template ID.
	 * 
	 * @param groupName
	 * 		Name of the group to delete
	 * @param templateId
	 * 		ID of the template for the group to be deleted. In case this value is set to null, only the group name is used for 
	 * 		deletion selection
	 * @return
	 * 		Returns the number of the deleted groups
	 */
	public int deleteByNameAndTemplateId(String groupName, Integer templateId) {
		StringBuilder buf = new StringBuilder("DELETE FROM " + modelClass.getSimpleName() + " g WHERE g.groupName = :groupName");
		if (templateId != null) {
			buf.append(" AND g.templateId = :templateId");
		}
		
		Query query = entityManager.createQuery(buf.toString());
		query.setParameter("groupName", groupName);
		if (templateId != null) {
			query.setParameter("templateId", templateId);
		}
		
		return query.executeUpdate();
	}
	
	/**
	 * Deletes groups with the specified name.
	 * 
	 * @param groupName
	 * 		The name of the group to delete
	 * @return
	 * 		Returns the number of the deleted groups
	 */
	
	public int deleteByName(String groupName) {
		return deleteByNameAndTemplateId(groupName, null);
	}
	
	/**
	 * Retrieves a group from the database
	 * 
	 * @param groupName
	 * 		The name of the group to retrieve
	 * @return
	 * 		Returns a list of RBTGroup objects
	 */
	public List<RBTGroup> getByGroupName(String groupName) {
		String sql = "select tg from RBTGroup tg where tg.groupName=?";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, groupName);

		@SuppressWarnings("unchecked")
		List<RBTGroup> results = query.getResultList();

		return results;
	}
	
	/**
	 * Retrieves a list of group names from the database
	 * 
	 * @return
	 * 		Returns a list of names of groups
	 */
	public List<String> getGroupNames() {
		String sql = "select distinct tg.groupName from RBTGroup tg";
		Query query = entityManager.createQuery(sql);

		@SuppressWarnings("unchecked")
		List<String> results = query.getResultList();
		
		if(results == null) {
			results = Collections.emptyList();
		}

		return results;
	}

}
