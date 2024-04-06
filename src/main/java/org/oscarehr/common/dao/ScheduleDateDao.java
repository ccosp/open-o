package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import org.oscarehr.common.model.ScheduleDate;

public interface ScheduleDateDao extends AbstractDao<ScheduleDate> {
    ScheduleDate findByProviderNoAndDate(String providerNo, Date date);
    List<ScheduleDate> findByProviderPriorityAndDateRange(String providerNo, char priority, Date date, Date date2);
    List<ScheduleDate> findByProviderAndDateRange(String providerNo, Date date, Date date2);
    List<ScheduleDate> search_scheduledate_c(String providerNo);
    List<ScheduleDate> search_numgrpscheduledate(String myGroupNo, Date sDate);
    List<Object[]> search_appttimecode(Date sDate, String providerNo);
    List<ScheduleDate> search_scheduledate_teamp(Date date, Date date2, String status, List<String> providerNos);
    List<ScheduleDate> search_scheduledate_datep(Date date, Date date2, String status);
    List<ScheduleDate> findByProviderStartDateAndPriority(String providerNo, Date apptDate, String priority);
}
