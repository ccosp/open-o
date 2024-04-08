package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import org.oscarehr.common.model.ScheduleTemplate;

public interface ScheduleTemplateDao extends AbstractDao<ScheduleTemplate> {
    List<ScheduleTemplate> findBySummary(String summary);
    List<Object[]> findSchedules(Date date_from, Date date_to, String provider_no);
    List<Object[]> findSchedules(Date dateFrom, List<String> providerIds);
    List<ScheduleTemplate> findByProviderNoAndName(String providerNo, String name);
    List<ScheduleTemplate> findByProviderNo(String providerNo);
    List<Object> findTimeCodeByProviderNo(String providerNo, Date date);
    List<Object> findTimeCodeByProviderNo2(String providerNo, Date date);
}
