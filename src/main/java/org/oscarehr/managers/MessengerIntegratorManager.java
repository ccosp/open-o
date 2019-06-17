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
    	long messageId = messageList.getMessage();
    	MessageTbl messageTbl = messagingManager.getMessage(loggedInInfo, (int) messageId);
		ProviderCommunicationTransfer providerCommunication = new ProviderCommunicationTransfer();
		List<MsgDemoMap> msgDemoMap = messengerDemographicManager.getAttachedDemographicList(loggedInInfo, (int) messageId);
		
		/* temporary work around through use of a JSONObject. This could be a future upgrade on the Integrator 
		 * side if inter-facility messaging becomes more popular.
		 */
		JSONObject jsonString = new JSONObject();
		jsonString.put(JSON_KEY.subject.name(), messageTbl.getSubject());
		jsonString.put(JSON_KEY.message.name(), messageTbl.getMessage());
		jsonString.put(JSON_KEY.from.name(), messageTbl.getSentBy());
		jsonString.put(JSON_KEY.copyto.name(), messageTbl.getSentTo());
		
		if(msgDemoMap != null && ! msgDemoMap.isEmpty() && msgDemoMap.size() > 0)
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
		
		/*
		 * it's possible to have more than one sender/reciever in the source.
		 * The first provider id is the message creator - located in the messageTbl.sentByNo column. 
		 * 
		 * for the moment it's assumed that "this facility" has an Id of zero (0).
		 * This can be changed once the assignment of facility id's is stablized in future changes to the 
		 * Integrator functionality. 
		 */ 		 
		List<MessageList> sourceProviderList = messageListDao.findByMessageAndIntegratedFacility(messageId, 0);
		List<String> sourceProviderIds = new ArrayList<String>();
		sourceProviderIds.add(messageTbl.getSentByNo());
		for(MessageList sourceProvider : sourceProviderList) 
		{
			sourceProviderIds.add(sourceProvider.getProviderNo());
		}
		providerCommunication.setSourceProviderId(StringUtils.join(sourceProviderIds,","));
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
		String[] sourceProviderIdList = null;
		String sourceProviderIds = "";
		Integer messageId = null;
		Integer sourceFacilityId = null;
		
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
			
			/*
			 * The source provider id column may also contain any other providers that were copied in 
			 * at the source. 
			 * The originator of the message will always be the first entry. 
			 * Anyone is welcome to do this a better way.
			 */
			sourceProviderIds = providerCommunication.getSourceProviderId();			
			if(sourceProviderIds.contains(",")) 
			{
				sourceProviderIdList = sourceProviderIds.split(",");
			}
			else
			{
				sourceProviderIdList = new String[] {sourceProviderIds};
			}
			
			if(sourceProviderIdList != null && sourceProviderIdList.length > 0)
			{
				messageTbl.setSentByNo(sourceProviderIdList[0]);				
			}
			
			sourceFacilityId = providerCommunication.getSourceIntegratorFacilityId();
			
			messageTbl.setSentByLocation(sourceFacilityId);
			messageTbl.setSentTo(copyto);
			messageTbl.setSubject(subject);
			messageTbl.setMessage(message);
			messageTbl.setType(Integer.parseInt(providerCommunication.getType()));
		
			/*
			 * Save the actual message and then populate the associated tables for 
			 * additional recipients and the related demogaphic chart.
			 */
			messageId = messagingManager.saveMessage(loggedInInfo, messageTbl);			
			if(messageId != null && messageId > 0)
			{
				
				/*
				 *  add additional providers from other facilities for 
				 *  whom the message was copied to.
				 */				
				String[] providerIds = new String[] {};
				
				if(providerCommunication.getDestinationProviderId().contains(",")) 
				{
					providerIds = providerCommunication.getDestinationProviderId().split(",");
				}
				else
				{
					providerIds = new String[] {providerCommunication.getDestinationProviderId()};
				}

				/*
				 *  It's assumed that "this facility" has an Id of zero.
				 *  This can be changed once the assignment of facility id's is stablized in future changes to the 
				 *  Integrator functionality.
				 *  
				 *  The identifier for this clinic is set in the oscarcommlocations table. The ID is set to 145 by default.
				 *  Adding this is a requirement for the use of a XML address book. However it's unknown if the address book 
				 *  feature is still in use. Use of the currentlocation column may need to be reconsidered in the future.
				 *  
				 *  For now, it's important to use the currentlocation column to distinguish between a provider id that 
				 *  exists in the local clinic (145) or a remote clinic (>=0)
				 *   
				 */
				messagingManager.addRecipientsToMessage(loggedInInfo, messageId, providerIds, messagingManager.getCurrentLocationId(), 0, providerCommunication.getSourceIntegratorFacilityId());
				
				if(demographic != null && ! demographic.isEmpty()) 
				{
					messengerDemographicManager.attachIntegratedDemographicToMessage(loggedInInfo, messageId, Integer.parseInt(demographic), providerCommunication.getSourceIntegratorFacilityId(), 0);
				}
				
				receivedMessages.add(providerCommunication.getId());
			}
		}
		
		/*
		 *  add additional providers that are included from the source facility.
		 *  This list was captured in the loop above.
		 */
		
		if(sourceProviderIdList != null && sourceProviderIdList.length > 1)
		{
			messagingManager.addRecipientsToMessage(loggedInInfo, messageId, sourceProviderIdList, 0, 0, sourceFacilityId);			
		}
		
		return receivedMessages;
	}

	/**
	 * retrieves the actual id for this Integrated facility.
	 * 
	 * @param loggedInInfo
	 * @return
	 */
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
