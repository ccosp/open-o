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
<%
    if (session.getValue("user") == null) response.sendRedirect("../../logout.jsp");
%>
<%@page contentType="text/xml" %>
<%@page
        import="oscar.oscarEncounter.oscarMeasurements.bean.*,oscar.oscarEncounter.oscarMeasurements.data.*" %>
<%@ page import="openo.oscarEncounter.oscarMeasurements.bean.EctMeasurementTypesBean" %>
<%@ page import="openo.oscarEncounter.oscarMeasurements.bean.EctMeasurementTypeBeanHandler" %>
<%@ page import="openo.oscarEncounter.oscarMeasurements.data.ExportMeasurementType" %>
<%
    String mstring = request.getParameter("mType");
    String export = "<ERROR>";
    if (mstring != null) {
        EctMeasurementTypeBeanHandler mType = new EctMeasurementTypeBeanHandler();
        EctMeasurementTypesBean measurementTypesBean = mType.getMeasurementType(mstring);

        ExportMeasurementType emt = new ExportMeasurementType();
        export = emt.export(measurementTypesBean);
    }
%><%=export%>
