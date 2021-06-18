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
package oscar.oscarResearch.oscarDxResearch.pageUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.dao.DiagnosticCodeDao;
import org.oscarehr.common.dao.Icd10Dao;
import org.oscarehr.common.dao.Icd9Dao;
import org.oscarehr.common.model.DiagnosticCode;
import org.oscarehr.common.model.Icd10;
import org.oscarehr.common.model.Icd9;
import org.oscarehr.managers.CodingSystemManager;
import org.oscarehr.util.JsonUtil;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import net.sf.json.JSONObject;

public class dxCodeSearchJSONAction extends DispatchAction {

	private static Logger logger = MiscUtils.getLogger(); 
	
	public ActionForward unspecified(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {
		return null;
	}
	
	@SuppressWarnings("unused")
	public ActionForward searchICD9(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {

		String keyword = request.getParameter("keyword");
		keyword = StringUtils.trimToEmpty(keyword);
		Icd9Dao dao = SpringUtils.getBean(Icd9Dao.class);		
		List<Icd9> icd9List =  dao.getIcd9(keyword);

		try {
	        jsonify(icd9List, response, new String [] {
	    			"handler", 
	    			"hibernateLazyInitializer"});
        } catch (IOException e) {
        	logger.error("JSON Error", e);
        }

		return null;
	}
	
	@SuppressWarnings("unused")
	public ActionForward searchICD10(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {

		String keyword = request.getParameter("keyword");
		keyword = StringUtils.trimToEmpty(keyword);
		Icd10Dao dao = SpringUtils.getBean(Icd10Dao.class);		
		List<Icd10> icd10List = dao.searchCode(keyword);
		
		try {
	        jsonify(icd10List, response, new String [] {
		    			"handler", 
		    			"hibernateLazyInitializer"});
        } catch (IOException e) {
        	logger.error("JSON Error", e);
        }

		return null;
	}
	
	/**
	 * This method searches the table by text description only. NOT by code
	 * This is intentional for smooth operation in BC Billing.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unused")
	public ActionForward searchMSP(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {

		String keyword = request.getParameter("keyword");
		keyword = StringUtils.trimToEmpty(keyword);
		DiagnosticCodeDao dao = SpringUtils.getBean(DiagnosticCodeDao.class);		
		List<DiagnosticCode> mspCodeList =  dao.search(keyword);
		
		try {
	        jsonify(mspCodeList, response, new String [] {
		    			"handler", 
		    			"hibernateLazyInitializer"});
        } catch (IOException e) {
        	logger.error("JSON Error", e);
        }

		return null;
	}
	
	@SuppressWarnings("unused")
	public ActionForward validateCode(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {

		String code = request.getParameter("keyword");
		code = StringUtils.trimToEmpty(code);
		String codeSystem = request.getParameter("codeSystem");
		CodingSystemManager codingSystemManager = SpringUtils.getBean(CodingSystemManager.class);
		boolean dxvalid = codingSystemManager.isCodeAvailable(codeSystem, code);
		
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.accumulate("dxvalid", dxvalid);
		
		response.setContentType("text/x-json");
		
		try(PrintWriter pout = response.getWriter()) {
			jsonResponse.write(pout);
		} catch (IOException e) {
        	logger.error("JSON Error", e);
		}
		
		return null;
	}

	@SuppressWarnings("unused")
	public ActionForward getDescription(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {

		String code = request.getParameter("keyword");
		code = StringUtils.trimToEmpty(code);
		String codeSystem = request.getParameter("codeSystem");
		
		CodingSystemManager codingSystemManager = SpringUtils.getBean(CodingSystemManager.class);
		String description = codingSystemManager.getCodeDescription(codeSystem, code);	
		boolean dxvalid = (description != null);
		
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.accumulate("dxvalid", dxvalid);
		jsonResponse.accumulate("description", description);
		jsonResponse.accumulate("code", code);
		
		response.setContentType("text/x-json");
		
		try(PrintWriter pout = response.getWriter()) {
			jsonResponse.write(pout);
		} catch (IOException e) {
        	logger.error("JSON Error", e);
		}
		
		return null;
	}

	private static void jsonify(final List<?> classList, 
	    		final HttpServletResponse response, String[] ignoreMethods) throws IOException {

		response.setContentType("text/x-json");
		
		String jsonstring = null;
		
		if(classList != null && ! classList.isEmpty())
		{
			jsonstring = JsonUtil.pojoCollectionToJson(classList, ignoreMethods);
		}
		
		if(jsonstring == null)
		{
			jsonstring = "{}";
		}

		try(PrintWriter pout = response.getWriter()) {
			pout.write(jsonstring);
		}         
    }
	 
		
}
