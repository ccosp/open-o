package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.RSchedule;
import org.springframework.stereotype.Repository;

@Repository(value="rScheduleDao")
@SuppressWarnings("unchecked")
public class RScheduleDaoImpl extends AbstractDaoImpl<RSchedule> implements RScheduleDao {

    public RScheduleDaoImpl() {
        super(RSchedule.class);
    }

    @Override
    public List<RSchedule> findByProviderAvailableAndDate(String providerNo, String available, Date sdate) {
		Query query = entityManager.createQuery("select s from RSchedule s where s.providerNo=? and s.available=? and s.sDate=?");
		query.setParameter(1, providerNo);
		query.setParameter(2, available);
		query.setParameter(3, sdate);

        List<RSchedule> results = query.getResultList();
		return results;
	}
	
    @Override
	public Long search_rschedule_overlaps(String providerNo, Date d1, Date d2, Date d3, Date d4, Date d5, Date d6, Date d7, Date d8, Date d9, Date d10, Date d11, Date d12,Date d13,Date d14) {
		Query query = entityManager.createQuery("select count(r.id) from RSchedule r where r.providerNo=? and ((r.sDate <? and r.eDate >=?) or (? < r.sDate and r.sDate < ?) or (? < r.eDate and r.eDate <= ?) or ( ? < r.sDate and r.eDate <= ?) or (r.sDate = ? and r.sDate = ?) or (r.eDate = ? and r.eDate <= ?) or (r.sDate = ? and r.eDate != ?)) and r.status = 'A'");
		query.setParameter(1, providerNo);
		query.setParameter(2, d1);
		query.setParameter(3, d2);
		query.setParameter(4, d3);
		query.setParameter(5, d4);
		query.setParameter(6, d5);
		query.setParameter(7, d6);
		query.setParameter(8, d7);
		query.setParameter(9, d8);
		query.setParameter(10, d9);
		query.setParameter(11, d10);
		query.setParameter(12, d11);
		query.setParameter(13, d12);
		query.setParameter(14, d13);
		query.setParameter(15, d14);

		Long results = (Long) query.getSingleResult();
		return results;
	}
	
	@Override
	public Long search_rschedule_exists(String providerNo, Date d1, Date d2) {
		Query query = entityManager.createQuery("select count(r.id) from RSchedule r where r.providerNo=? and r.sDate =? and r.eDate =? and r.status = 'A'");
		query.setParameter(1, providerNo);
		query.setParameter(2, d1);
		query.setParameter(3, d2);
		
		Long results = (Long) query.getSingleResult();
		return results;
	}
	
    @Override
	public RSchedule search_rschedule_current(String providerNo, String available, Date sdate) {
		Query query = entityManager.createQuery("select s from RSchedule s where s.providerNo=? and s.available=? and s.sDate <= ? and s.status='A' order by s.sDate desc");
		query.setParameter(1, providerNo);
		query.setParameter(2, available);
		query.setParameter(3, sdate);
		query.setMaxResults(1);

		RSchedule result = this.getSingleResultOrNull(query);
		return result;
	}
	
    @Override
	public List<RSchedule> search_rschedule_future(String providerNo, String available, Date sdate) {
		Query query = entityManager.createQuery("select s from RSchedule s where s.providerNo=? and s.available=? and s.sDate > ? and s.status='A' order by s.sDate");
		query.setParameter(1, providerNo);
		query.setParameter(2, available);
		query.setParameter(3, sdate);
		
		@SuppressWarnings("unchecked")
        List<RSchedule> results = query.getResultList();
		return results;
	}
	
    @Override
	public RSchedule search_rschedule_current1(String providerNo, Date sdate) {
		Query query = entityManager.createQuery("select s from RSchedule s where s.providerNo=? and s.sDate <= ? and s.status='A' order by s.sDate desc");
		query.setParameter(1, providerNo);
		query.setParameter(2, sdate);
		query.setMaxResults(1);
		
		RSchedule result = this.getSingleResultOrNull(query);
		return result;
	}
	
    @Override
	public RSchedule search_rschedule_current2(String providerNo, Date sdate) {
		Query query = entityManager.createQuery("select s from RSchedule s where s.providerNo=? and s.sDate >= ? and s.status='A' order by s.sDate");
		query.setParameter(1, providerNo);
		query.setParameter(2, sdate);
		query.setMaxResults(1);
		
		RSchedule result = this.getSingleResultOrNull(query);
		return result;
	}
	
    @Override
	public List<RSchedule> search_rschedule_future1(String providerNo, Date sdate) {
		Query query = entityManager.createQuery("select s from RSchedule s where s.providerNo=? and s.sDate > ? and s.status='A' order by s.sDate");
		query.setParameter(1, providerNo);
		query.setParameter(2, sdate);

        List<RSchedule> results = query.getResultList();
		return results;
	}

    @Override
	public List<RSchedule> findByProviderNoAndDates(String providerNo, Date apptDate) {
		Query query = createQuery("r", "r.providerNo = :providerNo AND r.sDate <= :apptDate AND r.eDate >= :apptDate");
		query.setParameter("providerNo", providerNo);
		query.setParameter("apptDate", apptDate);
		return query.getResultList();
    }

}
