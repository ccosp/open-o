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

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ page
        import="java.util.*, openo.oscarEncounter.oscarMeasurements.data.MeasurementMapConfig, openo.OscarProperties, openo.util.StringUtils,oscar.oscarEncounter.oscarMeasurements.bean.*" %>
<%@ page import="openo.oscarEncounter.oscarMeasurements.bean.EctMeasurementTypesBean" %>
<%@ page import="openo.oscarEncounter.oscarMeasurements.bean.EctMeasurementTypesBeanHandler" %>

<%

    MeasurementMapConfig mmc = new MeasurementMapConfig();

    EctMeasurementTypesBeanHandler hd = new EctMeasurementTypesBeanHandler();
    Vector<EctMeasurementTypesBean> vec = hd.getMeasurementTypeVector();

    String loinc = request.getParameter("loinc");
%>


<link rel="stylesheet" type="text/css"
      href="../../oscarMDS/encounterStyles.css">

<html>
<head>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
    <title>Measurement Mapping Configuration</title>

    <script type="text/javascript" language=javascript>

        function popupStart(vheight, vwidth, varpage, windowname) {
            var page = varpage;
            windowprops = "height=" + vheight + ",width=" + vwidth + ",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes";
            var popup = window.open(varpage, windowname, windowprops);
        }

        function newWindow(varpage, windowname) {
            var page = varpage;
            windowprops = "fullscreen=yes,toolbar=yes,directories=no,resizable=yes,dependent=yes,scrollbars=yes,location=yes,status=yes,menubar=yes";
            var popup = window.open(varpage, windowname, windowprops);
        }

        function reloadPage() {
            document.CONFIG.action = 'addMeasurementMap.jsp';
            return true;
        }


        <%String outcome = request.getParameter("outcome");
        if (outcome != null){
            if (outcome.equals("success")){
                %>
        alert("Successfully updated the measurement mappings");
        <%
    }else if (outcome.equals("failedcheck")){
        %>
        alert("Unable to update measurement mappings: A message is already mapped to the specified code for that message type");
        <%
    }else{
        %>
        alert("Failed to update the measurement mappings");
        <%
    }
}%>

    </script>
    <link rel="stylesheet" type="text/css" media="all" href="../share/css/extractedFromPages.css"/>
</head>

<body>
<form method="post" name="CONFIG" action="AddMeasurementMap.do">
    <table width="100%" height="100%" border="0">
        <tr class="MainTableTopRow">
            <td class="MainTableTopRow" colspan="9" align="left">
                <table width="100%">
                    <tr>
                        <td align="left"><input type="button" value=" <bean:message key="global.btnClose"/> "
                                                onClick="window.close()"></td>
                        <td align="right">
                            <oscar:help keywords="measurement" key="app.top1"/> |
                            <a href="javascript:popupStart(300,400,'../About.jsp')"><bean:message
                                    key="global.about"/></a> |
                            <a href="javascript:popupStart(300,400,'../License.jsp')"><bean:message
                                    key="global.license"/></a>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td valign="top">
                <center>
                    <table width="80%" border="1">
                        <tr>
                            <td colspan="2" valign="bottom" class="Header">Map Unmapped Identifier Codes</td>

                        </tr>
                        <tr>
                            <td class="Cell" width="20%">Select Measurement:</td>
                            <td class="Cell">
                                <select name="identifier">
                                    <option value="0">None Selected</option>

                                    <% for (EctMeasurementTypesBean measurementTypes : vec) { %>
                                    <option value="<%=measurementTypes.getType()%>,FLOWSHEET,<%=measurementTypes.getTypeDisplayName()%>"><%=measurementTypes.getTypeDisplayName()%>
                                        (<%=measurementTypes.getType()%>)
                                    </option>
                                    <% } %>
                                </select>
                            </td>

                        </tr>
                        <tr>
                            <td class="Cell" width="20%">To Map to Loinc Code : <%=loinc%>
                            </td>
                            <td class="Cell"><input type="hidden" name="loinc_code" value="<%=loinc%>"/></td>
                        </tr>
                        <tr>
                            <td colspan="2" class="Cell" align="center"><input
                                    type="submit" value=" Update Measurement Mapping "> <input
                                    type="button" value=" Add New Loinc Code "
                                    onclick="javascript:popupStart('300','600','newMeasurementMap.jsp','Add_New_Loinc_Code')">
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" class="Cell" align="center">NOTE: <a
                                    href="javascript:newWindow('http://www.regenstrief.org/medinformatics/loinc/relma','RELMA')">It
                                is suggested that you use the RELMA application to help determine
                                correct loinc codes.</a></td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <ul>
                                    <% for (EctMeasurementTypesBean measurementTypes : vec) { %>
                                    <li><%=measurementTypes.getTypeDisplayName()%> (<%=measurementTypes.getType()%>)
                                    </li>
                                    <% } %>
                                </ul>
                            </td>
                        </tr>
                    </table>
                </center>
            </td>
        </tr>
    </table>
</form>
</body>
</html>
