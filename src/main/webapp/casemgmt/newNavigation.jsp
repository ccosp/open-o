
<%--


    Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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

    This software was written for
    Centre for Research on Inner City Health, St. Michael's Hospital,
    Toronto, Ontario, Canada

--%>



<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>


<script type="text/javascript">
    //This object stores the key -> cmd value passed to action class and the id of the created div
    // and the value -> URL of the action class
    <c:set var="ctx" value="${pageContext.request.contextPath}" scope="request"/>        
</script>

<!--dummmy div to force browser to allocate space -->
<div id="leftColLoader" class="leftBox" style="width: 100%">
	<h3 style="width: 100%; background-color: #CCCCFF;">
		<a href="#" onclick="return false;"><bean:message key="oscarEncounter.LeftNavBar.msgLoading"/></a>
	</h3>
</div>

<form style="display: none;" name="dummyForm" action="">
	<input type="hidden" id="reloadDiv" name="reloadDiv" value="none" onchange="updateDiv();">
</form>
