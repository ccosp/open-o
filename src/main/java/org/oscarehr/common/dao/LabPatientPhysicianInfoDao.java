package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;

public interface LabPatientPhysicianInfoDao extends AbstractDao<LabPatientPhysicianInfo> {
    List<Object[]> findRoutings(Integer demographicNo, String labType);
    List<Object[]> findByPatientName(String status, String labType, String providerNo, String patientLastName, String patientFirstName, String patientHealthNumber);
    List<Object[]> findByDemographic(Integer demographicNo, String labType);
    List<Object[]> findLabServiceDatesByLabId(Integer labId);
    List<Integer> getLabResultsSince(Integer demographicNo, Date updateDate);
}
