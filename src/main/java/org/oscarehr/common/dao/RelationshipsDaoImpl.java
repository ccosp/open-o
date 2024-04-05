package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.Relationships;
import org.springframework.stereotype.Repository;
import oscar.util.ConversionUtils;

@Repository
public class RelationshipsDaoImpl extends AbstractDaoImpl<Relationships> implements RelationshipsDao {

	public RelationshipsDaoImpl() {
		super(Relationships.class);
	}

	@Override
	public List<Relationships> findAll() {
		String sql = "select x from Relationships x order by x.demographicNo";
		Query query = entityManager.createQuery(sql);
		@SuppressWarnings("unchecked")
		List<Relationships> results = query.getResultList();
		return results;
	}

	@Override
	public Relationships findActive(Integer id) {
		Query query = entityManager.createQuery("FROM " + modelClass.getSimpleName() + " r WHERE r.id = :id AND (r.deleted IS NULL OR r.deleted = '0')");
		query.setParameter("id", id);
		return getSingleResultOrNull(query);
	}

	@Override
	public List<Relationships> findByDemographicNumber(Integer demographicNumber) {
		Query query = entityManager.createQuery("FROM " + modelClass.getSimpleName() + " r WHERE r.demographicNo = :dN AND (r.deleted IS NULL OR r.deleted = '0')");
		query.setParameter("dN", demographicNumber);
		return query.getResultList();
	}

	@Override
	public List<Relationships> findActiveSubDecisionMaker(Integer demographicNumber) {
		Query query = entityManager.createQuery("FROM " + modelClass.getSimpleName() + " r WHERE r.demographicNo = :dN AND r.subDecisionMaker = :sdm AND (r.deleted IS NULL OR r.deleted = '0')");
		query.setParameter("dN", demographicNumber);
		query.setParameter("sdm", ConversionUtils.toBoolString(Boolean.TRUE));
		return query.getResultList();
	}

	@Override
	public List<Relationships> findActiveByDemographicNumberAndFacility(Integer demographicNumber, Integer facilityId) {
		Query query = entityManager.createQuery("FROM " + modelClass.getSimpleName() + " r WHERE r.demographicNo = :dN AND r.facilityId = :facilityId AND (r.deleted IS NULL OR r.deleted = '0')");
		query.setParameter("dN", demographicNumber);
		query.setParameter("facilityId", facilityId);
		return query.getResultList();
	}
}
