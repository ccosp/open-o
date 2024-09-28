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
<%@page import="ca.openosp.openo.ehrutil.LoggedInInfo" %>
<%@page import="ca.openosp.openo.ehrutil.MiscUtils" %>
<%@page import="ca.openosp.openo.ehrutil.DigitalSignatureUtils" %>
<%@page import="java.util.Enumeration" %>
<%@page import="ca.openosp.openo.PMmodule.web.ManageConsentAction" %>
<%@page import="ca.openosp.openo.ehrutil.WebUtils" %>
<%@page import="ca.openosp.openo.ehrutil.SpringUtils" %>
<%@page import="org.oscarehr.common.dao.*" %>
<%@page import="org.oscarehr.common.model.*" %>
<%@page import="ca.openosp.openo.ehrutil.SessionConstants" %>

<%
    LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
    Integer currentDemographicId = new Integer(request.getParameter("demographicId"));
    ManageConsentAction manageConsentAction = new ManageConsentAction(loggedInInfo, currentDemographicId);
    manageConsentAction.setSignatureRequestId(request.getParameter(DigitalSignatureUtils.SIGNATURE_REQUEST_ID_KEY));
    manageConsentAction.setExcludeMentalHealthData(WebUtils.isChecked(request, "excludeMentalHealth"));
    manageConsentAction.setConsentStatus(request.getParameter("consentStatus"));
    manageConsentAction.setSignatureStatus(request.getParameter("signatureStatus"));
    manageConsentAction.setExpiry(request.getParameter("consentExpiry"));

    @SuppressWarnings("unchecked")
    Enumeration<String> e = request.getParameterNames();
    while (e.hasMoreElements()) {
        String key = e.nextElement();
        // must check for "on" because some versions of IE submit "off" or ""
        if (key.startsWith("consent.") && "on".equals(request.getParameter(key))) manageConsentAction.addExclude(key);
    }

    manageConsentAction.storeAllConsents();
    response.sendRedirect(request.getContextPath() + "/ca/openosp/openo/PMmodule/ClientManager.do?id=" + currentDemographicId);
%>
