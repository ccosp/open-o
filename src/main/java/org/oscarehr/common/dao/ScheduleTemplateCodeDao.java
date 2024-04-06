package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.ScheduleTemplateCode;

public interface ScheduleTemplateCodeDao extends AbstractDao<ScheduleTemplateCode> {
    List<ScheduleTemplateCode> findAll();
    ScheduleTemplateCode getByCode(char code);
    List<ScheduleTemplateCode> findTemplateCodes();
    ScheduleTemplateCode findByCode(String code);
}
