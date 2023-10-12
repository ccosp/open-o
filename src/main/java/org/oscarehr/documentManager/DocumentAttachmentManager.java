package org.oscarehr.documentManager;

import org.oscarehr.common.dao.ConsultDocsDao;
import org.oscarehr.common.dao.EFormDocsDao;
import org.oscarehr.common.model.ConsultDocs;
import org.oscarehr.common.model.EFormDocs;
import org.oscarehr.managers.*;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentAttachmentManager {
	@Autowired
	SecurityInfoManager securityInfoManager;
	ConsultDocsDao consultDocsDao = SpringUtils.getBean(ConsultDocs.class);
	EFormDocsDao eFormDocsDao = SpringUtils.getBean(EFormDocsDao.class);
	DocumentAttach documentAttach = new DocumentAttach();

	public List<Integer> getConsultAttachments(LoggedInInfo loggedInInfo, Integer requestId, String docType, Integer demographicNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_con", SecurityInfoManager.READ, demographicNo)) {
			throw new RuntimeException("missing required security object (_con)");
		}

		List<Integer> consultAttachments = new ArrayList<>();
		List<ConsultDocs> consultDocs = consultDocsDao.findByRequestIdDocType(requestId, docType);
		for (ConsultDocs consultDocs1 : consultDocs) {
			consultAttachments.add(consultDocs1.getDocumentNo());
		}
		return consultAttachments;
	}

	public List<Integer> getEFormAttachments(LoggedInInfo loggedInInfo, Integer fdid, String docType, Integer demographicNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_eform", SecurityInfoManager.READ, demographicNo)) {
			throw new RuntimeException("missing required security object (_eform)");
		}

		List<Integer> eFormAttachments = new ArrayList<>();
		List<EFormDocs> eFormDocs = eFormDocsDao.findByFdidIdDocType(fdid, docType);
		for (EFormDocs eFormDocs1 : eFormDocs) {
			eFormAttachments.add(eFormDocs1.getDocumentNo());
		}
		return eFormAttachments;
	}

	public void attachToConsult(LoggedInInfo loggedInInfo, String docType, String[] attachments, String providerNo, Integer requestId, Integer demographicNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_con", SecurityInfoManager.WRITE, demographicNo)) {
			throw new RuntimeException("missing required security object (_con)");
		}

		documentAttach.attachToConsult(attachments, docType, providerNo, requestId);
	}

	public void attachToEForm(LoggedInInfo loggedInInfo, String docType, String[] attachments, String providerNo, Integer fdid, Integer demographicNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_eform", SecurityInfoManager.WRITE, demographicNo)) {
			throw new RuntimeException("missing required security object (_eform)");
		}

		documentAttach.attachToEForm(attachments, docType, providerNo, fdid);
	}
}
