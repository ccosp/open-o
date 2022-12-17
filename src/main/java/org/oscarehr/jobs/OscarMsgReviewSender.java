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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.oscarehr.jobs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.oscarehr.common.dao.ProviderDataDao;
import org.oscarehr.common.dao.ResidentOscarMsgDao;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.jobs.OscarRunnable;
import org.oscarehr.common.model.OscarMsgType;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.ProviderData;
import org.oscarehr.common.model.ResidentOscarMsg;
import org.oscarehr.common.model.Security;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.managers.MessagingManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.oscarMessenger.data.MessengerSystemMessage;


/**
 *
 * @author rjonasz
 */
public class OscarMsgReviewSender implements OscarRunnable {
    
    private Provider provider;
    private Security security;
    private static final String MESSAGE = "Hello, the following charts require your attention\n Sapere aude!";
    private static final String SUBJECT = "Chart Review";
    private static final Calendar DEFAULT_TIME = new GregorianCalendar(0, 0, 0, 9, 0);
    private final Logger logger = MiscUtils.getLogger();
    
    @Override
    public void run() {
        
        String userNo = provider.getProviderNo();
        LoggedInInfo loggedInInfo = new LoggedInInfo();
        loggedInInfo.setLoggedInSecurity(security);
        loggedInInfo.setLoggedInProvider(provider);

        logger.info("Starting to send OSCAR Review Messages");
        
        Integer defaultHour = DEFAULT_TIME.get(Calendar.HOUR_OF_DAY);
        Integer defaultMin = DEFAULT_TIME.get(Calendar.MINUTE);
        
        Calendar now = GregorianCalendar.getInstance();
        Integer currentHour = now.get(Calendar.HOUR_OF_DAY);
        Integer currentMinute = now.get(Calendar.MINUTE);
        
        currentHour = (currentMinute > 45 ? currentHour + 1 : currentHour);
        currentMinute = (currentMinute > 45 || currentMinute < 15 ? 0 : 30);
        
        
        String time = String.valueOf(currentHour) + ":" + String.valueOf(currentMinute);       
        ProviderDataDao providerDataDao = SpringUtils.getBean(ProviderDataDao.class);      
        List<ProviderData> providerList = providerDataDao.findAllBilling("1");
        
        List<String> providerNosList = new ArrayList<String>();
        for( ProviderData providerData : providerList ) {
            providerNosList.add(providerData.getId());
        }
        
        UserPropertyDAO propertyDao = SpringUtils.getBean(UserPropertyDAO.class);        
        List<UserProperty> properties = propertyDao.getPropValues(UserProperty.OSCAR_MSG_RECVD, time);       
        ResidentOscarMsgDao residentOscarMsgDao = SpringUtils.getBean(ResidentOscarMsgDao.class);
        List<ResidentOscarMsg> residentOscarMsgList;
        MessagingManager messagingManager = SpringUtils.getBean(MessagingManager.class);

        // String sentToWho = null;
        Integer messageId = null;
        
        for( UserProperty property : properties ) {
            
            if( providerNosList.indexOf(property.getProviderNo()) > -1 ) {
            
                residentOscarMsgList = residentOscarMsgDao.findBySupervisor(property.getProviderNo());
                StringBuilder msgInfo = new StringBuilder();
                int idx = 1;
                for( ResidentOscarMsg residentOscarMsg : residentOscarMsgList ) {
                    msgInfo = msgInfo
                    		.append(residentOscarMsg.getDemographic_no())
                    		.append(":")
                    		.append((residentOscarMsg.getAppointment_no()==null ? "null" : residentOscarMsg.getAppointment_no()))
                    		.append(":")
                    		.append(residentOscarMsg.getId())
                    		.append(":")
                    		.append(residentOscarMsg.getNote_id());
                    
                    if( idx < residentOscarMsgList.size() ) {
                        msgInfo = msgInfo.append(",");
                    }
                    ++idx;
                }

                providerNosList.remove(property.getProviderNo());
                if( residentOscarMsgList.size() > 0 ) {
                	
                	List<Integer> attachDemographic = new ArrayList<Integer>();
                	for( ResidentOscarMsg res : residentOscarMsgList ) {
                		attachDemographic.add(res.getDemographic_no());                       
                    }
                	
                    MessengerSystemMessage systemMessage = new MessengerSystemMessage(new String[] {property.getProviderNo()});
                    systemMessage.setType_link(msgInfo.toString());
                    systemMessage.setType(OscarMsgType.OSCAR_REVIEW_TYPE);
                    systemMessage.setSentByNo(userNo);                    
                    systemMessage.setSubject(SUBJECT);
                    systemMessage.setMessage(MESSAGE);
                    systemMessage.setAttachedDemographicNo(attachDemographic.toArray(new Integer[attachDemographic.size()]));
                    
                    messageId = messagingManager.sendSystemMessage(loggedInInfo, systemMessage);
                    
                    if( messageId != null ) {
                        logger.info("SENT Review OSCAR MESSAGE");
                    }
                }
            }
        }
        
        if( currentHour.equals(defaultHour) && currentMinute.equals(defaultMin) && providerNosList.size() > 0  ) {
            for( String providerNo : providerNosList ) {
                String userProp = propertyDao.getStringValue(providerNo, UserProperty.OSCAR_MSG_RECVD);
                
                if( userProp == null ) {
                    residentOscarMsgList = residentOscarMsgDao.findBySupervisor(providerNo);
                    StringBuilder msgInfo = new StringBuilder();
                    int idx = 1;
                    for( ResidentOscarMsg r : residentOscarMsgList ) {
                        msgInfo = msgInfo.append(r.getDemographic_no()).append(":").append(r.getAppointment_no()==null ? "null" : r.getAppointment_no()).append(":").append(r.getId()).append(":").append(r.getNote_id());
                        if( idx < residentOscarMsgList.size() ) {
                            msgInfo = msgInfo.append(",");
                        }
                        ++idx;
                    }
                    if( residentOscarMsgList.size() > 0 ) { 
                    	
                       	List<Integer> attachDemographic = new ArrayList<Integer>();
                    	for( ResidentOscarMsg res : residentOscarMsgList ) {
                    		attachDemographic.add(res.getDemographic_no());                       
                        }
                    	
                        MessengerSystemMessage systemMessage = new MessengerSystemMessage(new String[] {providerNo});
                        systemMessage.setType_link(msgInfo.toString());
                        systemMessage.setType(OscarMsgType.OSCAR_REVIEW_TYPE);
                        systemMessage.setSentByNo(userNo);                    
                        systemMessage.setSubject(SUBJECT);
                        systemMessage.setMessage(MESSAGE);
                        systemMessage.setAttachedDemographicNo(attachDemographic.toArray(new Integer[attachDemographic.size()]));
                        
                        messageId = messagingManager.sendSystemMessage(loggedInInfo, systemMessage);
                        
                        if( messageId != null ) {                       	
                            logger.info("SENT DEFAULT TIME OSCAR Review MESSAGE");                              
                        } 
                    }
                }
            }
        }
        
        logger.info("Completed Sending OSCAR Review Messages");
    }
   
    @Override
    public void setLoggedInProvider(Provider provider) {
    	this.provider = provider;
    }

    @Override
    public void setLoggedInSecurity(Security security) {
        this.security = security;
    }
    
    @Override
	public void setConfig(String string) {
	}
}
