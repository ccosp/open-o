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

package oscar.oscarReport.reportByTemplate.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.common.model.RBTGroup;
import org.oscarehr.managers.RBTGroupManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

import oscar.oscarReport.reportByTemplate.ReportManager;
import oscar.oscarReport.reportByTemplate.ReportObjectGeneric;

public class RBTGetTemplatesInGroupAction extends Action {

	private RBTGroupManager rbtGroupManager = SpringUtils.getBean(RBTGroupManager.class);
	
	private ReportManager reportManager = new ReportManager();
	
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                HttpServletRequest request, HttpServletResponse response) {

    	LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
    	String groupName = request.getParameter("groupName");   	
    	List<ReportObjectGeneric> templates = reportManager.getReportTemplatesNoParam();   	
    	Map<Integer, ReportObjectGeneric> templatesMap = new HashMap<Integer, ReportObjectGeneric>();
    	List<RBTGroup> templatesInGroup = rbtGroupManager.getGroup(loggedInInfo, groupName);	
 	
      	/*
      	 * Create a new master map of all templates.
      	 */
    	for (ReportObjectGeneric template : templates)
    	{ 	   		
    		templatesMap.put(Integer.parseInt(template.getTemplateId()), template);
    	}
    	
    	/*
    	 * Copy the main template map for sorting. 
    	 */
    	Map<Integer, ReportObjectGeneric> templatesNotInGroupMap = new HashMap<Integer, ReportObjectGeneric>(templatesMap);
    	
    	/*
    	 * Remove templates that are already included in this group (groupName)
    	 * This map will display a select list of available templates to be 
    	 * added to this group (groupName)
    	 */
    	for(RBTGroup rbtGroup : templatesInGroup)
    	{
    		templatesNotInGroupMap.remove(rbtGroup.getTemplateId());	
    	}

    	/*
    	 * Templates that are not contained in the given group (groupName)
    	 */
    	request.setAttribute("templatesNotInGroup", templatesNotInGroupMap.values());
    	
    	/*
    	 * All templates in database.
    	 */
    	request.setAttribute("templates", templatesMap);
    	
    	/*
    	 * Template id's that are contained in the given group (groupName)
    	 */
    	request.setAttribute("templatesInGroup", templatesInGroup);
    	
    	return mapping.findForward("success");
         
    }

}
