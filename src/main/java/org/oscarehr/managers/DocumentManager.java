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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.oscarehr.common.dao.*;
import org.oscarehr.common.model.*;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oscar.OscarProperties;
import oscar.dms.EDoc;
import oscar.log.LogAction;

@Service
public class DocumentManager {
	
	private static final String PARENT_DIR = OscarProperties.getInstance().getProperty("DOCUMENT_DIR");
	
	@Autowired
	private DocumentDao documentDao;

	@Autowired
	private CtlDocumentDao ctlDocumentDao;
	
    @Autowired
    protected SecurityInfoManager securityInfoManager;

	@Autowired
	private PatientConsentManager patientConsentManager;

	@Autowired
	private ProviderInboxRoutingDao providerInboxRoutingDao;

	@Autowired
	private ProviderLabRoutingDao providerLabRoutingDao;

	@Autowired
	private PatientLabRoutingDao patientLabRoutingDao;

	public Document getDocument(LoggedInInfo loggedInInfo, Integer id)
	{
		if ( ! securityInfoManager.hasPrivilege( loggedInInfo, "_edoc", "r", "" ) ) {
            throw new RuntimeException("Read Access Denied _edoc for provider " + loggedInInfo.getLoggedInProviderNo() );
        }
		
		Document result=documentDao.find(id);
		
		//--- log action ---
		if (result != null) {
			LogAction.addLogSynchronous(loggedInInfo, "DocumentManager.getDocument", "id=" + id);
		}

		return(result);
	}
	
	public List<Document> getDocumentsByDemographicNo(LoggedInInfo loggedInInfo, Integer demographicNo)
	{
		List<Document> result = documentDao.findByDemographicId(demographicNo+"");
		
		//--- log action ---
		if (result != null) {
			LogAction.addLog(loggedInInfo, "DocumentManager.getDocumentsByDemographicNo", "demographicNo=" + demographicNo, "","","");
		}

		return result;
	}
	
	public CtlDocument getCtlDocumentByDocumentId(LoggedInInfo loggedInInfo, Integer documentId)
	{
		
		if ( ! securityInfoManager.hasPrivilege( loggedInInfo, "_edoc", "r", "" ) ) {
            throw new RuntimeException("Read Access Denied _edoc for provider " + loggedInInfo.getLoggedInProviderNo() );
        }
		
		CtlDocument result=ctlDocumentDao.getCtrlDocument(documentId);
		
		//--- log action ---
		if (result != null) {
			LogAction.addLogSynchronous(loggedInInfo, "DocumentManager.getCtlDocumentByDocumentNoAndModule", "id=" + documentId);
		}

		return(result);
	}
	
	public Document addDocument(LoggedInInfo loggedInInfo, Document document, CtlDocument ctlDocument) {
		documentDao.persist(document);
		ctlDocument.getId().setDocumentNo(document.getDocumentNo());
		ctlDocumentDao.persist(ctlDocument);
		LogAction.addLogSynchronous(loggedInInfo, "DocumentManager.addDocument", "id=" + document.getId());
		return(document);
	}

	/**
	 * Creates a document and saves it to the provided demographic
	 * @param loggedInInfo The logged in info of the current user
	 * @param document Document to create
	 * @param demographicNo The demographic number to save the document to
	 * @param providerNo The optional provider number to route the document to
	 * @param documentData The document byte data
	 * @return Document record from the database once it has been created
	 * @throws IOException If actions related to getting document data fail
	 */
	public Document createDocument(LoggedInInfo loggedInInfo, Document document, Integer demographicNo, String providerNo, byte[] documentData) throws IOException {

		if ( ! securityInfoManager.hasPrivilege( loggedInInfo, "_edoc", "w", "" ) ) {
			throw new RuntimeException("Write Access Denied _edoc for provider " + loggedInInfo.getLoggedInProviderNo() );
		}

		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date today = new Date();
		// Generates filename and path data and saves the document data to the file system
		String documentPath = oscar.OscarProperties.getInstance().getProperty("DOCUMENT_DIR");
		String fileName = dateTimeFormat.format(today) + "_" + document.getDocfilename();
		File file = new File(documentPath + File.separator + fileName);
		FileUtils.writeByteArrayToFile(file, documentData);

		// Gets the number of pages for the document
		int numberOfPages = 1;
		if (fileName.toLowerCase().endsWith("pdf")) {
			PDDocument pdDocument = PDDocument.load(file);
			numberOfPages = pdDocument.getNumberOfPages();
		} else if (fileName.toLowerCase().endsWith("html")) {
			numberOfPages = 0;
		}
		document.setNumberofpages(numberOfPages);
		document.setDoccreator(loggedInInfo.getLoggedInProviderNo());
		document.setDocfilename(fileName);

		// Creates and saves the document
		saveDocument(document, demographicNo, providerNo);

		LogAction.addLogSynchronous(loggedInInfo, "DocumentManager.createDocument()", "Document ID: " + document.getId().toString() + " Demographic: " + demographicNo.toString() + " FileName: " + document.getDocfilename());

		return document;
	}

	public List<Document> getDocumentsUpdateAfterDate(LoggedInInfo loggedInInfo, Date updatedAfterThisDateExclusive, int itemsToReturn) {
		
		if ( ! securityInfoManager.hasPrivilege( loggedInInfo, "_edoc", "r", "" ) ) {
            throw new RuntimeException("Read Access Denied _edoc for provider " + loggedInInfo.getLoggedInProviderNo() );
        }
		
		List<Document> results = documentDao.findByUpdateDate(updatedAfterThisDateExclusive, itemsToReturn);

		LogAction.addLogSynchronous(loggedInInfo, "DocumentManager.getUpdateAfterDate", "updatedAfterThisDateExclusive=" + updatedAfterThisDateExclusive);

		return (results);
	}

	public List<Document> getDocumentsByProgramProviderDemographicDate(LoggedInInfo loggedInInfo, Integer programId, String providerNo, Integer demographicId, Calendar updatedAfterThisDateExclusive, int itemsToReturn) {
		
		if ( ! securityInfoManager.hasPrivilege( loggedInInfo, "_edoc", "r", "" ) ) {
            throw new RuntimeException("Read Access Denied _edoc for provider " + loggedInInfo.getLoggedInProviderNo() );
        }
		
		List<Document> results = documentDao.findByProgramProviderDemographicUpdateDate(programId, providerNo, demographicId, updatedAfterThisDateExclusive.getTime(), itemsToReturn);

		LogAction.addLog(loggedInInfo, "DocumentManager.getDocumentsByProgramProviderDemographicDate", "programId=" + programId, "providerNo="+providerNo, demographicId+"", "updatedAfterThisDateExclusive=" + updatedAfterThisDateExclusive.getTime() );

		return (results);
	}

	public List<String> getProvidersThatHaveAcknowledgedDocument(LoggedInInfo loggedInInfo, Integer documentId) {
		List<ProviderInboxItem> inboxList = providerInboxRoutingDao.getProvidersWithRoutingForDocument("DOC", documentId);
		List<String> providerList = new ArrayList<String>();
		for(ProviderInboxItem item: inboxList) {
			if(ProviderInboxItem.ACK.equals(item.getStatus())){
				//If this has been acknowledge add the provider_no to the list.
				providerList.add(item.getProviderNo());
			}
		}
		return providerList;
	}

	public Integer saveDocument( LoggedInInfo loggedInInfo, EDoc edoc ) {
		return this.saveDocument( loggedInInfo, edoc.getDocument(), edoc.getCtlDocument() );
	}

	public Integer saveDocument( LoggedInInfo loggedInInfo, Document document, CtlDocument ctlDocument) {

		if ( ! securityInfoManager.hasPrivilege( loggedInInfo, "_edoc", "w", "" ) ) {
            throw new RuntimeException("Write Access Denied _edoc for provider " + loggedInInfo.getLoggedInProviderNo() );
        }
		
		Integer savedId = null;
	
		if( document.getId() == null ) {
			savedId = addDocument( loggedInInfo, document );
		} else if( document.getId() > 0 ) {
			savedId = updateDocument( loggedInInfo, document );
		}
		
		ctlDocument.getId().setDocumentNo( savedId );
		
		if( savedId != null ) {
			ctlDocumentDao.persist( ctlDocument );
		}
		
		return savedId;
	}

	private void saveDocument(Document document, Integer demographicNo, String providerNo) {

		// Saves the document
		documentDao.persist(document);

		// Check that the demographic number is a valid number for a demographic, sets it to -1 if it isn't
		if (demographicNo == null || demographicNo < 1) {
			demographicNo = -1;
		}
		// Creates and saves the CtlDocument record
		CtlDocumentPK ctlDocumentPK = new CtlDocumentPK("demographic", demographicNo, document.getId());
		CtlDocument ctlDocument = new CtlDocument();
		ctlDocument.setId(ctlDocumentPK);
		ctlDocument.setStatus(String.valueOf(document.getStatus()));
		ctlDocumentDao.persist(ctlDocument);

		// Saves the patient and provider lab routings if the provided numbers are valid
		if (demographicNo > 0) {
			PatientLabRouting patientLabRouting = new PatientLabRouting(document.getId(), "DOC", demographicNo);
			patientLabRoutingDao.persist(patientLabRouting);
		}

		if (StringUtils.isNotEmpty(providerNo)) {
			ProviderLabRoutingModel providerLabRouting = new ProviderLabRoutingModel(providerNo, document.getId(), "N", "", new Date(), "DOC");
			providerLabRoutingDao.persist(providerLabRouting);
		}
	}
	
	private Integer addDocument( LoggedInInfo loggedInInfo, Document document ) {

		documentDao.persist( document );
		LogAction.addLog(loggedInInfo, "DocumentManager.saveDocument", "Document saved ", "Document No." + document.getDocumentNo(), "","");
		return document.getId();
	}
	
	private Integer updateDocument( LoggedInInfo loggedInInfo, Document document ) {
		documentDao.merge( document );
		LogAction.addLog(loggedInInfo, "DocumentManager.saveDocument", "Document updated ", "Document No." + document.getDocumentNo(), "","");
		return document.getId();
	}
	
	public void moveDocumentToOscarDocuments( LoggedInInfo loggedInInfo, Document document, String fromPath) {
		moveDocument( loggedInInfo, document, fromPath, null );
	}
	
	public void moveDocument( LoggedInInfo loggedInInfo, Document document, String fromPath, String toPath ) {
		
		if ( ! securityInfoManager.hasPrivilege( loggedInInfo, "_edoc", "x", "" ) ) {
            throw new RuntimeException("Read and Write Access Denied _edoc for provider " + loggedInInfo.getLoggedInProviderNo() );
        }
		
		// move the PDF from the temp location to Oscar's document directory.
		try {
			if( toPath == null ) {
				toPath = getParentDirectory();
			}
			Path from = FileSystems.getDefault().getPath( String.format("%1$s%2$s%3$s", fromPath, File.separator, document.getDocfilename() ) );
			Path to = FileSystems.getDefault().getPath( String.format("%1$s%2$s%3$s", toPath, File.separator, document.getDocfilename() ) );
			Files.move( from, to, StandardCopyOption.REPLACE_EXISTING );
			LogAction.addLog(loggedInInfo, "EformDataManager.moveDocument", "Document was moved", "Document No." + document.getDocumentNo(), "",fromPath + " to " + toPath);
		
		} catch (IOException e) {
			MiscUtils.getLogger().error("Document failed move. Id: " + document.getDocumentNo() + " From: " + fromPath + " To: " + toPath , e);
			LogAction.addLog(loggedInInfo, "EformDataManager.moveDocument", "Document failed move ", "Document No." + document.getDocumentNo(), "",fromPath + " to " + toPath);
		}
	}
	
	public static final String getParentDirectory() {
		return PARENT_DIR;
	}
	
	public String getPathToDocument(LoggedInInfo loggedInInfo, int documentId) {
		Document document = this.getDocument(loggedInInfo, documentId);
		String path = OscarProperties.getInstance().getProperty("DOCUMENT_DIR");
		
		if(! path.endsWith(File.separator)) {
			path = path + File.separator;
		}
		
		path = path + document.getDocfilename();		
 
		return path;
	}
}
