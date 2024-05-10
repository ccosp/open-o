package oscar.oscarEncounter.oscarConsultationRequest.pageUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.dao.EReferAttachmentDao;
import org.oscarehr.common.model.EReferAttachment;
import org.oscarehr.common.model.EReferAttachmentData;
import org.oscarehr.common.model.enumerator.DocumentType;
import org.oscarehr.documentManager.DocumentAttachmentManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EReferAction extends DispatchAction {
	private final DocumentAttachmentManager documentAttachmentManager = SpringUtils.getBean(DocumentAttachmentManager.class);

	public void attachOceanEReferralConsult(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		Integer demographicNo = Integer.parseInt(request.getParameter("demographicNo"));
		String documents = StringUtils.trimToEmpty(request.getParameter("documents"));
		if (documents.isEmpty()) { return; }

		EReferAttachment eReferAttachment = new EReferAttachment(demographicNo);
		List<EReferAttachmentData> attachments = new ArrayList<>();
		
		for (String document : documents.split("\\|")) {
			String type = document.replaceAll("\\d", "");
			Integer id = Integer.parseInt(document.substring(type.length()));
			EReferAttachmentData attachmentData = new EReferAttachmentData(eReferAttachment, id, type);
			attachments.add(attachmentData);
		}
		eReferAttachment.setAttachments(attachments);
		EReferAttachmentDao eReferAttachmentDao = SpringUtils.getBean(EReferAttachmentDao.class);
		eReferAttachmentDao.persist(eReferAttachment);

		try {
			response.getWriter().write(eReferAttachment.getId().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void editOceanEReferralConsult(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		Integer demographicNo = Integer.parseInt(request.getParameter("demographicNo"));
		Integer requestId = Integer.parseInt(request.getParameter("requestId"));
		String providerNo = loggedInInfo.getLoggedInProviderNo();
		String documents = StringUtils.trimToEmpty(request.getParameter("documents"));
		if (documents.isEmpty()) { return; }

		List<String> docs = new ArrayList<>();
		List<String> labs = new ArrayList<>();
		List<String> eforms = new ArrayList<>();
		List<String> hrms = new ArrayList<>();
		
		for (String document : documents.split("\\|")) {
			String type = document.replaceAll("\\d", "");
			switch(type) {
				case "D": docs.add(document.substring(type.length())); break;
				case "L": labs.add(document.substring(type.length())); break;
				case "E": eforms.add(document.substring(type.length())); break;
				case "H": hrms.add(document.substring(type.length())); break;
			}
		}

		documentAttachmentManager.attachToConsult(loggedInInfo, DocumentType.DOC, docs.toArray(new String[0]), providerNo, requestId, demographicNo, Boolean.TRUE);
		documentAttachmentManager.attachToConsult(loggedInInfo, DocumentType.LAB, labs.toArray(new String[0]), providerNo, requestId, demographicNo, Boolean.TRUE);
		documentAttachmentManager.attachToConsult(loggedInInfo, DocumentType.EFORM, eforms.toArray(new String[0]), providerNo, requestId, demographicNo, Boolean.TRUE);
		documentAttachmentManager.attachToConsult(loggedInInfo, DocumentType.HRM, hrms.toArray(new String[0]), providerNo, requestId, demographicNo, Boolean.TRUE);
	}
}
