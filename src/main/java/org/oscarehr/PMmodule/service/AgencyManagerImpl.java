package org.oscarehr.PMmodule.service;

import org.oscarehr.PMmodule.dao.AgencyDao;
import org.oscarehr.PMmodule.model.Agency;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AgencyManagerImpl implements AgencyManager {
	
	private AgencyDao dao;

	public void setAgencyDao(AgencyDao dao) {
		this.dao = dao;
	}

	@Override
	public Agency getLocalAgency() {
		Agency agency = dao.getLocalAgency();
		return agency;
	}

	@Override
	public void saveAgency(Agency agency) {
		dao.saveAgency(agency);
	}
}
