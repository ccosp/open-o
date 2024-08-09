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


package oscar.oscarProvider.pageUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;


public class ProEditPhoneNumAction extends Action {

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
        String forward;
        String providerNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();
        if ( providerNo == null)
              return mapping.findForward("eject");

        DynaActionForm frm = (DynaActionForm)form;
        
        UserPropertyDAO propertyDao = (UserPropertyDAO)SpringUtils.getBean(UserPropertyDAO.class);
		UserProperty prop = propertyDao.getProp(providerNo,"rxPhone");			
		if(prop != null) {
			prop.setValue((String)frm.get("faxNumber"));			
		} else {
			prop = new UserProperty();
			prop.setName("rxPhone");
			prop.setProviderNo(providerNo);
			prop.setValue((String)frm.get("faxNumber"));
		}
        propertyDao.saveProp(prop);
        
       request.setAttribute("status",new String("complete"));
       forward = new String("success");
        
        
        return mapping.findForward(forward);
        
    }
}
