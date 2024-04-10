package org.oscarehr.PMmodule.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.oscarehr.PMmodule.dao.ClientReferralDAO;
import org.oscarehr.PMmodule.exception.AlreadyAdmittedException;
import org.oscarehr.PMmodule.exception.AlreadyQueuedException;
import org.oscarehr.PMmodule.exception.ServiceRestrictionException;
import org.oscarehr.PMmodule.model.ClientReferral;
import org.oscarehr.PMmodule.model.ProgramClientRestriction;
import org.oscarehr.PMmodule.model.ProgramQueue;
import org.oscarehr.PMmodule.web.formbean.ClientSearchFormBean;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.DemographicExtDao;
import org.oscarehr.common.dao.JointAdmissionDao;
import org.oscarehr.common.model.Admission;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.DemographicExt;
import org.oscarehr.common.model.JointAdmission;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ClientManagerImpl implements ClientManager {

    private DemographicDao dao;
    private DemographicExtDao demographicExtDao;
    private ClientReferralDAO referralDAO;
    private JointAdmissionDao jointAdmissionDao;
    private ProgramQueueManager queueManager;
    private AdmissionManager admissionManager;
    private ClientRestrictionManager clientRestrictionManager;

    private boolean outsideOfDomainEnabled;

    //... rest of the code
}
