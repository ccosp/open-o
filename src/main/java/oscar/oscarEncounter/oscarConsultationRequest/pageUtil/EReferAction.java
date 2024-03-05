package oscar.oscarEncounter.oscarConsultationRequest.pageUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.common.dao.EReferAttachmentDao;
import org.oscarehr.common.model.EReferAttachment;
import org.oscarehr.common.model.EReferAttachmentData;
import org.oscarehr.util.SpringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EReferAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		Integer demographicNo = Integer.parseInt(request.getParameter("demographicNo"));
		String documents = StringUtils.trimToEmpty(request.getParameter("documents"));
		if (!documents.isEmpty()) {
			EReferAttachment eReferAttachment = new EReferAttachment(demographicNo);
			List<EReferAttachmentData> attachments = new ArrayList<>();
			String[] splitDocuments = documents.split("\\|");
			
			for (String document : splitDocuments) {
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
		
		return null;
	}
}
