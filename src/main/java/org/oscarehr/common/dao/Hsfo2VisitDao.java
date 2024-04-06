package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import org.oscarehr.common.model.Hsfo2Visit;

public interface Hsfo2VisitDao extends AbstractDao<Hsfo2Visit> {
    Hsfo2Visit getHsfoVisitById(int id);
    List<Hsfo2Visit> getHsfoVisitByDemographicNo(Integer demographic_no);
    List<Hsfo2Visit> getLockedVisitByDemographicNo(String demographic_no);
    List<Hsfo2Visit> getVisitRecordByPatientId(String patientId);
    Hsfo2Visit getPatientLatestVisitRecordByVisitDate(Date visitdate, String demographic_no);
    List<Hsfo2Visit> getVisitRecordInDateRangeByDemographicNo(String patientId, String startDate, String endDate);
    Hsfo2Visit getFirstVisitRecordForThePatient(String patientId);
    Hsfo2Visit getPatientBaseLineVisitData(String patientId);
    int getMaxVisitId();
}
