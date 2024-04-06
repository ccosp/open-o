package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.LabPatientPhysicianInfo;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class LabPatientPhysicianInfoDaoImpl extends AbstractDaoImpl<LabPatientPhysicianInfo> implements LabPatientPhysicianInfoDao {

    public LabPatientPhysicianInfoDaoImpl() {
        super(LabPatientPhysicianInfo.class);
    }

    @Override
    public List<Object[]> findRoutings(Integer demographicNo, String labType) {
        // ... implementation code ...
    }

    @Override
    public List<Object[]> findByPatientName(String status, String labType, String providerNo, String patientLastName, String patientFirstName, String patientHealthNumber) {
        // ... implementation code ...
    }

    @Override
    public List<Object[]> findByDemographic(Integer demographicNo, String labType) {
        // ... implementation code ...
    }

    @Override
    public List<Object[]> findLabServiceDatesByLabId(Integer labId) {
        // ... implementation code ...
    }

    @Override
    public List<Integer> getLabResultsSince(Integer demographicNo, Date updateDate) {
        // ... implementation code ...
    }
}
