package org.oscarehr.documentManager;

import org.oscarehr.common.dao.ConsultDocsDao;
import org.oscarehr.common.dao.EFormDocsDao;
import org.oscarehr.common.model.ConsultDocs;
import org.oscarehr.common.model.EFormDocs;
import org.oscarehr.util.SpringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocumentAttach {
	ConsultDocsDao consultDocsDao = SpringUtils.getBean(ConsultDocsDao.class);
	EFormDocsDao eFormDocsDao = SpringUtils.getBean(EFormDocsDao.class);

	public DocumentAttach() {
	}

	public void attachToConsult(String[] attachments, String docType, String providerNo, Integer requestId) {
		List<String> currentList = new ArrayList<>(Arrays.asList(attachments));
		List<ConsultDocs> consultDocsList = consultDocsDao.findByRequestIdDocType(requestId, docType);
		List<String> oldList = new ArrayList<>();
		for (ConsultDocs consultDoc : consultDocsList) {
			oldList.add(Integer.toString(consultDoc.getDocumentNo()));
		}
		detachFromConsult(currentList, oldList, docType, requestId);
		attachToConsult(currentList, oldList, docType, providerNo, requestId);
	}

	private void attachToConsult(List<String> currentList, List<String> oldList, String docType, String providerNo, Integer requestId) {
		for (String docId : currentList) {
			if (oldList.contains(docId)) { continue; }
			ConsultDocs consultDoc = new ConsultDocs(requestId, Integer.parseInt(docId), docType, providerNo);
			consultDocsDao.persist(consultDoc);
		}
	}

	private void detachFromConsult(List<String> currentList, List<String> oldList, String docType, Integer requestId) {
		for (String docId : oldList) {
			if (currentList.contains(docId)) { continue; }
			List<ConsultDocs> detachList = consultDocsDao.findByRequestIdDocNoDocType(requestId, Integer.valueOf(docId), docType);
			for (ConsultDocs consultDoc : detachList) {
				consultDoc.setDeleted("Y");
				consultDocsDao.merge(consultDoc);
			}
		}
	}

	public void attachToEForm(String[] attachments, String docType, String providerNo, Integer fdid) {
		List<String> currentList = new ArrayList<>(Arrays.asList(attachments));
		List<EFormDocs> eFormDocsList = eFormDocsDao.findByFdidIdDocType(fdid, docType);
		List<String> oldList = new ArrayList<>();
		for (EFormDocs eFormDoc : eFormDocsList) {
			oldList.add(Integer.toString(eFormDoc.getDocumentNo()));
		}
		detachFromEForm(currentList, oldList, docType, fdid);
		attachToEForm(currentList, oldList, docType, providerNo, fdid);
	}

	private void attachToEForm(List<String> currentList, List<String> oldList, String docType, String providerNo, Integer fdid) {
		for (String docId : currentList) {
			if (oldList.contains(docId)) { continue; }
			EFormDocs eFormDocs = new EFormDocs(fdid, Integer.parseInt(docId), docType, providerNo);
			eFormDocsDao.persist(eFormDocs);
		}
	}

	private void detachFromEForm(List<String> currentList, List<String> oldList, String docType, Integer fdid) {
		for (String docId : oldList) {
			if (currentList.contains(docId)) { continue; }
			List<EFormDocs> detachList = eFormDocsDao.findByFdidIdDocNoDocType(fdid, Integer.valueOf(docId), docType);
			for (EFormDocs eFormDoc : detachList) {
				eFormDoc.setDeleted("Y");
				eFormDocsDao.merge(eFormDoc);
			}
		}
	}
}
