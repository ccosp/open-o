package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.NativeSql;
import org.oscarehr.common.model.ScheduleTemplate;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class ScheduleTemplateDaoImpl extends AbstractDaoImpl<ScheduleTemplate> implements ScheduleTemplateDao {
    
    public ScheduleTemplateDaoImpl() {
        super(ScheduleTemplate.class);
    }
    
    @Override
    public List<ScheduleTemplate> findBySummary(String summary) {
        // ... existing implementation ...
    }

    @Override
    public List<Object[]> findSchedules(Date date_from, Date date_to, String provider_no) {
        // ... existing implementation ...
    }

    @Override
    public List<Object[]> findSchedules(Date dateFrom, List<String> providerIds) {
        // ... existing implementation ...
    }

    @Override
    public List<ScheduleTemplate> findByProviderNoAndName(String providerNo, String name) {
        // ... existing implementation ...
    }

    @Override
    public List<ScheduleTemplate> findByProviderNo(String providerNo) {
        // ... existing implementation ...
    }

    @Override
    @NativeSql({"scheduletemplate", "scheduledate"})
    public List<Object> findTimeCodeByProviderNo(String providerNo, Date date) {
        // ... existing implementation ...
    }

    @Override
    @NativeSql({"scheduletemplate", "scheduledate"})
    public List<Object> findTimeCodeByProviderNo2(String providerNo, Date date) {
        // ... existing implementation ...
    }
}
