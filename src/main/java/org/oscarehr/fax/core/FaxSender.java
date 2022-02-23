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
package org.oscarehr.fax.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.common.util.Base64Utility;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.http.HttpStatus;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.logging.log4j.Logger;
import org.oscarehr.common.dao.FaxClientLogDao;
import org.oscarehr.common.dao.FaxConfigDao;
import org.oscarehr.common.dao.FaxJobDao;
import org.oscarehr.common.model.FaxClientLog;
import org.oscarehr.common.model.FaxConfig;
import org.oscarehr.common.model.FaxJob;
import org.oscarehr.common.model.FaxJob.STATUS;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.OscarProperties;

public class FaxSender {
	
	private static String PATH = "/fax";
	private FaxConfigDao faxConfigDao = SpringUtils.getBean(FaxConfigDao.class);
	private FaxJobDao faxJobDao = SpringUtils.getBean(FaxJobDao.class);
    private FaxClientLogDao faxClientLogDao = SpringUtils.getBean(FaxClientLogDao.class);
	
	private Logger log = MiscUtils.getLogger();
	
	public void send() {
		
		List<FaxConfig> faxConfigList = faxConfigDao.findAll(null,null);		
		List<FaxJob> faxJobList;
		
		WebClient client;

		String document_dir = OscarProperties.getInstance().getProperty("DOCUMENT_DIR");

		for( FaxConfig faxConfig : faxConfigList ) {
			if( faxConfig.isActive() ) {
				
				client = WebClient.create(faxConfig.getUrl());
				client.path(PATH + "/send/" + faxConfig.getFaxUser());
				client.type(MediaType.APPLICATION_XML);
				client.accept(MediaType.APPLICATION_XML);
				
				String login = faxConfig.getSiteUser() + ":" + faxConfig.getPasswd();
				String authorizationHeader = "Basic " + Base64Utility.encode(login.getBytes());
				client.header("Authorization", authorizationHeader);	    

				faxJobList = faxJobDao.getReadyToSendFaxes(faxConfig.getFaxNumber());
				FaxJob faxJobId;

				log.info("SENDING " + faxJobList.size() + " faxes from fax account " + faxConfig.getSiteUser());

				String filename;

				for( FaxJob faxJob : faxJobList ) {

					FaxClientLog faxClientLog = faxClientLogDao.findClientLogbyFaxId(faxJob.getId());
					STATUS faxStatus = STATUS.ERROR;

					client.header("user", faxJob.getUser());
					client.header("passwd", faxConfig.getFaxPasswd());

					faxJob.setSenderEmail( faxConfig.getSenderEmail() );
					filename = faxJob.getFile_name();

					if(filename.contains(File.separator))
					{
						filename.replace(File.separator, "");
					}

					Path filePath = Paths.get(document_dir, filename);
					try {
						String base64 = Base64Utility.encode(Files.readAllBytes(filePath));
						faxJob.setDocument(base64);

						log.info("sending fax from file path " + filePath);

						Response httpResponse = client.post(faxJob);
						
						if( httpResponse.getStatus() == HttpStatus.SC_OK ) {							
							faxJobId = httpResponse.readEntity(FaxJob.class);
							faxJob.setDocument(null);
							faxJob.setJobId(faxJobId.getJobId());
							faxJob.setStatusString(faxJobId.getStatusString());
							faxStatus = faxJobId.getStatus();
						}
						else 
						{
							faxJob.setStatusString("WEB SERVICE RESPONDED WITH " + httpResponse.getStatus());
							log.error("WEB SERVICE RESPONDED WITH " + httpResponse.getStatus(), new IOException());
						}
						
					}
					catch(HttpHostConnectException e) 
					{
						faxStatus = FaxJob.STATUS.WAITING;
						faxJob.setStatusString("Connection error. Check internet connection " + faxJob.getFile_name());
						log.error("Connection error. Check internet connection " + faxJob.getFile_name());
					}
					catch(IOException e ) 
					{
						faxJob.setStatusString("CANNOT FIND " + faxJob.getFile_name());
						log.error("CANNOT FIND " + faxJob.getFile_name());
					}
					catch( Exception e ) {
						faxJob.setStatusString("PROBLEM COMMUNICATING WITH WEB SERVICE");
						log.error("PROBLEM COMMUNICATING WITH WEB SERVICE",e);
					} 
					finally 
					{
						faxJob.setStatus(faxStatus);
						faxJobDao.merge(faxJob);
						log.info("Updated Fax with jobid " + faxJob.getJobId() + " and status " + faxJob.getStatus());
						if(faxClientLog != null) {
							faxClientLog.setResult(faxStatus.name());
			                faxClientLog.setEndTime(new Date(System.currentTimeMillis()));
			                faxClientLogDao.merge(faxClientLog);
						}
					}
				}
				
			}
		}
	}

}
