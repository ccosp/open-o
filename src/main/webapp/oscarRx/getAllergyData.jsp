<%--

    Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for the
    Department of Family Medicine
    McMaster University
    Hamilton
    Ontario, Canada

--%>

<%@page import="org.apache.commons.lang.StringUtils" %>
<%@page import="org.oscarehr.common.model.Allergy" %>
<%@page import="org.oscarehr.PMmodule.caisi_integrator.RemoteDrugAllergyHelper" %>
<%@page import="org.oscarehr.util.LoggedInInfo" %>
<%@page import="org.oscarehr.util.MiscUtils" %>
<%@page import="java.util.*,net.sf.json.*,java.lang.reflect.*,java.io.*,org.apache.xmlrpc.*,oscar.oscarRx.util.*,oscar.oscarRx.data.*" %>
<%@ page import="org.oscarehr.common.dao.SystemPreferencesDao" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.common.model.SystemPreferences" %>
<%@ page import="ca.openosp.openo.oscarRx.pageUtil.RxSessionBean" %>
<%@ page import="ca.openosp.openo.oscarRx.data.RxPatientData" %>
<%@ page import="ca.openosp.openo.oscarRx.data.RxDrugData" %>
<%@ page import="ca.openosp.openo.OscarProperties" %>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>
<%
    String roleName$ = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed = true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_allergy" rights="r" reverse="<%=true%>">
    <%authed = false; %>
    <%response.sendRedirect("../securityError.jsp?type=_allergy");%>
</security:oscarSec>
<%
    if (!authed) {
        return;
    }
%>

<%
    LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
    SystemPreferencesDao systemPreferencesDao = SpringUtils.getBean(SystemPreferencesDao.class);
    boolean rxShowAllAllergyWarnings = systemPreferencesDao.isReadBooleanPreference(SystemPreferences.RX_PREFERENCE_KEYS.rx_show_highest_allergy_warning);
    String atcCode = request.getParameter("atcCode");
    String id = request.getParameter("id");

    String disabled = OscarProperties.getInstance().getProperty("rx3.disable_allergy_warnings", "false");
    if (disabled.equals("false")) {


        RxSessionBean rxSessionBean = (RxSessionBean) session.getAttribute("RxSessionBean");
        Allergy[] allergies = RxPatientData.getPatient(loggedInInfo, rxSessionBean.getDemographicNo()).getActiveAllergies();

        if (loggedInInfo.getCurrentFacility().isIntegratorEnabled()) {
            try {
                ArrayList<Allergy> remoteAllergies = RemoteDrugAllergyHelper.getRemoteAllergiesAsAllergyItems(loggedInInfo, rxSessionBean.getDemographicNo());

                // now merge the 2 lists
                for (Allergy alleryTemp : allergies) remoteAllergies.add(alleryTemp);
                allergies = remoteAllergies.toArray(new Allergy[0]);
            } catch (Exception e) {
                MiscUtils.getLogger().error("error getting remote allergies", e);
            }
        }

        Allergy[] allergyWarnings = null;
        RxDrugData drugData = new RxDrugData();
        List<Allergy> missing = new ArrayList<Allergy>();
        allergyWarnings = drugData.getAllergyWarnings(atcCode, allergies, missing);

        Allergy highestSeverityAllergy = null;

        JSONObject result = new JSONObject();
        result.put("id", id);
        JSONArray allergyResultArray = new JSONArray();
        if (allergyWarnings != null && allergyWarnings.length > 0) {
            highestSeverityAllergy = allergyWarnings[0];
            for (Allergy allergy : allergyWarnings) {
                JSONObject allergyResult = new JSONObject();
                allergyResult.put("DESCRIPTION", StringUtils.trimToEmpty(allergy.getDescription()));
                allergyResult.put("reaction", StringUtils.trimToEmpty(allergy.getReaction()));
                allergyResult.put("severity", StringUtils.trimToEmpty(allergy.getSeverityOfReactionDesc()));
                if (rxShowAllAllergyWarnings) {
                    Integer highestSeverity = Integer.valueOf(highestSeverityAllergy.getSeverityOfReaction());
                    Integer thisSeverity = Integer.valueOf(allergy.getSeverityOfReaction());
                    if (thisSeverity > highestSeverity) {
                        highestSeverityAllergy = allergy;
                    }
                } else {
                    allergyResultArray.add(allergyResult);
                }
            }
        }
        if (rxShowAllAllergyWarnings && highestSeverityAllergy != null) {
            JSONObject allergyResult = new JSONObject();
            allergyResult.put("DESCRIPTION", StringUtils.trimToEmpty(highestSeverityAllergy.getDescription()));
            allergyResult.put("reaction", StringUtils.trimToEmpty(highestSeverityAllergy.getReaction()));
            allergyResult.put("severity", StringUtils.trimToEmpty(highestSeverityAllergy.getSeverityOfReactionDesc()));
            allergyResultArray.add(allergyResult);
        }
        result.put("results", allergyResultArray);
        result.write(out);

    }
%>
