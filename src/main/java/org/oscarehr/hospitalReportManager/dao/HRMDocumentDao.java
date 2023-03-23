/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.hospitalReportManager.dao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.springframework.stereotype.Repository;

@Repository
public class HRMDocumentDao extends AbstractDao<HRMDocument> {

	public HRMDocumentDao() {
	    super(HRMDocument.class);
    }
	
	public List<HRMDocument> findById(int id) {
		String sql = "select x from " + this.modelClass.getName() + " x where x.id=?";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, id);
		@SuppressWarnings("unchecked")
		List<HRMDocument> documents = query.getResultList();
		return documents;
	}
	
	public List<HRMDocument> findAll(int offset, int limit) {
		String sql = "select x from " + this.modelClass.getName() + " x order by x.id";
		Query query = entityManager.createQuery(sql);
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		
		@SuppressWarnings("unchecked")
		List<HRMDocument> documents = query.getResultList();
		return documents;
	}
	
	public List<HRMDocument> findAll() {
		String sql = "select x from " + this.modelClass.getName() + " x order by x.id";
		Query query = entityManager.createQuery(sql);
		
		@SuppressWarnings("unchecked")
		List<HRMDocument> documents = query.getResultList();
		return documents;
	}
	
	
	public List<Integer> findByHash(String hash) {
		String sql = "select distinct id from " + this.modelClass.getName() + " x where x.reportHash=?";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, hash);
		@SuppressWarnings("unchecked")
		List<Integer> matches = query.getResultList();
		return matches;
	}
	
	public List<HRMDocument> findByNoTransactionInfoHash(String hash) {
		String sql = "select x from " + this.modelClass.getName() + " x where x.reportLessTransactionInfoHash=?";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, hash);
		@SuppressWarnings("unchecked")
		List<HRMDocument> matches = query.getResultList();
		return matches;
	}
	
	@SuppressWarnings("unchecked")
    public List<Integer> findAllWithSameNoDemographicInfoHash(String hash) {
		String sql = "select distinct parentReport from " + this.modelClass.getName() + " x where x.reportLessDemographicInfoHash=?";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, hash);
		List<Integer> matches = query.getResultList();
		
		if (matches != null && matches.size() == 1 && matches.get(0) == null) {
			sql = "select distinct id from " + this.modelClass.getName() + " x where x.reportLessDemographicInfoHash=?";
			query = entityManager.createQuery(sql);
			query.setParameter(1, hash);
			matches = query.getResultList();
		}
		return matches;
	}
	
	@SuppressWarnings("unchecked")
    public List<HRMDocument> findAllDocumentsWithRelationship(Integer docId) {
		List<HRMDocument> documentsWithRelationship = new LinkedList<HRMDocument>();
		// Get the document that was specified first
		HRMDocument firstDocument = this.find(docId);
		if (firstDocument != null) {
			String sql = null;
			Query query = null;
			if (firstDocument.getParentReport() != null) {
				// This is a child report; get the parent and all siblings of this report
				sql = "select x from " + this.modelClass.getName() + " x where x.id = ? order by x.id asc";
				query = entityManager.createQuery(sql);
				query.setParameter(1, firstDocument.getParentReport());
				documentsWithRelationship.addAll(query.getResultList());
				
				sql = "select x from " + this.modelClass.getName() + " x where x.parentReport = ? order by x.id asc";
				query = entityManager.createQuery(sql);
				query.setParameter(1, firstDocument.getParentReport());
				documentsWithRelationship.addAll(query.getResultList());

				
			} else {
				// This is a parent report
				sql = "select x from " + this.modelClass.getName() + " x where x.parentReport = ? order by x.id asc";
				query = entityManager.createQuery(sql);
				query.setParameter(1, firstDocument.getId());
				documentsWithRelationship = query.getResultList();
			}
		
		}

		return documentsWithRelationship;
		
	}
	
	public List<HRMDocument> getAllChildrenOf(Integer docId) {
		String sql = "select x from " + this.modelClass.getName() + " x where x.parentReport=? order by id asc";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, docId);
		@SuppressWarnings("unchecked")
		List<HRMDocument> documents = query.getResultList();
		return documents;
    }
	
	public List<HRMDocument> findByKey(String sourceFacility, String sourceFacilityReportNo, String deliverToId) {
		String sql = "select x from " + this.modelClass.getName() + " x where x.sourceFacility=? AND x.sourceFacilityReportNo = ? AND x.recipientId = ?";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, sourceFacility);
		query.setParameter(2, sourceFacilityReportNo);
		query.setParameter(3, deliverToId);
		
		@SuppressWarnings("unchecked")
		List<HRMDocument> documents = query.getResultList();
		return documents;
    }
	
	
	public List<HRMDocument> query(String providerNo, boolean providerUnmatched, boolean noSignOff, boolean demographicUnmatched, int start, int length, String orderColumn, String orderDirection) {
		
		if(orderColumn != null && !orderColumn.equals("formattedName") && !orderColumn.equals("dob") && !orderColumn.equals("reportDate")
				&& !orderColumn.equals("timeReceived") && !orderColumn.equals("sourceFacility")) {
			return new ArrayList<HRMDocument>();
		}
		if(orderDirection != null && !orderDirection.equalsIgnoreCase("ASC") && !orderDirection.equalsIgnoreCase("DESC")) {
			return new ArrayList<HRMDocument>();
		}
		String sql = "select x from " + this.modelClass.getName() + " x   ";

	//	if(providerNo != null || providerUnmatched) {
			sql += " inner JOIN x.matchedProviders p ";
	//	} 
		
		sql += " WHERE x.parentReport IS NULL  ";
		
		if(demographicUnmatched) {
			sql = sql + " AND SIZE(x.matchedDemographics) = 0 ";
		}
		
		if(providerUnmatched) {
			sql += "  AND p.providerNo = :pNo ";
		} else {
			if(providerNo != null) {
				sql += "  AND p.providerNo = :pNo ";
			}
			if(noSignOff) {
				sql += " AND p.signedOff = 0" ;
			}
		}
		
		
		if(!StringUtils.isEmpty(orderColumn) && !StringUtils.isEmpty(orderDirection)) {
			sql = sql + " ORDER BY x." + orderColumn + " " + orderDirection;
		}
		
		
		Query query = entityManager.createQuery(sql);
		if(providerNo != null || providerUnmatched) {
			
		}
		
		if(providerUnmatched) {
			query.setParameter("pNo", "-1");
		} else {
			if(providerNo != null) {
				query.setParameter("pNo", providerNo);
			}
		}
		
	
		query.setFirstResult(start);
		query.setMaxResults(length);
		
		@SuppressWarnings("unchecked")
		List<HRMDocument> documents = query.getResultList();
		return documents;
	}
	
	public long queryForCount(String providerNo, boolean providerUnmatched, boolean noSignOff, boolean demographicUnmatched, int start, int length, String orderColumn, String orderDirection) {
		
		if(orderColumn != null && !orderColumn.equals("formattedName") && !orderColumn.equals("dob") && !orderColumn.equals("reportDate")
				&& !orderColumn.equals("timeReceived") && !orderColumn.equals("sourceFacility")) {
			return 0;
		}
		if(orderDirection != null && !orderDirection.equalsIgnoreCase("ASC") && !orderDirection.equalsIgnoreCase("DESC")) {
			return 0;
		}
		String sql = "select count(x) from " + this.modelClass.getName() + " x   ";

	//	if(providerNo != null || providerUnmatched) {
			sql += " inner JOIN x.matchedProviders p ";
	//	} 
		
		sql += " WHERE x.parentReport IS NULL  ";
		
		if(demographicUnmatched) {
			sql = sql + " AND SIZE(x.matchedDemographics) = 0 ";
		}
		
		if(providerUnmatched) {
			sql += "  AND p.providerNo = :pNo ";
		} else {
			if(providerNo != null) {
				sql += "  AND p.providerNo = :pNo ";
			}
			if(noSignOff) {
				sql += " AND p.signedOff = 0" ;
			}
		}
		
		
		if(!StringUtils.isEmpty(orderColumn) && !StringUtils.isEmpty(orderDirection)) {
			sql = sql + " ORDER BY x." + orderColumn + " " + orderDirection;
		}
		
		
		Query query = entityManager.createQuery(sql);
		if(providerNo != null || providerUnmatched) {
			
		}
		
		if(providerUnmatched) {
			query.setParameter("pNo", "-1");
		} else {
			if(providerNo != null) {
				query.setParameter("pNo", providerNo);
			}
		}
		
	
		Long count = (Long)query.getSingleResult();
		

		return count;
	}
}
