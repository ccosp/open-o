<%--

    Copyright (c) 2008-2012 Indivica Inc.

    This software is made available under the terms of the
    GNU General Public License, Version 2, 1991 (GPLv2).
    License details are available via "indivica.ca/gplv2"
    and "gnu.org/licenses/gpl-2.0.html".

--%>
<!DOCTYPE html>
<%@page import="org.oscarehr.util.LoggedInInfo"%>
<%@page import="java.util.*, org.oscarehr.hospitalReportManager.*,org.oscarehr.hospitalReportManager.model.HRMCategory"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
      String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
      boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_hrm" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("{ pageContext.request.contextPath }/securityError.jsp?type=_hrm");%>
</security:oscarSec>
<%
if(!authed) {
	return;
}
%>


<%
	LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);

	String demographic_no = request.getParameter("demographic_no");
	String deepColor = "#CCCCFF", weakColor = "#EEEEFF";

	String orderBy = request.getParameter("orderBy") != null ?  request.getParameter("orderBy") : "report_date";
	String orderAsc = request.getParameter("orderAsc") != null ?  request.getParameter("orderAsc") : "false";
	boolean asc = new Boolean(orderAsc);

	ArrayList<HashMap<String,? extends Object>> hrmdocs;
	hrmdocs = HRMUtil.listHRMDocuments(loggedInInfo,orderBy, asc, demographic_no,true);

%>

<html:html lang="en">

<head>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/global.js"></script>
<title>HRM Document List</title>

    <link href="${ pageContext.request.contextPath }/css/bootstrap.css" rel="stylesheet" type="text/css"> <!-- Bootstrap 2.3.1 -->
    <link href="${ pageContext.request.contextPath }/css/DT_bootstrap.css" rel="stylesheet" type="text/css">
    <link href="${ pageContext.request.contextPath }/library/DataTables-1.10.12/media/css/jquery.dataTables.min.css" rel="stylesheet" >
    <script src="${ pageContext.request.contextPath }/library/jquery/jquery-3.6.4.min.js"></script>
    <script src="${ pageContext.request.contextPath }/js/global.js"></script>
    <script src="${ pageContext.request.contextPath }/library/DataTables/datatables.min.js"></script> <!-- DataTables 1.13.4 -->

    <script>
	    jQuery(document).ready( function () {
	        jQuery('#tblHRM').DataTable({
            "order": [],
            "language": {
                        "url": "<%=request.getContextPath() %>/library/DataTables/i18n/<bean:message key="global.i18nLanguagecode"/>.json"
                    }
            });
	    });
    </script>
    <script>
    function popupPage(varpage, windowname) {
        var page = "" + varpage;
        windowprops = "height=700,width=800,location=no,"
        + "scrollbars=yes,menubars=no,status=yes,toolbars=no,resizable=yes,top=10,left=200";
        var popup = window.open(page, windowname, windowprops);
        if (popup != null) {
           if (popup.opener == null) {
              popup.opener = self;
           }
           popup.focus();
        }
    }
    </script>
</head>
<body>

<table class="MainTable" id="scrollNumber1">
	<tr class="MainTableTopRow">
		<td class="MainTableTopRowLeftColumn" style="width:175px"><h4>HRM</h4></td>
		<td class="MainTableTopRowRightColumn">
		<table class="TopStatusBar" style="width:100%">
			<tr>
				<td><h4><bean:message key="hrm.displayHRMDocList.displaydocs" /></h4></td>
				<td>&nbsp;</td>
				<td style="text-align: right"><a
					href="javascript:popupStart(300,400,'Help.jsp')"><bean:message
					key="global.help" /></a> | <a
					href="javascript:popupStart(300,400,'About.jsp')"><bean:message
					key="global.about" /></a> | <a
					href="javascript:popupStart(300,400,'License.jsp')"><bean:message
					key="global.license" /></a></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td class="MainTableLeftColumn">
			</td>
		<td class="MainTableRightColumn">
				<table id="tblHRM" class="table table-striped table-hover table-condensed" style="width:100%">
                    <thead>
					<tr>

						<th><bean:message key="hrm.displayHRMDocList.reportType" /></th>
						<th><bean:message	key="hrm.displayHRMDocList.description" /></th>
						<th><bean:message	key="hrm.displayHRMDocList.reportStatus" /></th>
						<th>Report Date</th>
						<th><bean:message key="hrm.displayHRMDocList.timeReceived" /></th>
						<th>Category</th>
						<th>Class/Subclass/Accompanying Subclass</th>
					</tr>
                    </thead>
					<%


						for (int i = 0; i < hrmdocs.size(); i++)
						{
							HashMap<String,? extends Object> curhrmdoc = hrmdocs.get(i);
					%>
					<tr>

						<td><a href="#"
							ONCLICK="popupPage('<%=request.getContextPath() %>/hospitalReportManager/Display.do?id=<%=curhrmdoc.get("id")%>', 'HRM Report'); return false;"
							><%=curhrmdoc.get("report_type")%></a></td>
						<td><%=curhrmdoc.get("description")%></td>
						<td><%=curhrmdoc.get("report_status")%></td>
						<td style="text-align: center;"><%=curhrmdoc.get("report_date")%></td>
						<td style="text-align: center;"><%=curhrmdoc.get("time_received")%></td>
						<td><%=curhrmdoc.get("category") != null ? curhrmdoc.get("category")  : "" %>
						<td><%=curhrmdoc.get("class_subclass") != null ? curhrmdoc.get("class_subclass")  : "" %>
					</tr>
					<%
						}
							if (hrmdocs.size() <= 0)
							{
					%>
					<tr>
						<td ><bean:message key="eform.showmyform.msgNoData" /></td>
						<td></td><!-- this empty td is here so that the number of columns matches the <th>, an important requirement when using jquery datatables-->
						<td></td><!-- this empty td is here so that the number of columns matches the <th>, an important requirement when using jquery datatables-->
						<td></td><!-- this empty td is here so that the number of columns matches the <th>, an important requirement when using jquery datatables-->
						<td></td><!-- this empty td is here so that the number of columns matches the <th>, an important requirement when using jquery datatables-->
						<td></td><!-- this empty td is here so that the number of columns matches the <th>, an important requirement when using jquery datatables-->
						<td></td><!-- this empty td is here so that the number of columns matches the <th>, an important requirement when using jquery datatables-->
	
					</tr>
					<%
						}
					%>
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