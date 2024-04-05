package org.oscarehr.common.dao;
import java.util.List;
import org.oscarehr.casemgmt.model.CaseManagementCPP;
import org.oscarehr.casemgmt.model.CaseManagementNote;
import org.oscarehr.common.model.EChart;

public interface EChartDao extends AbstractDao<EChart> {
    EChart getLatestChart(int demographicNo);
    String saveEchart(CaseManagementNote note, CaseManagementCPP cpp, String userName, String lastStr);
    void updateEchartOngoing(CaseManagementCPP cpp);
    void saveCPPIntoEchart(CaseManagementCPP cpp, String providerNo);
    Integer getMaxIdForDemographic(Integer demoNo);
    List<EChart> getChartsForDemographic(Integer demoNo);
    List<EChart> findByDemoIdAndSubject(Integer demoNo, String subj);
}
