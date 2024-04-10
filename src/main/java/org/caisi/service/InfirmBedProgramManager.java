package org.caisi.service;

import java.util.Date;
import java.util.List;

import org.apache.struts.util.LabelValueBean;

public interface InfirmBedProgramManager {
    void setProviderDefaultProgramDao(ProviderDefaultProgramDao providerDefaultProgramDao);
    void setBedProgramDao(BedProgramDao dao);
    void setProgramDao(ProgramDao dao);
    void setDemographicDao(DemographicDao dao);
    List getPrgramNameID();
    List getPrgramName();
    List<LabelValueBean> getProgramBeans();
    List<LabelValueBean> getProgramBeans(String providerNo, Integer facilityId);
    List<LabelValueBean> getProgramBeansByFacilityId(Integer facilityId);
    List getProgramForApptViewBeans(String providerNo, Integer facilityId);
    List getDemographicByBedProgramIdBeans(int programId, Date dt, String archiveView);
    int getDefaultProgramId();
    int getDefaultProgramId(String providerNo);
    void setDefaultProgramId(String providerNo, int programId);
    Boolean getProviderSig(String providerNo);
    void toggleSig(String providerNo);
    ProgramProviderDAO getProgramProviderDAOT();
    void setProgramProviderDAOT(ProgramProviderDAO programProviderDAOT);
    String[] getProgramInformation(int programId);
}
