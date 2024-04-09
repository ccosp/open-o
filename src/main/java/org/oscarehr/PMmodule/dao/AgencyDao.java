package org.oscarehr.PMmodule.dao;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.oscarehr.PMmodule.model.Agency;
import org.oscarehr.util.MiscUtils;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

public interface AgencyDao{

    public Agency getLocalAgency();

    public void saveAgency(Agency agency);

}