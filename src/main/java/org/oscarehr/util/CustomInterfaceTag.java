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


package org.oscarehr.util;

import java.io.IOException;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.logging.log4j.Logger;
import org.oscarehr.provider.web.CppPreferencesUIBean;

import org.owasp.encoder.Encode;
import oscar.OscarProperties;

public class CustomInterfaceTag extends TagSupport {

    Logger logger = MiscUtils.getLogger();
    private String name;
    private String section;

    @Override
    public int doStartTag() throws JspException {
        OscarProperties props = OscarProperties.getInstance();
        String customJs = props.getProperty("cme_js");

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

        if (name != null && !name.isEmpty()) {
            customJs = name;
        }
        if (customJs == null || customJs.isEmpty()) {
            customJs = "default";
        }

        if (customJs.equals("default") && getSection().equals("cme")) {
            //check preferences
            CppPreferencesUIBean bean = new CppPreferencesUIBean(loggedInInfo.getLoggedInProviderNo());
            bean.loadValues();
            if (bean.getEnable() != null && bean.getEnable().equals("on")) {
                logger.info("Use preference based echart");
                try {
                    JspWriter out = super.pageContext.getOut();
                    out.println(this.getPreferenceBasedEChart(bean));
                } catch (IOException e) {
                    logger.error("Error:", e);
                }
                return SKIP_BODY;
            }
        }

        if (customJs != null && !customJs.isEmpty()) {
            JspWriter out = super.pageContext.getOut();
            String contextPath = this.pageContext.getServletContext().getContextPath();
            try {
                if (getSection() != null && !getSection().isEmpty()) {
                    boolean hide_ConReport = props.isPropertyActive("hide_ConReport_link");
                    boolean cardswipe = props.getBooleanProperty("cardswipe", "false");
                    String customTag = "";
                    if (customJs.equalsIgnoreCase("ocean")) {
                        customTag = "ocean-host=" + Encode.forUriComponent(props.getProperty("ocean_host"));
                    }

                    int randomNo = new Random().nextInt();
                    out.println("<script id=\"mainScript\" src=\"" + contextPath + "/js/custom/" + customJs + "/" + getSection() + ".js?no-cache=" + randomNo + "&autoRefresh=true\" hide_ConReport=\"" + hide_ConReport + "\" cardswipe=\"" + cardswipe + "\" " + customTag + " ></script>");
                }
            } catch (IOException e) {
                logger.error("Error", e);
            }
        }
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }


    private String getPreferenceBasedEChart(CppPreferencesUIBean bean) {
        StringBuilder sb = new StringBuilder();
        sb.append("<script>");
        sb.append("jQuery(document).ready(function(){");
        sb.append("issueNoteUrls = {");
        boolean flag = false, row1 = false, row2 = false;
        if (!bean.getSocialHxPosition().equals("")) {
            sb.append("div" + bean.getSocialHxPosition() + ":    ctx + \"/CaseManagementView.do?hc=996633&method=listNotes&providerNo=\" + providerNo + \"&demographicNo=\" + demographicNo + \"&issue_code=SocHistory&title=\" + socHistoryLabel + \"&cmd=div" + bean.getSocialHxPosition() + "\"");
            flag = true;
            if (bean.getSocialHxPosition().startsWith("R1")) {
                row1 = true;
            }
            if (bean.getSocialHxPosition().startsWith("R2")) {
                row2 = true;
            }
        }
        if (!bean.getMedicalHxPosition().equals("")) {
            if (flag) {
                sb.append(",");
            }
            sb.append("div" + bean.getMedicalHxPosition() + ":    ctx + \"/CaseManagementView.do?hc=996633&method=listNotes&providerNo=\" + providerNo + \"&demographicNo=\" + demographicNo + \"&issue_code=MedHistory&title=\" + medHistoryLabel + \"&cmd=div" + bean.getMedicalHxPosition() + "\"");
            flag = true;
            if (bean.getMedicalHxPosition().startsWith("R1")) {
                row1 = true;
            }
            if (bean.getMedicalHxPosition().startsWith("R2")) {
                row2 = true;
            }
        }
        if (!bean.getOngoingConcernsPosition().equals("")) {
            if (flag) {
                sb.append(",");
            }
            sb.append("div" + bean.getOngoingConcernsPosition() + ":    ctx + \"/CaseManagementView.do?hc=996633&method=listNotes&providerNo=\" + providerNo + \"&demographicNo=\" + demographicNo + \"&issue_code=Concerns&title=\" + onGoingLabel + \"&cmd=div" + bean.getOngoingConcernsPosition() + "\"");
            flag = true;
            if (bean.getOngoingConcernsPosition().startsWith("R1")) {
                row1 = true;
            }
            if (bean.getOngoingConcernsPosition().startsWith("R2")) {
                row2 = true;
            }
        }
        if (!bean.getRemindersPosition().equals("")) {
            if (flag) {
                sb.append(",");
            }
            sb.append("div" + bean.getRemindersPosition() + ":    ctx + \"/CaseManagementView.do?hc=996633&method=listNotes&providerNo=\" + providerNo + \"&demographicNo=\" + demographicNo + \"&issue_code=Reminders&title=\" + remindersLabel + \"&cmd=div" + bean.getRemindersPosition() + "\"");
            flag = true;
            if (bean.getRemindersPosition().startsWith("R1")) {
                row1 = true;
            }
            if (bean.getRemindersPosition().startsWith("R2")) {
                row2 = true;
            }
        }
        sb.append("};");

        //can we delete a row?
        if (!row1) {
            sb.append("removeCppRow(1);");
        }
        if (!row2) {
            sb.append("removeCppRow(2);");
        }

        sb.append("init();");

        //show/hide Cpp items
        if ("".equals(bean.getPreventionsDisplay())) {
            sb.append("hideCpp('preventions');");
        }
        if ("".equals(bean.getDxRegistryDisplay())) {
            sb.append("hideCpp('Dx');");
        }
        if ("".equals(bean.getFormsDisplay())) {
            sb.append("hideCpp('forms');");
        }
        if ("".equals(bean.getEformsDisplay())) {
            sb.append("hideCpp('eforms');");
        }
        if ("".equals(bean.getDocumentsDisplay())) {
            sb.append("hideCpp('docs');");
        }
        if ("".equals(bean.getLabsDisplay())) {
            sb.append("hideCpp('labs');");
        }
        if ("".equals(bean.getMeasurementsDisplay())) {
            sb.append("hideCpp('measurements');");
        }
        if ("".equals(bean.getConsultationsDisplay())) {
            sb.append("hideCpp('consultation');");
        }
        if ("".equals(bean.getHrmDisplay())) {
            sb.append("hideCpp('HRM');");
        }
        if ("".equals(bean.getAllergiesDisplay())) {
            sb.append("hideCpp('allergies');");
        }
        if ("".equals(bean.getMedicationsDisplay())) {
            sb.append("hideCpp('Rx');");
        }
        if ("".equals(bean.getOtherMedsDisplay())) {
            sb.append("hideCpp('OMeds');");
        }
        if ("".equals(bean.getRiskFactorsDisplay())) {
            sb.append("hideCpp('RiskFactors');");
        }
        if ("".equals(bean.getFamilyHxDisplay())) {
            sb.append("hideCpp('FamHistory');");
        }
        if ("".equals(bean.getUnresolvedIssuesDisplay())) {
            sb.append("hideCpp('unresolvedIssues');");
        }
        if ("".equals(bean.getResolvedIssuesDisplay())) {
            sb.append("hideCpp('resolvedIssues');");
        }
        if ("".equals(bean.getEpisodesDisplay())) {
            sb.append("hideCpp('episode');");
        }

        sb.append("});");
        sb.append("function notifyIssueUpdate() {}");
        sb.append("function notifyDivLoaded(divId) {}");
        sb.append("</script>");
        return sb.toString();
    }
}
