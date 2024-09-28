<%--

    Copyright (c) 2008-2012 Indivica Inc.

    This software is made available under the terms of the
    GNU General Public License, Version 2, 1991 (GPLv2).
    License details are available via "indivica.ca/gplv2"
    and "gnu.org/licenses/gpl-2.0.html".

--%>

<%@page import="org.apache.commons.lang3.StringUtils" %>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security" %>
<%
    String roleName$ = (String) session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed = true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_admin,_admin.misc" rights="r" reverse="<%=true%>">
    <%authed = false; %>
    <%response.sendRedirect("../securityError.jsp?type=_admin&type=_admin.misc");%>
</security:oscarSec>
<%
    if (!authed) {
        return;
    }
%>

<%@page import="ca.openosp.openo.hospitalReportManager.model.HRMCategory" %>
<%@page import="ca.openosp.openo.ehrutil.SpringUtils" %>
<%@page import="ca.openosp.openo.hospitalReportManager.dao.HRMCategoryDao" %>
<%@page import="ca.openosp.openo.ehrutil.MiscUtils" %>
<%
    HRMCategoryDao hrmCategoryDao = (HRMCategoryDao) SpringUtils.getBean(HRMCategoryDao.class);

    String action = request.getParameter("action");

    if ("add".equals(action)) {
        String id = request.getParameter("id");
        String categoryName = request.getParameter("categoryName");
        String subClassNameMnemonic = request.getParameter("subClassNameMnemonic");

        if (StringUtils.isEmpty(id)) {
            HRMCategory category = new HRMCategory();
            category.setCategoryName(categoryName);
            category.setSubClassNameMnemonic(subClassNameMnemonic);
            hrmCategoryDao.persist(category);
        } else {
            HRMCategory category = hrmCategoryDao.find(Integer.parseInt(id));
            category.setCategoryName(categoryName);
            category.setSubClassNameMnemonic(subClassNameMnemonic);
            hrmCategoryDao.merge(category);
        }


    } else if ("delete".equals(action)) {
        Integer id = new Integer(request.getParameter("id"));
        hrmCategoryDao.remove(id);
    } else {
        MiscUtils.getLogger().error("Missed case, action=" + action);
    }

    response.sendRedirect("hrmCategories.jsp");
%>
