package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import org.oscarehr.common.model.ReportAgeSex;

public interface ReportAgeSexDao extends AbstractDao<ReportAgeSex> {
    List<ReportAgeSex> findBeforeReportDate(Date reportDate);
    void deleteAllByDate(Date reportDate);
    void populateAll(String yearOfBirth);
    Long count_reportagesex_roster(String roster, String sex, String providerNo, int age, Date dateStarted, Date dateEnded);
    Long count_reportagesex_noroster(String roster, String sex, String providerNo,int minAge, int maxAge, Date dateStarted, Date dateEnded);
    Long count_reportagesex(String roster, String sex, String providerNo, int minAge, int maxAge, Date startDate, Date endDate);
}
