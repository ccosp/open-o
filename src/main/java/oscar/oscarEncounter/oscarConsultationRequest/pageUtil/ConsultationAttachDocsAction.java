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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.model.EFormData;
import org.oscarehr.managers.ConsultationManager;
import org.oscarehr.managers.FormsManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import oscar.eform.EFormUtil;
import oscar.oscarEncounter.data.EctFormData;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;

public class ConsultationAttachDocsAction extends DispatchAction {


	@SuppressWarnings("unused")
	public ActionForward fetchAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) {
		
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String demographicNo = request.getParameter("demographicNo");
		String requestId = request.getParameter("requestId");
        FormsManager formsManager = SpringUtils.getBean(FormsManager.class);
        ConsultationManager consultationManager = SpringUtils.getBean(ConsultationManager.class);
		
        List<EDoc> allDocuments = EDocUtil.listDocs(loggedInInfo, "demographic", demographicNo, null, EDocUtil.PRIVATE, EDocUtil.EDocSort.OBSERVATIONDATE);
        CommonLabResultData commonLabResultData = new CommonLabResultData();
        List<LabResultData> allLabs = commonLabResultData.populateLabResultsData(loggedInInfo, "",demographicNo, "", "","","U");
        Collections.sort(allLabs); 
        
		List<EDoc> attachedDocuments = EDocUtil.listDocs(loggedInInfo, demographicNo, requestId, EDocUtil.ATTACHED);
        List<LabResultData> attachedLabs = commonLabResultData.populateLabResultsData(loggedInInfo, demographicNo, requestId, CommonLabResultData.ATTACHED);
        List<String> attachedDocumentIds = new ArrayList<String>();
        List<String> attachedLabIds = new ArrayList<String>();
        List<String> attachedFormIds = new ArrayList<String>();
        List<String> attachedEFormIds = new ArrayList<String>();

        List<EctFormData.PatientForm> allForms = formsManager.getLatestVersionOfEncounterFormsbyDemographicNumber(loggedInInfo, Integer.parseInt(demographicNo));

        List<EctFormData.PatientForm> attachedForms = null;
        if(requestId != null && ! requestId.isEmpty() && ! "null".equals(requestId)) {
           attachedForms = consultationManager.getAttachedForms(loggedInInfo, Integer.parseInt(requestId), Integer.parseInt(demographicNo));
        }

        List<EFormData> allEForms = EFormUtil.listPatientEformsCurrent(new Integer(demographicNo), true, 0, 100);

        List<EFormData> attachedEForms = null;
        if(requestId != null && ! requestId.isEmpty() && ! "null".equals(requestId)) {
           attachedEForms = consultationManager.getAttachedEForms(requestId);
        }

        if(attachedDocuments != null) {      	
        	for(EDoc document : attachedDocuments) {
        		attachedDocumentIds.add(document.getDocId());
        	}
        }
        
        if(attachedLabs != null) {
           	for(LabResultData labResultData : attachedLabs) {
           		attachedLabIds.add(labResultData.segmentID);
        	}
        }

        if(attachedForms != null) {
            for(EctFormData.PatientForm attachedForm : attachedForms) {
                attachedFormIds.add(attachedForm.formId+"");
            }
        }

        if(attachedEForms != null) {
            for(EFormData attachedEForm : attachedEForms) {
                attachedEFormIds.add(attachedEForm.getId()+"");
            }
        }
        
        request.setAttribute("attachedDocumentIds", attachedDocumentIds);
        request.setAttribute("attachedLabIds", attachedLabIds);
        request.setAttribute("attachedFormIds", attachedFormIds);
        request.setAttribute("attachedEFormIds", attachedEFormIds);


        request.setAttribute("allDocuments", allDocuments);
        request.setAttribute("allLabs", allLabs);
        request.setAttribute("allForms", allForms);
        request.setAttribute("allEForms", allEForms);

		return mapping.findForward("fetchAll");
	} 
}
