package org.caisi.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.struts.util.LabelValueBean;
import org.caisi.dao.BedProgramDao;
import org.oscarehr.PMmodule.dao.ProgramDao;
import org.oscarehr.PMmodule.dao.ProgramProviderDAO;
import org.oscarehr.PMmodule.model.Program;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.ProviderDefaultProgramDao;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.ProviderDefaultProgram;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

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
