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


package oscar.oscarTickler.tld;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.oscarehr.common.dao.ViewDao;
import org.oscarehr.common.model.View;
import org.oscarehr.managers.TicklerManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import java.util.Map;


/**
 *
 * @author Jay Gallagher
 */
public class TicklerTag extends TagSupport {


   public TicklerTag() {
	numNewLabs = 0;
   }

   public int doStartTag() throws JspException    {
		HttpServletRequest request=(HttpServletRequest)pageContext.getRequest();
		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);

		if(providerNo!=null){
            String assignedTo = providerNo;
            // get the tickler default setting for task assigned to
            ViewDao viewDao = SpringUtils.getBean(ViewDao.class);
            Map<String, View> settingsMap = viewDao.getView("tickler", (String) loggedInInfo.getSession().getAttribute("userrole"), providerNo);
            if(settingsMap != null && ! settingsMap.isEmpty()) {
                String assignedProvider = settingsMap.get("assignedTo").getValue();
                if(assignedProvider != null && ! assignedProvider.isEmpty()) {
                    assignedTo = assignedProvider;
                }
            }
	    	TicklerManager ticklerManager = SpringUtils.getBean(TicklerManager.class);
	        numNewLabs = ticklerManager.getActiveTicklerCount(loggedInInfo, assignedTo);
	    }  
	   
        try        {
            JspWriter out = super.pageContext.getOut();
            if(numNewLabs > 0) 
                out.print("<span class='tabalert'>  ");
            else
                out.print("<span>  ");
        } catch(Exception p) {MiscUtils.getLogger().error("Error",p);
        }
        return(EVAL_BODY_INCLUDE);
    }


    public void setProviderNo(String providerNo1)    {
       providerNo = providerNo1;
    }

    public String getProviderNo()    {
        return providerNo;
    }


    public int doEndTag()        throws JspException    {
       try{
          JspWriter out = super.pageContext.getOut();
          if (numNewLabs>0)
              out.print("<sup>"+numNewLabs+"</sup></span>");
          else
              out.print("</span>");
       }catch(Exception p) {MiscUtils.getLogger().error("Error",p);
       }
       return EVAL_PAGE;
    }

    private String providerNo;
    private int numNewLabs;
}
