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

<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<%@page import="org.oscarehr.myoscar.utils.MyOscarLoggedInInfo" %>
<%@page import="org.oscarehr.phr.util.MyOscarUtils" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/phr-tag.tld" prefix="phr" %>

<%@ page import="ca.openosp.openo.oscarProvider.data.ProviderData" %>

<%
    String errorMsg = (String) request.getAttribute("error_msg");
    boolean errors = false;
    String cssPrefix = "objectGreen";
    if (errorMsg != null) errors = true;
    if (errors) {
        cssPrefix = "objectRed";
    }
%>

<%
    String providerName = request.getSession().getAttribute("userfirstname") + " " +
            request.getSession().getAttribute("userlastname");
    String providerNo = (String) request.getSession().getAttribute("user");
    ProviderData providerData = new ProviderData();
    providerData.setProviderNo(providerNo);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/phr/phr.css"/>
    <title>PHR Call</title>
    <script type="text/javascript" language="JavaScript"
            src="<%=request.getContextPath()%>/share/javascript/prototype.js"></script>
    <script type="text/javascript" language="JavaScript" src="<%=request.getContextPath()%>/phr/phr.js"></script>
    <script type="text/javascript" language="JavaScript">
        function startCloseWindowTimeout() {
            var sec = 1500;
            setTimeout("closeWindow()", sec);
        }

        function closeWindow() {
            window.opener = self;
            window.close();
        }

        function onloadd() {
            if (document.getElementById("phrPassword"))
                document.getElementById("phrPassword").focus();
        }
    </script>
    <style type="text/css">
        .myoscarLoginElementNoAuth {
            border: 0;
            padding-left: 3px;
            color: black;
            padding-right: 3px;
            background-color: #f3e9e9;
            text-align: left;
            font-weight: normal;
        }

        .myoscarLoginElementAuth {
            border: 0;
            color: black;
            padding-left: 3px;
            padding-right: 3px;
            background-color: #d9ecd8;
            text-align: left;
            font-weight: normal;
        }

        .moreInfoBoxoverBody {
            border: 1px solid #9fbbe8;
            padding: 1px;
            padding-left: 3px;
            padding-right: 3px;
            border-top: 0px;
            font-size: 10px;
            background-color: white;
        }

        .moreInfoBoxoverHeader {
            border: 1px solid #9fbbe8;
            background-color: #e8ecf3;
            padding: 2px;
            padding-left: 3px;
            padding-right: 3px;
            border-bottom: 0px;
            font-size: 10px;
            color: red;
        }
    </style>
</head>
<body onload="onloadd();">
<div class="<%=cssPrefix%>">
    <div class="<%=cssPrefix%>Header">Personal Health Record Action</div>
    <%if (errors) {%>
    <font class="announcementRed"><%=errorMsg%>
    </font>
    <%} else {%>
    <center><font class="announcementGreen">SUCCESS, added to send queue.</font></center>
    <center><font style="font-size: 10px;"><sup>(It will be sent next time you log in)</sup></font></center>
    <br/>
    <%
        if (MyOscarUtils.isMyOscarEnabled((String) session.getAttribute("user"))) {
            MyOscarLoggedInInfo myOscarLoggedInInfo = MyOscarLoggedInInfo.getLoggedInInfo(session);
    %>
    <div class="myoscarLoginElementAuth">
        Status: <b>Logged in as <%=providerName%>
    </b> (<%=myOscarLoggedInInfo.getLoggedInPerson().getUserName()%>)<br/>
        <center>Closing Window... <a href="javascript:;" onclick="closeWindow()">close</a></center>
        <%-- if no errors and logged in, close window--%>
        <%if (!errors) {%>
        <script type="text/javascript" language="JavaScript">startCloseWindowTimeout()</script>
        <%}%>
    </div>
    <%
    } else {
    %>
    <div class="myoscarLoginElementNoAuth">
        <form action="<%=request.getContextPath()%>/phr/Login.do" name="phrLogin" method="POST"
              style="margin-bottom: 0px;">
            <logic:present name="phrUserLoginErrorMsg">
                <div class="phrLoginErrorMsg"><font color="red"><bean:write name="phrUserLoginErrorMsg"/></font>
                <logic:present name="phrTechLoginErrorMsg">
                    <a href="javascript:;"
                       title="fade=[on] requireclick=[off] cssheader=[moreInfoBoxoverHeader] cssbody=[moreInfoBoxoverBody] singleclickstop=[on] header=[MyOSCAR Server Response:] body=[<bean:write name="phrTechLoginErrorMsg"/> </br>]">More
                        Info</a></div>
                </logic:present>
            </logic:present>
            Status: <b>Not logged in</b><br/>
            <%=providerName%> password: <input type="password" id="phrPassword" name="phrPassword"
                                               style="font-size: 8px; width: 40px;"><br/>
            <center>
                <a href="javascript: document.forms['phrLogin'].submit()">Login & Send Now</a> &nbsp;&nbsp;
                <a href="javascript:;" onclick="closeWindow()">Send Later</a>
            </center>
            <input type="hidden" name="forwardto" value="<%=request.getServletPath()%>">
        </form>
    </div>
    <%
            }

        }
    %>
</div>
<script type="text/javascript" src="<%=request.getContextPath()%>/share/javascript/boxover.js"></script>
</body>
</html>
