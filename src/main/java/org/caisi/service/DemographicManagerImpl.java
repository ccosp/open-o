//CHECKSTYLE:OFF
package org.caisi.service;

import ca.openosp.openo.common.dao.DemographicDao;
import ca.openosp.openo.common.model.Demographic;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class DemographicManagerImpl implements DemographicManager {

    private DemographicDao demographicDao = null;

    public void setDemographicDao(DemographicDao demographicDao) {
        this.demographicDao = demographicDao;
    }

    @Override
    public Demographic getDemographic(String demographic_no) {
        return demographicDao.getDemographic(demographic_no);
    }

    @Override
    public List getDemographics() {
        return demographicDao.getDemographics();
    }

    @Override
    public List getProgramIdByDemoNo(String demoNo) {
        return demographicDao.getProgramIdByDemoNo(Integer.parseInt(demoNo));
    }

    @Override
    public List getDemoProgram(Integer demoNo) {
        return demographicDao.getDemoProgram(demoNo);
    }
}
