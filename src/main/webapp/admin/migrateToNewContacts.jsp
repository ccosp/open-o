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
<%@page import="org.oscarehr.util.LoggedInInfo"%>
<%@page contentType="text/html"%>
<%@ include file="/casemgmt/taglibs.jsp"%>
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


<%
String curUser_no = (String) session.getAttribute("user");
%>

<html:html>
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/share/jquery/jquery-1.4.2.js"></script>
<script type="text/javascript" src="../share/javascript/Oscar.js"></script>
<link rel="stylesheet" type="text/css" href="../oscarEncounter/encounterStyles.css">

<script type="text/JavaScript">
function popupPage(vheight,vwidth,varpage) { //open a new popup window
  var page = "" + varpage;
  windowprops = "height="+vheight+",width="+vwidth+",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes,screenX=0,screenY=0,top=0,left=0";//360,680
  var popup=window.open(page, "groupno", windowprops);
  if (popup != null) {
    if (popup.opener == null) {
      popup.opener = self;
    }
    popup.focus();
  }
}
</script>
<html:base />

<title>Migration Tool</title>


</head>


<body>
        <table class="MainTable" id="scrollNumber1" name="encounterTable" style="margin: 0px;">
            <tr class="topbar">
                <td class="MainTableTopRowLeftColumn" width="60px">Migrate Contacts</td>
                <td class="MainTableTopRowRightColumn">
                    <table class="TopStatusBar">
                        <tr>
                            <td>Migrate relationships to contacts</td>
                            <td style="text-align: right;"  > 
                                    <a href="javascript: popupStart(300, 400, 'About.jsp')">About</a> |
                                    <a href="javascript: popupStart(300, 400, 'License.jsp')">License</a>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
         </table>
         <br/>
         
         <%if(request.getParameter("action") == null) { %>
        <table  cellspacing="0" style="margin: 0px;" >
            <tr><td>
            	<ul>
           			<li>This utility will move all the relationships to DemographicContacts in the Database.</li>
            		<li>You must enable NEW_CONTACTS_UI in your properties file</li>
            		<li>This action cannot be undone</li>
            	</ul>
            </td></tr>
        </table>
        
        <br/>
        
        <form action="migrateToNewContacts.jsp">
        	<input type="submit" value="Perform Migration"/>
        	<input type="hidden" name="action" value="migrate"/>
        </form>
        
        <% } else { 
        	boolean result = org.oscarehr.admin.web.MigrateRelationshipsToContactsHelper.doMigration(LoggedInInfo.getLoggedInInfoFromSession(request));
        	if(result) {
        		%><h4>Migration was successful</h4><%
        	} else {
        		%><h4>Migration failed</h4><%
        	}
        } %>
         
</body>

</html:html>
