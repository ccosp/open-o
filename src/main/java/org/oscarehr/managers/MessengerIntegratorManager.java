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
import java.util.Iterator;
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
import org.oscarehr.common.model.OscarMsgType;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * FOR USE WITH INTEGRATOR MESSAGING ONLY
 *
 */
@Service
public class MessengerIntegratorManager {
	
	private enum JSON_KEY{subject, message, from, copyto, demographic, demographicNo, sourceFacility, linked}
	
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
    	int destinationFacilityId = messageList.getDestinationFacilityId();
    			
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
			jsonString.put(JSON_KEY.demographic.name(), createAttachedDemographicObject(loggedInInfo, msgDemoMap, destinationFacilityId));
		}

		Calendar sentDate = Calendar.getInstance();
		sentDate.setTime(messageTbl.getDate());
		providerCommunication.setActive(true);
		providerCommunication.setDestinationIntegratorFacilityId(destinationFacilityId);
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
		providerCommunication.setType(OscarMsgType.INTEGRATOR_TYPE+"");	
		providerCommunication.setData(jsonString.toString().getBytes("UTF-8"));

    	return providerCommunication; 
    }
	
	/**
	 * This method checks against the destination facility for demographic files that are already linked 
	 * and then prepares a JSONObject of all the demographic numbers and sourceFacility ids 
	 * that should be attached to a message. 
	 * 
	 * The demographic number is sent as "unlinked" if the demographic is not found to be linked
	 * in the Integrator. It will be up to the reciever to choose to link the demographic file with a local file 
	 * or to import the demographic file.
	 * 
	 * For example: 
	 * 
	 * This is a demographic that is not linked. The source facility number indicates 
	 * the facility where the demographic file resides.
	 *  
	 * demographicNo : 1234
	 * sourceFacility : 4
	 * linked : false
	 * 
	 * This demographic is linked. The sourcefacility will be 0 or the destination facility id
	 * to indicate that the demographic file exists in the destination facility
	 * 
	 * demographicNo : 6795
	 * sourceFacility : 0 or 2
	 * linked : true
	 * 
	 * @param msgDemoMapList
	 * @param sourceFacilityId
	 * @return
	 */
	private JSONArray createAttachedDemographicObject(LoggedInInfo loggedInInfo, List<MsgDemoMap> msgDemoMapList, int destinationFacilityId) {
		JSONArray jsonArray = new JSONArray();
		
		for(MsgDemoMap msgDemoMap : msgDemoMapList)
		{
			int demographicNo = msgDemoMap.getDemographic_no();
			
			/*
			 * First check if the given demographic is linked in the destination facility
			 */
			JSONArray linkedDemographicIdArray = getLinkedDemographicIdList(loggedInInfo, demographicNo, destinationFacilityId);

			
			/*
			 * Linked demographics were found, attach them to this list
			 */
			if(linkedDemographicIdArray != null)
			{
				jsonArray.add(linkedDemographicIdArray);
			}
			
			/*
			 * No linked demographics were found. Send the local demographic file for consideration 
			 * by the recieving user to import.
			 */
			else 
			{
				/*
				 * Single results need to be in an array to maintain consistency. 
				 */
				JSONArray demographicObjectArray = new JSONArray();
				JSONObject demographicObject = new JSONObject();
				
				demographicObject.put(JSON_KEY.demographicNo.name(), demographicNo);
				
				/* 
				 * the demographic file exists only in this facility. Indicate this so that
				 * the end user has the option to import this demographic file at the 
				 * destination 
				 */				
				demographicObject.put(JSON_KEY.sourceFacility.name(), getThisFacilityId(loggedInInfo));
				
				// the demographic is linked; make this true
				demographicObject.put(JSON_KEY.linked.name(), Boolean.FALSE);
				
				demographicObjectArray.add(demographicObject);
				
				jsonArray.add(demographicObjectArray);
			}
		}
		
		return jsonArray;
	}
	
	/**
	 * Ideally there should only be one linked demographic.
	 * It's problematic if there are more than 1 linked demographic in the remote facility. 
	 * They are being captured here in an array so that the end user is notified of multiple 
	 * links, and then may want to clean them up.
	 * 
	 * @param loggedInInfo
	 * @param demographicNo
	 * @param destinationFacilityId
	 * @return
	 */
	private JSONArray getLinkedDemographicIdList(LoggedInInfo loggedInInfo, final int demographicNo, int destinationFacilityId) {
		List<Integer> linkedDemographicNumberList = messengerDemographicManager.getLinkedDemographicIdsFromSourceFacility(loggedInInfo, demographicNo, destinationFacilityId);
		JSONArray demographicObjectArray = null;
		
		for(Integer linkedDemographicNumber : linkedDemographicNumberList)
		{
			if(demographicObjectArray == null)
			{
				demographicObjectArray = new JSONArray();
			}
			
			JSONObject demographicObject = new JSONObject();
			demographicObject.put(JSON_KEY.demographicNo.name(), linkedDemographicNumber);
			demographicObject.put(JSON_KEY.sourceFacility.name(), destinationFacilityId);
			
			// the demographic is linked; make this true
			demographicObject.put(JSON_KEY.linked.name(), Boolean.TRUE);
			
			demographicObjectArray.add(demographicObject);
		}
		
		return demographicObjectArray;
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
		Integer messageId = null;
		Integer sourceFacilityId = null;

		for(ProviderCommunicationTransfer providerCommunication : providerCommunicationList)
		{
			sourceFacilityId = providerCommunication.getSourceIntegratorFacilityId();
			
			MessageTbl messageTbl = new MessageTbl();
			String messageString = new String(providerCommunication.getData(), "UTF-8");
			JSONObject jsonObject = JSONObject.fromObject(messageString);
			String subject = jsonObject.getString(JSON_KEY.subject.name());
			String message = jsonObject.getString(JSON_KEY.message.name());
			String from = jsonObject.getString(JSON_KEY.from.name());
			String copyto = jsonObject.getString(JSON_KEY.copyto.name());	
			String sourceProviderIds = providerCommunication.getSourceProviderId();
	
	
			messageTbl.setSentByLocation(sourceFacilityId);
			messageTbl.setDate(providerCommunication.getSentDate().getTime());
			messageTbl.setTime(providerCommunication.getSentDate().getTime());
			messageTbl.setSentBy(from);
			
			/*
			 * The source provider id column may also contain any other providers that were copied in 
			 * at the source. 
			 * The originator of the message will always be the first entry. 
			 */	
			
			// spaces can be evil
			sourceProviderIds = sourceProviderIds.replaceAll("\\s", "");
			String[] sourceProviderIdArray = new String[] {};
	
			if(sourceProviderIds.contains(","))
			{
				sourceProviderIdArray = sourceProviderIds.split(",");
			}
			else
			{
				sourceProviderIdArray = new String[] {sourceProviderIds};
			}
				
			messageTbl.setSentByNo(sourceProviderIdArray[0]);
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
				messagingManager.addRecipientsToMessage(loggedInInfo, messageId, providerIds, messagingManager.getCurrentLocationId(), 0, sourceFacilityId, MessageList.STATUS_NEW);
				
				/*
				 *  add additional providers that are included from the source facility.
				 *  The first entry does not get saved here.
				 */		
				if(sourceProviderIdArray.length > 1)
				{
			
					List<String> sourceProviderIdList = new ArrayList<String>();
					for(int i = 1; i < sourceProviderIdArray.length; i++ )
					{
						sourceProviderIdList.add(sourceProviderIdArray[i]);
					}
					
					messagingManager.addRecipientsToMessage(loggedInInfo, messageId, sourceProviderIdList.toArray(new String[sourceProviderIdList.size()]), 0, 0, sourceFacilityId, MessageList.STATUS_REMOTE);			
				}

				/*
				 * Attach any demographic files that came in with the message.
				 */
				if(jsonObject.containsKey(JSON_KEY.demographic.name()))
				{
					attachIncomingDemographcToMessage(loggedInInfo, jsonObject.getJSONArray(JSON_KEY.demographic.name()), messageId);
				}

				receivedMessages.add(providerCommunication.getId());
			}
		}
		
		return receivedMessages;
	}
	
	@SuppressWarnings("unchecked")
	private void attachIncomingDemographcToMessage(LoggedInInfo loggedInInfo, final JSONArray demographicList, final int messageId) {

		Iterator<JSONArray> demographicArray = demographicList.iterator();
		while(demographicArray.hasNext())
		{
			Iterator<JSONObject> demographicObject = demographicArray.next().iterator();
			while(demographicObject.hasNext())
			{
				JSONObject demographic = demographicObject.next();
				int demographicNo = Integer.parseInt(demographic.getString(JSON_KEY.demographicNo.name()));
				int sourceFacility = Integer.parseInt(demographic.getString(JSON_KEY.sourceFacility.name()));
				
				/*
				 *  This is a local demographic if the demographic is linked
				 *  and the source facility id matches this facility 
				 */
				if(demographic.getBoolean(JSON_KEY.linked.name()) 
						&& getThisFacilityId(loggedInInfo) == sourceFacility)
				{
					messengerDemographicManager.attachDemographicToMessage(loggedInInfo, messageId, demographicNo);
				}
				
				/*
				 * Otherwise this demographic goes into the pile of import proposals. 
				 */
				else
				{
					messengerDemographicManager.attachIntegratedDemographicToMessage(loggedInInfo, messageId, demographicNo, sourceFacility);
				}
			}			
		}
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
