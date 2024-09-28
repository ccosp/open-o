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

<%@page import="ca.openosp.openo.ehrutil.LoggedInInfo" %>
<%@page import="ca.openosp.openo.myOscar.utils.MyOscarLoggedInInfo" %>
<%@page import="ca.openosp.openo.common.service.myoscar.AllergiesManager" %>
<%@page import="ca.openosp.openo.common.service.myoscar.ImmunizationsManager" %>
<%@page import="ca.openosp.openo.common.service.myoscar.MeasurementsManager" %>
<%@page import="ca.openosp.openo.common.service.myoscar.PrescriptionMedicationManager" %>
<%@page import="ca.openosp.openo.ehrutil.WebUtils" %>
<%@page import="ca.openosp.openo.ehrutil.LocaleUtils" %>
<%@page import="ca.openosp.openo.ehrutil.MiscUtils" %>
<%@page import="ca.openosp.openo.phr.util.MyOscarUtils" %>
<%
    LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

    String parentPage = request.getParameter("parentPage");

    try {
        Integer demographicNo = Integer.parseInt(request.getParameter("demographicId"));
        String medicalDataType = request.getParameter("medicalDataType");
        MyOscarLoggedInInfo myOscarLoggedInInfo = MyOscarLoggedInInfo.getLoggedInInfo(session);

        if ("Allergies".equals(medicalDataType)) {
            AllergiesManager.sendAllergiesToMyOscar(loggedInInfo, myOscarLoggedInInfo, demographicNo);
        } else if ("Immunizations".equals(medicalDataType)) {
            ImmunizationsManager.sendImmunizationsToMyOscar(loggedInInfo, myOscarLoggedInInfo, demographicNo);
        } else if ("Measurements".equals(medicalDataType)) {
            MeasurementsManager.sendMeasurementsToMyOscar(loggedInInfo, myOscarLoggedInInfo, demographicNo);
        } else if ("Prescriptions".equals(medicalDataType)) {
            PrescriptionMedicationManager.sendPrescriptionsMedicationsToMyOscar(loggedInInfo, myOscarLoggedInInfo, demographicNo);
        } else {
            response.sendRedirect(parentPage);
        }

        WebUtils.addInfoMessage(session, LocaleUtils.getMessage(request, "ItemsHaveBeenSentToPHR"));
    } catch (Exception e) {
        MiscUtils.getLogger().error("error", e);
        WebUtils.addErrorMessage(session, LocaleUtils.getMessage(request, "UnexpectedError"));
    }

    response.sendRedirect(parentPage);
%>
