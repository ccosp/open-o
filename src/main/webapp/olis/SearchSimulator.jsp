<%--

    Copyright (c) 2008-2012 Indivica Inc.

    This software is made available under the terms of the
    GNU General Public License, Version 2, 1991 (GPLv2).
    License details are available via "indivica.ca/gplv2"
    and "gnu.org/licenses/gpl-2.0.html".

--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page
        import="java.util.*,oscar.oscarReport.reportByTemplate.*,org.oscarehr.olis.*,org.oscarehr.olis.model.*, org.oscarehr.olis.dao.*, ca.openosp.openo.ehrutil.SpringUtils, org.joda.time.DateTime, org.joda.time.format.DateTimeFormat, org.joda.time.format.DateTimeFormatter" %>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>
<%
    if (session.getAttribute("userrole") == null) response.sendRedirect("../logout.jsp");
    String roleName$ = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    if (session.getAttribute("user") == null) response.sendRedirect("../logout.jsp");
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>OLIS Search Simulator</title>
    <link rel="stylesheet" type="text/css"
          href="../share/css/OscarStandardLayout.css">

    <script type="text/javascript" language="JavaScript"
            src="../share/javascript/prototype.js"></script>
    <script type="text/javascript" language="JavaScript"
            src="../share/javascript/Oscar.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
    <script type="text/javascript">
        jQuery.noConflict();
    </script>
    <script type="text/JavaScript">
        function popupPage(vheight, vwidth, varpage) { //open a new popup window
            var page = "" + varpage;
            windowprops = "height=" + vheight + ",width=" + vwidth + ",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=0,screenY=0,top=0,left=0";//360,680
            var popup = window.open(page, "groupno", windowprops);
            if (popup != null) {
                if (popup.opener == null) {
                    popup.opener = self;
                }
                popup.focus();
            }
        }
    </script>
    <style type="text/css">
        table.outline {
            margin-top: 50px;
            border-bottom: 1pt solid #888888;
            border-left: 1pt solid #888888;
            border-top: 1pt solid #888888;
            border-right: 1pt solid #888888;
        }

        table.grid {
            border-bottom: 1pt solid #888888;
            border-left: 1pt solid #888888;
            border-top: 1pt solid #888888;
            border-right: 1pt solid #888888;
        }

        td.gridTitles {
            border-bottom: 2pt solid #888888;
            font-weight: bold;
            text-align: center;
        }

        td.gridTitlesWOBottom {
            font-weight: bold;
            text-align: center;
        }

        td.middleGrid {
            border-left: 1pt solid #888888;
            border-right: 1pt solid #888888;
            text-align: center;
        }

        label {
            float: left;
            width: 120px;
            font-weight: bold;
        }

        label.checkbox {
            float: left;
            width: 116px;
            font-weight: bold;
        }

        label.fields {
            float: left;
            width: 80px;
            font-weight: bold;
        }

        span.labelLook {
            font-weight: bold;
        }

        input, textarea, select {
        / / margin-bottom: 5 px;
        }

        textarea {
            width: 450px;
            height: 100px;
        }

        .boxes {
            width: 1em;
        }

        #submitbutton {
            margin-left: 120px;
            margin-top: 5px;
            width: 90px;
        }

        br {
            clear: left;
        }
    </style>
</head>
<body vlink="#0000FF" class="BodyStyle">
<form action="<%=request.getContextPath()%>/olis/Results.do" method="POST">
    <table class="MainTable">
        <tr class="MainTableTopRow">
            <td class="MainTableTopRowLeftColumn">OLIS</td>
            <td class="MainTableTopRowRightColumn">
                <table class="TopStatusBar" style="width: 100%;">
                    <tr>
                        <td>Search Simulator</td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td class="MainTableLeftColumn" valign="top" width="160px;">&nbsp;</td>
            <td class="MainTableRightColumn" valign="top">
                <div>ERP Message:</div>
                <textarea name="olisResponseContent" style="width:640px; height:640px;"></textarea>
                <div>XML Message (for simulating errors):</div>
                <textarea name="olisXmlResponse" style="width:640px; height:640px;"></textarea>
                <div><input type="submit" value="Submit"/></div>
            </td>
        </tr>
    </table>
</form>
</body>
</html>
