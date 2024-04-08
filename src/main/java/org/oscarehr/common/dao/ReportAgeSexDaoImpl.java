package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.NativeSql;
import org.oscarehr.common.model.ReportAgeSex;
import org.springframework.stereotype.Repository;

@Repository
public class ReportAgeSexDaoImpl extends AbstractDaoImpl<ReportAgeSex> implements ReportAgeSexDao {

    public ReportAgeSexDaoImpl() {
        super(ReportAgeSex.class);
    }

    @Override
    public List<ReportAgeSex> findBeforeReportDate(Date reportDate) {
        String sql = "select x from ReportAgeSex x where x.reportDate=?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1,reportDate);

        @SuppressWarnings("unchecked")
        List<ReportAgeSex> results = query.getResultList();
        return results;
    }

    @Override
    public void deleteAllByDate(Date reportDate) {
        String sql = "delete from ReportAgeSex x where x.reportDate <= ?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1,reportDate);
        query.executeUpdate();
    }

    @NativeSql("reportagesex")
    @Override
    public void populateAll(String yearOfBirth) {
        String copyQuery = 
            "INSERT INTO reportagesex(demographic_no, age, roster, sex, provider_no, reportdate, status, date_joined) " +
            "SELECT d.demographic_no, FLOOR(DATEDIFF(CURRENT_DATE(), STR_TO_DATE(CONCAT(d.year_of_birth,'-',d.month_of_birth,'-',d.date_of_birth), '%Y-%m-%d' )) / 365.25), d.roster_status, d.sex, d.provider_no, CURRENT_DATE(), d.patient_status, d.date_joined " +
            "FROM demographic d WHERE d.year_of_birth >= :yearOfBirth";
        Query query = entityManager.createNativeQuery(copyQuery);
        query.setParameter("yearOfBirth", yearOfBirth);
        query.executeUpdate();
    }

    @Override
    public Long count_reportagesex_roster(String roster, String sex, String providerNo, int age, Date dateStarted, Date dateEnded) {
        String sql = "select count(x) from ReportAgeSex x where (x.status<>'OP' and x.status<>'IN' and x.status<>'DE') and x.roster=? and x.sex like ? and x.providerNo = ? and x.age >= ? and x.dateJoined >= ? and x.dateJoined <= ?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1,roster);
        query.setParameter(2,sex);
        query.setParameter(3,providerNo);
        query.setParameter(4,age);
        query.setParameter(5,dateStarted);
        query.setParameter(6,dateEnded);

        Long results = (Long)query.getSingleResult();
        return results;
    }

    @Override
    public Long count_reportagesex_noroster(String roster, String sex, String providerNo,int minAge, int maxAge, Date dateStarted, Date dateEnded) {
        String sql = "select count(x)  from ReportAgeSex x  where (x.status<>'OP' and x.status<>'IN' and x.status<>'DE') and x.roster<>? and x.sex like ? and x.providerNo=? and x.age >= ? and x.age <=? and x.dateJoined >=? and x.dateJoined <=?";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1,roster);
        query.setParameter(2,sex);
        query.setParameter(3,providerNo);
        query.setParameter(4,minAge);
        query.setParameter(5,maxAge);
        query.setParameter(6,dateStarted);
        query.setParameter(7,dateEnded);

        Long results = (Long)query.getSingleResult();
        return results;
    }

    @Override
    public Long count_reportagesex(String roster, String sex, String providerNo, int minAge, int maxAge, Date startDate, Date endDate) {
        String sql = 
                "select count(x) from ReportAgeSex x " +
                "where " +
                "( x.status <> 'OP' and x.status <> 'IN' and x.status <> 'DE') and " +
                "x.roster like ? and " +
                "x.sex like ? and " +
                "x.providerNo=? and " +
                "x.age >= ? and " +
                "x.age <=? and " +
                "x.dateJoined >=? and " +
                "x.dateJoined <=?";

        Query query = entityManager.createQuery(sql);
        query.setParameter(1,roster);
        query.setParameter(2,sex);
        query.setParameter(3,providerNo);
        query.setParameter(4,minAge);
        query.setParameter(5,maxAge);
        query.setParameter(6,startDate);
        query.setParameter(7,endDate);

        Long results = (Long)query.getSingleResult();
        return results;
    }
}
