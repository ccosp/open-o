package org.oscarehr.email.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.model.EmailAttachment;
import org.oscarehr.util.MiscUtils;

public class EmailSendAction extends DispatchAction {
    private static final Logger logger = MiscUtils.getLogger();

    public ActionForward sendEmail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        List<EmailAttachment> emailAttachmentList = (List<EmailAttachment>) request.getAttribute("emailAttachments");
        if (emailAttachmentList != null) {
            System.out.println(emailAttachmentList.size());
        }

        if (request.getAttribute("emailAttachments") != null) {
            System.out.println("hi");
        }
        
        request.setAttribute("emailSuccessful", true);
        return mapping.findForward("success");
    }
}
