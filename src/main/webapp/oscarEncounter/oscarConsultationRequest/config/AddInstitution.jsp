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

<%@ page import="java.util.ResourceBundle" %>
<% java.util.Properties oscarVariables = OscarProperties.getInstance(); %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>
<%
    String roleName$ = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed = true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_admin,_admin.consult" rights="w" reverse="<%=true%>">
    <%authed = false; %>
    <%response.sendRedirect("../../../securityError.jsp?type=_admin&type=_admin.consult");%>
</security:oscarSec>
<%
    if (!authed) {
        return;
    }
%>

<%@page import="ca.openosp.openo.oscarEncounter.oscarConsultationRequest.config.pageUtil.EctConAddInstitutionForm" %>
<%@ page import="ca.openosp.openo.oscarEncounter.oscarConsultationRequest.config.pageUtil.EctConTitlebar" %>
<%@ page import="ca.openosp.openo.OscarProperties" %>

<html:html lang="en">

    <%
        ResourceBundle oscarR = ResourceBundle.getBundle("oscarResources", request.getLocale());

        String transactionType = new String(oscarR.getString("oscarEncounter.oscarConsultationRequest.config.AddInstitution.addOperation"));
        int whichType = 1;
        if (request.getAttribute("upd") != null) {
            transactionType = new String(oscarR.getString("oscarEncounter.oscarConsultationRequest.config.AddInstitution.updateOperation"));
            whichType = 2;
        }
    %>

    <head>
        <script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
        <title><%=transactionType%>
        </title>
        <html:base/>
        <link rel="stylesheet" type="text/css" media="all" href="../share/css/extractedFromPages.css"/>
    </head>
    <script language="javascript">
        function BackToOscar() {
            window.close();
        }
    </script>

    <link rel="stylesheet" type="text/css" href="../../encounterStyles.css">
    <body class="BodyStyle" vlink="#0000FF">

    <html:errors/>
    <!--  -->
    <table class="MainTable" id="scrollNumber1" name="encounterTable">
        <tr class="MainTableTopRow">
            <td class="MainTableTopRowLeftColumn">Consultation</td>
            <td class="MainTableTopRowRightColumn">
                <table class="TopStatusBar">
                    <tr>
                        <td class="Header"><%=transactionType%>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr style="vertical-align: top">
            <td class="MainTableLeftColumn">
                <%
                    EctConTitlebar titlebar = new EctConTitlebar(request);
                    out.print(titlebar.estBar(request));
                %>
            </td>
            <td class="MainTableRightColumn">
                <table cellpadding="0" cellspacing="2"
                       style="border-collapse: collapse" bordercolor="#111111" width="100%"
                       height="100%">

                    <!----Start new rows here-->
                    <%
                        String added = (String) request.getAttribute("Added");
                        if (added != null) { %>
                    <tr>
                        <td><font color="red"> <bean:message
                                key="oscarEncounter.oscarConsultationRequest.config.AddInstitution.msgInstitutionAdded"
                                arg0="<%=added%>"/> </font></td>
                    </tr>
                    <%}%>
                    <tr>
                        <td>

                            <html:form action="/oscarEncounter/AddInstitution">
                                <table>
                                    <%
                                        if (request.getAttribute("id") != null) {
                                            EctConAddInstitutionForm thisForm;
                                            thisForm = (EctConAddInstitutionForm) request.getAttribute("EctConAddInstitutionForm");
                                            thisForm.setId((String) request.getAttribute("id"));
                                            thisForm.setName((String) request.getAttribute("name"));
                                            thisForm.setCity((String) request.getAttribute("city"));
                                            thisForm.setProvince((String) request.getAttribute("province"));
                                            thisForm.setPostal((String) request.getAttribute("postal"));
                                            thisForm.setAddress((String) request.getAttribute("address"));
                                            thisForm.setPhone((String) request.getAttribute("phone"));
                                            thisForm.setFax((String) request.getAttribute("fax"));
                                            thisForm.setWebsite((String) request.getAttribute("website"));
                                            thisForm.setEmail((String) request.getAttribute("email"));
                                            thisForm.setAnnotation((String) request.getAttribute("annotation"));
                                        }
                                    %>
                                    <html:hidden name="EctConAddInstitutionForm" property="id"/>
                                    <tr>
                                        <td>Name</td>
                                        <td><html:text name="EctConAddInstitutionForm" property="name"/></td>

                                    </tr>
                                    <tr>
                                        <td>Address</td>
                                        <td><html:text name="EctConAddInstitutionForm" property="address"/></td>
                                        <td>City</td>
                                        <td><html:text name="EctConAddInstitutionForm" property="city"/></td>
                                    </tr>
                                    <tr>
                                        <td>Province</td>
                                        <td><html:text name="EctConAddInstitutionForm" property="province"/></td>
                                        <td>Postal Code</td>
                                        <td><html:text name="EctConAddInstitutionForm" property="postal"/></td>
                                    </tr>
                                    <tr>
                                        <td>Phone</td>
                                        <td><html:text name="EctConAddInstitutionForm" property="phone"/></td>
                                        <td>Fax</td>
                                        <td colspan="4"><html:text name="EctConAddInstitutionForm" property="fax"/></td>
                                    </tr>
                                    <tr>
                                        <td>Website</td>
                                        <td><html:text name="EctConAddInstitutionForm" property="website"/></td>
                                        <td>Email</td>
                                        <td colspan="4"><html:text name="EctConAddInstitutionForm"
                                                                   property="email"/></td>
                                    </tr>

                                    <tr>
                                        <td>Annotation
                                        </td>
                                        <td colspan="4"><html:textarea name="EctConAddInstitutionForm"
                                                                       property="annotation" cols="30" rows="3"/>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td colspan="6">
                                            <input type="hidden" name="whichType" value="<%=whichType%>"/>
                                            <input type="submit" name="transType" value="<%=transactionType%>"/>
                                        </td>
                                    </tr>
                                </table>
                            </html:form>
                        </td>
                    </tr>
                    <!----End new rows here-->

                    <tr height="100%">
                        <td></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td class="MainTableBottomRowLeftColumn"></td>
            <td class="MainTableBottomRowRightColumn"></td>
        </tr>
    </table>
    </body>
</html:html>
