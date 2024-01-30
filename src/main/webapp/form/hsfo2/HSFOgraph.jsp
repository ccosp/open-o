<%--

    Copyright (C) 2007  Heart & Stroke Foundation
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
<%@ page import="java.io.OutputStream" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="org.jfree.data.time.TimeSeries" %>
<%@ page import="org.jfree.data.time.Day" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.jfree.chart.JFreeChart" %>
<%@ page import="org.jfree.chart.ChartFactory" %>
<%@ page import="org.jfree.chart.ChartUtils" %>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
    String roleName2$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName2$%>" objectName="_form" rights="r" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../../securityError.jsp?type=_form");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>

<%
OutputStream o = response.getOutputStream();

org.jfree.data.time.TimeSeriesCollection dataset = new org.jfree.data.time.TimeSeriesCollection();

String id = (String) request.getParameter("id");
HashMap[] harray = (HashMap[]) session.getAttribute(id);


for (HashMap dat : harray){
    String name = (String) dat.get("name");
    
    HashMap[] setData = (HashMap[]) dat.get("data");
    TimeSeries s1 = new TimeSeries(name);
    for(HashMap d : setData){
       
        if(d !=null){
           s1.addOrUpdate(new Day( (Date) d.get("date")) ,
                Double.parseDouble(""+d.get("data")) );
        }
    }
    dataset.addSeries(s1);
}
String chartTitle = "";

JFreeChart chart = ChartFactory.createTimeSeriesChart(
chartTitle, 
"Date", "", 
dataset, 
true, 
true, 
false 
); 


response.setContentType("image/png"); 
ChartUtils.writeChartAsPNG(o, chart, 350, 275);
session.removeAttribute(id);
out.close(); 
%>
