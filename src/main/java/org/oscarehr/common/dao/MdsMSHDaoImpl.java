package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.MdsMSH;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class MdsMSHDaoImpl extends AbstractDaoImpl<MdsMSH> implements MdsMSHDao {

	public MdsMSHDaoImpl() {
		super(MdsMSH.class);
	}

	public List<Object[]> findLabsByAccessionNumAndId(Integer id, String controlId) {
		String sql = "FROM MdsMSH a, MdsMSH b " + "WHERE a.controlId like :controlId " + "AND b.id = :id" + "ORDER BY a.controlId";
		Query query = entityManager.createQuery(sql);
		query.setParameter("id", id);
		query.setParameter("controlId", controlId);
		return query.getResultList();
	}
	
	public List<Object[]> findMdsSementDataById(Integer id) {
		String sql = "select mdsMSH.dateTime, mdsMSH.controlId, min(mdsZFR.reportFormStatus) " +
				"FROM MdsMSH mdsMSH, MdsZFR mdsZFR " +
				"WHERE mdsMSH.id = mdsZFR.id " +
				"AND mdsMSH.id = :segmentID" +
				"GROUP BY mdsMSH.id";
		Query query = entityManager.createQuery(sql);
		query.setParameter("id", id);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getLabResultsSince(Integer demographicNo, Date updateDate) {
		String query = "select m.id from MdsMSH m, PatientLabRouting p WHERE m.id = p.labNo and p.labType='MDS' and p.demographicNo = ?1 and (m.dateTime > ?2 or p.created > ?3) ";
		Query q = entityManager.createQuery(query);
		
		q.setParameter(1, demographicNo);
		q.setParameter(2, updateDate);
		q.setParameter(3, updateDate);
		
		
		return q.getResultList();    
	}
}
