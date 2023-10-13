package org.oscarehr.documentManager;

import org.oscarehr.common.dao.ConsultDocsDao;
import org.oscarehr.common.dao.EFormDocsDao;
import org.oscarehr.common.model.ConsultDocs;
import org.oscarehr.common.model.EFormDocs;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.common.model.enumerator.DocumentType;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentAttachmentManager {
	@Autowired
	private SecurityInfoManager securityInfoManager;
	@Autowired
	private ConsultDocsDao consultDocsDao;
	@Autowired
	private EFormDocsDao eFormDocsDao;
	private final DocumentAttach documentAttach = new DocumentAttach();

	/*
	 * Note to developer, after completly implementing DocumentAttachmentManager and DocumentAttach
	 * Move out requestId/fdid, providerNo, and demographicNo and define it using
	 * DocumentAttachmentManager/DocumentAttach constructor and getter/setter
	 * By doing that it will decrease number parameters(MAX 3) in methods - right way of coding
	 */

	public List<Integer> getConsultAttachments(LoggedInInfo loggedInInfo, Integer requestId, DocumentType documentType, Integer demographicNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_con", SecurityInfoManager.READ, demographicNo)) {
			throw new RuntimeException("missing required security object (_con)");
		}

		List<Integer> consultAttachments = new ArrayList<>();
		List<ConsultDocs> consultDocs = consultDocsDao.findByRequestIdDocType(requestId, documentType.getType());
		for (ConsultDocs consultDocs1 : consultDocs) {
			consultAttachments.add(consultDocs1.getDocumentNo());
		}
		return consultAttachments;
	}

	public List<Integer> getEFormAttachments(LoggedInInfo loggedInInfo, Integer fdid, DocumentType documentType, Integer demographicNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_eform", SecurityInfoManager.READ, demographicNo)) {
			throw new RuntimeException("missing required security object (_eform)");
		}

		List<Integer> eFormAttachments = new ArrayList<>();
		List<EFormDocs> eFormDocs = eFormDocsDao.findByFdidIdDocType(fdid, documentType.getType());
		for (EFormDocs eFormDocs1 : eFormDocs) {
			eFormAttachments.add(eFormDocs1.getDocumentNo());
		}
		return eFormAttachments;
	}

	public void attachToConsult(LoggedInInfo loggedInInfo, DocumentType documentType, String[] attachments, String providerNo, Integer requestId, Integer demographicNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_con", SecurityInfoManager.WRITE, demographicNo)) {
			throw new RuntimeException("missing required security object (_con)");
		}

		documentAttach.attachToConsult(attachments, documentType, providerNo, requestId);
	}

	public void attachToEForm(LoggedInInfo loggedInInfo, DocumentType documentType, String[] attachments, String providerNo, Integer fdid, Integer demographicNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_eform", SecurityInfoManager.WRITE, demographicNo)) {
			throw new RuntimeException("missing required security object (_eform)");
		}

		documentAttach.attachToEForm(attachments, documentType, providerNo, fdid);
	}
}
