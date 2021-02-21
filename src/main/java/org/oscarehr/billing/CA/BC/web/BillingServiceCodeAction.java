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
package org.oscarehr.billing.CA.BC.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.caisi_integrator.util.MiscUtils;
import org.oscarehr.managers.BillingManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

import net.sf.json.JSONObject;

public class BillingServiceCodeAction extends DispatchAction {

	private static BillingManager billingManager = SpringUtils.getBean(BillingManager.class);
	
    @Override
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
    	return null;
    }
    
    /**
     * Update the description of an MSP Service Fee Code. 
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return success : false|true
     */
    @SuppressWarnings("unused")
	public void updateDescription(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
    	LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
    	String description = request.getParameter("codedescription");
    	String codeid = request.getParameter("codeid");
    	description = StringEscapeUtils.escapeSql(description);
    	description = StringEscapeUtils.escapeJavaScript(description);

		JSONObject jsonObject = new JSONObject();
		
    	int updatedCodeId = 0;
    	
    	if(codeid != null && ! codeid.isEmpty())
    	{
    		updatedCodeId = billingManager.updateMSPServiceCodeDescription(loggedInInfo, Integer.parseInt(codeid), description);
    	}
    	
		jsonObject.accumulate("codeid", updatedCodeId);
    	
    	if(updatedCodeId > 0)
    	{
    		jsonObject.accumulate("success", true);
    	}
    	else
    	{
    		jsonObject.accumulate("success", false);
    	}
    	
		try(PrintWriter out = response.getWriter())
		{
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			out.print(jsonObject);
			out.flush();
		} catch (IOException e) {
			MiscUtils.getLogger().error("JSON Error", e);
		}
    }
	
}
