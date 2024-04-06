package org.oscarehr.common.dao;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.SurveyData;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class SurveyDataDaoImpl extends AbstractDaoImpl<SurveyData> implements SurveyDataDao {

    public SurveyDataDaoImpl() {
        super(SurveyData.class);
    }

    public int getMaxProcessed(String surveyId) {
        Query q = entityManager.createQuery("SELECT MAX(s.processed) FROM SurveyData s WHERE s.surveyId = :sid");
        q.setParameter("sid", surveyId);
        Object result = q.getSingleResult();
        if (result == null) {
            return 0;
        }
        return (Integer)result;
    }

    public int getProcessCount(String surveyId) {
        String sql = "SELECT COUNT(s.id) FROM SurveyData s " +
                "WHERE s.surveyId = :sid " +
                "AND s.processed IS NULL " +
                "AND s.status = 'A'";
        Query query = entityManager.createQuery(sql);
        query.setParameter("sid", surveyId);
        Object result = query.getSingleResult();
        if (result == null) {
            return 0;
        }
        return ((Long)result).intValue();	    
    }

    public List<SurveyData> findByDemoSurveyIdAndPeriod(Integer demoNo, String surveyId, int cutoffInDays) {
        Query query = createQuery("sd", "sd.surveyId = :surveyId AND sd.demographicNo = :demoNo AND sd.surveyDate >= :surveyDate");
        query.setParameter("surveyId", surveyId);
        query.setParameter("demoNo", demoNo);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.roll(Calendar.DAY_OF_YEAR, cutoffInDays * (-1));
        query.setParameter("surveyDate", calendar.getTime());
        return query.getResultList();	    
    }

    public List<Object[]> countStatuses(String surveyId) {
        String sql = "SELECT sd.status , COUNT(sd.status) FROM SurveyData sd WHERE sd.surveyId = :surveyId GROUP BY sd.status";
        Query query = entityManager.createQuery(sql);
        query.setParameter("surveyId", surveyId);
        return query.getResultList();
    }

    public List<Object[]> countAnswers(String surveyId) {
        String sql = "SELECT sd.answer, COUNT(sd.answer) FROM SurveyData sd WHERE sd.surveyId = :surveyId AND sd.status = 'A' GROUP BY sd.answer";
        Query query = entityManager.createQuery(sql);
        query.setParameter("surveyId", surveyId);
        return query.getResultList();
    }
}
