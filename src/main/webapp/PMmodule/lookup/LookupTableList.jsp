<%--

    Copyright (c) 2005, 2009 IBM Corporation and others.
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

    Contributors:
        <Quatro Group Software Systems inc.>  <OSCAR Team>

--%>
<%@ include file="/taglibs.jsp" %>


<table width="100%" height="100%" cellpadding="0px" cellspacing="0px">
    <tr>
        <th class="pageTitle" align="center"><span
                id="_ctl0_phBody_lblTitle" align="left">Lookup Tables Management
			</span></th>
    </tr>
    <tr>
        <td align="left" class="buttonBar2"><html:link
                action="/PMmodule/Admin/SysAdmin.do"
                style="color:Navy;text-decoration:none;">
            <img border=0 src=
                    <html:rewrite page="/images/close16.png"/>/>&nbsp;Close&nbsp;&nbsp;|</html:link>
        </td>

    </tr>
    <tr>
        <td align="left"></td>
    </tr>
    <tr>
        <td height="100%">
            <div
                    style="color: Black; background-color: White; border-width: 1px; border-style: Ridge;
                    height: 100%; width: 100%; overflow: auto;" id="scrollBar">


                <c:forEach var="module" items="${lookupTableListForm.modules}">
                    <UL>
                        <li>
                            <c:out value="${module.description}"></c:out>
                            <ul>
                                <c:forEach var="lkTable" items="${module.associates}">
                                    <li><html:link action="/Lookup/LookupCodeList.do" paramName="lkTable"
                                                   paramProperty="code" paramId="id">
                                        <bean:write name="lkTable" property="description"/></html:link></li>
                                </c:forEach>
                            </ul>
                        </li>
                    </UL>
                </c:forEach>


            </div>
        </td>
    </tr>
</table>
