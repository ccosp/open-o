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

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.CtlDocument;
import org.springframework.stereotype.Repository;

@Repository
public class CtlDocumentDao extends AbstractDao<CtlDocument>{

	public CtlDocumentDao() {
		super(CtlDocument.class);
	}
	
	public CtlDocument getCtrlDocument(Integer docId) {
		Query query = entityManager.createQuery("select x from CtlDocument x where x.id.documentNo=?");
		query.setParameter(1, docId);
		
		return(getSingleResultOrNull(query));
	}

    public List<CtlDocument> findByDocumentNoAndModule(Integer ctlDocNo, String module) {
		Query query = entityManager.createQuery("select x from CtlDocument x where x.id.documentNo=? and x.id.module = ?");
		query.setParameter(1, ctlDocNo);
		query.setParameter(2, module);
		
		@SuppressWarnings("unchecked")
        List<CtlDocument> cList = query.getResultList();
		return cList;
    }

}
