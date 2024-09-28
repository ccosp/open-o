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

<%@page import="ca.openosp.openo.common.model.ProfessionalSpecialist" %>


<tr>

    <td class="tite4">

        CC:

        <select name="docName" id="docName">
            <%
                java.util.List<ProfessionalSpecialist> dc = (java.util.List<ProfessionalSpecialist>) request.getAttribute("professionalSpecialists");
                for (int i = 0; i < dc.size(); i++) {
                    ProfessionalSpecialist lb = (ProfessionalSpecialist) dc.get(i);
            %>
            <option value="<%=lb.getId() %>"><%=lb.getLastName() %>,<%=lb.getFirstName() %>
            </option>
            <%} %>
        </select>
        <input type="button" value="add" onclick="addCCName()">
    </td>


    <td class="tite3"><input style="with:100%" name="ext_cc" value="<%=request.getAttribute("requestCc") %>"></td>

</tr>
