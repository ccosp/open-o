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

<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
    String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_search" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect(request.getContextPath() + "/securityError.jsp?type=_search");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>

<!DOCTYPE HTML>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar"%>

<% Boolean isMobileOptimized = session.getAttribute("mobileOptimized") != null; %>

<html:html lang="en">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title><bean:message key="demographic.search.title" /></title>
<script type="text/javascript">

        function setfocus() {
            document.titlesearch.keyword.focus();
            document.titlesearch.keyword.select();            
        }
        
        function checkTypeIn() {
          var dob = document.titlesearch.keyword; typeInOK = true;          
          if (dob.value.indexOf('%b610054') == 0 && dob.value.length > 18){
             document.titlesearch.keyword.value = dob.value.substring(8,18);
             document.titlesearch.search_mode[4].checked = true;             
          }

			if(document.titlesearch.search_mode[0].checked) {
				var keyword = document.titlesearch.keyword.value; 
      			var keywordLowerCase = keyword.toLowerCase();
      			document.titlesearch.keyword.value = keywordLowerCase;		
			}
			
          if(document.titlesearch.search_mode[2].checked) {
            if(dob.value.length==8) {
              dob.value = dob.value.substring(0, 4)+"-"+dob.value.substring(4, 6)+"-"+dob.value.substring(6, 8);
            }
            if(dob.value.length != 10) {
              alert('<bean:message key="demographic.search.msgWrongDOB"/>');
              typeInOK = false;
            }
            return typeInOK ;
          } else {
            return true;
          }
        }        
        
        function searchInactive() {
            document.titlesearch.ptstatus.value="inactive";
            if (checkTypeIn()) document.titlesearch.submit();
        }
            
        function searchAll() {
            document.titlesearch.ptstatus.value="";
            if (checkTypeIn()) document.titlesearch.submit();
        }

        function searchOutOfDomain() {
            document.titlesearch.outofdomain.value="true";
            if (checkTypeIn()) document.titlesearch.submit();
        }
         
        </script>

<%-- <link rel="stylesheet" type="text/css" media="all" href="../demographic/searchdemographicstyle.css"  />--%>
	<script src="${pageContext.request.contextPath}/library/jquery/jquery-3.6.4.min.js" type="text/javascript"></script>
	<script src="${pageContext.request.contextPath}/library/bootstrap/3.0.0/js/bootstrap.min.js" type="text/javascript"></script>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/library/jquery/jquery-ui-1.12.1.min.css"/>
	<link href="${pageContext.request.contextPath}/library/bootstrap/3.0.0/css/bootstrap.css" rel="stylesheet" type="text/css"/>
	<link rel="stylesheet" type="text/css" media="all" href="${pageContext.request.contextPath}/share/css/searchBox.css"  />
</head>
<body onload="setfocus()">
<div class="container">
<h2 style="margin:auto 15px;"><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-search" viewBox="0 0 16 16">
	<path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0"/>
</svg>Search Patient</h2>
    <%@ include file="zdemographicfulltitlesearch.jsp"%>

<!-- <security:oscarSec roleName="<%=roleName$%>" objectName="_demographic.addnew" rights="r">  -->
    <div class="createNew">
	<a href="demographicaddarecordhtm.jsp"><b><font size="+1"><bean:message
	key="demographic.search.btnCreateNew" /></font></b></a> 
    </div>
<!-- </security:oscarSec> -->
		
	<oscar:oscarPropertiesCheck
	property="SHOW_FILE_IMPORT_SEARCH" value="yes">
           &nbsp;&nbsp;&nbsp;<a href="demographicImport.jsp"><b><font
		size="+1"><bean:message	key="demographic.search.importNewDemographic" /></font></b></a>
</oscar:oscarPropertiesCheck>
</div>
</body>
</html:html>
