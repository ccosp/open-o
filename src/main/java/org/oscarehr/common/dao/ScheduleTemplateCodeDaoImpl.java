package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.ScheduleTemplateCode;
import org.springframework.stereotype.Repository;

@Repository
public class ScheduleTemplateCodeDaoImpl extends AbstractDaoImpl<ScheduleTemplateCode> implements ScheduleTemplateCodeDao {
	
	public ScheduleTemplateCodeDaoImpl() {
		super(ScheduleTemplateCode.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ScheduleTemplateCode> findAll() {
		Query query = createQuery("x", null);
		return query.getResultList();
	}
		
	@Override
	public ScheduleTemplateCode getByCode(char code) {
		Query query = entityManager.createQuery("select s from ScheduleTemplateCode s where s.code=?");
		query.setParameter(1, code);
		
		@SuppressWarnings("unchecked")
		List<ScheduleTemplateCode> results = query.getResultList();
		if(!results.isEmpty()) {
			return results.get(0);
		}
		return null;
	}
	
	@Override
	public List<ScheduleTemplateCode> findTemplateCodes() {
		Query query = entityManager.createQuery("select s from ScheduleTemplateCode s where s.bookinglimit > 0 and s.duration <>''");
		
		@SuppressWarnings("unchecked")
		List<ScheduleTemplateCode> results = query.getResultList();
		
		return results;
	}
	
	@Override
	public ScheduleTemplateCode findByCode(String code) {
		Query query = entityManager.createQuery("select s from ScheduleTemplateCode s where s.code like ?");
		query.setParameter(1, code);
		
		@SuppressWarnings("unchecked")
		List<ScheduleTemplateCode> results = query.getResultList();
		if(!results.isEmpty()) {
			return results.get(0);
		}
		return null;
	}
}
