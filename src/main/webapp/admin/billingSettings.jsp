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
    String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
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
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="org.owasp.encoder.Encode" %>
<%@ page import="org.oscarehr.common.dao.PropertyDao" %>
<%@ page import="org.oscarehr.common.dao.SystemPreferencesDao" %>
<%@ page import="org.oscarehr.common.model.Property" %>
<%@ page import="oscar.util.StringUtils" %>
<%@ page import="oscar.oscarBilling.ca.bc.data.BillingFormData" %>
<%@ page import="oscar.OscarProperties" %>

<jsp:useBean id="dataBean" class="java.util.Properties"/>
<%
    SystemPreferencesDao systemPreferencesDao = SpringUtils.getBean(SystemPreferencesDao.class);

    PropertyDao propertyDao = SpringUtils.getBean(PropertyDao.class);
    String billRegion = OscarProperties.getInstance().getProperty("billregion", "").trim();
    List<String> billingSettingsKeys = Arrays.asList("display_patient_dob_on_3rd_party_invoices",
            "display_patient_hin_on_3rd_party_invoices", "enable_3rd_party_billing_footer",
            "3rd_party_billing_footer_text", "enable_footer_patient_id", "auto_populate_refer",
            "bc_billing_min_invoices", "bc_billing_max_invoices", "bc_default_service_location",
            "display_dx23", "load_all_providers_by_default",
            "enable_add_dxCode_to_disease_registry_in_eChart");

    StringBuilder errorMessages = new StringBuilder();

    if (request.getParameter("dboperation") != null && !request.getParameter("dboperation").isEmpty() && request.getParameter("dboperation").equals("Save")) {
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

    }
%>

<html:html locale="true">
    <head>
        <title>Billing Settings</title>
<script src="<%=request.getContextPath()%>/" type="text/javascript"></script>
        <link href="<%=request.getContextPath() %>/css/bootstrap.css" rel="stylesheet" type="text/css">

        <script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery-1.7.1.min.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath() %>/js/bootstrap.js"></script>
        <script type="text/javascript" language="JavaScript" src="<%= request.getContextPath() %>/share/javascript/Oscar.js"></script>
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
        </script>
    </head>

    <%
        for(String key : billingSettingsKeys) {
            List<Property> properties = propertyDao.findGlobalByName(key);
            if (!properties.isEmpty() && properties.get(0).getName() != null && properties.get(0).getValue() != null) {
                dataBean.setProperty(properties.get(0).getName(), properties.get(0).getValue());
            }
        }
    %>

    <body vlink="#0000FF" class="BodyStyle">
    <div id="warning_text" style="color: #e26e6e; visibility: hidden;"><b>Warning: Footer text size is too large! This may cause undesired formatting!</b></div>
    <h4>Manage OSCAR Billing Settings</h4>
    <form name="billingSettingsForm" method="post" action="billingSettings.jsp">
        <%=errorMessages.toString()%>
        <input type="hidden" name="dboperation" value="">
        <table id="displaySettingsTable" class="table table-bordered table-striped table-hover table-condensed">
            <tbody>
            <oscar:oscarPropertiesCheck property="billregion" value="BC">
            <tr>
                <td>Auto-populate Referring Physician on Billing Form for All Providers?: </td>
                <td>
                    <input id="auto_populate_refer-true" type="radio" value="true" name="auto_populate_refer"
                            <%=(dataBean.getProperty("auto_populate_refer", "false").equals("true")) ? "checked" : ""%> />
                    Yes
                    <input id="auto_populate_refer-false" type="radio" value="false" name="auto_populate_refer"
                            <%=(dataBean.getProperty("auto_populate_refer", "false").equals("false")) ? "checked" : ""%> />
                    No
                </td>
            </tr>
            <tr>
                <td>Set the default Teleplan service location for new invoices: </td>
                <td>
                    <select id="bc_default_service_location" name="bc_default_service_location">
                        <%
                            BillingFormData billingFormData = new BillingFormData();
                            BillingFormData.BillingVisit[] billingVisits = billingFormData.getVisitType(billRegion);
                            String defaultServiceLocation = dataBean.getProperty("bc_default_service_location", "");
                            if (StringUtils.isNullOrEmpty(defaultServiceLocation)) {
                                // Get the visittype property
                                String visitType = OscarProperties.getInstance().getProperty("visittype");
                                // If the visittype contains a pipe separator, get the value before the pipe separator Eg. A|Practitioner's Office - In Community
                                defaultServiceLocation = visitType != null && visitType.contains("|") ? visitType.split("\\|")[0] : visitType;
                            }
                            for(BillingFormData.BillingVisit billingVisit : billingVisits) {
                                String visitType = billingVisit.getVisitType();
                                String visitDescription = visitType + " | " + billingVisit.getDescription();
                                String selected="";
                                if (visitType.equals(defaultServiceLocation)) {
                                    selected=" selected=\"selected\" ";
                                }
                        %>
                        <option value="<%=visitType%>" <%=selected%>><%=visitDescription%></option>
                        <%  } %>
                    </select>
                </td>
            </tr>
            </oscar:oscarPropertiesCheck>
            </tbody>
        </table>

        <input type="button" onclick="document.forms['billingSettingsForm'].dboperation.value='Save'; document.forms['billingSettingsForm'].submit();" name="saveBillingSettings" value="Save"/>
    </form>
    </body>
</html:html>