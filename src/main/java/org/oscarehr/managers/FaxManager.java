/**
 * Copyright (c) 2015-2019. The Pharmacists Clinic, Faculty of Pharmaceutical Sciences, University of British Columbia. All Rights Reserved.
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
 * The Pharmacists Clinic
 * Faculty of Pharmaceutical Sciences
 * University of British Columbia
 * Vancouver, British Columbia, Canada
 */

package org.oscarehr.managers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts.action.DynaActionForm;
import org.oscarehr.common.dao.FaxClientLogDao;
import org.oscarehr.common.dao.FaxConfigDao;
import org.oscarehr.common.dao.FaxJobDao;
import org.oscarehr.common.model.FaxClientLog;
import org.oscarehr.common.model.FaxConfig;
import org.oscarehr.common.model.FaxJob;
import org.oscarehr.common.model.FaxJob.STATUS;
import org.oscarehr.fax.core.FaxRecipient;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;
import oscar.dms.EDocUtil;
import oscar.log.LogAction;
import oscar.util.ConcatPDF;

@Service
public class FaxManager {
	
	@Autowired
	private FaxConfigDao faxConfigDao;
	
	@Autowired
	private FaxClientLogDao faxClientLogDao;
	
	@Autowired
	private FaxJobDao faxJobDao;
	
	@Autowired
	private SecurityInfoManager securityInfoManager;
	
	@Autowired
	private FaxDocumentManager faxDocumentManager;
	
	@Autowired
	private NioFileManager nioFileManager;
	
	private Logger logger = MiscUtils.getLogger();
	
	public enum TransactionType {CONSULTATION, EFORM, FORM, RX, DOCUMENT}
	
	/**
	 * Render a fax document from the given parameters.
	 * Return a path to the fax document so it can be reviewed by the sender.
	 * 
	 * @return
	 */
	public Path renderFaxDocument(LoggedInInfo loggedInInfo, TransactionType transactionType, int transactionId, int demographicNo) {
		
		Path renderedDocument;
		
		switch(transactionType)
		{
			case CONSULTATION: 	renderedDocument = renderConsultationRequest(loggedInInfo, transactionId, demographicNo);
				break;
			case DOCUMENT: 		renderedDocument = renderDocument(loggedInInfo, transactionId, demographicNo);
				break;
			case EFORM: 		renderedDocument = renderEform(loggedInInfo, transactionId, demographicNo);
				break;
			case FORM: 			renderedDocument = renderForm(loggedInInfo, transactionId, demographicNo);
				break;
			case RX: 			renderedDocument = renderPrescription(loggedInInfo, transactionId, demographicNo);
				break;
			default:			renderedDocument = null;
				break;
		}	
		
		return renderedDocument;
	}
	
	public Path renderConsultationRequest(LoggedInInfo loggedInInfo, int requestId, int demographicNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_con", SecurityInfoManager.WRITE, demographicNo)) {
			throw new RuntimeException("missing required security object (_con)");
		}
		
		logger.info("Rendering consultation request document number " + requestId + " for fax preview.");
		
		return null;
	}
	
	public Path renderDocument(LoggedInInfo loggedInInfo, int documentNo, int demographicNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_edoc", SecurityInfoManager.WRITE, demographicNo)) {
			throw new RuntimeException("missing required security object (_edoc)");
		}
		
		logger.info("Rendering document number " + documentNo + " for fax preview.");
		return null;
	}
	
	public Path renderEform(LoggedInInfo loggedInInfo, int eformId, int demographicNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_eform", SecurityInfoManager.WRITE, demographicNo)) {
			throw new RuntimeException("missing required security object (_eform)");
		}
		logger.info("Rendering eform number " + eformId + " for fax preview.");
		return faxDocumentManager.getEformFaxDocument(loggedInInfo, eformId);
	}
	
	public Path renderPrescription(LoggedInInfo loggedInInfo, int rxId, int demographicNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_rx", SecurityInfoManager.WRITE, demographicNo)) {
			throw new RuntimeException("missing required security object (_rx)");
		}
		logger.info("Rendering prescription number " + rxId + " for fax preview.");

		return null;
	}
	
	public Path renderForm(LoggedInInfo loggedInInfo, int formId, int demographicNo) {
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_form", SecurityInfoManager.WRITE, demographicNo)) {
			throw new RuntimeException("missing required security object (_form)");
		}
		
		logger.info("Rendering form number " + formId + " for fax preview.");
		
		return null;
	}
	
	/**
	 * Calls the save method after the faxJob(s) are created. 
	 * 
	 * The FaxJob list that is returned contains persisted FaxJob Objects
	 * 
	 * This method has a specific purpose for the FaxAction class.  Use the 
	 * createFaxJob(LoggedInInfo loggedInInfo, Map<String, Object> faxJobMap) signature otherwise.
	 * 
	 * @param loggedInInfo
	 * @param faxActionForm
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<FaxJob> createAndSaveFaxJob(LoggedInInfo loggedInInfo, DynaActionForm faxActionForm) {
		return createAndSaveFaxJob(loggedInInfo, faxActionForm.getMap());
	}
	
	/**
	 * 1.) Creates the faxJob
	 * 2.) duplicates the faxJob for each recipient 
	 * 3.) saves all the faxJobs to be sent.
	 * 
	 * Map should contain values for:
	 * 
	 * faxFilePath
	 * recipient
	 * recipientFaxNumber
	 * comments (for cover page)
	 * isCoverpage
	 * senderFaxNumber
	 * demographicNo
	 * copytoRecipients (as String[])
	 * 
	 * The FaxJob list that is returned contains persisted FaxJob Objects
	 * 
	 * @param loggedInInfo
	 * @param faxJobMap
	 * @return
	 */
	public List<FaxJob> createAndSaveFaxJob(LoggedInInfo loggedInInfo, Map<String, Object> faxJobMap) {
		
		FaxJob faxJob = createFaxJob(loggedInInfo, faxJobMap);	
		List<FaxJob> faxJobList	= new ArrayList<FaxJob>();
		
		/*
		 * add the first job that contains the original recipient.
		 */
		faxJobList.add(faxJob);
		
		/*
		 * duplicate the fax job for each copy to recipient
		 * The original reciever is already retained in the list. 
		 */
		String[] copytoRecipients = (String[]) faxJobMap.get("copyToRecipients");	
		if(copytoRecipients != null && copytoRecipients.length > 0)
		{
			List<FaxJob> faxJobRecipients = addRecipients(loggedInInfo, faxJob, copytoRecipients);
			faxJobList.addAll(faxJobRecipients);
		}

		return saveFaxJob(loggedInInfo, faxJobList);
	}
	
	/**
	 * The beginning of a new Fax job from the parameters in the given Map.
	 * 
	 * Map should contain values for:
	 * 
	 * faxFilePath
	 * recipient
	 * recipientFaxNumber
	 * comments (for cover page)
	 * isCoverpage
	 * senderFaxNumber
	 * demographicNo
	 * copytoRecipients (as String[])
	 * 
	 * The FaxJob returned is NEW UN-PERSISTED FaxJob Object with a single recipient 
	 * 
	 * 
	 * @param loggedInInfo
	 * @param faxActionForm
	 * @return
	 */
	public FaxJob createFaxJob(LoggedInInfo loggedInInfo, Map<String, Object> faxJobMap) {
		
  		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_fax", SecurityInfoManager.WRITE, null)) {
			throw new RuntimeException("missing required security object (_fax)");
		}
  		
		String faxFilePath 			= (String) faxJobMap.get("faxFilePath");
		String recipient 			= (String) faxJobMap.get("recipient");
		String recipientFaxNumber 	= (String) faxJobMap.get("recipientFaxNumber");
		String comments 			= (String) faxJobMap.get("comments");
		String isCoverpage 			= (String) faxJobMap.get("isCoverpage");
		String senderFaxNumber 		= (String) faxJobMap.get("senderFaxNumber");
		Integer demographicNo		= (Integer) faxJobMap.get("demographicNo");
		recipientFaxNumber			= recipientFaxNumber.replaceAll("\\D", "");
		//TODO not sure how to make this happen yet.
		
//		String isOverrideFaxNumber	= faxActionForm.getString("isOverrideFaxNumber");
//		String overrideFaxNumber	= faxActionForm.getString("overrideFaxNumber");

	    FaxJob faxJob 				= new FaxJob();   
		Path faxDocument 			= Paths.get(faxFilePath);
		FaxConfig faxConfig 		= faxConfigDao.getActiveConfigByNumber(senderFaxNumber);
		
		/*
		 * Build the foundation of a faxJob
		 */		
		faxJob.setFile_name(faxFilePath);
		faxJob.setNumPages(EDocUtil.getPDFPageCount(faxFilePath));	
		faxJob.setFax_line(senderFaxNumber);
		faxJob.setStamp(new Date());
		faxJob.setOscarUser(loggedInInfo.getLoggedInProviderNo());
		faxJob.setDemographicNo(demographicNo);
		faxJob.setRecipient(recipient);
		faxJob.setDestination(recipientFaxNumber);
		faxJob.setStatus(FaxJob.STATUS.WAITING);
		
		/*
		 * This will be null if the fax number is invalid.
		 * No valid account - No fax
		 */
		if(faxConfig == null)
		{
			logger.error("Fax account " + faxJob.getFax_line() + " is not found, invalid, or inactive");
			faxJob.setStatus(STATUS.ERROR);
			return faxJob;
		}
		
	    faxJob.setUser(faxConfig.getFaxUser());
		
		/*
		 * No document - No Fax. Return an error. 
		 */
		if(! Files.exists(faxDocument))
		{
			faxJob.setStatus(STATUS.ERROR);
			return faxJob;
		}
			
		/*
		 * Add a cover page if the user checked "yes" 
		 */
		if("true".equalsIgnoreCase(isCoverpage))
		{
			try {
				faxDocument = addCoverPage(loggedInInfo, comments, faxDocument);
			} catch (IOException e) {
				logger.error("failed to add cover page ", e);
				faxJob.setStatus(STATUS.ERROR);
				return faxJob;
			}
		}
		
		return faxJob;

	}
	
	/**
	 * Add recipients from an indexed array of JSON formatted strings
	 * 
	 * name:<recipient>
	 * fax:<recipient fax number>
	 * 
	 * @param loggedInInfo
	 * @param faxJob
	 * @param faxRecipients
	 * @return
	 */
	public List<FaxJob> addRecipients(LoggedInInfo loggedInInfo, FaxJob faxJob, String[] faxRecipients) {
		
		List<FaxRecipient> faxRecipientArray = new ArrayList<FaxRecipient>();
		for(String copytoRecipient : faxRecipients)
		{
			/*
			 *  assumes that the recipient entry is a JSONObject
			 */
			copytoRecipient = "{" + copytoRecipient + "}";
			JSONObject copytoRecipientJson = JSONObject.fromObject(copytoRecipient);
			FaxRecipient faxRecipient = new FaxRecipient(copytoRecipientJson);
			faxRecipientArray.add(faxRecipient);
		}
		return addRecipients(loggedInInfo, faxJob, faxRecipientArray);
	}
	
	/**
	 * Create 1 faxJob for each fax recipient. Sets each faxJob to the 
	 * default status of WAITNG.
	 * 
	 * @param loggedInInfo
	 * @param faxJob
	 * @param faxRecipient
	 */
	public List<FaxJob> addRecipients(LoggedInInfo loggedInInfo, FaxJob faxJob, List<FaxRecipient> faxRecipients) {
		
  		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_fax", SecurityInfoManager.WRITE, null)) {
			throw new RuntimeException("missing required security object (_fax)");
		}
		
		List<FaxJob> faxJobList = new ArrayList<FaxJob>();
				
		outer : for(FaxRecipient faxRecipient : faxRecipients) 
		{	
			/*
			 * Avoid duplicate fax numbers. 
			 */
			if(faxJob.getDestination() == faxRecipient.getFax()) 
			{
				continue;
			}
			
			for(FaxJob faxJobItem : faxJobList) {
				if(faxJobItem.getDestination() == faxRecipient.getFax())
				{
					continue outer;
				}
			}
			
	  		FaxJob faxJobCopy = new FaxJob(faxJob);
	  		faxJobCopy.setDestination(faxRecipient.getFax());
	  		faxJobCopy.setRecipient(faxRecipient.getName());			    		
			
	  		faxJobList.add(faxJobCopy);
		}
		return faxJobList;
	}
	
	/**
	 * Persist to the database for transmission later if the fax account is valid.
	 * 
	 * The given faxjob must contain a valid sender fax number and username.
	 * 
	 * @param loggedInInfo
	 * @param faxJob
	 * @param faxRecipient
	 * @return
	 */
	public List<FaxJob> saveFaxJob(LoggedInInfo loggedInInfo, List<FaxJob> faxJobList) {
		
 		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_fax", SecurityInfoManager.WRITE, null)) {
			throw new RuntimeException("missing required security object (_fax)");
		}
		
		List<FaxJob> savedFaxJobs = new ArrayList<FaxJob>();
		
		for(FaxJob faxJob : faxJobList)
		{
			saveFaxJob(loggedInInfo, faxJob);
			savedFaxJobs.add(faxJob);
		}
  		return savedFaxJobs;
	}
	
	/**
	 * Create new or update fax job. 
	 * @param loggedInInfo
	 * @param faxJob
	 * @return
	 */
	public FaxJob saveFaxJob(LoggedInInfo loggedInInfo, FaxJob faxJob) {
		
 		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_fax", SecurityInfoManager.WRITE, null)) {
			throw new RuntimeException("missing required security object (_fax)");
		}
 		
 		Integer faxJobId = faxJob.getId();
 		
 		if(faxJobId == null)
 		{
 			 faxJobDao.persist(faxJob);
 		}
 		else 
 		{
 			faxJobDao.merge(faxJob);			
 		}
	    
	    if(faxJob.getId() == null || faxJob.getId() < 1 )
	    {
	    	faxJob.setStatus(STATUS.ERROR);
	    }
	    
	    return faxJob;
	}
	
	/**
	 * prepend a fax cover page to the given existing PDF document. 
	 * 
	 * @param loggedInInfo
	 * @param note
	 * @param currentDocument
	 * @throws IOException
	 */
	public Path addCoverPage(LoggedInInfo loggedInInfo, String note, Path currentDocument) throws IOException {
  		
		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_fax", SecurityInfoManager.WRITE, null)) {
			throw new RuntimeException("missing required security object (_fax)");
		}
  		
  		byte[] coverPage = faxDocumentManager.createCoverPage(loggedInInfo, note);
  		
		try(OutputStream currentDocumentStream = Files.newOutputStream(currentDocument);
				ByteArrayInputStream coverPageStream = new ByteArrayInputStream(coverPage))
		{		
			List<Object> documentList = new ArrayList<Object>();
			documentList.add(coverPageStream);
			ConcatPDF.concat(documentList, currentDocumentStream);				
		}
  			
		return currentDocument;
	}

	/**
	 * Get a preview image of the documents being faxed.
	 * 
	 * @param loggedInInfo
	 * @param filePath
	 * @return
	 */
	public Path getFaxPreviewImage(LoggedInInfo loggedInInfo, Path filePath) {
  		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_fax", SecurityInfoManager.READ, null)) {
			throw new RuntimeException("missing required security object (_fax)");
		}
  		
  		Path outfile = null;
		if (filePath != null && Files.exists(filePath)) {
			outfile = nioFileManager.createCacheVersion2(loggedInInfo, filePath.getParent().toString(), filePath.getFileName().toString(), 1);
		}
		return outfile;
	}
	
	/**
	 * Sets both the global user log and the fax job log. 
	 * 
	 * @param loggedInInfo
	 * @param faxJob
	 * @param transactionType
	 * @param transactionId
	 */
	public void logFaxJob(LoggedInInfo loggedInInfo, FaxJob faxJob, TransactionType transactionType, int transactionId) {

		FaxClientLog faxClientLog = new FaxClientLog();
		faxClientLog.setFaxId(faxJob.getId());
		faxClientLog.setProviderNo(loggedInInfo.getLoggedInProviderNo()); 
		faxClientLog.setStartTime(new Date(System.currentTimeMillis()));
		faxClientLog.setRequestId(transactionId);
		faxClientLog.setTransactionType(transactionType.name());

		faxClientLogDao.persist(faxClientLog); 
	}
	
	/**
	 * Update the transaction logs with a new status.
	 * @param loggedInInfo
	 * @param faxJob
	 */
	public void updateFaxLog(LoggedInInfo loggedInInfo, FaxJob faxJob) {
		
		FaxClientLog faxClientLog = faxClientLogDao.findClientLogbyFaxId(faxJob.getId());
		LogAction.addLogSynchronous(loggedInInfo, faxJob.getStatus().name(), faxClientLog.getTransactionType() + ":" + faxClientLog.getRequestId());
		faxClientLog.setResult(faxJob.getStatus().name());
        faxClientLog.setEndTime(new Date(System.currentTimeMillis()));
        faxClientLogDao.merge(faxClientLog);
		
	}
	
	/**
	 * Returns all the active sender accounts in this system.
	 * @param loggedInInfo
	 * @return
	 */
	public List<FaxConfig> getFaxGatewayAccounts(LoggedInInfo loggedInInfo) {		
  		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_fax", SecurityInfoManager.READ, null)) {
			throw new RuntimeException("missing required security object (_fax)");
		}
  		
  		List<FaxConfig> accounts = faxConfigDao.findAll(0, 50);
  		List<FaxConfig> sanitizedAccounts = new ArrayList<FaxConfig>();
  		for(FaxConfig account : accounts)
  		{
  			if(account.isActive())
  			{
	  			account.setFaxPasswd(null);
	  			account.setPasswd(null);
	  			sanitizedAccounts.add(account);
  			}
  			
  		}		
		return sanitizedAccounts;
	}
	
	/**
	 * Get all fax jobs with a waiting to be sent status by 
	 * sender fax number.
	 * 
	 * @return
	 */
	public List<FaxJob> getOutGoingFaxes(LoggedInInfo loggedInInfo, String senderFaxNumber) {
  		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_fax", SecurityInfoManager.READ, null)) {
			throw new RuntimeException("missing required security object (_fax)");
		}
  		
		return faxJobDao.getReadyToSendFaxes(senderFaxNumber);
	}
	
	/**
	 * Clear the preview cache and temp directory.
	 * 
	 * @param loggedInInfo
	 * @param filePath
	 */
	public boolean flush(LoggedInInfo loggedInInfo, String filePath) {
  		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_fax", SecurityInfoManager.READ, null)) {
			throw new RuntimeException("missing required security object (_fax)");
		}
  		
  		/*
  		 * Remove the preview cache.
  		 */
  		boolean cache = nioFileManager.removeCacheVersion(loggedInInfo, filePath);
  		
  		/*
  		 * Delete the temp file
  		 */
  		boolean temp = nioFileManager.deleteTempFile(filePath);
  		
  		return (cache && temp);
	}
	
	/**
	 * Check if fax services are enabled. 
	 * @return
	 */
	public static boolean isEnabled(LoggedInInfo loggedInInfo) {
		SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
  		if (!securityInfoManager.hasPrivilege(loggedInInfo, "_fax", SecurityInfoManager.READ, null)) {
			throw new RuntimeException("missing required security object (_fax)");
		}
		FaxConfigDao faxConfigDao = SpringUtils.getBean(FaxConfigDao.class);
  		List<FaxConfig> accounts = faxConfigDao.findAll(0, 50);
  		for(FaxConfig account : accounts)
  		{
  			if(account.isActive())
  			{
  				return Boolean.TRUE;
  			}
  			
  		}
  		return Boolean.FALSE;
	}

}
