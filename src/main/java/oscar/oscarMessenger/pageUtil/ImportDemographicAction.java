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

package oscar.oscarMessenger.pageUtil;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.managers.MessengerDemographicManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

public class ImportDemographicAction extends Action {
	
	private static MessengerDemographicManager messengerDemographicManager = SpringUtils.getBean(MessengerDemographicManager.class); 

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
    {   	
	
    	String remoteFacilityId = request.getParameter("remoteFacilityId");
    	String remoteDemographicNo = request.getParameter("remoteDemographicNo");
    	String messageID = request.getParameter("messageID");
    	String selectedDemographicNo = request.getParameter("selectedDemographicNo");

    	LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
    	
    	if(loggedInInfo.getCurrentFacility().isIntegratorEnabled())
    	{
    		List<Demographic> demographicList = messengerDemographicManager.importDemographic(loggedInInfo, 
    				Integer.parseInt(remoteFacilityId), Integer.parseInt(remoteDemographicNo), Integer.parseInt(messageID));
    		if(demographicList != null)
    		{
    			request.setAttribute("demographicUserSelect", demographicList);
    			request.setAttribute("remoteDemographicNo", remoteDemographicNo);
    		}
    	}
    	
    	if(selectedDemographicNo != null) 
    	{
    		messengerDemographicManager.linkDemographicWithRemote(loggedInInfo, 
    				Integer.parseInt(selectedDemographicNo), Integer.parseInt(remoteFacilityId), Integer.parseInt(remoteDemographicNo), Integer.parseInt(messageID));
    	}
    	
    	request.setAttribute("boxType", "0");
    	request.setAttribute("messageID", messageID);

    	return mapping.findForward("success");
    }
}
