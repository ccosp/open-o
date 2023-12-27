package org.oscarehr.email.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.model.EmailAttachment;
import org.oscarehr.common.model.EmailConfig;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.managers.EmailComposeManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.PDFGenerationException;
import org.oscarehr.util.SpringUtils;

import oscar.util.StringUtils;

public class EmailComposeAction extends DispatchAction {
    private static final Logger logger = MiscUtils.getLogger();
    private DemographicManager demographicManager = SpringUtils.getBean(DemographicManager.class);
    private EmailComposeManager emailComposeManager = SpringUtils.getBean(EmailComposeManager.class);

    public ActionForward prepareComposeEFormMailer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        String fdid = (String) request.getAttribute("fdid");
        String demographicId = (String) request.getAttribute("demographicId");
        String[] attachedDocuments = (String[]) request.getAttribute("attachedDocuments");
		String[] attachedLabs = (String[]) request.getAttribute("attachedLabs");
		String[] attachedForms = (String[]) request.getAttribute("attachedForms");
		String[] attachedEForms = (String[]) request.getAttribute("attachedEForms");
		String[] attachedHRMDocuments = (String[]) request.getAttribute("attachedHRMDocuments");

        List<EmailAttachment> emailAttachmentList = new ArrayList<>();
        try {
            emailAttachmentList.addAll(emailComposeManager.prepareEFormAttachments(loggedInInfo, fdid, attachedEForms));
            emailAttachmentList.addAll(emailComposeManager.prepareEDocAttachments(loggedInInfo, attachedDocuments));
            emailAttachmentList.addAll(emailComposeManager.prepareLabAttachments(loggedInInfo, attachedLabs));
            emailAttachmentList.addAll(emailComposeManager.prepareHRMAttachments(loggedInInfo, attachedHRMDocuments));
            emailAttachmentList.addAll(emailComposeManager.prepareFormAttachments(request, response, attachedForms));
        } catch (PDFGenerationException e) {
            logger.error(e.getMessage(), e);
            return emailComposeError(request, mapping, "This eForm (and attachments, if applicable) could not be emailed. \\n\\n" + e.getMessage(), "eFormError");
        }

        String receiverName = demographicManager.getDemographicFormattedName(loggedInInfo, Integer.parseInt(demographicId));
        String receiverEmail = demographicManager.getDemographicEmail(loggedInInfo, Integer.parseInt(demographicId));
        if (StringUtils.isNullOrEmpty(receiverEmail)) { return emailComposeError(request, mapping, "Unable to proceed: Please include the demographic's email address in the records.", "eFormError"); }
        
        List<EmailConfig> senderAccounts = emailComposeManager.getAllSenderAccounts();
        if (senderAccounts.size() == 0) { return emailComposeError(request, mapping, "Unable to proceed: Please setup the sender's account first.", "eFormError"); }

        request.getSession().setAttribute("emailAttachmentList", emailAttachmentList);
        request.setAttribute("receiverName", receiverName);
        request.setAttribute("receiverEmail", receiverEmail);
        request.setAttribute("senderAccounts", senderAccounts);

        return mapping.findForward("compose");
    }

    private ActionForward emailComposeError(HttpServletRequest request, ActionMapping mapping, String errorMessage, String forwardTo) {
        request.setAttribute("errorMessage", errorMessage);
        return mapping.findForward("eFormError");
    }
}
