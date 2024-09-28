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
<security:oscarSec roleName="<%=roleName$%>" objectName="_admin.billing,_admin" rights="w" reverse="<%=true%>">
    <%authed = false; %>
    <%response.sendRedirect("../../../securityError.jsp?type=_admin&type=_admin.billing");%>
</security:oscarSec>
<%
    if (!authed) {
        return;
    }
%>

<%@ page import="java.math.*, java.util.*, java.io.*, java.sql.*, oscar.*, java.net.*,ca.openosp.openo.MyDateFormat"
         errorPage="/errorpage.jsp" %>
<%@page import="ca.openosp.openo.ehrutil.MiscUtils" %>


<%@ page import="ca.openosp.openo.ehrutil.SpringUtils" %>
<%@ page import="ca.openosp.openo.billing.CA.BC.model.TeleplanS00" %>
<%@ page import="ca.openosp.openo.billing.CA.BC.dao.TeleplanS00Dao" %>
<%@ page import="ca.openosp.openo.Misc" %>

<%
    TeleplanS00Dao teleplanS00Dao = SpringUtils.getBean(TeleplanS00Dao.class);
%>

<html>
<head>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
    <link rel="stylesheet" href="billing.css">
    <title>Teleplan Reconcilliation</title>
    <script language="JavaScript">

        function popupPage(vheight, vwidth, varpage) { //open a new popup window
            var page = "" + varpage;
            windowprops = "height=" + vheight + ",width=" + vwidth + ",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes";
            var popup = window.open(page, "attachment", windowprops);
            if (popup != null) {
                if (popup.opener == null) {
                    popup.opener = self;
                }
                popup.focus();
            }
        }
    </script>
</head>

<body bgcolor="#EBF4F5" text="#000000" leftmargin="0" topmargin="0"
      marginwidth="0" marginheight="0">

<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr bgcolor="#486ebd">
        <th align='LEFT'><input type='button' name='print' value='Print'
                                onClick='window.print()'></th>
        <th align='CENTER'><font face="Arial, Helvetica, sans-serif"
                                 color="#FFFFFF">Teleplan Reconcilliation - Billed Report</font></th>
        <th align='RIGHT'><input type='button' name='close' value='Close'
                                 onClick='window.close()'></th>
    </tr>
</table>
<%
    GregorianCalendar now = new GregorianCalendar();
    int curYear = now.get(Calendar.YEAR);
    int curMonth = (now.get(Calendar.MONTH) + 1);
    int curDay = now.get(Calendar.DAY_OF_MONTH);

    String nowDate = String.valueOf(curYear) + "/" + String.valueOf(curMonth) + "/" + String.valueOf(curDay);
%>

<% String raNo = "", flag = "", plast = "", pfirst = "", pohipno = "", proNo = "";
    String filepath = "", filename = "", header = "", headerCount = "", total = "", paymentdate = "", payable = "", totalStatus = "", deposit = ""; //request.getParameter("filename");
    String transactiontype = "", providerno = "", specialty = "", account = "", patient_last = "", patient_first = "", provincecode = "", hin = "", ver = "", billtype = "", location = "";
    String servicedate = "", serviceno = "", servicecode = "", amountsubmit = "", amountpay = "", amountpaysign = "", explain = "", error = "";
    String proFirst = "", proLast = "", demoFirst = "", demoLast = "", apptDate = "", apptTime = "", checkAccount = "";


    String officeNo = request.getParameter("officeNo");
%>
<table width="100%" border="1" cellspacing="0" cellpadding="0"
       bgcolor="#EFEFEF">
    <form>
        <tr>
            <td width="5%" height="16">Office No</td>
            <td width="5%" height="16">Practitioner</td>
            <td width="5%" height="16">Service Code</td>
            <td height="16">Payment Date</td>
            <td width="5%" height="16" alight="right">Billed Amount</td>
            <td width="5%" height="16" align="right">Paid Amount</td>
            <td width="2%" height="16">EP1</td>
            <td width="2%" height="16">EP2</td>
            <td width="2%" height="16">EP3</td>
            <td width="2%" height="16">AJC1</td>
            <td width="5%" height="16">AJA1</td>
            <td width="2%" height="16">AJC2</td>
            <td width="5%" height="16">AJA2</td>
            <td width="2%" height="16">AJC3</td>
            <td width="5%" height="16">AJA3</td>
            <td width="2%" height="16">AJC4</td>
            <td width="5%" height="16">AJA4</td>
            <td width="2%" height="16">AJC5</td>
            <td width="5%" height="16">AJA5</td>
            <td width="2%" height="16">AJC6</td>
            <td width="5%" height="16">AJA6</td>
            <td width="2%" height="16">AJC7</td>
            <td width="5%" height="16">AJA7</td>
            <td width="10%" height="16">Status</td>
        </tr>
            <%
              String[] param = new String[1];          
              param[0] = Misc.forwardZero(officeNo,7);
              
              String[] param0 = new String[2];
              
              List<TeleplanS00> results = teleplanS00Dao.findByOfficeNumbers(Arrays.asList(new String[]{Misc.forwardZero(officeNo,7)}));
              int counter = 0;
              for (TeleplanS00 result:results) {   
                  counter ++;
                 account = result.getOfficeNo();
                   
          %>
        <tr>
            <td width="5%" height="16"><a
                    href="javascript: popupPage(700,750,'adjustBill.jsp?billing_no=<%=result.getOfficeNo()%>')"><%=result.getOfficeNo()%>
            </a>&nbsp;
            </td>
            <td width="5%" height="16"><%=result.getPractitionerNo()%>&nbsp;
            </td>
            <td width="5%" height="16"><%=result.getBillFeeSchedule()%>&nbsp;
            </td>
            <td height="16"><%=result.getPayment()%>&nbsp;</td>
            <td width="5%" height="16" align="right"><%=moneyFormat(result.getBillAmount())%>&nbsp;
            </td>
            <td width="5%" height="16" align=right><%=moneyFormat(result.getPaidAmount())%>
            </td>
            <td width="2%" height="16"><%=result.getExp1()%>&nbsp;
            </td>
            <td width="2%" height="16"><%=result.getExp2()%>&nbsp;
            </td>
            <td width="2%" height="16"><%=result.getExp3()%>&nbsp;
            </td>
            <td width="2%" height="16"><%=result.getAjc1()%>&nbsp;
            </td>
            <td width="5%" height="16"><%=moneyFormat(result.getAja1())%>&nbsp;
            </td>
            <td width="2%" height="16"><%=result.getAjc2()%>&nbsp;
            </td>
            <td width="5%" height="16"><%=moneyFormat(result.getAja2())%>&nbsp;
            </td>
            <td width="2%" height="16"><%=result.getAjc3()%>&nbsp;
            </td>
            <td width="5%" height="16"><%=moneyFormat(result.getAja3())%>&nbsp;
            </td>
            <td width="2%" height="16"><%=result.getAjc4()%>&nbsp;
            </td>
            <td width="5%" height="16"><%=moneyFormat(result.getAja4())%>&nbsp;
            </td>
            <td width="2%" height="16"><%=result.getAjc5()%>&nbsp;
            </td>
            <td width="5%" height="16"><%=moneyFormat(result.getAja5())%>&nbsp;
            </td>
            <td width="2%" height="16"><%=result.getAjc6()%>&nbsp;
            </td>
            <td width="5%" height="16"><%=moneyFormat(result.getAja6())%>&nbsp;
            </td>
            <td width="2%" height="16"><%=result.getAjc7()%>&nbsp;
            </td>
            <!--<td width="5%" height="16"><%=moneyFormat(result.getAja7())%>&nbsp; </td>-->
            <td width="5%" height="16"><%=result.getS00Type()%>&nbsp;
            </td>
            <td width="5%" height="16"
                align=right><%=String.valueOf(result.getLineCode()).compareTo("P") == 0 ? "Paid as billed" : String.valueOf(result.getLineCode()).compareTo("R") == 0 ? "Refusal" : String.valueOf(result.getLineCode()).compareTo("H") == 0 ? "Recycle" : ""%>
            </td>
        </tr>
            <% }
       
          if(counter == 0){%>
        <tr>
            <td colspan="23" align="center">No Records found.</td>
        </tr>
            <%}else{
                String s = ""; 
                if (counter > 1) { s = "s";}%>
        <tr>
            <td colspan="23" align="center"><%=counter%> Record<%=s%> found.
            </td>
        </tr>
            <%}
      %>


</table>


</body>
</html>
<%!
    String moneyFormat(String str) {
        String moneyStr = "0.00";
        try {
            moneyStr = new java.math.BigDecimal(str).movePointLeft(2).toString();
        } catch (Exception moneyException) {
            MiscUtils.getLogger().error("Error", moneyException);
        }
        return moneyStr;
    }
%>
