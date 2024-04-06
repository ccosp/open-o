package org.oscarehr.common.dao;

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.EFormDocs;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class EFormDocsDaoImpl extends AbstractDaoImpl<EFormDocs> implements EFormDocsDao {

	public EFormDocsDaoImpl() {
		super(EFormDocs.class);
	}

	public List<EFormDocs> findByFdidIdDocNoDocType(Integer fdid, Integer documentNo, String docType) {
	  	String sql = "select x from EFormDocs x where x.fdid=? and x.documentNo=? and x.docType=? and x.deleted is NULL";
    	Query query = entityManager.createQuery(sql);
    	query.setParameter(1,fdid);
    	query.setParameter(2,documentNo);
    	query.setParameter(3,docType);

        List<EFormDocs> results = query.getResultList();
        return results;
	}

	public List<EFormDocs> findByFdidIdDocType(Integer fdid, String docType) {
		String sql = "select x from EFormDocs x where x.fdid=?1 and x.docType=?2 and x.deleted is NULL";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1,fdid);
		query.setParameter(2,docType);

		return query.getResultList();
	}

	public List<EFormDocs> findByFdid(Integer fdid) {
	  	String sql = "select x from EFormDocs x where x.fdid=? and x.deleted is NULL";
    	Query query = entityManager.createQuery(sql);
    	query.setParameter(1,fdid);

        List<EFormDocs> results = query.getResultList();
        return results;
	}

	public List<Object[]> findLabs(Integer fdid) {
		String sql = "FROM EFormDocs cd, PatientLabRouting plr " +
				"WHERE plr.labNo = cd.documentNo " +
				"AND cd.fdid = :fdid " +
				"AND cd.docType = :docType " +
				"AND cd.deleted IS NULL " +
				"ORDER BY cd.documentNo";
		Query q = entityManager.createQuery(sql);
		q.setParameter("fdid", fdid);
		q.setParameter("docType", EFormDocs.DOCTYPE_LAB);
		return q.getResultList();
	}
}
