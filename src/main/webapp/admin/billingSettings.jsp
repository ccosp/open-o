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
<!DOCTYPE HTML>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
    String roleName$ = session.getAttribute("userrole") + "," + session.getAttribute("user");
    boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_admin" rights="r" reverse="<%=true%>">
    <%authed=false; %>
    <%response.sendRedirect("../securityError.jsp?type=_admin");%>
</security:oscarSec>
<%
    if(!authed) {
        return;
    }
%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.owasp.encoder.Encode" %>
<%@ page import="org.oscarehr.common.dao.PropertyDao" %>
<%@ page import="org.oscarehr.common.model.Property" %>
<%@ page import="oscar.util.StringUtils" %>
<%@ page import="oscar.oscarBilling.ca.bc.data.BillingFormData" %>
<%@ page import="oscar.OscarProperties" %>
<%@ page import="oscar.oscarClinic.ClinicData" %>
<%@ page import="org.oscarehr.common.model.SystemPreferences" %>
<%@ page import="org.oscarehr.common.dao.SystemPreferencesDao" %>
<%@ page import="java.util.*" %>


<jsp:useBean id="dataBean" class="java.util.Properties"/>
<%!
    PropertyDao propertyDao = SpringUtils.getBean(PropertyDao.class);
    BillingFormData billingFormData = new BillingFormData();
    SystemPreferencesDao systemPreferencesDao = SpringUtils.getBean(SystemPreferencesDao.class);
%>
<%


    String billRegion = OscarProperties.getInstance().getProperty("billregion", "").trim();
    List<String> billingSettingsKeys = Arrays.asList( "auto_populate_refer", "bc_default_service_location", "default_billing_form");

    /*
     * Save on page reload.
     * TODO: not really the best method, but will work until there is time to refactor.
     */
    if (request.getParameter("dboperation") != null && !request.getParameter("dboperation").isEmpty() && request.getParameter("dboperation").equals("Save")) {

        request.setAttribute("success", false);

        // save billing settings into Properties table.
        for(String key : billingSettingsKeys) {
            List<Property> property = propertyDao.findGlobalByName(key);
            String newValue = request.getParameter(key);

            if (property.isEmpty()) {
                Property newProperty = new Property();
                newProperty.setName(key);
                newProperty.setValue(newValue);
                propertyDao.persist(newProperty);
            } else {
                for (Property p : property) {
                    p.setValue(newValue);
                    propertyDao.merge(p);
                }
            }
        }

        // save system settings
        for(SystemPreferences.GENERAL_SETTINGS_KEYS key : SystemPreferences.GENERAL_SETTINGS_KEYS.values()) {
            // do not override the enumerator!
            String newValue = request.getParameter(key.name());
            SystemPreferences currentValue = systemPreferencesDao.findPreferenceByName(key);
            if(currentValue == null) {
                SystemPreferences systemPreferences = new SystemPreferences();
                systemPreferences.setName(key.name());
                systemPreferences.setValue(newValue);
                systemPreferences.setUpdateDate(new Date());
                systemPreferencesDao.persist(systemPreferences);
            } else {
                // if the custom clinic info is set to off then the info should not be saved
                if(key.equals(SystemPreferences.GENERAL_SETTINGS_KEYS.invoice_custom_clinic_info)
                        && ! "on".equals(request.getParameter(SystemPreferences.GENERAL_SETTINGS_KEYS.invoice_use_custom_clinic_info.name()))) {
                    continue;
                }
                currentValue.setValue(newValue);
                currentValue.setUpdateDate(new Date());
                systemPreferencesDao.merge(currentValue);
            }
        }

        request.setAttribute("success", true);
    }


    for(String key : billingSettingsKeys) {
        List<Property> properties = propertyDao.findGlobalByName(key);
        if (!properties.isEmpty() && properties.get(0).getName() != null && properties.get(0).getValue() != null) {
            dataBean.setProperty(properties.get(0).getName(), properties.get(0).getValue());
        }
    }

    List<SystemPreferences> preferences = systemPreferencesDao.findPreferencesByNames(SystemPreferences.GENERAL_SETTINGS_KEYS.class);
    for(SystemPreferences preference : preferences) {
        dataBean.setProperty(preference.getName(), preference.getValue());
    }

    pageContext.setAttribute("clinicData", new ClinicData());
%>

<html:html locale="true">
    <head>
        <title>Billing Settings</title>
        <script src="<%=request.getContextPath()%>/csrfguard" type="text/javascript"></script>
        <link href="<%=request.getContextPath() %>/css/bootstrap.css" rel="stylesheet" type="text/css">

        <script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery-1.7.1.min.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath() %>/js/bootstrap.js"></script>
        <script type="text/javascript" src="<%= request.getContextPath() %>/share/javascript/Oscar.js"></script>
        <script>
            function hasScrollbar(element_id)
            {
                var elem = document.getElementById(element_id);
                if (elem.clientHeight < elem.scrollHeight) {
                    document.getElementById("warning_text").style.visibility = "visible";
                } else {
                    document.getElementById("warning_text").style.visibility = "hidden";
                }
            }
            function setClinicInfo(){
                let useCustom = document.getElementById('invoice_use_custom_clinic_info');
                let clinicInfo = document.getElementById('invoice_custom_clinic_info');
                clinicInfo.disabled = useCustom === null || !useCustom.checked;
            }
        </script>
    </head>

    <body vlink="#0000FF" class="BodyStyle">

    <h4>Manage OSCAR Billing Settings</h4>
    <form name="billingSettingsForm" method="post" action="billingSettings.jsp">

        <input type="hidden" name="dboperation" value="">
        <table id="displaySettingsTable" class="table table-bordered table-striped table-hover table-condensed">
            <tbody>
            <oscar:oscarPropertiesCheck property="billregion" value="BC">
            <tr>
                <td>Auto-populate Referring Physician on Billing Form for All Providers?: </td>
                <td>
                    <label for="auto_populate_refer-true" class="radio inline">
                    <input id="auto_populate_refer-true" type="radio" value="true" name="auto_populate_refer"
                            <%=(dataBean.getProperty("auto_populate_refer", "false").equals("true")) ? "checked" : ""%> />
                    Yes</label>
                    <label for="auto_populate_refer-false" class="radio inline">
                    <input id="auto_populate_refer-false" type="radio" value="false" name="auto_populate_refer"
                            <%=(dataBean.getProperty("auto_populate_refer", "false").equals("false")) ? "checked" : ""%> />
                    No</label>
                </td>
            </tr>
            <tr>
                <td><label for="bc_default_service_location">Set the default Teleplan service location for new invoices:</label> </td>
                <td>
                    <select id="bc_default_service_location" name="bc_default_service_location">
                        <%
                            List<BillingFormData.BillingVisit> billingVisits = billingFormData.getVisitType(billRegion);
                            String defaultServiceLocation = dataBean.getProperty("bc_default_service_location", "");
                            if (StringUtils.isNullOrEmpty(defaultServiceLocation)) {
                                // Get the visittype property
                                defaultServiceLocation = OscarProperties.getInstance().getProperty("visittype");
                            }

                            // this captures and modifies any legacy codes that may be still hanging around
                            if(defaultServiceLocation.contains("|")) {
                                defaultServiceLocation = defaultServiceLocation.split("\\|")[0].trim();
                            }

                            for(BillingFormData.BillingVisit billingVisit : billingVisits) {
                        %>
                        <option value="<%=Encode.forHtmlAttribute(billingVisit.getVisitType())%>" <%= billingVisit.getVisitType().equalsIgnoreCase(defaultServiceLocation) ? "selected" : ""%>>
                            <%=Encode.forHtmlContent(billingVisit.getDescription())%>
                        </option>
                        <%  } %>a
                    </select>
                </td>
            </tr>
                <tr>
                    <td><label for="default_billing_form">Set the default Billing Form for all invoices:</label></td>
                    <td>
                        <select id="default_billing_form" name="default_billing_form">
                        <%
                            BillingFormData.BillingForm[] billformlist = billingFormData.getFormList();
                            String currentSelection = OscarProperties.getInstance().getProperty("default_view");
                            String currentUserSetting = dataBean.getProperty("default_billing_form");
                            // current user setting overrides the oscar properties setting
                            if(currentUserSetting != null && ! currentUserSetting.isEmpty()) {
                                currentSelection = currentUserSetting;
                            }
                            currentSelection = currentSelection.trim();
                            for(BillingFormData.BillingForm billingForm : billformlist) {
                        %>
                                <option value="<%=Encode.forHtmlAttribute(billingForm.getFormCode())%>" <%= billingForm.getFormCode().equalsIgnoreCase(currentSelection) ? "selected" : "" %> >
                                    <%=Encode.forHtmlContent(billingForm.getDescription())%>
                                </option>
                           <% } %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Set clinic information to display on all private invoices:</td>
                    <td>
                        <label for="invoice_use_custom_clinic_info" class="checkbox">
                        <input type="checkbox" id="invoice_use_custom_clinic_info" name="invoice_use_custom_clinic_info" onclick="setClinicInfo()" ${ "on" eq dataBean["invoice_use_custom_clinic_info"] ? "checked" : ""} />
                        Use Custom</label>

                        <br>
                        <textarea style="resize: none;" rows="5" id="invoice_custom_clinic_info" name="invoice_custom_clinic_info" maxlength="250"
                            ${empty dataBean["invoice_use_custom_clinic_info"] ? "disabled" : ""} >${"on" eq dataBean["invoice_use_custom_clinic_info"] ? dataBean["invoice_custom_clinic_info"] : clinicData.label }</textarea>
                    </td>
                </tr>
            </oscar:oscarPropertiesCheck>
            </tbody>
        </table>
        <input type="button" onclick="document.forms['billingSettingsForm'].dboperation.value='Save'; document.forms['billingSettingsForm'].submit();" name="saveBillingSettings" value="Save"/>
       ${ success ? "<span style=\'color:green;\'>Settings Saved</span>" : "<span style=\'color:red;\'></span>" }
    </form>
    </body>
</html:html>