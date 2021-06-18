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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.oscarehr.managers.RBTGroupManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;


public class RBTRemoveFromGroupAction extends Action {
    
	private RBTGroupManager rbtGroupManager = SpringUtils.getBean(RBTGroupManager.class);
	
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                HttpServletRequest request, HttpServletResponse response) {

    	LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request); 
    	
     	String tid = request.getParameter("tid");
     	String groupName = request.getParameter("groupName");
     	rbtGroupManager.remTemplateFromGroup(loggedInInfo, groupName, Integer.parseInt(tid));
     	request.setAttribute("group_view", groupName);
     	
     	return mapping.findForward("success");

    }
    
}
