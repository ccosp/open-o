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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.PMmodule.caisi_integrator.CaisiIntegratorManager;
import org.oscarehr.caisi_integrator.util.MiscUtils;
import org.oscarehr.caisi_integrator.ws.CachedFacility;
import org.oscarehr.caisi_integrator.ws.ProviderCommunicationTransfer;
import org.oscarehr.common.dao.MessageListDao;
import org.oscarehr.common.model.Facility;
import org.oscarehr.common.model.MessageList;
import org.oscarehr.common.model.MessageTbl;
import org.oscarehr.common.model.MsgDemoMap;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;

/**
 * FOR USE WITH INTEGRATOR MESSAGING ONLY
 *
 */
@Service
public class MessengerIntegratorManager {
	
	private enum JSON_KEY{subject, message, from, copyto, demographicNo}
	
	@Autowired
	private MessageListDao messageListDao;
	@Autowired
	private SecurityInfoManager securityInfoManager;
	@Autowired
	private FacilityManager facilityManager;
	@Autowired
	private MessagingManager messagingManager;
	@Autowired
	private MessengerDemographicManager messengerDemographicManager;

	/**
	 * Retrieve all Integrator destined messages by Facility profile.  The messages will be fetched based on the 
	 * last Facility update date attribute.
	 * These messages are being fetched from the local facility for sending to the Integrator.
	 * 
	 * @param loggedInInfo
	 * @param facilities
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public List<ProviderCommunicationTransfer> getIntegratedMessages(LoggedInInfo loggedInInfo, List<org.oscarehr.caisi_integrator.ws.CachedFacility> facilities, String status) 
			throws UnsupportedEncodingException {
    	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.READ, null)) {
			throw new SecurityException("missing required security object (_msg)");
		}
    	
    	List<ProviderCommunicationTransfer> integratedMessageList = new ArrayList<ProviderCommunicationTransfer>();

    	for(org.oscarehr.caisi_integrator.ws.CachedFacility facility : facilities) 
    	{
    		List<ProviderCommunicationTransfer> messageList = getIntegratedMessages(loggedInInfo, facility, status);   		
    		if(messageList != null && ! messageList.isEmpty())
    		{
        		integratedMessageList.addAll(messageList);
    		}
    	}
    	
    	return integratedMessageList;
	}
	
	/**
	 * @param loggedInInfo
	 * @param facility
	 * @param status
	 * @return List<ProviderCommunicationTransfer> or NULL for no results.
	 * @throws UnsupportedEncodingException 
	 */
	public List<ProviderCommunicationTransfer> getIntegratedMessages(LoggedInInfo loggedInInfo, org.oscarehr.caisi_integrator.ws.CachedFacility facility, String status) 
			throws UnsupportedEncodingException {
    	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.READ, null)) {
			throw new SecurityException("missing required security object (_msg)");
		}
    	
    	List<MessageList> integratedMessageList = messageListDao.findByIntegratedFacility(facility.getIntegratorFacilityId(), status);    	
    	if(integratedMessageList.isEmpty())
    	{
    		return null;
    	}
    	
    	// Sort the selected integrated messages into groups of provider id's by message id.
    	Map<Long, MessageList> messageIds = new HashMap<Long, MessageList>();
    	for(MessageList integratedMessage : integratedMessageList)
    	{
    		messageIds.put(integratedMessage.getMessage(), integratedMessage);
    	}
    	
    	Map<Long, List<String>> messageProviderMap = null; 
    	for(Long messageId : messageIds.keySet())
    	{
    		List<MessageList> messageList = messageListDao.findByMessageAndIntegratedFacility(messageId, facility.getIntegratorFacilityId());
    		if(messageList.size() > 1)
    		{
    			List<String> providerIds = new ArrayList<String>();
    			if(messageProviderMap == null)
    			{
    				messageProviderMap = new HashMap<Long, List<String>>();
    			}

	    		for(MessageList message : messageList) 
	    		{
	    			providerIds.add(message.getProviderNo());
	    		}
	    		
	    		messageProviderMap.put(messageId, providerIds);
    		}
    	}

    	List<ProviderCommunicationTransfer> providerCommunicationTransferList = new ArrayList<ProviderCommunicationTransfer>();   	
    	for(MessageList message : messageIds.values())
    	{
    		ProviderCommunicationTransfer providerCommunicationTransfer = getIntegratedMessage(loggedInInfo, message);
    		
    		// override the current provider number if there is a list of provider numbers available.
    		if(messageProviderMap != null) {
    			List<String> sendTo = messageProviderMap.get(message.getMessage());
    			providerCommunicationTransfer.setDestinationProviderId(StringUtils.join(sendTo,","));
    		}
    		
    		providerCommunicationTransferList.add(providerCommunicationTransfer);
    	}
    	
    	// set the status of each message to sent
    	for(MessageList message : integratedMessageList)
    	{
   		messagingManager.setMessageStatus(loggedInInfo, message, MessageList.STATUS_SENT);
    	}
    	
    	return providerCommunicationTransferList;
	}
	
	/**
	 * @param loggedInInfo
	 * @param messageList
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public ProviderCommunicationTransfer getIntegratedMessage(LoggedInInfo loggedInInfo, MessageList messageList) 
			throws UnsupportedEncodingException {
    	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.READ, null)) {
			throw new SecurityException("missing required security object (_msg)");
		}

    	MessageTbl messageTbl = messagingManager.getMessage(loggedInInfo, (int) messageList.getMessage());
		ProviderCommunicationTransfer providerCommunication = new ProviderCommunicationTransfer();
		List<MsgDemoMap> msgDemoMap = messengerDemographicManager.getAttachedDemographicList(loggedInInfo, (int) messageList.getMessage());
		
		/* temporary work around with the JSONObject. This could be a future upgrade on the Integrator 
		 * side if inter-facility messaging becomes popular.
		 */
		JSONObject jsonString = new JSONObject();
		jsonString.put(JSON_KEY.subject.name(), messageTbl.getSubject());
		jsonString.put(JSON_KEY.message.name(), messageTbl.getMessage());
		jsonString.put(JSON_KEY.from.name(), messageTbl.getSentBy());
		jsonString.put(JSON_KEY.copyto.name(), messageTbl.getSentTo());
		
		if(msgDemoMap != null && msgDemoMap.isEmpty() && msgDemoMap.size() > 0)
		{
			jsonString.put(JSON_KEY.demographicNo.name(), msgDemoMap.get(0).getDemographic_no());
		}

		Calendar sentDate = Calendar.getInstance();
		sentDate.setTime(messageTbl.getDate());
		providerCommunication.setActive(true);
		providerCommunication.setDestinationIntegratorFacilityId(messageList.getDestinationFacilityId());
		providerCommunication.setDestinationProviderId(messageList.getProviderNo());
		providerCommunication.setSentDate(sentDate);
		providerCommunication.setSourceIntegratorFacilityId(getThisFacilityId(loggedInInfo));
		providerCommunication.setSourceProviderId(messageTbl.getSentByNo());
		providerCommunication.setType(messageTbl.getType()+"");	
		providerCommunication.setData(jsonString.toString().getBytes("UTF-8"));

    	return providerCommunication; 
    }
	
	/**
	 * Save messages from the Integrator into the Oscar messenger inbox.
	 * @param loggedInInfo
	 * @param providerCommunicationList
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public List<Integer> receiveIntegratedMessages(LoggedInInfo loggedInInfo, List<ProviderCommunicationTransfer> providerCommunicationList) 
			throws UnsupportedEncodingException {
    	if(!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", SecurityInfoManager.WRITE, null)) {
			throw new SecurityException("missing required security object (_msg)");
		}
    	
    	List<Integer> receivedMessages = new ArrayList<Integer>();
    	
		for(ProviderCommunicationTransfer providerCommunication : providerCommunicationList)
		{
			MessageTbl messageTbl = new MessageTbl();
			String messageString = new String(providerCommunication.getData(), "UTF-8");
			JSONObject jsonObject = JSONObject.fromObject(messageString);
			String subject = jsonObject.getString(JSON_KEY.subject.name());
			String message = jsonObject.getString(JSON_KEY.message.name());
			String from = jsonObject.getString(JSON_KEY.from.name());
			String copyto = jsonObject.getString(JSON_KEY.copyto.name());
			String demographic = null; 
			if(jsonObject.containsKey(JSON_KEY.demographicNo.name()))
			{
				demographic = jsonObject.getString(JSON_KEY.demographicNo.name());
			}

			messageTbl.setDate(providerCommunication.getSentDate().getTime());
			messageTbl.setTime(providerCommunication.getSentDate().getTime());
			messageTbl.setSentBy(from);
			messageTbl.setSentByNo(providerCommunication.getSourceProviderId());
			messageTbl.setSentTo(copyto);
			messageTbl.setSubject(subject);
			messageTbl.setMessage(message);
			messageTbl.setType(Integer.parseInt(providerCommunication.getType()));
			messageTbl.setSentByLocation(providerCommunication.getSourceIntegratorFacilityId());
			
			Integer messageId = messagingManager.saveMessage(loggedInInfo, messageTbl);
			
			if(messageId != null && messageId > 0)
			{
				String[] providerIds = new String[] {};
				if(providerCommunication.getDestinationProviderId().contains(",")) 
				{
					providerIds = providerCommunication.getDestinationProviderId().split(",");
				}
				else
				{
					providerIds = new String[] {providerCommunication.getDestinationProviderId()};
				}

				messagingManager.addRecipientsToMessage(loggedInInfo, messageId, providerIds, messagingManager.getCurrentLocationId(), 0, providerCommunication.getSourceIntegratorFacilityId());
				
				if(demographic != null && ! demographic.isEmpty()) 
				{
					messengerDemographicManager.attachIntegratedDemographicToMessage(loggedInInfo, messageId, Integer.parseInt(demographic), providerCommunication.getSourceIntegratorFacilityId(), 0);
				}
				
				receivedMessages.add(providerCommunication.getId());
			}
		}
		
		return receivedMessages;
	}

    public int getThisFacilityId(LoggedInInfo loggedInInfo) {
    	Facility thisFacility = facilityManager.getDefaultFacility(loggedInInfo);
    	int facilityId = 0;
    	CachedFacility cachedFacility = null;
    	
    	if(thisFacility != null && thisFacility.isIntegratorEnabled())
    	{
	    	try {
				cachedFacility = CaisiIntegratorManager.getCurrentRemoteFacility(loggedInInfo, thisFacility);
			} catch (MalformedURLException e) {
				MiscUtils.getLogger().error("Error getting facility ID ", e);
			}
    	}
    	
    	if(cachedFacility != null)
    	{
    		facilityId = cachedFacility.getIntegratorFacilityId();
    	}
    	
    	return facilityId;
    }
}
