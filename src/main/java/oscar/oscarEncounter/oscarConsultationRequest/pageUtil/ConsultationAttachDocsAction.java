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


package oscar.oscarEncounter.oscarConsultationRequest.pageUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.DocumentException;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.model.ConsultDocs;
import org.oscarehr.common.model.EFormData;
import org.oscarehr.hospitalReportManager.HRMUtil;
import org.oscarehr.hospitalReportManager.HRMPDFCreator;
import org.oscarehr.managers.ConsultationManager;
import org.oscarehr.managers.FaxManager;
import org.oscarehr.managers.FormsManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import oscar.eform.EFormUtil;
import oscar.form.util.FormTransportContainer;
import oscar.oscarEncounter.data.EctFormData;
import oscar.oscarLab.ca.all.pageUtil.LabPDFCreator;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;

public class ConsultationAttachDocsAction extends DispatchAction {

    private final Logger logger = MiscUtils.getLogger();
    FaxManager faxManager = SpringUtils.getBean(FaxManager.class);

	@SuppressWarnings("unused")
	public ActionForward fetchAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) {
		
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String demographicNo = request.getParameter("demographicNo");
		String requestId = request.getParameter("requestId");
        FormsManager formsManager = SpringUtils.getBean(FormsManager.class);
        ConsultationManager consultationManager = SpringUtils.getBean(ConsultationManager.class);                

        //Get all LAB information for demographic, along with which are already attached
        List<String> attachedLabIds = new ArrayList<String>();
        CommonLabResultData commonLabResultData = new CommonLabResultData();
        List<LabResultData> allLabs = commonLabResultData.populateLabResultsData(loggedInInfo, "",demographicNo, "", "","","U");
        Collections.sort(allLabs); 
        List<LabResultData> attachedLabs = commonLabResultData.populateLabResultsData(loggedInInfo, demographicNo, requestId, CommonLabResultData.ATTACHED);
        if(attachedLabs != null) {
           	for(LabResultData labResultData : attachedLabs) {
           		attachedLabIds.add(labResultData.segmentID);
        	}
        }

        //Get all DOCUMENT information for demographic, along with which are already attached
        List<String> attachedDocumentIds = new ArrayList<String>();
        List<EDoc> allDocuments = EDocUtil.listDocs(loggedInInfo, "demographic", demographicNo, null, EDocUtil.PRIVATE, EDocUtil.EDocSort.OBSERVATIONDATE);
        List<EDoc> attachedDocuments = EDocUtil.listDocs(loggedInInfo, demographicNo, requestId, EDocUtil.ATTACHED);                        
        if(attachedDocuments != null) {      	
        	for(EDoc document : attachedDocuments) {
        		attachedDocumentIds.add(document.getDocId());
        	}
        }

        //Get all FORM information for demographic, along with which are already attached
        List<String> attachedFormIds = new ArrayList<String>();
        List<EctFormData.PatientForm> allForms = formsManager.getEncounterFormsbyDemographicNumber(loggedInInfo, Integer.parseInt(demographicNo), false, true);
        List<EctFormData.PatientForm> attachedForms = null;
        if(requestId != null && ! requestId.isEmpty() && ! "null".equals(requestId)) {
           attachedForms = consultationManager.getAttachedForms(loggedInInfo, Integer.parseInt(requestId), Integer.parseInt(demographicNo));
           if(attachedForms != null) {
                for(EctFormData.PatientForm attachedForm : attachedForms) {
                    attachedFormIds.add(attachedForm.formId+"");
                }
            }
        }
        
        //Get all EFORM information for demographic, along with which are already attached
        List<String> attachedEFormIds = new ArrayList<String>();
        List<EFormData> allEForms = EFormUtil.listPatientEformsCurrent(new Integer(demographicNo), true);
        List<EFormData> attachedEForms = null;
        if(requestId != null && ! requestId.isEmpty() && ! "null".equals(requestId)) {
           attachedEForms = consultationManager.getAttachedEForms(requestId);
            if(attachedEForms != null) {
                for(EFormData attachedEForm : attachedEForms) {
                    attachedEFormIds.add(attachedEForm.getId()+"");
                }
            }
        }    

        //Get all HRM information for demographic, along with which are already attached
        List<String> attachedHRMDocumentIds = new ArrayList<String>();        
        ArrayList<HashMap<String,? extends Object>> allHRMDocuments = HRMUtil.listHRMDocuments(loggedInInfo, "report_date", false, demographicNo,false);
        List<ConsultDocs> attachedHRMDocuments = null;
        if(requestId != null && ! requestId.isEmpty() && ! "null".equals(requestId)) {
            attachedHRMDocuments = consultationManager.getAttachedDocumentsByType(loggedInInfo, Integer.parseInt(requestId), ConsultDocs.DOCTYPE_HRM);
            if(attachedHRMDocuments != null) {
                for (ConsultDocs consultDocs : attachedHRMDocuments) {
                    attachedHRMDocumentIds.add(String.valueOf(consultDocs.getDocumentNo()));
                }
            }
        }        
        
        request.setAttribute("attachedDocumentIds", attachedDocumentIds);
        request.setAttribute("attachedLabIds", attachedLabIds);
        request.setAttribute("attachedFormIds", attachedFormIds);
        request.setAttribute("attachedEFormIds", attachedEFormIds);
        request.setAttribute("attachedHRMDocumentIds", attachedHRMDocumentIds);


        request.setAttribute("allDocuments", allDocuments);
        request.setAttribute("allLabs", allLabs);
        request.setAttribute("allForms", allForms);
        request.setAttribute("allEForms", allEForms);
        request.setAttribute("allHRMDocuments", allHRMDocuments);

        return mapping.findForward("fetchAll");
    } 
}
