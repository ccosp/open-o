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
import org.oscarehr.email.core.Email;
import org.oscarehr.managers.EmailManager;
import org.oscarehr.util.EmailSendingException;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

public class EmailSendAction extends DispatchAction {
    private static final Logger logger = MiscUtils.getLogger();
    private EmailManager emailManager = SpringUtils.getBean(EmailManager.class);

    public ActionForward sendEmail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        Email email = prepareEmailParameter(request);
        EmailLog emailLog = new EmailLog();
        try {
            emailLog = emailManager.sendEmail(email);
            request.setAttribute("emailSuccessful", true);
        } catch (EmailSendingException e) {
            request.setAttribute("emailSuccessful", false);
            request.setAttribute("errorMessage", e.getMessage());
            logger.error(e.getMessage(), e);
        }
        
        request.setAttribute("emailLog", emailLog);
        return mapping.findForward("success");
    }

    private Email prepareEmailParameter(HttpServletRequest request) {
        HttpSession session = request.getSession();
        List<EmailAttachment> emailAttachmentList = (List<EmailAttachment>) session.getAttribute("emailAttachmentList");
        String fromEmail = request.getParameter("senderEmailAddress");
        String toEmail = request.getParameter("receiverEmailAddress");
        String subject = request.getParameter("subjectEmail");
        String body = request.getParameter("bodyEmail");

        Email email = new Email();
        email.setSender(fromEmail);
        email.setRecipient(toEmail);
        email.setSubject(subject);
        email.setMessage(body);
        email.setAttachments(emailAttachmentList);

        session.removeAttribute("emailAttachmentList");

        return email;
    }
}
