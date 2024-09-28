//CHECKSTYLE:OFF
/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * <p>
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


// form_class - a part of class name
// c_lastVisited, formId - if the form has multiple pages
package openo.form;

import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.form.util.JasperReportPdfPrint;
import oscar.log.LogAction;
import oscar.log.LogConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

public final class FrmAction extends JSONAction {

    Logger log = org.oscarehr.util.MiscUtils.getLogger();
    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

    @SuppressWarnings("rawtypes")
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {

        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_form", "w", null)) {
            throw new SecurityException("missing required security object (_form)");
        }

        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        int newID = -1;
        FrmRecord rec = null;

        /* submission of this form is a failure by default */
        String where = "failure";
        ActionForward actionForward = mapping.findForward(where);
        boolean saveSuccess = Boolean.FALSE;
        String formClassName = request.getParameter("form_class");
        String submitType = request.getParameter("submit");
        String id = request.getParameter("formId");
        Integer formId = null;

        if (id == null || id.isEmpty()) {
            id = request.getParameter("ID");
        }

        if (id != null && !id.isEmpty()) {
            id = id.trim();
            formId = Integer.parseInt(id);
        }

        try {

            Integer demographicNo = Integer.parseInt(request.getParameter("demographic_no").trim());

            FrmRecordFactory recorder = new FrmRecordFactory();
            rec = recorder.factory(formClassName);
            Properties props = new Properties();

            log.info("SUBMIT " + submitType);

            //if we are graphing, we need to grab info from db and add it to request object
            if ("graph".equals(submitType)) {
                //Rourke needs to know what type of graph is being plotted
                String graphType = request.getParameter("__graphType");
                if (graphType != null) {
                    rec.setGraphType(graphType);
                }

                props = rec.getGraph(loggedInInfo, demographicNo, formId);

                for (Enumeration e = props.propertyNames(); e.hasMoreElements(); ) {
                    String name = (String) e.nextElement();
                    request.setAttribute(name, props.getProperty(name));
                }
                newID = formId;
            }
            //if we are printing all pages of form, grab info from db and merge with current page info
            else if (request.getParameter("submit").equals("printAll") || request.getParameter("submit").equals("printAllJasperReport")) {

                if (rec instanceof JasperReportPdfPrint) {
                    boolean isRourkeForm2020 = true;
                    List<Integer> pagesToPrint = new ArrayList<Integer>();
                    List<String> cfgPages = Arrays.asList(request.getParameterValues("__cfgfile"));
                    for (int i = 1; i <= 4; i++) {
                        if (cfgPages.contains("rourke2017printCfgPg" + i)) {
                            pagesToPrint.add(i);
                            isRourkeForm2020 = false;
                        } else {
                            cfgPages.contains("rourke2020printCfgPg" + i);
                            pagesToPrint.add(i);
                        }
                    }

                    response.setContentType("application/pdf");
                    if (isRourkeForm2020) {
                        response.setHeader("Content-Disposition", "attachment; filename=\"Rourke2020_" + formId + ".pdf\"");
                    } else {
                        response.setHeader("Content-Disposition", "attachment; filename=\"Rourke2017_" + formId + ".pdf\"");
                    }
                    ((JasperReportPdfPrint) rec).PrintJasperPdf(response.getOutputStream(), loggedInInfo, demographicNo, formId, pagesToPrint);
                    newID = 0;
                    return null;
                } else {
                    props = rec.getFormRecord(loggedInInfo, Integer.parseInt(request.getParameter("demographic_no")), formId);

                    String name;
                    for (Enumeration e = props.propertyNames(); e.hasMoreElements(); ) {
                        name = (String) e.nextElement();
                        if (request.getParameter(name) == null) {
                            request.setAttribute(name, props.getProperty(name));
                        }
                    }
                    newID = formId;
                }
            } else if (request.getParameter("update") != null && request.getParameter("update").equals("true")) {
                boolean bMulPage = request.getParameter("c_lastVisited") != null;
                String name;

                if (bMulPage) {
                    String curPageNum = request.getParameter("c_lastVisited");
                    String commonField = request.getParameter("commonField") != null ? request
                            .getParameter("commonField") : "&'";
                    curPageNum = curPageNum.length() > 3 ? ("" + curPageNum.charAt(0)) : curPageNum;
                    Properties currentParam = new Properties();
                    for (Enumeration varEnum = request.getParameterNames(); varEnum.hasMoreElements(); ) {
                        name = (String) varEnum.nextElement();
                        currentParam.setProperty(name, "");
                    }
                    for (Enumeration varEnum = props.propertyNames(); varEnum.hasMoreElements(); ) {
                        name = (String) varEnum.nextElement();
                        // kick off the current page elements, commonField on the current page
                        if (name.startsWith(curPageNum + "_") || (name.startsWith(commonField) && currentParam.containsKey(name))) {
                            props.remove(name);
                        }
                    }
                    props = currentParam;

                }
                //update the current record
                for (Enumeration varEnum = request.getParameterNames(); varEnum.hasMoreElements(); ) {
                    name = (String) varEnum.nextElement();
                    props.setProperty(name, request.getParameter(name));
                }

                props.setProperty("provider_no", (String) request.getSession().getAttribute("user"));
                newID = rec.saveFormRecord(props);
                LogAction.addLog((String) request.getSession().getAttribute("user"), LogConst.UPDATE, request
                        .getParameter("form_class"), "" + newID, request.getRemoteAddr(), request.getParameter("demographic_no"));
            } else if (request.getParameter("submit").equals("autosaveAjax")) {
                quickSaveForm(rec, request, response);
                return null;
            } else if (request.getParameter("submit").equals("saveFormLetter")) {
                for (Enumeration varEnum = request.getParameterNames(); varEnum.hasMoreElements(); ) {
                    String name = (String) varEnum.nextElement();
                    props.setProperty(name, request.getParameter(name));
                }
                props.setProperty("provider_no", (String) request.getSession().getAttribute("user"));
                LogAction.addLog((String) request.getSession().getAttribute("user"), LogConst.UPDATE, request
                        .getParameter("form_class"), "" + newID, request.getRemoteAddr(), request.getParameter("demographic_no"));

                return null;
            } else {
                boolean bMulPage = request.getParameter("c_lastVisited") != null;
                String name;

                if (bMulPage) {
                    String curPageNum = request.getParameter("c_lastVisited");
                    String commonField = request.getParameter("commonField") != null ? request.getParameter("commonField") : "&'";
                    curPageNum = curPageNum.length() > 3 ? ("" + curPageNum.charAt(0)) : curPageNum;

                    //copy an old record
                    props = rec.getFormRecord(loggedInInfo, demographicNo, formId);

                    //empty the current page
                    Properties currentParam = new Properties();
                    for (Enumeration varEnum = request.getParameterNames(); varEnum.hasMoreElements(); ) {
                        name = (String) varEnum.nextElement();
                        currentParam.setProperty(name, "");
                    }
                    for (Enumeration varEnum = props.propertyNames(); varEnum.hasMoreElements(); ) {
                        name = (String) varEnum.nextElement();
                        // kick off the current page elements, commonField on the current page
                        if (name.startsWith(curPageNum + "_")
                                || (name.startsWith(commonField) && currentParam.containsKey(name))) {
                            props.remove(name);
                        }
                    }
                }

                //update the current record
                for (Enumeration varEnum = request.getParameterNames(); varEnum.hasMoreElements(); ) {
                    name = (String) varEnum.nextElement();
                    props.setProperty(name, request.getParameter(name));
                }

                props.setProperty("provider_no", (String) request.getSession().getAttribute("user"));
                newID = rec.saveFormRecord(props);

                if (newID > 0) {
                    log.info(formClassName + " new form ID " + newID + " successfully saved.");
                    saveSuccess = Boolean.TRUE;
                } else {
                    log.info(formClassName + " form ID " + formId + " failed to save.");
                }

                String ip = request.getRemoteAddr();
                LogAction.addLog((String) request.getSession().getAttribute("user"),
                        LogConst.ADD,
                        formClassName,
                        "" + newID,
                        ip,
                        demographicNo + "");

            }

            /*
             * Forward to the proper link based on the submitType if this form validates
             * and is successfully saved.
             */
            if (newID > -1) {
                String strAction = rec.findActionValue(submitType);
                actionForward = mapping.findForward(strAction);
                where = actionForward.getPath();
                where = rec.createActionURL(where, strAction, demographicNo + "", "" + newID);
                actionForward = new ActionForward(where);
            }

        } catch (Exception ex) {
            // throw new ServletException(ex);
            MiscUtils.getLogger().error("Exception for form " + formClassName + " Save failed.", ex);
        }

        log.info("Forwarding form " + formClassName + " to " + actionForward.getPath());

        request.setAttribute("saveSuccess", saveSuccess);

        return actionForward;
    }

    private void quickSaveForm(FrmRecord formRecord, HttpServletRequest request, HttpServletResponse response) {
        Properties props = new Properties();
        for (Enumeration<String> varEnum = request.getParameterNames(); varEnum.hasMoreElements(); ) {
            String name = varEnum.nextElement();
            props.setProperty(name, request.getParameter(name));
        }
        props.setProperty("provider_no", (String) request.getSession().getAttribute("user"));
        try {
            int newFormId = formRecord.saveFormRecord(props);
            LogAction.addLog((String) request.getSession().getAttribute("user"),
                    LogConst.ADD, request.getParameter("form_class"), String.valueOf(newFormId),
                    request.getRemoteAddr(), request.getParameter("demographic_no"));


            String newUrl = "?formname=" + props.getProperty("form_class") +
                    "&demographic_no=" + props.getProperty("demographic_no") +
                    (StringUtils.isNotEmpty(props.getProperty("remoteFacilityId")) ? "&remoteFacilityId=" + props.getProperty("remoteFacilityId") : "") +
                    (StringUtils.isNotEmpty(props.getProperty("appointmentNo")) ? "&appointmentNo=" + props.getProperty("appointmentNo") : "") +
                    "&formId=" + newFormId;

            JSONObject jsonObject = new JSONObject();
            jsonObject.element("success", true);
            jsonObject.element("newFormId", newFormId);
            jsonObject.element("newNewUrl", newUrl);
            jsonObject.element("formAutosaveDate", new SimpleDateFormat("h:mm a").format(new Date()));
            jsonResponse(response, jsonObject);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
