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

<%@ include file="/casemgmt/taglibs.jsp" %>
<%@ page import="java.util.ResourceBundle"%>

<%
    if (session.getValue("user") == null)
        response.sendRedirect("../logout.htm");
    String curUser_no;
    curUser_no = (String) session.getAttribute("user");
    String tite = (String) request.getAttribute("provider.title");

    ResourceBundle bundle = ResourceBundle.getBundle("oscarResources", request.getLocale());

    String providertitle = (String) request.getAttribute("providertitle");
    String providermsgPrefs = (String) request.getAttribute("providermsgPrefs");
    String providermsgProvider = (String) request.getAttribute("providermsgProvider");
    String providermsgEdit = (String) request.getAttribute("providermsgEdit");
    String providerbtnSubmit = (String) request.getAttribute("providerbtnSubmit");
    String providermsgSuccess = (String) request.getAttribute("providermsgSuccess");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<c:set var="ctx" value="${pageContext.request.contextPath}"
       scope="request"/>
<html:html>
    <head>
        <script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
        <html:base/>
        <title><%=bundle.getString(providertitle)%></title>

        <link rel="stylesheet" type="text/css"
              href="../oscarEncounter/encounterStyles.css">
        <!-- calendar stylesheet -->
        <link rel="stylesheet" type="text/css" media="all"
              href="<c:out value="${ctx}"/>/share/calendar/calendar.css"
              title="win2k-cold-1">

        <script src="<c:out value="${ctx}"/>/share/javascript/prototype.js"
                type="text/javascript"></script>
        <script src="<c:out value="${ctx}"/>/share/javascript/scriptaculous.js"
                type="text/javascript"></script>

        <!-- main calendar program -->
        <script type="text/javascript"
                src="<c:out value="${ctx}"/>/share/calendar/calendar.js"></script>

        <!-- language for the calendar -->
        <script type="text/javascript"
                src="<c:out value="${ctx}"/>/share/calendar/lang/<bean:message key="global.javascript.calendar"/>"></script>

        <!-- the following script defines the Calendar.setup helper function, which makes
                       adding a calendar a matter of 1 or 2 lines of code. -->
        <script type="text/javascript"
                src="<c:out value="${ctx}"/>/share/calendar/calendar-setup.js"></script>
        <script type="text/javascript">
            function setup() {
                Calendar.setup({
                    inputField: "staleDate",
                    ifFormat: "%Y-%m-%d",
                    showsTime: false,
                    button: "staleDate_cal",
                    singleClick: true,
                    step: 1
                });
            }

            function validate() {
                var date = document.getElementById("staleDate");
                if (date.value == "") {
                    alert("Please select a date before saving");
                    return false;
                }

                return true;
            }
        </script>

    </head>

    <body class="BodyStyle" vlink="#0000FF">

    <table class="MainTable" id="scrollNumber1" name="encounterTable">
        <tr class="MainTableTopRow">
            <td class="MainTableTopRowLeftColumn"><%=bundle.getString(providermsgPrefs)%></td>
            <td style="color: white" class="MainTableTopRowRightColumn"><%=bundle.getString(providermsgProvider)%></td>
        </tr>
        <tr>
            <td class="MainTableLeftColumn">&nbsp;</td>
            <td class="MainTableRightColumn">
                <%if (request.getAttribute("status") == null) {%> <%=bundle.getString(providermsgEdit)%> <c:out value="${rxPageSizeProperty.value}"/> <html:form
                    action="/setProviderStaleDate.do">
                <input type="hidden" name="method" value="<c:out value="${method}"/>">
                <!--html:text property="rxPageSizeProperty.value" /-->
                <html:select property="rxPageSizeProperty.value">
                    <html:option value="PageSize.A4">A4</html:option>
                    <html:option value="PageSize.A6">A6</html:option>
                </html:select>
                <input type="submit" value="<%=bundle.getString(providerbtnSubmit)%>"/>
            </html:form> <%} else {%> <%=bundle.getString(providermsgSuccess)%> <br>
                <%}%>
            </td>
        </tr>
        <tr>
            <td class="MainTableBottomRowLeftColumn"></td>
            <td class="MainTableBottomRowRightColumn"></td>
        </tr>
    </table>
    </body>
</html:html>
