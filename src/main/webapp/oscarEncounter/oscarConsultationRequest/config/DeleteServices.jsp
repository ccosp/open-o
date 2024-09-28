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


<%@ page import="java.util.ResourceBundle" %>
<%@ page import="openo.oscarEncounter.oscarConsultationRequest.config.pageUtil.EctConTitlebar" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<html:html lang="en">
    <jsp:useBean id="displayServiceUtil" scope="request"
                 class="openo.oscarEncounter.oscarConsultationRequest.config.pageUtil.EctConDisplayServiceUtil"/>
    <%
        displayServiceUtil.estServicesVectors();


    %>

    <head>
        <script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
        <title><bean:message
                key="oscarEncounter.oscarConsultationRequest.config.DeleteServices.title"/>
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
                        <td class="Header"><bean:message
                                key="oscarEncounter.oscarConsultationRequest.config.DeleteServices.title"/>
                        </td>
                        <td></td>
                        <td style="text-align: right" NOWRAP><a
                                href="javascript:window.close();"><bean:message
                                key="global.btnClose"/></a> |
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
                    <tr>
                        <td><bean:message
                                key="oscarEncounter.oscarConsultationRequest.config.DeleteServices.msgCheckOff"/><br>

                        </td>
                    </tr>
                    <tr>
                        <td>
                            <html:form action="/oscarEncounter/DelService">
                            <input type="submit" name="delete"
                                   value="<bean:message key="oscarEncounter.oscarConsultationRequest.config.DeleteServices.btnDeleteService"/>">
                            <div class="ChooseRecipientsBox1">
                                <table>
                                    <tr>
                                        <th>&nbsp;</th>
                                        <th><bean:message
                                                key="oscarEncounter.oscarConsultationRequest.config.DeleteServices.service"/>
                                        </th>


                                    </tr>
                                    <tr>
                                        <td><!--<div class="ChooseRecipientsBox1">--> <%

                                 for(int i=0;i < displayServiceUtil.serviceId.size(); i++){
                                 String  serId     = (String) displayServiceUtil.serviceId.elementAt(i);
                                 String  serName   = (String) displayServiceUtil.serviceName.elementAt(i);
                              %>

                                    <tr>
                                        <td><input type="checkbox" name="service" value="<%=serId%>">
                                        </td>
                                        <td><%= serName%>
                                        </td>
                                    </tr>
                                            <% }%>
                        </td>
                    </tr>
                </table>
                </div>
                </html:form></td>
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
