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

<%
    //reportbilledvisit2.jsp?sdate=2002-04-15&edate=2003-03-31

    String curUser_no = (String) session.getAttribute("user");
    String orderby = request.getParameter("orderby") != null ? request.getParameter("orderby") : ("b.billing_date");
    String deepcolor = "#CCCCFF", weakcolor = "#EEEEFF";
%>
<%@ page
        import="java.util.*, java.sql.*, oscar.*, java.text.*, oscar.oscarDB.*,java.net.*"
        errorPage="../appointment/errorpage.jsp" %>
<%@ page import="ca.openosp.openo.oscarDB.DBPreparedHandler" %>
<%@ page import="ca.openosp.openo.oscarDB.DBPreparedHandlerParam" %>
<%@ page import="ca.openosp.openo.MyDateFormat" %>
<html>
<head>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
    <title>PATIENT NO SHOW LIST</title>
    <meta http-equiv=Expires content=-1>
    <!--link rel="stylesheet" href="../ehrweb.css" -->
    <script language="JavaScript">
        <!--
        function setfocus() {
            this.focus();
            //document.titlesearch.keyword.select();
        }


        //-->
    </SCRIPT>
</head>
<body bgproperties="fixed" onLoad="setfocus()" topmargin="0"
      leftmargin="0" rightmargin="0">
<%
    //busy ... busy ... busy ..................................................<br>
//display splash-msg first
    out.flush();

// get total patientNum/rosterNR/rosterRO/sexF/sexM/
    String sdate = request.getParameter("sdate") != null ? request.getParameter("sdate") : "2003-04-01";
    String edate = request.getParameter("edate") != null ? request.getParameter("edate") : "2003-12-31";
    Vector vNurse = new Vector();
    Vector vNurseNo = new Vector();
    Vector vServiceCode = new Vector();
    Vector vServiceDesc = new Vector();
    Properties props = new Properties();
    DBPreparedHandler db = new DBPreparedHandler();
    DBPreparedHandlerParam[] params = new DBPreparedHandlerParam[2];
    params[0] = new DBPreparedHandlerParam(MyDateFormat.getSysDate(sdate));
    params[1] = new DBPreparedHandlerParam(MyDateFormat.getSysDate(edate));
// get nurse name list
    String sql = "select provider_no, last_name, first_name from provider where provider_type like 'nurse%'";
    ResultSet rs = db.queryResults(sql);
    while (rs.next()) {
        vNurse.add(oscar.Misc.getString(rs, "last_name") + ", " + oscar.Misc.getString(rs, "first_name"));
        vNurseNo.add(oscar.Misc.getString(rs, "provider_no"));
    }

// get service code list
    sql = "select distinct(service_code), service_desc from billingdetail bd where bd.status!='D' and bd.appointment_date>=? and bd.appointment_date<=? order by service_code";
    rs = db.queryResults(sql, params);
    while (rs.next()) {
        vServiceCode.add(oscar.Misc.getString(rs, "service_code"));
        vServiceDesc.add(oscar.Misc.getString(rs, "service_desc"));
    }

    for (int i = 0; i < vServiceCode.size(); i++) {
        // get total pat
        sql = "select count(distinct(b.demographic_no)) from billing b, billingdetail bd where b.billing_no=bd.billing_no  and b.billing_date>='" + sdate + "' and b.billing_date<='" + edate + "' and b.status!='D' and bd.status!='D' and bd.service_code='" + vServiceCode.get(i) + "' and bd.service_desc='" + vServiceDesc.get(i) + "'";
        rs = db.queryResults(sql, params);
        while (rs.next()) {
            props.setProperty(vServiceCode.get(i) + "pat" + vServiceDesc.get(i), oscar.Misc.getString(rs, "count(distinct(b.demographic_no))"));
        }

/*
	out.println("<hr>");
	out.println("Pat - " +vServiceCode.get(i) + " : " + props.getProperty(vServiceCode.get(i) + "pat" + vServiceDesc.get(i)) + "<br>");
	out.println("busy ... busy ... busy ..................................................<br>");
	out.flush();
*/

        // get total vis
        sql = "select count(distinct(b.billing_no)) from billing b, billingdetail bd where b.billing_no=bd.billing_no  and b.billing_date>='" + sdate + "' and b.billing_date<='" + edate + "' and b.status!='D' and bd.status!='D' and bd.service_code='" + vServiceCode.get(i) + "' and bd.service_desc='" + vServiceDesc.get(i) + "'";
        rs = db.queryResults(sql, params);
        while (rs.next()) {
            props.setProperty(vServiceCode.get(i) + "vis" + vServiceDesc.get(i), oscar.Misc.getString(rs, "count(distinct(b.billing_no))"));
        }

/*
	out.println("<hr>");
	out.println("vis - " +vServiceCode.get(i) + " : " + props.getProperty(vServiceCode.get(i) + "vis" + vServiceDesc.get(i)) + "<br>");
	out.println("busy ... busy ... busy ..................................................<br>");
	out.flush();
*/

        // get pat Physician
        sql = "select count(distinct(b.demographic_no)) from billing b, billingdetail bd, provider p where b.billing_no=bd.billing_no and b.creator=p.provider_no and b.billing_date>='" + sdate + "' and b.billing_date<='" + edate + "' and b.status!='D' and bd.status!='D' and bd.service_code='" + vServiceCode.get(i) + "' and  p.provider_type='doctor'" + " and bd.service_desc='" + vServiceDesc.get(i) + "'";
        rs = db.queryResults(sql, params);
        while (rs.next()) {
            props.setProperty(vServiceCode.get(i) + "patPhy" + vServiceDesc.get(i), oscar.Misc.getString(rs, "count(distinct(b.demographic_no))"));
        }

/*
	out.println("<hr>");
	out.println("Pat Physician- " +vServiceCode.get(i) + " : " + props.getProperty(vServiceCode.get(i) + "patPhy" + vServiceDesc.get(i)) + "<br>");
	out.println("busy ... busy ... busy ..................................................<br>");
	out.flush();
*/

        // get vis Physician
        sql = "select count(distinct(b.billing_no)) from billing b, billingdetail bd, provider p where b.billing_no=bd.billing_no and b.creator=p.provider_no and b.billing_date>='" + sdate + "' and b.billing_date<='" + edate + "' and b.status!='D' and bd.status!='D' and bd.service_code='" + vServiceCode.get(i) + "' and  p.provider_type='doctor'" + " and bd.service_desc='" + vServiceDesc.get(i) + "'";
        rs = db.queryResults(sql, params);
        while (rs.next()) {
            props.setProperty(vServiceCode.get(i) + "visPhy" + vServiceDesc.get(i), oscar.Misc.getString(rs, "count(distinct(b.billing_no))"));
        }

/*
	out.println("<hr>");
	out.println("vis Physician- " +vServiceCode.get(i) + " : " + props.getProperty(vServiceCode.get(i) + "visPhy" + vServiceDesc.get(i)) + "<br>");
	out.println("busy ... busy ... busy ..................................................<br>");
	out.flush();
*/

        // get pat Resident
        sql = "select count(distinct(b.demographic_no)) from billing b, billingdetail bd, provider p where b.billing_no=bd.billing_no and b.creator=p.provider_no and b.billing_date>='" + sdate + "' and b.billing_date<='" + edate + "' and b.status!='D' and bd.status!='D' and bd.service_code='" + vServiceCode.get(i) + "' and  p.provider_type='resident'" + " and bd.service_desc='" + vServiceDesc.get(i) + "'";
        rs = db.queryResults(sql, params);
        while (rs.next()) {
            props.setProperty(vServiceCode.get(i) + "patRes" + vServiceDesc.get(i), oscar.Misc.getString(rs, "count(distinct(b.demographic_no))"));
        }

/*
	out.println("<hr>");
	out.println("Pat Resident- " +vServiceCode.get(i) + " : " + props.getProperty(vServiceCode.get(i) + "patRes" + vServiceDesc.get(i)) + "<br>");
	out.println("busy ... busy ... busy ..................................................<br>");
	out.flush();
*/

        // get vis Resident
        sql = "select count(distinct(b.billing_no)) from billing b, billingdetail bd, provider p where b.billing_no=bd.billing_no and b.creator=p.provider_no and b.billing_date>='" + sdate + "' and b.billing_date<='" + edate + "' and b.status!='D' and bd.status!='D' and bd.service_code='" + vServiceCode.get(i) + "' and  p.provider_type='resident'" + " and bd.service_desc='" + vServiceDesc.get(i) + "'";
        rs = db.queryResults(sql, params);
        while (rs.next()) {
            props.setProperty(vServiceCode.get(i) + "visRes" + vServiceDesc.get(i), oscar.Misc.getString(rs, "count(distinct(b.billing_no))"));
        }

/*
	out.println("<hr>");
	out.println("vis Resident- " +vServiceCode.get(i) + " : " + props.getProperty(vServiceCode.get(i) + "visRes" + vServiceDesc.get(i)) + "<br>");
	out.println("busy ... busy ... busy ..................................................<br>");
	out.flush();
*/

        // get pat num for Nurses
        for (int j = 0; j < vNurseNo.size(); j++) {
            sql = "select count(distinct(b.demographic_no)) from billing b, billingdetail bd  where b.billing_no=bd.billing_no and bd.service_code='" + vServiceCode.get(i) + "' and b.creator='" + vNurseNo.get(j) + "' and b.billing_date>='" + sdate + "' and b.billing_date<='" + edate + "' and b.status!='D' and bd.status!='D'" + " and bd.service_desc='" + vServiceDesc.get(i) + "'";
            rs = db.queryResults(sql, params);
            while (rs.next()) {
                props.setProperty(vServiceCode.get(i) + "patNurse" + j + vServiceDesc.get(i), oscar.Misc.getString(rs, "count(distinct(b.demographic_no))"));
            }
/*
		out.println("<hr>");
		out.println("Pat - patNurse (" + vNurse.get(j) + "): " + props.getProperty("patNurse" + j + vServiceDesc.get(i)) + "<br>");
		out.println("busy ... busy ... busy ..................................................<br>");
		out.flush();
*/

            sql = "select count(distinct(b.billing_no)) from billing b, billingdetail bd  where b.billing_no=bd.billing_no and bd.service_code='" + vServiceCode.get(i) + "' and b.creator='" + vNurseNo.get(j) + "' and b.billing_date>='" + sdate + "' and b.billing_date<='" + edate + "' and b.status!='D' and bd.status!='D'" + " and bd.service_desc='" + vServiceDesc.get(i) + "'";
            rs = db.queryResults(sql, params);
            while (rs.next()) {
                props.setProperty(vServiceCode.get(i) + "visNurse" + j + vServiceDesc.get(i), oscar.Misc.getString(rs, 1));
            }
/*
		out.println("<hr>");
		out.println("Vis - patNurse (" + vNurse.get(j) + "): " + props.getProperty("visNurse" + j + vServiceDesc.get(i)) + "<br>");
		out.println("busy ... busy ... busy ..................................................<br>");
		out.flush();
*/
        }

    }


    rs.close();
%>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr bgcolor=<%=deepcolor%>>
        <th><font face="Helvetica">PATIENT VISIT LIST </font></th>
        <th width="10%" nowrap><input type="button" name="Button"
                                      value="Print" onClick="window.print()"><input type="button"
                                                                                    name="Button" value=" Exit "
                                                                                    onClick="window.close()"></th>
    </tr>
</table>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr>
        <td>Period: (<%=sdate%> ~ <%=edate%>)</td>
    </tr>
</table>

<table width="100%" border="1" bgcolor="#ffffff" cellspacing="1"
       cellpadding="0">
    <tr bgcolor=<%=deepcolor%> align="center">
        <TH width="5%">ServiceCode</TH>
        <TH width="10%">Desc</TH>
        <TH width="6%">Pat_Total</TH>
        <TH width="6%">Vis_Total</TH>

        <TH width="6%">Pat_Phys</TH>
        <TH width="6%">Vis_Phys</TH>

        <TH width="6%">Pat_Res</TH>
        <TH width="6%">Vis_Res</TH>

        <% for (int i = 0; i < vNurseNo.size(); i++) { %>
        <TH width="6%"><%="pat_" + vNurse.get(i)%>
        </TH>
        <TH width="6%"><%="vis_" + vNurse.get(i)%>
        </TH>
        <% } %>

    </tr>

    <% for (int i = 0; i < vServiceCode.size(); i++) { %>
    <tr bgcolor="<%= i%2==0?weakcolor:"white"%>">
        <td align="center"><%=vServiceCode.get(i)%>
        </td>
        <td><%=vServiceDesc.get(i)%>
        </td>
        <td align="center"><%=props.getProperty(vServiceCode.get(i) + "pat" + vServiceDesc.get(i))%>
        </td>
        <td align="center"><%=props.getProperty(vServiceCode.get(i) + "vis" + vServiceDesc.get(i))%>
        </td>

        <td align="center"><%=props.getProperty(vServiceCode.get(i) + "patPhy" + vServiceDesc.get(i))%>
        </td>
        <td align="center"><%=props.getProperty(vServiceCode.get(i) + "visPhy" + vServiceDesc.get(i))%>
        </td>

        <td align="center"><%=props.getProperty(vServiceCode.get(i) + "patRes" + vServiceDesc.get(i))%>
        </td>
        <td align="center"><%=props.getProperty(vServiceCode.get(i) + "visRes" + vServiceDesc.get(i))%>
        </td>

        <% for (int j = 0; j < vNurseNo.size(); j++) { %>
        <td align="center"><%=props.getProperty(vServiceCode.get(i) + "patNurse" + j + vServiceDesc.get(i))%>
        </td>
        <td align="center"><%=props.getProperty(vServiceCode.get(i) + "visNurse" + j + vServiceDesc.get(i))%>
        </td>
        <% } %>
    </tr>
    <% } %>


</table>
</body>
</html>
