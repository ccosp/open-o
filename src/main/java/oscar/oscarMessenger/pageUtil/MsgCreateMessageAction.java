/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


package oscar.oscarMessenger.pageUtil;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.OscarMsgType;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.managers.MessagingManager;
import org.oscarehr.managers.MessengerDemographicManager;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.oscarMessenger.data.ContactIdentifier;
import oscar.oscarMessenger.data.MsgProviderData;

public class MsgCreateMessageAction extends Action {

	private static UserPropertyDAO userPropertyDao = SpringUtils.getBean(UserPropertyDAO.class);
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private static MessengerDemographicManager messengerDemographicManager = SpringUtils.getBean(MessengerDemographicManager.class);

    public ActionForward execute(ActionMapping mapping,
				 ActionForm form,
				 HttpServletRequest request,
				 HttpServletResponse response)
	throws IOException, ServletException {
    	LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
    	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", "w", null)) {
			throw new SecurityException("missing required security object (_msg)");
		}
    	
            //FIXME no more sessions
            oscar.oscarMessenger.pageUtil.MsgSessionBean bean;
            bean = (oscar.oscarMessenger.pageUtil.MsgSessionBean)request.getSession().getAttribute("msgSessionBean");
            String userNo   = bean.getProviderNo();
            String userName = bean.getUserName();
            String att      = bean.getAttachment();
            String pdfAtt      = bean.getPDFAttachment();
            bean.nullAttachment();
            String message      = ((MsgCreateMessageForm)form).getMessage();
            String[] providers  = ((MsgCreateMessageForm)form).getProvider();
            String subject      = ((MsgCreateMessageForm)form).getSubject();
            bean.setMessage(null);
            bean.setSubject(null);
            
            MiscUtils.getLogger().debug("Providers: " + Arrays.toString(providers));
            MiscUtils.getLogger().debug("Subject: " + subject);
            MiscUtils.getLogger().debug("Message: " + message);
            
            String sentToWho    = null;
            String messageId    = null;
            String demographic_no = ((MsgCreateMessageForm)form).getDemographic_no();
            if (demographic_no != null && (demographic_no.equals("") || "null".equals(demographic_no)) ){           	
            	demographic_no = null;
            }

            java.util.ArrayList<MsgProviderData> providerListing, remoteProviderListing;


            subject.trim();
            if (subject.length() == 0) {subject = "none";}

            //FIXME remove these deprecated methods and use the Messenger Managers instead (the deprecated classes still use JDBC)
            oscar.oscarMessenger.data.MsgMessageData messageData = new oscar.oscarMessenger.data.MsgMessageData();
            providers               = messageData.getDups4(providers);              
            providerListing         = messageData.getProviderStructure(loggedInInfo, providers);
            
            //FIXME currently unused - I think...
            remoteProviderListing   = messageData.getRemoteProvidersStructure();
            
            /*
             * A demographic that has not consented to the Integrator cannot be sent to remote providers.
             * This is a short circuit that will return a warning that the patient has not consented.
             * This message will not send until the non-consenting patient has consented -or- is removed 
             * from the message.
             */
            if(demographic_no != null 
            		&& ("1".equals( userPropertyDao.getProp( UserProperty.INTEGRATOR_PATIENT_CONSENT ).getValue()) 
            				|| "1".equals( userPropertyDao.getProp( UserProperty.INTEGRATOR_DEMOGRAPHIC_CONSENT ).getValue())))
            {
            	if( MessagingManager.doesContainRemoteRecipient(loggedInInfo, providerListing)
            			&& ! messengerDemographicManager.isPatientConsentedForIntegrator(loggedInInfo, Integer.parseInt(demographic_no)))
            	{
            		return error(mapping, request, (MsgCreateMessageForm) form, "oscarMessenger.CreateMessage.patientConsentError");
            	}
            }
            
            /*
             * The Integrator does not support attachments at this time.  Stop attachments from being sent externally. 
             */
            if( (att != null || pdfAtt != null) &&  MessagingManager.doesContainRemoteRecipient(loggedInInfo, providerListing))
            {
            	return error(mapping, request, (MsgCreateMessageForm) form, "oscarMessenger.CreateMessage.attachmentsNotPermitted");
            }

            //FIXME remove these deprecated methods and use the Messenger Managers instead
            sentToWho = messageData.createSentToString(providerListing);
            sentToWho = sentToWho + " " + messageData.getRemoteNames(remoteProviderListing);
            sentToWho = sentToWho.trim();
            messageId = messageData.sendMessage2(message,subject,userName,sentToWho,userNo,providerListing,att, pdfAtt, OscarMsgType.GENERAL_TYPE);

            // link msg and demographic if both messageId and demographic_no are not null.
            if(messageId!=null && demographic_no!=null){
                messengerDemographicManager.attachDemographicToMessage(loggedInInfo, Integer.parseInt(messageId), Integer.parseInt(demographic_no));
            }

            request.setAttribute("SentMessageProvs",sentToWho.toString());

            return (mapping.findForward("success"));
    }
    
    private ActionForward error(ActionMapping mapping, HttpServletRequest request, MsgCreateMessageForm form, String messageKey) {
    		MessageResources msgResource = getResources(request);
    		String message = msgResource.getMessage(messageKey);
			request.setAttribute("createMessageError", message);
			request.setAttribute("messageSubject", form.getSubject());
			request.setAttribute("messageBody", form.getMessage());
			request.setAttribute("demographic_no", form.getDemographic_no());
			List<ContactIdentifier> replyList = MessagingManager.createContactIdentifierList(form.getProvider());
            JSONArray jsonArray = new JSONArray();
			request.setAttribute("replyList", jsonArray.addAll(replyList));
	    	return mapping.findForward("error");
    }

}
