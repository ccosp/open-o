package org.oscarehr.email.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.model.EmailAttachment;
import org.oscarehr.common.model.EmailLog;
import org.oscarehr.managers.EmailComposeManager;
import org.oscarehr.managers.EmailManager;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

public class EmailSendAction extends DispatchAction {
    private static final Logger logger = MiscUtils.getLogger();
    private EmailManager emailManager = SpringUtils.getBean(EmailManager.class);
    private EmailComposeManager emailComposeManager = SpringUtils.getBean(EmailComposeManager.class);

    public ActionForward sendEmail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        List<EmailAttachment> emailAttachmentList = (List<EmailAttachment>) session.getAttribute("emailAttachmentList");
        String fromEmail = request.getParameter("senderEmailAddress");
        String toEmail = request.getParameter("receiverEmailAddress");
        String subject = request.getParameter("subjectEmail");
        String body = request.getParameter("bodyEmail");

        EmailLog emailDraft = emailComposeManager.prepareEmailForOutbox(fromEmail, toEmail, subject, body, emailAttachmentList);
        Boolean isEmailSent = emailManager.sendEmail(emailDraft);
        EmailLog emailLog = emailComposeManager.updateEmailStatus(emailDraft, isEmailSent); 
        
        request.setAttribute("emailSuccessful", isEmailSent);
        request.setAttribute("emailLog", emailLog);
        session.removeAttribute("emailAttachmentList");
        return mapping.findForward("success");
    }
}
