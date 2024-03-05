package org.oscarehr.common.dao;
import org.hibernate.Hibernate;
import org.oscarehr.common.model.EReferAttachment;
import org.springframework.stereotype.Repository;
import javax.persistence.Query;
import java.util.Calendar;
import java.util.List;
@Repository
public class EReferAttachmentDao extends AbstractDao<EReferAttachment> {
	public EReferAttachmentDao() {
		super(EReferAttachment.class);
	}
	
	public EReferAttachment getRecentByDemographic(Integer demographicNo) {
		EReferAttachment eReferAttachment = null;
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, -1);
		
		String sql = "SELECT e FROM " + modelClass.getSimpleName() + " e WHERE e.archived = FALSE AND e.demographicNo = :demographicNo AND e.created > :expiry";
		Query query = entityManager.createQuery(sql);
		query.setParameter("demographicNo", demographicNo);
		query.setParameter("expiry", calendar.getTime());
		
		List<EReferAttachment> eReferAttachments = query.getResultList();
		
		if (!eReferAttachments.isEmpty()) {
			eReferAttachment = eReferAttachments.get(0);
			Hibernate.initialize(eReferAttachment.getAttachments());
		}
		
		return eReferAttachment;
	}
}