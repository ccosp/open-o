package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import org.oscarehr.common.model.DxRegistedPTInfo;
import org.oscarehr.common.model.Dxresearch;
import oscar.oscarResearch.oscarDxResearch.bean.dxCodeSearchBean;
<<<<<<< HEAD

import java.util.Date;
import java.util.List;

public interface DxresearchDAO extends AbstractDao<Dxresearch> {
    List<DxRegistedPTInfo> getPatientRegisted(List<Dxresearch> dList, List<String> doctorList);
    List<DxRegistedPTInfo> patientRegistedDistincted(List<dxCodeSearchBean> searchItems, List<String> doctorList);
    List<DxRegistedPTInfo> patientRegistedAll(List<dxCodeSearchBean> searchItems, List<String> doctorList);
    List<DxRegistedPTInfo> patientRegistedActive(List<dxCodeSearchBean> searchItems, List<String> doctorList);
    List<DxRegistedPTInfo> patientRegistedResolve(List<dxCodeSearchBean> searchItems, List<String> doctorList);
    List<DxRegistedPTInfo> patientRegistedDeleted(List<dxCodeSearchBean> searchItems, List<String> doctorList);
    List<Dxresearch> getDxResearchItemsByPatient(Integer demographicNo);
    void save(Dxresearch d);
    List<Dxresearch> getByDemographicNo(int demographicNo);
    List<Dxresearch> find(int demographicNo, String codeType, String code);
    List<Dxresearch> findActive(String codeType, String code);
    boolean entryExists(int demographicNo, String codeType, String code);
    boolean activeEntryExists(int demographicNo, String codeType, String code);
    void removeAllAssociationEntries();
    List<Object[]> findResearchAndCodingSystemByDemographicAndCondingSystem(String codingSystem, String demographicNo);
    List<Dxresearch> findCurrentByCodeTypeAndCode(String codeType, String code);
    List<Dxresearch> getByDemographicNoSince(int demographicNo, Date lastUpdateDate);
    List<Integer> getByDemographicNoSince(Date lastUpdateDate);
    List<Dxresearch> findNonDeletedByDemographicNo(Integer demographicNo);
    List<Integer> findNewProblemsSinceDemokey(String keyName);
    String getDescription(String codingSystem, String code);
    public List<Dxresearch> findByDemographicNoResearchCodeAndCodingSystem(Integer demographicNo, String dxresearchCode, String codingSystem);
    public List<dxCodeSearchBean> getQuickListItems(String quickListName);
    public List<Object[]> getDataForInrReport(Date fromDate, Date toDate);
=======
import oscar.oscarResearch.oscarDxResearch.bean.dxQuickListItemsHandler;

/**
 *
 * @author toby
 */

public interface DxresearchDAO extends AbstractDao<Dxresearch> {

    public List<DxRegistedPTInfo> getPatientRegisted(List<Dxresearch> dList, List<String> doctorList);

    public List<DxRegistedPTInfo> patientRegistedDistincted(List<dxCodeSearchBean> searchItems,
            List<String> doctorList);

    public List<DxRegistedPTInfo> patientRegistedAll(List<dxCodeSearchBean> searchItems, List<String> doctorList);

    public List<DxRegistedPTInfo> patientRegistedActive(List<dxCodeSearchBean> searchItems, List<String> doctorList);

    public List<DxRegistedPTInfo> patientRegistedResolve(List<dxCodeSearchBean> searchItems, List<String> doctorList);

    public List<DxRegistedPTInfo> patientRegistedDeleted(List<dxCodeSearchBean> searchItems, List<String> doctorList);

    public List<DxRegistedPTInfo> patientRegistedStatus(String status, List<dxCodeSearchBean> searchItems,
            List<String> doctorList);

    public List<dxCodeSearchBean> getQuickListItems(String quickListName);

    public List<Dxresearch> getDxResearchItemsByPatient(Integer demographicNo);

    public void save(Dxresearch d);

    public List<Dxresearch> getByDemographicNo(int demographicNo);

    public List<Dxresearch> find(int demographicNo, String codeType, String code);

    public List<Dxresearch> findActive(String codeType, String code);

    public boolean entryExists(int demographicNo, String codeType, String code);

    public boolean activeEntryExists(int demographicNo, String codeType, String code);

    public void removeAllAssociationEntries();

    public List<Dxresearch> findByDemographicNoResearchCodeAndCodingSystem(Integer demographicNo, String dxresearchCode,
            String codingSystem);

    public List<Object[]> getDataForInrReport(Date fromDate, Date toDate);

    public Integer countResearches(String researchCode, Date sdate, Date edate);

    public Integer countBillingResearches(String researchCode, String diagCode, String creator, Date sdate, Date edate);

    public List<Object[]> findResearchAndCodingSystemByDemographicAndCondingSystem(String codingSystem,
            String demographicNo);

    public List<Dxresearch> findCurrentByCodeTypeAndCode(String codeType, String code);

    public List<Dxresearch> getByDemographicNoSince(int demographicNo, Date lastUpdateDate);

    public List<Integer> getByDemographicNoSince(Date lastUpdateDate);

    public List<Dxresearch> findNonDeletedByDemographicNo(Integer demographicNo);

    public List<Integer> findNewProblemsSinceDemokey(String keyName);

    public String getDescription(String codingSystem, String code);
>>>>>>> 0edf23bd75b285de0f7d27e0f02ef8dd1cce55a3
}
