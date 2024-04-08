package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import org.oscarehr.common.model.DxRegistedPTInfo;
import org.oscarehr.common.model.Dxresearch;
import oscar.oscarResearch.oscarDxResearch.bean.dxCodeSearchBean;

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
}
