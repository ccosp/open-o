package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import org.oscarehr.common.model.OcanStaffForm;

public interface OcanStaffFormDao extends AbstractDao<OcanStaffForm> {

	OcanStaffForm findLatestCompletedInitialOcan(Integer facilityId, Integer clientId);
	Object[] findLatestCompletedInitialOcan_startDates(Integer facilityId, Integer clientId);
	OcanStaffForm findLatestCompletedReassessment(Integer facilityId, Integer clientId);
	Object[] findLatestCompletedReassessment_startDates(Integer facilityId, Integer clientId);
	OcanStaffForm findLatestCompletedDischargedAssessment(Integer facilityId, Integer clientId);
	OcanStaffForm findLatestByFacilityClient(Integer facilityId, Integer clientId, String ocanType);
	OcanStaffForm findByProviderAndSubmissionId(String providerNo, Integer submissionId, String type );
	OcanStaffForm findLatestCbiFormsByFacilityAdmissionId(Integer facilityId, Integer admissionId, Boolean signed);
	List<OcanStaffForm> getLatestCbiFormsByGroupOfAdmissionId();
	List<Integer> findClientsWithOcan(Integer facilityId);
	OcanStaffForm getLastCompletedOcanForm(Integer facilityId, Integer clientId);
	OcanStaffForm getLastCompletedOcanFormByOcanType(Integer facilityId, Integer clientId, String ocanType);
	List<OcanStaffForm> findByFacilityClient(Integer facilityId, Integer clientId, String ocanType);
	OcanStaffForm findOcanStaffFormById(Integer ocanStaffFormId);
	List<OcanStaffForm> findLatestSignedOcanForms(Integer facilityId, String formVersion, Date startDate, Date endDate,String ocanType);
	List<OcanStaffForm> findLatestSignedOcanForms(Integer facilityId, Integer clientId);
	List<OcanStaffForm> findUnsubmittedOcanFormsByOcanType(Integer facilityId, String ocanType,String assessmentid);
	List<OcanStaffForm> findUnsubmittedOcanForms(Integer facilityId);
	List<OcanStaffForm> findSubmittedOcanFormsByAssessmentId(Integer assessmentId);
	List<OcanStaffForm> findAllByFacility(Integer facilityId);
	List<OcanStaffForm> findBySubmissionId(Integer facilityId,Integer submissionId);
	OcanStaffForm findLatestByAssessmentId(Integer facilityId,Integer assessmentId);
	List<Integer> getAllOcanClients(Integer facilityId);
	List<OcanStaffForm> findLatestOcanFormsByStaff(Integer facilityId, String providerNo);
	List<OcanStaffForm> findLatestByConsumer(Integer facilityId, Integer consumerId);
}
