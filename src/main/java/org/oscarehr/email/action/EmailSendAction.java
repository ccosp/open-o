package org.oscarehr.email.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.model.EmailAttachment;
import org.oscarehr.common.model.EmailLog;
import org.oscarehr.common.model.EmailLog.EmailStatus;
import org.oscarehr.email.core.Email;
import org.oscarehr.managers.EformDataManager;
import org.oscarehr.managers.EmailManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

public class EmailSendAction extends DispatchAction {
    private static final Logger logger = MiscUtils.getLogger();
    private EmailManager emailManager = SpringUtils.getBean(EmailManager.class);
    private EformDataManager eformDataManager = SpringUtils.getBean(EformDataManager.class);

    public ActionForward sendEFormEmail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        Email email = prepareEmailFields(request);
        EmailLog emailLog = emailManager.sendEmail(email);
        if (!emailLog.getStatus().equals(EmailStatus.SUCCESS)) { 
            request.setAttribute("isEmailSuccessful", false); 
        } else {
            request.setAttribute("isEmailSuccessful", true);
            if (request.getParameter("deleteEFormAfterEmail") != null && "true".equalsIgnoreCase(request.getParameter("deleteEFormAfterEmail"))) { eformDataManager.removeEFormData(loggedInInfo, request.getParameter("fdid")); }
        }
        request.setAttribute("isOpenEForm", request.getParameter("openEFormAfterEmail"));
        request.setAttribute("fdid", request.getParameter("fdid"));
        request.setAttribute("emailLog", emailLog);
        return mapping.findForward("success");
    }

    private Email prepareEmailFields(HttpServletRequest request) {
        String fromEmail = request.getParameter("senderEmailAddress");
        String[] receiverEmails = request.getParameterValues("receiverEmailAddress");
        String subject = request.getParameter("subjectEmail");
        String body = request.getParameter("bodyEmail");
        String encryptedMessage = request.getParameter("encryptedMessage");
        String password = request.getParameter("emailPDFPassword");
        String passwordClue = request.getParameter("emailPDFPasswordClue");
        String isEncrypted = request.getParameter("isEmailEncrypted");
        String isAttachmentEncrypted = request.getParameter("isEmailAttachmentEncrypted");
        String chartDisplayOption = request.getParameter("patientChartOption");
        String documentGenerationOption = request.getParameter("emailPDFOption");
        String transactionType = request.getParameter("transactionType");
        String demographicNo = request.getParameter("demographicId");
        List<EmailAttachment> emailAttachmentList = (List<EmailAttachment>) request.getSession().getAttribute("emailAttachmentList");

        Email email = new Email();
        email.setSender(fromEmail);
        email.setRecipients(receiverEmails);
        email.setSubject(subject);
        email.setBody(StringEscapeUtils.escapeHtml(body));
        email.setEncryptedMessage(StringEscapeUtils.escapeHtml(encryptedMessage));
        email.setPassword(password);
        email.setPasswordClue(passwordClue);
        email.setIsEncrypted(isEncrypted);
        email.setIsAttachmentEncrypted(isAttachmentEncrypted);
        email.setChartDisplayOption(chartDisplayOption);
        email.setDocumentGenerationOption(documentGenerationOption);
        email.setTransactionType(transactionType);
        email.setDemographicNo(demographicNo);
        email.setAttachments(emailAttachmentList);

        request.getSession().removeAttribute("emailAttachmentList");

        return email;
    }
}
