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

        String receiverName = demographicManager.getDemographicFormattedName(loggedInInfo, Integer.parseInt(demographicId));
        String receiverEmail = demographicManager.getDemographicEmail(loggedInInfo, Integer.parseInt(demographicId));
        Boolean hasEmailConsent = emailComposeManager.hasEmailConsent(Integer.parseInt(demographicId)); 
        if (StringUtils.isNullOrEmpty(receiverEmail) || !hasEmailConsent) { return emailComposeError(request, mapping, "Unable to proceed: Patient's email address is not provided or consent is not given", "eFormError"); }
        
        List<EmailConfig> senderAccounts = emailComposeManager.getAllSenderAccounts();
        if (senderAccounts.size() == 0) { return emailComposeError(request, mapping, "Unable to proceed: Please setup the sender's account first.", "eFormError"); }

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
        emailComposeManager.sanitizeAttachments(emailAttachmentList);

        request.setAttribute("receiverName", receiverName);
        request.setAttribute("receiverEmail", receiverEmail);
        request.setAttribute("senderAccounts", senderAccounts);
        request.getSession().setAttribute("emailAttachmentList", emailAttachmentList);

        return mapping.findForward("compose");
    }

    private ActionForward emailComposeError(HttpServletRequest request, ActionMapping mapping, String errorMessage, String forwardTo) {
        request.setAttribute("errorMessage", errorMessage);
        return mapping.findForward("eFormError");
    }
}
