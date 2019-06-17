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
package org.oscarehr.managers;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.oscarehr.common.dao.MessageListDao;
import org.oscarehr.common.dao.MessageTblDao;
import org.oscarehr.common.dao.OscarCommLocationsDao;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.dao.ProviderDataDao;
import org.oscarehr.common.model.ProviderData;
import org.oscarehr.common.model.MessageList;
import org.oscarehr.common.model.MessageTbl;
import org.oscarehr.common.model.MsgDemoMap;
import org.oscarehr.common.model.OscarCommLocations;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.oscarehr.util.SpringUtils;
import oscar.log.LogAction;
import oscar.oscarMessenger.data.ContactIdentifier;
import oscar.oscarMessenger.data.MessengerSystemMessage;
import oscar.oscarMessenger.data.MsgDisplayMessage;
import oscar.oscarMessenger.data.MsgMessageData;
import oscar.oscarMessenger.data.MsgProviderData;


@Service
public class MessagingManager {

	@Autowired
	private MessageListDao messageListDao;
	@Autowired
    private MessageTblDao messageTblDao;
	@Autowired
	private SecurityInfoManager securityInfoManager;
	@Autowired
	private OscarCommLocationsDao oscarCommLocationsDao;
	@Autowired
	private MessengerDemographicManager messengerDemographicManager;
			
	/**
	 * PREFERRED METHOD
	 * Get the entire inbox for the logged-in provider only.
	 * Results can be reduced by specifying the message status and/or a result limit. 
	 * @param loggedInInfo
	 * @param providerNo
	 * @param messageStatus
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<MsgDisplayMessage> getInbox(LoggedInInfo loggedInInfo, String messageStatus, int offset, int limit) {
    	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.READ, null)) {
			throw new SecurityException("missing required security object (_msg)");
		}
    	
    	List<MessageList> messageList = getMyInboxMessages(loggedInInfo, loggedInInfo.getLoggedInProviderNo(), messageStatus, offset, limit);
    	List<MsgDisplayMessage> inboxMessages = new ArrayList<MsgDisplayMessage>();
    	for(MessageList messageItem : messageList)
    	{
    		MsgDisplayMessage msgDisplayMessage = getInboxMessage(loggedInInfo, (int) messageItem.getMessage());
    		inboxMessages.add(msgDisplayMessage);
    	}
    	
    	return inboxMessages;
	}
	
	public MsgDisplayMessage getInboxMessage(LoggedInInfo loggedInInfo, int messageId) {
    	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.READ, null)) {
			throw new SecurityException("missing required security object (_msg)");
		}
    	
		MessageTbl messageTbl = getMessage(loggedInInfo, messageId);
		MsgDisplayMessage msgDisplayMessage = new MsgDisplayMessage();
		String attachedDemographics = messengerDemographicManager.getAttachedDemographicNamesAndAges(loggedInInfo, messageTbl.getId());

		String sentDate = messageTbl.getDate().toString();
		String sentTime = messageTbl.getTime().toString();

		Integer messageType = messageTbl.getType();
		if(messageType == null) {
			messageType = 0;
		}
		
		msgDisplayMessage.setStatus(messageTbl.getActionStatus());
		msgDisplayMessage.setMessageId(messageTbl.getId()+"");
		msgDisplayMessage.setSentby(messageTbl.getSentBy());
		msgDisplayMessage.setSentto(messageTbl.getSentTo());
		msgDisplayMessage.setThedate(sentDate);
		msgDisplayMessage.setThetime(sentTime);
		msgDisplayMessage.setThesubject(messageTbl.getSubject());
		msgDisplayMessage.setNameage(attachedDemographics);
		msgDisplayMessage.setMessageBody(messageTbl.getMessage());
		msgDisplayMessage.setType(messageType);
		msgDisplayMessage.setTypeLink(messageTbl.getType_link());
		
		String attachment = messageTbl.getAttachment();
		byte[] pdfAttachment = messageTbl.getPdfAttachment();
		
		if (attachment == null || "".equals(attachment)) {
			msgDisplayMessage.setAttach("0");
		} else {
			msgDisplayMessage.setAttach("1");
		}
		if ( pdfAttachment == null) {
			msgDisplayMessage.setPdfAttach("0");
		} else {
			msgDisplayMessage.setPdfAttach("1");
		}
		
		return msgDisplayMessage;
	}
	
	public MessageTbl getMessage(LoggedInInfo loggedInInfo, int messageId) {
    	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.READ, null)) {
			throw new SecurityException("missing required security object (_msg)");
		}
		return messageTblDao.find(messageId);
	}
	
	public List<MessageList> getMyInboxMessages(LoggedInInfo loggedInInfo, String providerNo, int offset, int limit) {	
    	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.READ, null)) {
			throw new SecurityException("missing required security object (_msg)");
		}
    	
		return getMyInboxMessages(loggedInInfo, providerNo, null, offset, limit);
	}
	
	public List<MessageList> getMyInboxMessages(LoggedInInfo loggedInInfo, String providerNo, String status, int offset, int limit) {		
    	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.READ, null)) {
			throw new SecurityException("missing required security object (_msg)");
		}
    	
		List<MessageList> msgs = messageListDao.search(providerNo, status, offset, limit);
		
		for(MessageList msg:msgs) {
	        	LogAction.addLogSynchronous(loggedInInfo, "MessagingManager.getMyInboxMessages", "msglistid="+msg.getId());
		}
		 
		return msgs;
	}

	public Integer getMyInboxMessagesCount(LoggedInInfo loggedInInfo, String providerNo, String status) {
    	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.READ, null)) {
			throw new SecurityException("missing required security object (_msg)");
		}
    	
		Integer result = messageListDao.searchAndReturnTotal(providerNo, status);
		 
		return result;
	} 
	
	public int getMyInboxMessageCount(LoggedInInfo loggedInInfo, String providerNo, boolean onlyWithPatientAttached) {		
    	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.READ, null)) {
			throw new SecurityException("missing required security object (_msg)");
		}
    	
		if(!onlyWithPatientAttached) {
			return getMyInboxMessagesCount(loggedInInfo, providerNo, MessageList.STATUS_NEW);
		} else {
			return messageListDao.findUnreadByProviderAndAttachedCount(providerNo);
		}	
	}
		
	/**
	 * Get the count of all messages attached to the given demographic Id.
	 * @param loggedInInfo
	 * @param demographicNo
	 * @return
	 */
	public int getInboxCountByDemographicNo(LoggedInInfo loggedInInfo, int demographicNo) {
		List<MsgDemoMap> demoMap = messengerDemographicManager.getMessageMapByDemographicNo(loggedInInfo, demographicNo);
		if(demoMap != null) 
		{
			return demoMap.size();
		}
		return 0;
	}
	
	/**
	 * Change the status of a message (sent, read, del, new)
	 * @param loggedInInfo
	 * @param messageId
	 * @param status
	 */
	public Long setMessageStatus(LoggedInInfo loggedInInfo, MessageList messageList, String status) {
	   	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.UPDATE, null)) {
				throw new SecurityException("missing required security object (_msg)");
		}

   		messageList.setStatus(status);
		messageListDao.merge(messageList);
		return messageList.getMessage();
	}
	
	/**
	 * Set the message opened by this local provider from new to read. Matching provider numbers from 
	 * remote locations will be ignored.
	 * 
	 * @param loggedInInfo
	 * @param messageId
	 * @param providerNo
	 */
	public void setMessageRead(LoggedInInfo loggedInInfo, Long messageId, String providerNo) {
		List<MessageList> messageList = messageListDao.findByProviderNoAndMessageNo(providerNo, messageId);
		for(MessageList message : messageList)
		{
			if(! MessageList.STATUS_DELETED.equalsIgnoreCase(message.getStatus()) 
					&& ! MessageList.STATUS_SENT.equalsIgnoreCase(message.getStatus())
					&& message.getDestinationFacilityId() < 1) 
	   		{
				setMessageStatus(loggedInInfo, message, MessageList.STATUS_READ);
		   	}
		}
	}
	
	public void deleteMessage(LoggedInInfo loggedInInfo, int messageId) {
		List<MessageList> messageList = messageListDao.findByMessage((long) messageId);
		for(MessageList message : messageList)
		{
			setMessageStatus(loggedInInfo, message, MessageList.STATUS_DELETED);
		}
	}
	
	public String getLabRecallMsgSubjectPref(LoggedInInfo loggedInInfo){
    	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.READ, null)) {
			throw new SecurityException("missing required security object (_msg)");
		}
    	  	
		String subject = "";
		String providerNo = loggedInInfo.getLoggedInProviderNo();
		
	    	UserPropertyDAO userPropertyDao = SpringUtils.getBean(UserPropertyDAO.class);
	    	UserProperty prop = userPropertyDao.getProp(providerNo, UserProperty.LAB_RECALL_MSG_SUBJECT);
	    	
	    	if(prop!=null){
	    		subject = prop.getValue();
	    	}
    	
		return subject;
	}
	
	public String getLabRecallDelegatePref(LoggedInInfo loggedInInfo){
    	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.READ, null)) {
			throw new SecurityException("missing required security object (_msg)");
		}
    	
		String delegate = "";
		String providerNo = loggedInInfo.getLoggedInProviderNo();

	    	UserPropertyDAO userPropertyDao = SpringUtils.getBean(UserPropertyDAO.class);
	    	UserProperty prop = userPropertyDao.getProp(providerNo, UserProperty.LAB_RECALL_DELEGATE);
	    	
	    	if(prop!=null){
	    		delegate = prop.getValue();		
	    	}

		return delegate;
	}
	
	public String getDelegateName(String delegate){

		String delegateName = "";
		ProviderDataDao dao = SpringUtils.getBean(ProviderDataDao.class);
		ProviderData pd = dao.findByProviderNo(delegate);
		
		delegateName = pd.getLastName() + ", " + pd.getFirstName();
			
		return delegateName;
	}
	
    /**
     * Use this method for sending messages from the Oscar system to Oscar users. 
     */
    public Integer sendSystemMessage(LoggedInInfo loggedInInfo, MessengerSystemMessage systemMessage) {
    	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.WRITE, null)) {
			throw new SecurityException("missing required security object (_msg)");
		}

    	if(systemMessage.getRecipients() == null || systemMessage.getRecipients().length == 0) {
    		// no recipients
    		return null;
    	}
    	
    	//TODO transition from using the MsgMessageData Object.
    	MsgMessageData messageData = new MsgMessageData();
		String[] recipients = removeDuplicates(systemMessage.getRecipients());
    	
    	ArrayList<MsgProviderData> providerListing = messageData.getProviderStructure(loggedInInfo, recipients);
        String sentToWho = messageData.createSentToString(providerListing);
    	ArrayList<MsgProviderData> remoteProviderListing = messageData.getRemoteProvidersStructure();
        sentToWho = sentToWho + " " + messageData.getRemoteNames(remoteProviderListing);
        Integer messageIdInteger = null;
        String messageId = messageData.sendMessageReview(
        		systemMessage.getMessage(), 
        		systemMessage.getSubject(),
        		systemMessage.getSentBy(),
        		sentToWho,
        		systemMessage.getSentByNo(),
        		providerListing,
        		systemMessage.getAttachment(), 
        		systemMessage.getPdfAttachment(), 
        		systemMessage.getType(),
        		systemMessage.getType_link());

    	if( messageId != null && systemMessage.getAttachedDemographicNo() != null) { 
    		messageIdInteger = Integer.parseInt(messageId);
    		messengerDemographicManager.attachDemographicToMessage(loggedInInfo, messageIdInteger, systemMessage.getAttachedDemographicNo());
    	}
    	
    	return messageIdInteger;
    }
       
    /**
     * Attach all providers that should receive this message. 
     * @param loggedInInfo
     * @param messageId
     * @param recipients
     * @param destinationFacility
     * @param sourceFacilityId
     */
	public void addRecipientsToMessage(LoggedInInfo loggedInInfo, int messageId, ContactIdentifier[] contactIdentifier) {
    	for(ContactIdentifier contact : contactIdentifier) {
    		addRecipientToMessage(loggedInInfo, messageId, contact);
    	}
    }
	
    public void addRecipientToMessage(LoggedInInfo loggedInInfo, int messageId, ContactIdentifier contactIdentifier) {
    	addRecipientToMessage(loggedInInfo, messageId, contactIdentifier.getContactId(), contactIdentifier.getClinicLocationNo(), contactIdentifier.getFacilityId());
    }
    
    public void addRecipientsToMessage(LoggedInInfo loggedInInfo, int messageId, String[] providerNoArray, int clinicLocationNo, int facilityId, int sourceFacilityId) {
    	providerNoArray = removeDuplicates(providerNoArray);
    	for(String providerNo : providerNoArray)
    	{
    		addRecipientToMessage(loggedInInfo, messageId, providerNo, clinicLocationNo, facilityId, sourceFacilityId);		
    	}
    }
    
    public void addRecipientToMessage(LoggedInInfo loggedInInfo, int messageId, String providerNo, int clinicLocationNo, int facilityId, int sourceFacilityId) {
    	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.WRITE, null)) {
			throw new SecurityException("missing required security object (_msg)");
		}
    	
		MessageList messageList = new MessageList();
		messageList.setMessage(messageId);
		messageList.setProviderNo(providerNo);
		messageList.setStatus(MessageList.STATUS_NEW);
		messageList.setRemoteLocation(clinicLocationNo);
		messageList.setDestinationFacilityId(facilityId);
		messageList.setSourceFacilityId(sourceFacilityId);
		messageListDao.persist(messageList);
    }

    public void addRecipientToMessage(LoggedInInfo loggedInInfo, int messageId, String providerNo, int clinicLocationNo, int facilityId) {
    	addRecipientToMessage(loggedInInfo, messageId, providerNo, clinicLocationNo, facilityId, 0);
    }

    /**
     * A combined result of both the local reply recipients and recipients located in remote 
     * facilities.
     */
    public List<ContactIdentifier> getAllMessageReplyRecipients(LoggedInInfo loggedInInfo, int messageId) {
       	List<ContactIdentifier> contactIdentifierList = new ArrayList<ContactIdentifier>();
       	contactIdentifierList.addAll(getAllLocalReplyRecipients(loggedInInfo, messageId));
       	contactIdentifierList.addAll(getAllRemoteReplyRecipients(loggedInInfo, messageId));
       	return contactIdentifierList; 
    }
    
    /**
     * Recipients that were copied in on the message that have an origin in the client from where 
     * the reply is originating. 
     * 
     * @param loggedInInfo
     * @param messageId
     * @return
     */
    public List<ContactIdentifier> getAllLocalReplyRecipients(LoggedInInfo loggedInInfo, int messageId) {
       	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.READ, null)) {
    		throw new SecurityException("missing required security object (_msg)");
    	}
       	
       	List<MessageList> messageList = messageListDao.findByMessageNoAndLocationNo((long) messageId, getCurrentLocationId());
       	List<ContactIdentifier> contactIdentifierList = new ArrayList<ContactIdentifier>();
       	for(MessageList message : messageList) {
       		if(! loggedInInfo.getLoggedInProviderNo().equals(message.getProviderNo()))
       		{
	       		ContactIdentifier contactIdentifier = new ContactIdentifier();
	       		contactIdentifier.setClinicLocationNo(message.getRemoteLocation());
	       		contactIdentifier.setContactId(message.getProviderNo());
	       		contactIdentifier.setFacilityId(0);
	       		contactIdentifierList.add(contactIdentifier);
       		}
       	}
       	return contactIdentifierList;
    }
    
    /**
     * Recipients that were copied in on the message but have an origin in one of the included 
     * remote facilities. 
     * 
     * @param loggedInInfo
     * @param messageId
     * @return
     */
    public List<ContactIdentifier> getAllRemoteReplyRecipients(LoggedInInfo loggedInInfo, int messageId) {
       	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.READ, null)) {
    		throw new SecurityException("missing required security object (_msg)");
    	}
       	
    	List<MessageList> messageList = messageListDao.findByMessage((long) messageId);
       	List<ContactIdentifier> contactIdentifierList = new ArrayList<ContactIdentifier>();
       	int currentLocationId = getCurrentLocationId();
       	for(MessageList message : messageList) {  
       		if(message.getRemoteLocation() != currentLocationId)
       		{
	       		ContactIdentifier contactIdentifier = new ContactIdentifier();
	       		contactIdentifier.setClinicLocationNo(message.getRemoteLocation());
	       		contactIdentifier.setContactId(message.getProviderNo());
	       		contactIdentifier.setFacilityId(message.getSourceFacilityId());
	       		contactIdentifierList.add(contactIdentifier);
       		}
       	}
       	return contactIdentifierList;
    }
    
    /**
     * Save the message content.
     * 
     * @param loggedInInfo
     * @param messageTbl
     * @return
     */
    public Integer saveMessage(LoggedInInfo loggedInInfo, MessageTbl messageTbl) {
    	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.WRITE, null)) {
			throw new SecurityException("missing required security object (_msg)");
		}
    	
    	messageTblDao.persist(messageTbl);
    	return messageTbl.getId();
    }
    
    /**
     * Get the current address book location id for this clinic from the oscarcommlocations table. 
     * 
     * @return
     */
	public int getCurrentLocationId() {
		int currentLocationId = 0;
    	List<OscarCommLocations> oscarCommLocations = oscarCommLocationsDao.findByCurrent1(1);
    	Integer oscarCommLocationsID = null;
    	
    	if(oscarCommLocations != null) 
    	{
    		oscarCommLocationsID = oscarCommLocations.get(0).getId();
    	}
    	
    	if(oscarCommLocationsID != null)
    	{
    		currentLocationId = oscarCommLocationsID;
    	}  
		return currentLocationId;
	}
	
	/**
	 * remove duplicate values from any string array.
	 * @param strarray
	 * @return
	 */
	public String[] removeDuplicates(String[] strarray){	  
	   List<String> arrayList = new ArrayList<String>(Arrays.asList(strarray));
	   Set<String> hashSet = new HashSet<String>(arrayList);
	   String[] outputArray = new String[hashSet.size()];
	   return hashSet.toArray(outputArray);
	}
	
}
