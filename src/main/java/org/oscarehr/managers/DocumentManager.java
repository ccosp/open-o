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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.oscarehr.common.dao.CtlDocumentDao;
import org.oscarehr.common.dao.DocumentDao;
import org.oscarehr.common.model.ConsentType;
import org.oscarehr.common.dao.DocumentDao.DocumentType;
import org.oscarehr.common.dao.DocumentDao.Module;
import org.oscarehr.common.model.CtlDocument;
import org.oscarehr.common.model.CtlDocumentPK;
import org.oscarehr.common.model.Document;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oscar.OscarProperties;
import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
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

	public Document getDocument(LoggedInInfo loggedInInfo, Integer id)
	{
		if ( ! securityInfoManager.hasPrivilege( loggedInInfo, "_edoc", "r", "" ) ) {
            throw new RuntimeException("Read Access Denied _edoc for provider " + loggedInInfo.getLoggedInProviderNo() );
        }
		
		Document result=documentDao.find(id);
		
		//--- log action ---
		if (result != null) {
            LogAction.addLog(loggedInInfo, "DocumentManager.getDocument", "id=" + id, "","","");
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
			LogAction.addLog(loggedInInfo, "DocumentManager.getCtlDocumentByDocumentNoAndModule", "id=" + documentId, "","","");
		}

		return(result);
	}
	
	public List<Document> getDocumentsUpdateAfterDate(LoggedInInfo loggedInInfo, Date updatedAfterThisDateExclusive, int itemsToReturn) {
		
		if ( ! securityInfoManager.hasPrivilege( loggedInInfo, "_edoc", "r", "" ) ) {
            throw new RuntimeException("Read Access Denied _edoc for provider " + loggedInInfo.getLoggedInProviderNo() );
        }
		
		List<Document> results = documentDao.findByUpdateDate(updatedAfterThisDateExclusive, itemsToReturn);

		LogAction.addLog(loggedInInfo, "DocumentManager.getUpdateAfterDate", "updatedAfterThisDateExclusive=" + updatedAfterThisDateExclusive, "", "", "Number items " + itemsToReturn);

		return (results);
	}
	
	public List<Document> getDocumentsByDemographicIdUpdateAfterDate(LoggedInInfo loggedInInfo, Integer demographicId, Date updatedAfterThisDateExclusive) {
		List<Document> results = new ArrayList<Document>();
		ConsentType consentType = patientConsentManager.getProviderSpecificConsent(loggedInInfo);
		if (patientConsentManager.hasPatientConsented(demographicId, consentType)) {
			results = documentDao.findByDemographicUpdateAfterDate(demographicId, updatedAfterThisDateExclusive);
			LogAction.addLogSynchronous(loggedInInfo, "DocumentManager.getDocumentsByDemographicIdUpdateAfterDate", "demographicId="+demographicId+" updatedAfterThisDateExclusive="+updatedAfterThisDateExclusive);
		}
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
	
	public Integer saveDocument( LoggedInInfo loggedInInfo, EDoc edoc ) {
		return this.saveDocument( loggedInInfo, edoc.getDocument(), edoc.getCtlDocument() );
	}

	public Integer saveDocument( LoggedInInfo loggedInInfo, Document document, CtlDocument ctlDocument ) {

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
		String path = null; 
		
		if(document != null) {
			path = OscarProperties.getInstance().getProperty("DOCUMENT_DIR");
			
			if(! path.endsWith(File.separator)) {
				path = path + File.separator;
			}
			
			path = path + document.getDocfilename();		
		}
		
		return path;
	}
	
	/**
	 * Fetch by demographic number and given document type 
	 * ie: get only LAB documents for the given demographic number. 
	 * 
	 * @param LoggedInInfo loggedInInfo
	 * @param int demographicNo
	 * @param enum DocumentType
	 * @return
	 */
	public List<Document> getDemographicDocumentsByDocumentType(LoggedInInfo loggedInInfo, int demographicNo, DocumentType documentType) {
		if(!securityInfoManager.hasPrivilege(loggedInInfo, "_newCasemgmt.documents", SecurityInfoManager.READ, null)) {
			throw new RuntimeException("Access Denied");
		}
		
		CtlDocumentPK ctlDocumentPk = new CtlDocumentPK(Module.DEMOGRAPHIC.getName(), demographicNo, null);
		List<CtlDocument> ctlDocumentList = ctlDocumentDao.findByCtlDocumentPK(ctlDocumentPk);
		List<Integer> documentIdList = new ArrayList<Integer>();
		for(CtlDocument ctlDocument : ctlDocumentList)
		{
			documentIdList.add(ctlDocument.getId().getDocumentNo());
		}
		
		LogAction.addLogSynchronous(loggedInInfo, "DocumentManager.getDemographicDocumentsByDocumentType", "fetching documents of type " + documentType.getName() + " for demographic " + demographicNo);
		
		return documentDao.findByIdsAndDoctype(documentIdList, documentType);
	}
	
	/**
	 * Add a document to Oscar's document library.  Don't forget to reference the document 
	 * with addDocumentRelationship - if required.
	 * 
	 *  This method actually saves the Document contents to the file system
	 * 
	 * @param loggedInInfo
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public Document addDocument(LoggedInInfo loggedInInfo, Document document, CtlDocument ctlDocument) throws Exception {
		if(!securityInfoManager.hasPrivilege(loggedInInfo, "_newCasemgmt.documents", SecurityInfoManager.WRITE, null)) {
			throw new RuntimeException("Access Denied");
		}
	
		try {
			EDocUtil.writeDocContent(document.getDocfilename(), document.getBase64Binary());
			saveDocument(loggedInInfo, document, ctlDocument);

			if(document.getId() != null && document.getId() > 0)
			{
				LogAction.addLogSynchronous(loggedInInfo, "DocumentManager.addDocument", "Document Id: " + document.getId());				
				return document;
			}
			
		} catch (Exception e) {
			LogAction.addLogSynchronous(loggedInInfo, "DocumentManager.addDocument", "Exception thrown during document save: " + e.getMessage());
			throw e;
		}
	
		return null;
	}

}
