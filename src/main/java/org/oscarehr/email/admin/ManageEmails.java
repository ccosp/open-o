package org.oscarehr.email.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.common.model.EmailAttachment;
import org.oscarehr.common.model.EmailConfig;
import org.oscarehr.common.model.EmailLog;
import org.oscarehr.common.model.EmailLog.EmailStatus;
import org.oscarehr.common.model.EmailLog.TransactionType;
import org.oscarehr.email.core.EmailStatusResult;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.managers.EmailComposeManager;
import org.oscarehr.managers.EmailManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.PDFGenerationException;
import org.oscarehr.util.SpringUtils;

import oscar.form.JSONAction;
import oscar.util.StringUtils;

public class ManageEmails extends JSONAction {
    private static final Logger logger = MiscUtils.getLogger();

	private final DemographicManager demographicManager = SpringUtils.getBean(DemographicManager.class);
	private final EmailComposeManager emailComposeManager = SpringUtils.getBean(EmailComposeManager.class);
	private final EmailManager emailManager = SpringUtils.getBean(EmailManager.class);

	public ActionForward showEmailManager(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("emailStatusList", EmailStatus.values());
		request.setAttribute("senderAccountList", emailComposeManager.getAllSenderAccounts());
		return mapping.findForward("show");
	}

	public ActionForward fetchEmails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String emailStatus = request.getParameter("emailStatus");
		String senderEmailAddress = request.getParameter("senderEmailAddress");
		String dateBeginStr = request.getParameter("dateBegin");
		String dateEndStr = request.getParameter("dateEnd");
		String demographic_no = request.getParameter("demographic_no");

		if(emailStatus != null && emailStatus.equalsIgnoreCase("-1")) { emailStatus = null; }
		if(senderEmailAddress != null && senderEmailAddress.equalsIgnoreCase("-1")) { senderEmailAddress = null; }
		if("null".equalsIgnoreCase(demographic_no) || "".equals(demographic_no)) { demographic_no = null; }

		List<EmailStatusResult> emailStatusResults = emailManager.getEmailStatusByDateDemographicSenderStatus(loggedInInfo, dateBeginStr, dateEndStr, demographic_no, senderEmailAddress, emailStatus);
		request.setAttribute("emailStatusResults", emailStatusResults);

		return mapping.findForward("emailstatus");
	}

	public void setResolved(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String emailLogId = request.getParameter("logId");
		if(!StringUtils.isInteger(emailLogId)) { 
			errorResponse(response, "errorMessage", "Invalid email log id");
			return;
		}
		emailManager.updateEmailStatus(loggedInInfo, Integer.parseInt(emailLogId), EmailStatus.RESOLVED, null);
	}

	public ActionForward resendEmail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String emailLogId = request.getParameter("logId");
		if(!StringUtils.isInteger(emailLogId)) {
			errorResponse(response, "errorMessage", "Invalid email log id"); 
			return null; 
		}

		EmailLog emailLog = emailComposeManager.prepareEmailForResend(loggedInInfo, Integer.parseInt(emailLogId));
		List<EmailAttachment> emailAttachmentList = new ArrayList<>();
		try {
			emailAttachmentList = emailComposeManager.refreshEmailAttachments(request, response, emailLog);
		} catch(PDFGenerationException e) {
			request.setAttribute("emailErrorMessage", "This eForm (and attachments, if applicable) could not be emailed. \\n\\n" + e.getMessage());
			request.setAttribute("isEmailError", true);
		}
		
		String[] emailConsent = emailComposeManager.getEmailConsentStatus(loggedInInfo, emailLog.getDemographicNo());
		String receiverName = demographicManager.getDemographicFormattedName(loggedInInfo, emailLog.getDemographicNo());
		List<?>[] receiverEmailList = emailComposeManager.getRecipients(loggedInInfo, emailLog.getDemographicNo());
		List<EmailConfig> senderAccounts = emailComposeManager.getAllSenderAccounts();

		request.setAttribute("demographicId", emailLog.getDemographicNo());
		request.setAttribute("transactionType", TransactionType.DIRECT);
        request.setAttribute("emailConsentName", emailConsent[0]);
        request.setAttribute("emailConsentStatus", emailConsent[1]);
		request.setAttribute("receiverName", receiverName);
        request.setAttribute("receiverEmailList", receiverEmailList[0]);
        request.setAttribute("invalidReceiverEmailList", receiverEmailList[1]);
		request.setAttribute("senderAccounts", senderAccounts);
		request.setAttribute("senderEmail", emailLog.getFromEmail());
		request.setAttribute("subjectEmail", emailLog.getSubject());
		request.setAttribute("bodyEmail", emailLog.getBody());
		request.setAttribute("encryptedMessageEmail", emailLog.getEncryptedMessage());
        request.setAttribute("emailPDFPassword", emailLog.getPassword());
        request.setAttribute("emailPDFPasswordClue", emailLog.getPasswordClue());
		request.setAttribute("isEmailEncrypted", emailLog.getIsEncrypted());
		request.setAttribute("isEmailAttachmentEncrypted", emailLog.getIsAttachmentEncrypted());
		request.setAttribute("emailPatientChartOption", emailLog.getChartDisplayOption().getValue());
        request.getSession().setAttribute("emailAttachmentList", emailAttachmentList);

		return mapping.findForward("compose");
	}
}
