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
package org.oscarehr.ws.rest;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.oscarehr.common.model.Document;
import org.oscarehr.common.model.EncounterForm;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.managers.DocumentManager;
import org.oscarehr.managers.EformDataManager;
import org.oscarehr.managers.FormsManager;
import org.oscarehr.managers.LabManager;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.to.DocumentResponse;
import org.oscarehr.ws.rest.to.model.DocumentCategory;
import org.oscarehr.ws.rest.to.model.DocumentTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import oscar.form.util.FormTransportContainer;
import oscar.oscarEncounter.data.EctFormData.PatientForm;

@Path("/patientDocuments")
@Component("documentService")
public class DocumentService extends AbstractServiceImpl {
	
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	
	@Autowired
	SecurityInfoManager securityInfoManager;
	
	@Autowired
	private DocumentManager documentManager;
	
	@Autowired
	private FormsManager formsManager;
	
	@Autowired
	private LabManager labManager;
	
	@Autowired 
	private EformDataManager eformDataManager;

	@GET
	@Path("documentCategories")
	@Produces(MediaType.APPLICATION_JSON) 
	public Response getDocumentCategories() {
		return Response.status(Status.OK).entity(DocumentCategory.values()).build();
	}
	
	@GET
	@Path("documentTables")
	@Produces(MediaType.APPLICATION_JSON) 
	public Response getDocumentTables() {
		List<EncounterForm> encounterFormList = formsManager.getAllEncounterForms();
		return Response.status(Status.OK).entity(encounterFormList).build();
	}
	
	@GET
	@Path("/allDocuments/{demographicNo}")
	@Produces(MediaType.APPLICATION_JSON)
	public DocumentResponse getAllDocumentsByDemographicNo(@PathParam("demographicNo") int demographicNo){
		DocumentResponse documentResponse = new DocumentResponse();
		for(DocumentCategory category : DocumentCategory.values()) 
		{
			documentResponse.mergeAll( getAllDocumentsByDemographicNoAndCategory( demographicNo, category.getCategory() ) ); 
		} 		 
		return documentResponse;
	}
	
	@GET
	@Path("/allDocuments/{demographicNo}/{documentCategory}")
	@Produces(MediaType.APPLICATION_JSON)
	public DocumentResponse getAllDocumentsByDemographicNoAndCategory(
			@PathParam("demographicNo") int demographicNo, 
			@PathParam("documentCategory") String documentCategory){
		DocumentResponse documentResponse;
		String category = documentCategory.trim().toUpperCase();
		
		switch(DocumentCategory.valueOf(category)) 
		{
		case EDOCUMENT: documentResponse = getDocumentsByDemographicNo(demographicNo);
			break;
		case EFORM: documentResponse = getEFormsByDemographicNo(demographicNo);
			break;
		case FORM: documentResponse = getFormsByDemographicNo(demographicNo);
			break;
		case HL7LAB: documentResponse = getHL7LabsByDemographicNo(demographicNo);
			break;
		default: documentResponse = null;
			break;
		}
		
		return documentResponse; 
	}
	
	@GET
	@Path("/documentStream/{documentId}/{documentCategory}/{documentTable}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response streamDocumentData(
			@PathParam("documentId") int documentId, 
			@PathParam("documentCategory") String documentCategory,
			@PathParam("documentTable") String documentTable){
		Response response;
		String category = documentCategory.trim().toUpperCase();
		
		switch(DocumentCategory.valueOf(category))
		{
		case EDOCUMENT: response = getDocument(documentId);
			break;
		case EFORM: response = getEForm(documentId);
			break;
		case FORM: response = getForm(documentId, documentTable);
			break;
		case HL7LAB: response = getHL7Lab(documentId);
			break;
		default: response = Response.status(Status.NO_CONTENT).build();
			break;
		}
		
		return response;
	}

	@GET
	@Path("/edocuments/{demographicNo}")
	@Produces(MediaType.APPLICATION_JSON)
	public DocumentResponse getDocumentsByDemographicNo(@PathParam("demographicNo") int demographicNo){
		List<Document> documents = documentManager.getDocumentsByDemographicNo(getLoggedInInfo(), demographicNo);
		DocumentResponse documentResponse = new DocumentResponse();
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		
		for(Document document : documents) {
			int id = document.getId();
			String name = document.getDocdesc();
			if(name == null || name == "") {
				name = document.getDocfilename();
			}
			Date documentDate = document.getContentdatetime();	
			String dateString = dateFormat.format(documentDate);
			DocumentTo1 documentTo1 = new DocumentTo1( id, name, dateString, DocumentCategory.EDOCUMENT.name(), "application/pdf");
			documentTo1.setContentType(document.getContenttype());
			documentTo1.setDocumentDescription(document.getDocdesc());
			documentResponse.add( documentTo1 );			
		}
		
		// sort response by date descending
		if(! documentResponse.getDocuments().isEmpty()) {
			Collections.sort(documentResponse.getDocuments(), Collections.reverseOrder());
		}

		return documentResponse;
	}
	
	@GET
	@Path("/eforms/{demographicNo}")
	@Produces(MediaType.APPLICATION_JSON)
	public DocumentResponse getEFormsByDemographicNo(@PathParam("demographicNo") int demographicNo){
		DocumentResponse documentResponse = new DocumentResponse();
		List<Map<String,Object>> eformList = eformDataManager.findCurrentByDemographicIdNoData(getLoggedInInfo(), demographicNo);
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		
		for(Map<String,Object> eformData : eformList) {
			Date eformDate = (Date) eformData.get("formDate");
			String dateString = dateFormat.format(eformDate);
			documentResponse.add( new DocumentTo1( 
					(Integer) eformData.get("id"), 
					(String) eformData.get("formName"),
					dateString,
					DocumentCategory.EFORM.name(), 
					"application/pdf") );
		}
		
		if(! documentResponse.getDocuments().isEmpty()) {
			Collections.sort(documentResponse.getDocuments(), Collections.reverseOrder());
		}
		
		return documentResponse;
	}
	
	@GET
	@Path("/hl7labs/{demographicNo}")
	@Produces(MediaType.APPLICATION_JSON)
	public DocumentResponse getHL7LabsByDemographicNo(@PathParam("demographicNo") int demographicNo){
		DocumentResponse documentResponse = new DocumentResponse();
		List<Hl7TextInfo> hl7TextInfoList = labManager.getHl7TextInfo(getLoggedInInfo(), demographicNo);

		for(Hl7TextInfo hl7TextInfo : hl7TextInfoList) {
			documentResponse.add( new DocumentTo1( 
					hl7TextInfo.getLabNumber(), 
					hl7TextInfo.getDiscipline(),
					hl7TextInfo.getObrDate(),
					DocumentCategory.HL7LAB.name(), 
					"application/pdf") );
		}
		
		if(! documentResponse.getDocuments().isEmpty()) {
			Collections.sort(documentResponse.getDocuments(), Collections.reverseOrder());
		}
		
		return documentResponse;
	}
	
	@GET
	@Path("/forms/{demographicNo}")
	@Produces(MediaType.APPLICATION_JSON)
	public DocumentResponse getFormsByDemographicNo(@PathParam("demographicNo") int demographicNo){
		
		DocumentResponse documentResponse = new DocumentResponse();
		List<PatientForm> patientFormList = formsManager.getEncounterFormsbyDemographicNumber(getLoggedInInfo(), demographicNo);
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		
		for(PatientForm patientForm : patientFormList) {
			Date dateCreated = patientForm.created;
			String dateString = "";
			if(dateCreated != null) {
				dateString = dateFormat.format(dateCreated);
			}
			documentResponse.add( new DocumentTo1( 
					Integer.parseInt(patientForm.getFormId()), 
					patientForm.getFormName(),
					dateString,
					DocumentCategory.FORM.name(),
					"application/pdf",
					patientForm.getTable()) );
		}
		
		if(! documentResponse.getDocuments().isEmpty()) {
			Collections.sort(documentResponse.getDocuments(), Collections.reverseOrder());
		}
		
		return documentResponse;
	}

	@GET
	@Path("/edocument/{documentId}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getDocument(@PathParam("documentId") int documentId){
		Document document = documentManager.getDocument(getLoggedInInfo(), documentId);
		String path = documentManager.getFullPathToDocument(document.getDocfilename());
		ResponseBuilder responseBuilder = getFile(path);
		String contentType = document.getContenttype();
		if(contentType != null && ! contentType.isEmpty())
		{
			// default is preset to application/octet_stream
			responseBuilder.header("Content-Type", contentType);
		}
		return responseBuilder.build();
	}
	
	@GET
	@Path("/eform/{eformId}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getEForm(@PathParam("eformId") int eformId){		
		java.nio.file.Path path = eformDataManager.createEformPDF(getLoggedInInfo(), eformId);	
		return getFile(path).header("Content-Type", "application/pdf").build();
	}
	
	@GET
	@Path("/hl7Lab/{hl7LabId}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getHL7Lab(@PathParam("hl7LabId") int hl7LabId){
		java.nio.file.Path path = labManager.getHl7MessageAsPDF(getLoggedInInfo(), hl7LabId);
		return getFile(path).header("Content-Type", "application/pdf").build();
	}

	@GET
	@Path("/form/{formId}/{documentTable}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getForm(@PathParam("formId") int formId, @PathParam("documentTable") String documentTable) {
			
		ResponseBuilder response = Response.status(Status.NO_CONTENT);
		if(documentTable != null && documentTable != "") 
		{	
			StringBuilder formPath = new StringBuilder();
			formPath.append("/form/forwardshortcutname.jsp");
			formPath.append(String.format("?%1$s=%2$s", "formname", documentTable));
			formPath.append(String.format("&%1$s=%2$s", "demographic_no", ""));
			formPath.append(String.format("&%1$s=%2$s", "formId", formId));
			java.nio.file.Path path = null;
			
			Message message = PhaseInterceptorChain.getCurrentMessage();
			HttpServletRequest httpServletRequest = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
			HttpServletResponse httpServletResponse = (HttpServletResponse) message.get(AbstractHTTPDestination.HTTP_RESPONSE);
			
			try {
				FormTransportContainer formTransportContainer 
					= new FormTransportContainer( httpServletResponse, httpServletRequest, formPath.toString());
				path = formsManager.saveFormAsTempPdf(getLoggedInInfo(), formTransportContainer);
			} catch (ServletException e) {
				MiscUtils.getLogger().error("Error fetching form ", e);
			} catch (IOException e) {
				MiscUtils.getLogger().error("Error fetching form ", e);
			}

			if(path != null) {
				response = getFile(path); 
			}
		}

		return response.header("Content-Type", "application/pdf").build();
	}
	
	private final ResponseBuilder getFile(final String filePath) {
		java.nio.file.Path path = null;
		if(filePath != null && ! filePath.isEmpty()) {
			path = FileSystems.getDefault().getPath(filePath);
		}
		return getFile(path);
	}

	private final ResponseBuilder getFile(final java.nio.file.Path path) {
		
		if ( ! securityInfoManager.hasPrivilege(getLoggedInInfo(), "_edoc", SecurityInfoManager.READ, null)) {
			throw new RuntimeException("missing required security object (_edoc)");
		}
		
	    ResponseBuilder response = Response.status(Status.NO_CONTENT);	

	    if(path != null && path.toFile().exists()) {
		    response = Response.ok( path.toFile() );
		    response.header("Content-Disposition", "attachment; filename=" + path.getFileName());
	    }
	    
	    return response;
	}
	
}
