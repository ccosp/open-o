package org.oscarehr.PMmodule.service;

import org.oscarehr.PMmodule.model.Agency;

public interface AgencyManager {
    Agency getLocalAgency();
    void saveAgency(Agency agency);
}
