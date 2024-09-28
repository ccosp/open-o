<!DOCTYPE html>
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

<%@ page import="java.util.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="oscar.login.*, oscar.oscarDB.*, ca.openosp.openo.MyDateFormat" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    String roleName$ = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    String curUser_no = (String) session.getAttribute("user");
    boolean isSiteAccessPrivacy = false;
    boolean authed = true;
%>

<security:oscarSec roleName="<%=roleName$%>" objectName="_admin,_admin.reporting" rights="r" reverse="<%=true%>">
    <%authed = false; %>
    <%response.sendRedirect("../securityError.jsp?type=_admin&type=_admin.reporting");%>
</security:oscarSec>
<%
    if (!authed) {
        return;
    }
%>


<security:oscarSec objectName="_site_access_privacy" roleName="<%=roleName$%>" rights="r" reverse="false">
    <%isSiteAccessPrivacy = true; %>
</security:oscarSec>


<%
    String tdTitleColor = "#CCCC99";
    String tdSubtitleColor = "#CCFF99";
    String tdInterlColor = "white";
    String startDate = request.getParameter("startDate");
    String endDate = request.getParameter("endDate");
    Vector vecProvider = new Vector();
    Properties prop = null;
    String providerName = "";
    String sql = "";
//provider name list, date, action, create button
%>

<%
    DBPreparedHandler dbObj = new DBPreparedHandler();
    Properties propName = new Properties();
    // select provider list
    if (isSiteAccessPrivacy) {
        sql = "select p.* from provider p INNER JOIN providersite s ON p.provider_no = s.provider_no WHERE s.site_id IN (SELECT site_id from providersite where provider_no=" + curUser_no + ") order by p.first_name, p.last_name";
    } else {
        sql = "select * from provider p order by p.first_name, p.last_name ";
    }
    ResultSet rs = dbObj.queryResults(sql);

    while (rs.next()) {
        propName.setProperty(Misc.getString(rs, "provider_no"), Misc.getString(rs, "first_name") + " " + Misc.getString(rs, "last_name"));
        prop = new Properties();

        prop.setProperty("providerNo", Misc.getString(rs, "provider_no"));
        prop.setProperty("name", Misc.getString(rs, "first_name") + " " + Misc.getString(rs, "last_name"));
        vecProvider.add(prop);
    }
%>

<%@page import="ca.openosp.openo.Misc" %>
<%@ page import="org.owasp.encoder.Encode" %>
<%@ page import="ca.openosp.openo.oscarDB.DBPreparedHandler" %>
<%@ page import="ca.openosp.openo.oscarDB.DBPreparedHandlerParam" %>
<%@ page import="ca.openosp.openo.OscarProperties" %>
<html:html lang="en">
    <script src="${pageContext.request.contextPath}/csrfguard"></script>
    <head>


        <script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery-1.9.1.min.js"></script>
        <script src="<%=request.getContextPath() %>/js/bootstrap.min.js"></script>

        <script type="text/javascript" src="<%=request.getContextPath() %>/js/bootstrap-datepicker.js"></script>

        <link href="<%=request.getContextPath() %>/css/bootstrap.min.css" rel="stylesheet">

        <link href="<%=request.getContextPath() %>/css/datepicker.css" rel="stylesheet" type="text/css">


        <link rel="stylesheet" href="<%=request.getContextPath() %>/css/font-awesome.min.css">
        <title>Log Report</title>
        <script language="JavaScript">

            <!--
            function setfocus() {
                this.focus();
                //  document.titlesearch.keyword.select();
            }

            function onSub() {
                if (document.myform.startDate.value == "" || document.myform.endDate.value == "") {
                    alert("Please set Start and End Dates.");
                    return false;
                } else {
                    return true;
                }
            }

            //-->
        </script>

        <style>

            label {
                margin-top: 6px;
                margin-bottom: 0px;
            }
        </style>

    </head>
    <body>
    <form name="myform" class="well form-horizontal" action="logReport.jsp" method="POST" onSubmit="return(onSub());">
        <fieldset>
            <h3>Log Admin Report <small>Please select the provider, start and end dates.</small></h3>

            <div class="span4">
                <label>Provider: </label>

                <select name="providerNo">
                    <option value="*">All</option>
                    <%
                        for (int i = 0; i < vecProvider.size(); i++) {
                            String prov = ((Properties) vecProvider.get(i)).getProperty("providerNo", "");
                            String selected = request.getParameter("providerNo");
                    %>
                    <option value="<%=prov %>"
                            <% if ((selected != null) && (selected.equals(prov))) { %> selected
                            <% } %>><%= Encode.forHtmlContent(((Properties) vecProvider.get(i)).getProperty("name", "")) %>
                    </option>
                    <%
                        }
                    %>
                </select>
            </div>

            <div class="span4">
                <label>Content Type:</label>
                <select name="content">
                    <option value="admin">Admin</option>
                    <option value="login">Log in</option>
                </select>
            </div>

            <div class="span4">
                <label>Start Date: </label>
                <div class="input-append">
                    <input type="text" name="startDate" id="startDate1" value="<%=startDate!=null?startDate:""%>"
                           pattern="^\d{4}-((0\d)|(1[012]))-(([012]\d)|3[01])$" autocomplete="off"/>
                    <span class="add-on"><i class="icon-calendar"></i></span>
                </div>
            </div>

            <div class="span4">
                <label>End Date: </label>
                <div class="input-append">
                    <input type="text" name="endDate" id="endDate1" value="<%=endDate!=null?endDate:""%>"
                           pattern="^\d{4}-((0\d)|(1[012]))-(([012]\d)|3[01])$" autocomplete="off"/>
                    <span class="add-on"><i class="icon-calendar"></i></span>
                </div>
            </div>


            <div class="span8" style="padding-top:10px;">
                <input class="btn btn-primary" type="submit" name="submit" value="Run Report">
            </div>

        </fieldset>
    </form>
    <%
        out.flush();
        //String startDate = "";
        //String endDate = "";
        boolean bAll = false;
        Vector<Properties> vec = new Vector<Properties>();
        String providerNo = "";
        if (request.getParameter("submit") != null) {
            providerNo = request.getParameter("providerNo");
            String action = request.getParameter("submit");
            String content = request.getParameter("content");
            if (content.equals("login")) content = "login";
            if (content.equals("admin")) content = "%";

            String sDate = request.getParameter("startDate");
            String eDate = request.getParameter("endDate");
            String strDbType = OscarProperties.getInstance().getProperty("db_type").trim();
            if ("".equals(sDate) || sDate == null) sDate = "1900-01-01";
            if ("".equals(eDate) || eDate == null) eDate = "2999-01-01";

            DBPreparedHandlerParam[] params = new DBPreparedHandlerParam[2];
            params[0] = new DBPreparedHandlerParam(MyDateFormat.getSysDateEX(eDate, 1));
            params[1] = new DBPreparedHandlerParam(MyDateFormat.getSysDate(sDate));

            sql = "select * from log force index (datetime) where provider_no='" + providerNo + "' and dateTime <= ?";
            sql += " and dateTime >= ? and content like '" + content + "' order by dateTime desc ";

            if ("*".equals(providerNo)) {
                bAll = true;
                if (isSiteAccessPrivacy) {
                    sql = "select * from log force index (datetime) where dateTime <= ?";
                    sql += " and dateTime >= ? and content like '" + content + "' ";
                    sql += "and provider_no IN (SELECT provider_no FROM providersite WHERE site_id IN (SELECT site_id from providersite where provider_no= " + curUser_no + ") )";
                    sql += " order by dateTime desc ";
                } else {
                    sql = "select * from log force index (datetime) where dateTime <= ?";
                    sql += " and dateTime >= ? and content like '" + content + "' order by dateTime desc ";
                }
            }
            rs = dbObj.queryResults(sql, params);
            while (rs.next()) {
                prop = new Properties();
                prop.setProperty("dateTime", "" + rs.getTimestamp("dateTime"));
                prop.setProperty("action", Encode.forHtmlContent(Misc.getString(rs, "action")));
                prop.setProperty("content", Encode.forHtmlContent(Misc.getString(rs, "content")));
                prop.setProperty("contentId", Encode.forHtmlContent(Misc.getString(rs, "contentId")));
                prop.setProperty("ip", Misc.getString(rs, "ip"));
                prop.setProperty("provider_no", Encode.forHtmlContent(Misc.getString(rs, "provider_no")));
                prop.setProperty("demographic_no", Encode.forHtmlContent(Misc.getString(rs, "demographic_no")));
                prop.setProperty("data", Encode.forHtmlContent(Misc.getString(rs, "data")).replaceAll("\n", "\n<br/>"));
                vec.add(prop);
            }

            //startDate = sDate;
            //endDate = eDate;
        }
    %>
    <h4><%
        if (propName.getProperty(providerNo, "").equals("")) {
            out.print("All");
        } else {
            out.print(propName.getProperty(providerNo, ""));
        }
    %> - Log Report</h4>

    <button class="btn pull-right" onClick="window.print()" style="margin-bottom:4px">
        <i class="icon-print"></i> Print
    </button>


    <p>Period: ( <%= startDate == null ? "" : startDate %> ~ <%= endDate == null ? "" : endDate %>)</p>
    <table class="table table-bordered table-striped table-hover table-condensed">
        <tr bgcolor="<%=tdTitleColor%>">
            <TH>Time</TH>
            <TH>Action</TH>
            <TH>Content</TH>
            <TH>Keyword</TH>
            <TH>IP</TH>
            <% if (bAll) { %>
            <TH>Provider</TH>
            <% } %>
            <TH>Demo</TH>
            <TH>Data</TH>
        </tr>
                <%
String catName = "";
String color = "";
int codeNum = 0;
int vecNum = 0;
for (int i = 0; i < vec.size(); i++) {
	prop = (Properties) vec.get(i);
    color = i%2==0?tdInterlColor:"white";
%>
        <tr bgcolor="<%=color %>" align="center">
            <td><c:out value='<%=prop.getProperty("dateTime")%>'/></td>
            <td><c:out value='<%=prop.getProperty("action")%>'/></td>
            <td><c:out value='<%=prop.getProperty("content")%>'/></td>
            <td><c:out value='<%=prop.getProperty("contentId")%>'/></td>
            <td><c:out value='<%=prop.getProperty("ip")%>'/></td>
            <% if (bAll) { %>
            <td><c:out value='<%=propName.getProperty(prop.getProperty("provider_no"), "")%>'/></td>
            <% } %>
            <td><c:out value='<%=prop.getProperty("demographic_no")%>'/></td>
            <td><c:out value='<%=prop.getProperty("data") %>'/></td>
        </tr>

                <% } %>

        <script type="text/javascript">
            var startDate = $("#startDate1").datepicker({
                format: "yyyy-mm-dd"
            });

            var endDate = $("#endDate1").datepicker({
                format: "yyyy-mm-dd"
            });
        </script>
    </body>
</html:html>
