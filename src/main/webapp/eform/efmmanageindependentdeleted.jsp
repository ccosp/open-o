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

<%@page import="java.util.*,oscar.eform.*" %>
<%@page import="org.oscarehr.web.eform.EfmPatientFormList" %>
<%@page import="org.oscarehr.util.SpringUtils" %>
<%@page import="org.oscarehr.PMmodule.dao.ProviderDao" %>
<%@ page import="ca.openosp.openo.eform.EFormUtil" %>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>
<%
    if (session.getAttribute("userrole") == null) response.sendRedirect("../logout.jsp");
    String country = request.getLocale().getCountry();

    ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);

    String orderByRequest = request.getParameter("orderby");
    String orderBy = "";
    if (orderByRequest == null) orderBy = EFormUtil.DATE;
    else orderBy = orderByRequest;
%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>

<html:html lang="en">

    <head>
        <title><bean:message key="eform.showmyform.title"/></title>

        <script type="text/javascript">
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

            function checkSelectBox() {
                var selectVal = document.forms[0].group_view.value;
                if (selectVal == "default") {
                    return false;
                }
            }
        </script>
    </head>

    <body>
    <%@ include file="efmTopNav.jspf" %>


    <div class="well">
        <h3 style="display:inline"><bean:message key="admin.admin.frmIndependent"/>s</h3> <i
            class="icon-question-sign"></i> <oscar:help keywords="patient independent" key="app.top1"/>

        <p>View: <a href="<%=request.getContextPath()%>/eform/efmmanageindependent.jsp"
                    class="contentLink"><bean:message key="eform.independent.btnCurrent"/></a> | <bean:message
                key="eform.independent.btnDeleted"/></p>

        <table id="scrollNumber1" name="encounterTable" class="table table-condensed table-striped">
            <thead>
            <tr>
                <th>
                    <a href="<%=request.getContextPath()%>/eform/efmmanageindependent.jsp?orderby=<%=EFormUtil.NAME%>"
                       class="contentLink">
                        <bean:message key="eform.showmyform.btnFormName"/>
                    </a>
                </th>
                <th>
                    <a href="<%=request.getContextPath()%>/eform/efmmanageindependent.jsp?orderby=<%=EFormUtil.SUBJECT%>"
                       class="contentLink">
                        <bean:message key="eform.showmyform.btnSubject"/>
                    </a>
                </th>
                <th>
                    <a href="<%=request.getContextPath()%>/eform/efmmanageindependent.jsp?orderby=<%=EFormUtil.PROVIDER%>"
                       class="contentLink">
                        <bean:message key="eform.showmyform.btnFormProvider"/>
                    </a>
                </th>
                <th>
                    <a href="<%=request.getContextPath()%>/eform/efmmanageindependent.jsp" class="contentLink">
                        <bean:message key="eform.showmyform.formDate"/>
                    </a>
                </th>
                <th>
                    <bean:message key="eform.showmyform.msgAction"/>
                </th>
            </tr>
            </thead>

            <tbody>
            <%
                ArrayList<HashMap<String, ? extends Object>> eForms;
                eForms = EFormUtil.listPatientIndependentEForms(orderBy, EFormUtil.DELETED);

                for (int i = 0; i < eForms.size(); i++) {
                    HashMap<String, ? extends Object> curform = eForms.get(i);
            %>
            <tr bgcolor="<%=((i % 2) == 1)?"#F2F2F2":"white"%>">
                <td><a href="#"
                       ONCLICK="popupPage('<%=request.getContextPath()%>/eform/efmshowform_data.jsp?fdid=<%=curform.get("fdid")%>', '<%="FormP" + i%>'); return false;"
                       TITLE="<bean:message key="eform.showmyform.msgViewFrm"/>"
                       onmouseover="window.status='<bean:message
                               key="eform.showmyform.msgViewFrm"/>'; return true"><%=curform.get("formName")%>
                </a></td>
                <td><%=curform.get("formSubject")%>
                </td>
                <td align='center'><%=providerDao.getProviderNameLastFirst((String) curform.get("providerNo"))%>
                </td>
                <td align='center'><%=curform.get("formDate")%>
                </td>
                <td align='center'>
                    <a href="<%=request.getContextPath()%>/eform/unRemoveEForm.do?callpage=independent&fdid=<%=curform.get("fdid")%>"
                       onClick="javascript: return confirm('Are you sure you want to restore this eform?');"
                       class="contentLink"><bean:message key="global.btnRestore"/></a>
                </td>
            </tr>
            <%
                }
                if (eForms.size() <= 0) {
            %>
            <tr>
                <td align='center' colspan='5'>
                    <bean:message key="eform.showmyform.msgNoData"/>
                </td>
            </tr>
            <%
                }
            %>
            </tbody>
        </table>


        <%@ include file="efmFooter.jspf" %>
    </div>
    </body>
</html:html>
