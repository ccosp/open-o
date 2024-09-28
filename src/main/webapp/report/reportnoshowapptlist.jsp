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

<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>
<%
    String roleName$ = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed = true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_report,_admin.reporting" rights="r" reverse="<%=true%>">
    <%authed = false; %>
    <%response.sendRedirect("../securityError.jsp?type=_report&type=_admin.reporting");%>
</security:oscarSec>
<%
    if (!authed) {
        return;
    }
%>

<%@ page import="java.util.*, java.sql.*, oscar.*, java.text.*, java.lang.*,java.net.*"
         errorPage="../appointment/errorpage.jsp" %>

<%@ page import="org.oscarehr.util.SpringUtils" %>

<%@ page import="org.oscarehr.common.model.MyGroup" %>
<%@ page import="org.oscarehr.common.dao.MyGroupDao" %>

<%@ page import="org.oscarehr.common.model.ProviderData" %>
<%@ page import="org.oscarehr.common.dao.ProviderDataDao" %>
<%@ page import="openo.MyDateFormat" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<jsp:useBean id="patientBean" class="openo.AppointmentMainBean" scope="page"/>
<jsp:useBean id="myGroupBean" class="java.util.Vector" scope="page"/>
<jsp:useBean id="providerBean" class="java.util.Properties" scope="session"/>


<%

    String curProvider_no = (String) session.getAttribute("user");
    String orderby = request.getParameter("orderby") != null ? request.getParameter("orderby") : ("appointment_date");
    String deepcolor = "#CCCCFF", weakcolor = "#EEEEFF";

    MyGroupDao dao = SpringUtils.getBean(MyGroupDao.class);
    ProviderDataDao providerDataDao = SpringUtils.getBean(ProviderDataDao.class);


    String[][] dbQueries = new String[][]{
            {"search_noshowappt", "select a.appointment_no, a.appointment_date,a.name, a.provider_no, a.start_time, a.end_time, d.last_name, d.first_name from appointment a, demographic d where (a.status = 'N' or a.status = 'NS') and a.provider_no = ? and a.appointment_date >= ? and a.appointment_date<= ? and a.demographic_no=d.demographic_no  order by " + orderby},
    };

    String[][] responseTargets = new String[][]{};
    patientBean.doConfigure(dbQueries, responseTargets);

    boolean isSiteAccessPrivacy = false;
    boolean isTeamAccessPrivacy = false;
%>
<security:oscarSec objectName="_site_access_privacy" roleName="<%=roleName$%>" rights="r"
                   reverse="false"><%isSiteAccessPrivacy = true; %></security:oscarSec>
<security:oscarSec objectName="_team_access_privacy" roleName="<%=roleName$%>" rights="r"
                   reverse="false"><%isTeamAccessPrivacy = true; %></security:oscarSec>

<%
    List<ProviderData> pdList = null;
    HashMap<String, String> providerMap = new HashMap<String, String>();

    //multisites function
    if (isSiteAccessPrivacy || isTeamAccessPrivacy) {

        if (isSiteAccessPrivacy)
            pdList = providerDataDao.findByProviderSite(curProvider_no);

        if (isTeamAccessPrivacy)
            pdList = providerDataDao.findByProviderTeam(curProvider_no);

        for (ProviderData providerData : pdList) {
            providerMap.put(providerData.getId(), "true");
        }
    }
%>

<html:html lang="en">
    <head>
        <script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
        <title><bean:message key="report.reportnoshowapptlist.title"/></title>
        <link rel="stylesheet" href="../web.css">
        <script language="JavaScript">
            <!--
            function setfocus() {
                this.focus();
                //document.titlesearch.keyword.select();
            }

            //-->
        </SCRIPT>
    </head>
    <%
        GregorianCalendar now = new GregorianCalendar();
        String createtime = now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DAY_OF_MONTH) + " " + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
        now.add(now.DATE, 1);
        int curYear = now.get(Calendar.YEAR);
        int curMonth = (now.get(Calendar.MONTH) + 1);
        int curDay = now.get(Calendar.DAY_OF_MONTH);

        ResultSet rsdemo = null;
        boolean bodd = false;
        boolean bGroup = false;
        String sdate = request.getParameter("sdate") != null ? request.getParameter("sdate") : (curYear + "-" + curMonth + "-" + curDay);
        String provider_no = request.getParameter("provider_no") != null ? request.getParameter("provider_no") : "175";

        //initial myGroupBean if neccessary
        if (provider_no.startsWith("_grp_")) {
            bGroup = true;
            List<MyGroup> myGroups = dao.getGroupByGroupNo(provider_no.substring(5));
            Collections.sort(myGroups, MyGroup.LastNameComparator);
            for (MyGroup myGroup : myGroups) {
                myGroupBean.add(myGroup.getId().getProviderNo());
            }
        }
    %>
    <body bgproperties="fixed" onLoad="setfocus()" topmargin="0" leftmargin="0" rightmargin="0">

    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr bgcolor=<%=deepcolor%>>
            <th><font face="Helvetica"><bean:message
                    key="report.reportnoshowapptlist.msgTitle"/></font></th>
            <th width="10%" nowrap><input type="button" name="Button"
                                          value="<bean:message key="global.btnPrint" />"
                                          onClick="window.print()"><input type="button" name="Button"
                                                                          value="<bean:message key="global.btnExit" />"
                                                                          onClick="window.close()"></th>
        </tr>
    </table>

    <%
        boolean bFistL = true; //first line in a table for TH
        String strTemp = "";
        String[] param = new String[3];
        param[1] = sdate;
        param[2] = createtime;
        int pnum = bGroup ? myGroupBean.size() : 1;
        for (int i = 0; i < pnum; i++) {
            param[0] = bGroup ? ((String) myGroupBean.get(i)) : provider_no;
            rsdemo = patientBean.queryResults(param, "search_noshowappt");
            while (rsdemo.next()) {
                //multisites. skip record if not belong to same site/team
                if (isSiteAccessPrivacy || isTeamAccessPrivacy) {
                    if (providerMap.get(rsdemo.getString("provider_no")) == null) continue;
                }

                bodd = bodd ? false : true;
                if (!strTemp.equals(rsdemo.getString("provider_no"))) { //new provider for a new table
                    strTemp = rsdemo.getString("provider_no");
                    bFistL = true;
                    out.println("</table> <p>");
                }
                if (bFistL) {
                    bFistL = false;
                    bodd = false;
    %>

    <table width="480" border="0" cellspacing="1" cellpadding="0">
        <tr>
            <td><%=providerBean.getProperty(rsdemo.getString("provider_no")) %>
            </td>
            <td align="right"></td>
        </tr>
    </table>
    <table width="100%" border="1" bgcolor="#ffffff" cellspacing="1" cellpadding="0">
        <tr bgcolor=<%=deepcolor%> align="center">
            <TH width="20%"><b><a
                    href="reportnoshowapptlist.jsp?provider_no=<%=provider_no%>&sdate=<%=sdate%>&orderby=appointment_date"><bean:message
                    key="report.reportapptsheet.msgApptDate"/></a></b></TH>
            <TH width="20%"><b><a
                    href="reportnoshowapptlist.jsp?provider_no=<%=provider_no%>&sdate=<%=sdate%>&orderby=start_time"><bean:message
                    key="report.reportapptsheet.msgStartTime"/></a> </b></TH>
            <TH width="20%"><b><a
                    href="reportnoshowapptlist.jsp?provider_no=<%=provider_no%>&sdate=<%=sdate%>&orderby=end_time"><bean:message
                    key="report.reportapptsheet.msgEndTime"/></a> </b></TH>
            <TH width="10%"><b><a
                    href="reportnoshowapptlist.jsp?provider_no=<%=provider_no%>&sdate=<%=sdate%>&orderby=name"><bean:message
                    key="report.reportapptsheet.msgName"/></a></b></TH>
            <TH width="30%"><b><bean:message
                    key="report.reportapptsheet.msgComments"/></b></TH>
        </tr>
        <%
            }
        %>
        <tr bgcolor="<%=bodd?weakcolor:"white"%>">
            <td align="center"><a href=#
                                  onClick="popupPage(300,700,'../appointment/appointmentcontrol.jsp?displaymode=edit&dboperation=search&appointment_no=<%=rsdemo.getString("appointment_no")%>&provider_no=<%=curProvider_no%>&year=<%=MyDateFormat.getYearFromStandardDate(rsdemo.getString("appointment_date"))%>&month=<%=MyDateFormat.getMonthFromStandardDate(rsdemo.getString("appointment_date"))%>&day=<%=MyDateFormat.getDayFromStandardDate(rsdemo.getString("appointment_date"))%>&start_time=<%=rsdemo.getString("start_time")%>&demographic_no=');return false;">
                <%=rsdemo.getString("appointment_date")%>
            </a></td>
            <td align="center"><%=rsdemo.getString("start_time")%>
            </td>
            <td align="center"><%=rsdemo.getString("end_time")%>
            </td>
            <td align="center"><%=rsdemo.getString("name")%>
            </td>
            <td>&nbsp;<bean:message
                    key="report.reportnoshowapptlist.msgNoShow"/></td>
        </tr>
        <%
                }
            }
        %>

    </table>
    </body>
</html:html>
