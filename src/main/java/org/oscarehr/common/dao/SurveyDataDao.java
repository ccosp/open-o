package org.oscarehr.common.dao;

import java.util.Calendar;
import java.util.List;

import org.oscarehr.common.model.SurveyData;

public interface SurveyDataDao extends AbstractDao<SurveyData> {

    int getMaxProcessed(String surveyId);

    int getProcessCount(String surveyId);

    List<SurveyData> findByDemoSurveyIdAndPeriod(Integer demoNo, String surveyId, int cutoffInDays);

    List<Object[]> countStatuses(String surveyId);

    List<Object[]> countAnswers(String surveyId);
}
