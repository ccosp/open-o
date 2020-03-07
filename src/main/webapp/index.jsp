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

<%@ page import="org.oscarehr.common.service.AcceptableUseAgreementManager"%>
<%@ page import="oscar.OscarProperties, javax.servlet.http.Cookie, oscar.oscarSecurity.CookieSecurity, oscar.login.UAgentInfo" %>
<%@ page import="java.net.URLEncoder"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix="c" %>
<%@ taglib uri="/WEB-INF/caisi-tag.tld" prefix="caisi" %>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>

<caisi:isModuleLoad moduleName="ticklerplus">
	<c:if test="${ not empty sessionScope.user }">
		<c:redirect url="${ pageContext.request.contextPath }/provider/providercontrol.jsp" />
	</c:if>  
</caisi:isModuleLoad>

<c:set var="AcceptableUseAgreementManager" value="${ org.oscarehr.common.service.AcceptableUseAgreementManager }" scope="request" />

<%
pageContext.setAttribute("OscarProperties", OscarProperties.getInstance());

// clear old cookies
Cookie prvCookie = new Cookie(CookieSecurity.providerCookie, "");
prvCookie.setPath("/");
prvCookie.setMaxAge(0);
response.addCookie(prvCookie);

/* String econsultUrl = props.getProperty("backendEconsultUrl"); */

//Gets the request URL
StringBuffer oscarUrl = request.getRequestURL();
//Determines the initial length by subtracting the length of the servlet path from the full url's length
Integer urlLength = oscarUrl.length() - request.getServletPath().length();
//Sets the length of the URL, found by subtracting the length of the servlet path from the length of the full URL, that way it only gets up to the context path
oscarUrl.setLength(urlLength);
%>

<jsp:useBean id="LoginResourceBean" beanName="oscar.login.LoginResourceBean" type="oscar.login.LoginResourceBean" scope="page" ></jsp:useBean>

<html:html locale="true">
    <head>
    <title>
	    <c:choose>
	    	<c:when test="${ not empty OscarProperties['logintitle'] }">
	    		<c:out value="${ OscarProperties['logintitle'] }" />
	    	</c:when>
	    	<c:otherwise>
	    		<bean:message key="loginApplication.title"/>
	    	</c:otherwise>
	    </c:choose>
    </title>
    
	<c:set var="login_error" value="" scope="page" />
	
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/images/Oscar.ico" />
	<link href='${pageContext.request.contextPath}/css/Roboto.css' rel='stylesheet' type='text/css' />
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.1.min.js" ></script>

    <script type="text/javascript">
    
        function showHideItem(id){
            if(document.getElementById(id).style.display == 'none')
            {
                document.getElementById(id).style.display = 'block';
            }
            else
           	{
                document.getElementById(id).style.display = 'none';
           	}
        }
        
		  <!-- hide
		  function setfocus() {
		    document.loginForm.username.focus();
		    document.loginForm.username.select();
		  }
		  function popupPage(vheight,vwidth,varpage) {
		    var page = "" + varpage;
		    windowprops = "height="+vheight+",width="+vwidth+",location=no,scrollbars=yes,menubars=no,toolbars=no,resizable=yes";
		    var popup=window.open(page, "gpl", windowprops);
		  }
		  -->
  			function addStartTime() {
            	document.getElementById("oneIdLogin").href += (Math.round(new Date().getTime() / 1000).toString());
			}


			function enhancedOrClassic(choice) {
				document.getElementById("loginType").value = choice;
			}
    </script>
       
    <style type="text/css">
            body { 
               margin: 0;
				font-family: 'Roboto', Helvetica, Arial, sans-serif;
				font-size: 16px;
				color: #333333;
				background-color: #ffffff;
            }
            
            * {
			    -webkit-box-sizing: border-box;
			    -moz-box-sizing: border-box;
			    box-sizing: border-box;
			}
            
            h1 {
                font-size: 38px;
		    	font-weight: 300;
		    }
		    
		    button, input, optgroup, select, textarea {
			    margin: 0;
			    font: inherit;
			    color: inherit;
			}
		    
		    input {
			    line-height: normal;
			}
            
            button, input, select, textarea {
			    font-family: inherit;
			    font-size: inherit;
			    line-height: inherit;
			}
			
			.heading, .loginContainer {
				text-align: center;
			}
			
			.powered {
				margin-right: auto;
				margin-left: auto;
			}
			
			.powered .details {
				text-align: right;
			    margin: 10px 20px 0 0;
			    float: left;
			    width: 35%;
			}
			
            .loginContainer {
            	padding: 30px 15px;
				margin-right: auto;
				margin-left: auto;
            }
            .auaContainer {
				margin-right: auto;
				margin-left: auto;
				text-align:center;
				z-index:3;width:70%;
            }
            
            .panel {
                margin-bottom: 20px;
			    background-color: #fff;
			    border: 1px solid transparent;
			    border-radius: 4px;
			    -webkit-box-shadow: 0 1px 1px rgba(0,0,0,.05);
			    box-shadow: 0 1px 1px rgba(0,0,0,.05);
			}
			
			.panel-body {
			    padding: 10px 40px 40px;
			}

			.panel-danger {
				border-color: #ebccd1;
			}
			.panel-danger > .panel-heading {
				color: #a94442;
				background-color: #f2dede;
				border-color: #ebccd1;
			}
			.panel-danger > .panel-heading + .panel-collapse > .panel-body {
				border-top-color: #ebccd1;
			}
			.panel-danger > .panel-heading .badge {
				color: #f2dede;
				background-color: #a94442;
			}
			.panel-danger > .panel-footer + .panel-collapse > .panel-body {
				border-bottom-color: #ebccd1;
			}
            .panel-default {
           		border-color: #ddd;
            }
            
			.form-group {
			    margin-bottom: 15px;
			}
			
			label {
			    display: inline-block;
			    max-width: 100%;
			    margin-bottom: 5px;
			    font-weight: 700;
			}
			
			.form-control {
			    display: block;
			    width: 100%;
			    height: 34px;
			    padding: 6px 12px;
			    font-size: 14px;
			    line-height: 1.42857143;
			    color: #555;
			    background-color: #fff;
			    background-image: none;
			    border: 1px solid #ccc;
			    border-radius: 4px;
			    -webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
			    box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
			    -webkit-transition: border-color ease-in-out .15s,-webkit-box-shadow ease-in-out .15s;
			    -o-transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
			    transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
			}
			
			.has-error .form-control {
			    border-color: #a94442;
			    -webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
			    box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
			}
			
			.btn {
			    display: inline-block;
			    padding: 6px 12px;
			    margin-bottom: 0;
			    font-size: 14px;
			    font-weight: 400;
			    line-height: 1.42857143;
			    text-align: center;
			    white-space: nowrap;
			    vertical-align: middle;
			    -ms-touch-action: manipulation;
			    touch-action: manipulation;
			    cursor: pointer;
			    -webkit-user-select: none;
			    -moz-user-select: none;
			    -ms-user-select: none;
			    user-select: none;
			    background-image: none;
			    border: 1px solid transparent;
			    border-radius: 4px;
			}
			
			.btn-primary {
			    color: #fff;
			    background-color: #53b848;
			    border-color: #3f9336;
			}
			
			.btn-block {
			    display: block;
			    width: 100%;
			}
			
			button, html input[type=button], input[type=reset], input[type=submit] {
			    -webkit-appearance: button;
			    cursor: pointer;
			}
			
			.btn.active.focus, .btn.active:focus, .btn.focus, .btn:active.focus, .btn:active:focus, .btn:focus {
			    outline: thin dotted;
			    outline: 5px auto -webkit-focus-ring-color;
			    outline-offset: -2px;
			}
			
			.btn.focus, .btn:focus, .btn:hover {
			    color: #333;
			    text-decoration: none;
			}
			
			.btn.active, .btn:active {
			    background-image: none;
			    outline: 0;
			    -webkit-box-shadow: inset 0 3px 5px rgba(0,0,0,.125);
			    box-shadow: inset 0 3px 5px rgba(0,0,0,.125);
			}
			
			.btn[disabled="disabled"]:hover {
				cursor: not-allowed;
			}
			
			.btn-primary.focus, .btn-primary:focus {
			    color: #fff;
			    background-color: #3f9336;
			    border-color: #3f9336;
			}
			
			.btn-primary:hover {
			    color: #fff;
			    background-color: #3f9336;
			    border-color: #3f9336;
			}
			
			.btn-primary.active, .btn-primary:active, .open>.dropdown-toggle.btn-primary {
			    color: #fff;
			    background-color: #3f9336;
			    border-color: #3f9336;
			}
			
			.btn-primary.active, .btn-primary:active, .open>.dropdown-toggle.btn-primary {
			    background-image: none;
			}
			
			input[type=button].btn-block, input[type=reset].btn-block, input[type=submit].btn-block {
			    width: 100%;
				margin-bottom: 10px;
			}
			
			.btn.active.focus, .btn.active:focus, .btn.focus, .btn:active.focus, .btn:active:focus, .btn:focus {
			    outline: thin dotted;
			    outline: 5px auto -webkit-focus-ring-color;
			    outline-offset: -2px;
			}
			
			.btn-primary.active.focus, .btn-primary.active:focus, .btn-primary.active:hover, .btn-primary:active.focus, .btn-primary:active:focus, .btn-primary:active:hover, .open>.dropdown-toggle.btn-primary.focus, .open>.dropdown-toggle.btn-primary:focus, .open>.dropdown-toggle.btn-primary:hover {
			    color: #fff;
			    background-color: #3f9336;
			    border-color: #3f9336;
			}
      
            span#buildInfo{
                float: right;
                color:#000000;
                font-size: xx-small;
                text-align: right;
                position: absolute;
                right: 0;
                padding:6px;
            }
            
			span.extrasmall{
			    font-size: small;
			    float: left;
			    margin: 10px 0 20px;
			}

			.topbar {
				margin:10px auto;
			}

			.clinic-text {
				display: inline;
				font-weight: 400;
			}
			
            @media (min-width: 768px) {
				.loginContainer, .powered {
					width: 450px;
				}
			}
			
			@media (min-width: 992px) {
				
			}
			
			@media (min-width: 1200px) {
				
			}
			
			.oneIdLogin {
				background-color: #000;
				width: 60%;
				height: 34px;
				margin: 0px auto;
			}
			
			.oneIdLogo {
				background-color: transparent;
				background: url("${pageContext.request.contextPath}/images/oneId/oneIDLogo.png");
				border: none;
				display: inline-block;
				float: left;
      			vertical-align: bottom;
      			width: 70px;
      			height: 16px;
			}
			
			.oneIDText {
				display: inline-block;
				float: left;
				padding-left: 10px
			}
        </style>
        
 </head>
    
 <body onLoad="setfocus()">
 
        <div class="topbar">
			<div class="clinic-text">
				<c:out value="${ OscarProperties['login_screen_header_text'] }" />		
			</div>
            <span id="buildInfo">
            	<c:out value="${ OscarProperties.buildTag }" />
            </span>
        </div>
        
        <div class="heading">
        <!--  clinic logo  -->
        	<img src="${ pageContext.request.contextPath }/loginResource/clinicLogo.png" width="450px" style="margin: 0px auto;" 
        		onerror="this.style.display='none'"/>
        		
        	<c:out value="${ clinicText }" />
        </div>
        
        <div class="loginContainer">
	        <div class="panel panel-default">

			    <div class="panel-heading">
			    <!-- Oscar logo -->
		        	<img title="OSCAR EMR Login" src="${pageContext.request.contextPath}/images/Logo.png" 
		        		width="300" style="margin:0px;margin-top:25px;" 
		        		onerror="document.getElementById('default_logo').style.display='block'; this.style.display='none'; " />
		        		
	        		<h2 id="default_logo" style="display:none;">
	        			OSCAR EMR Login
	        		</h2>
		        </div>
	        	
	        	<h4>
		        	<c:choose>
			        	<c:when test="${ not empty email }">
							Hello <c:out value="${ email }" />
							<br />
							Please login with your OSCAR credentials to link your accounts.
			        	</c:when>
			        	<c:otherwise>
			        		<c:out value="${ errorMessage }" />
			        	</c:otherwise>
		        	</c:choose>	
	        	</h4>
	        	
	        	<c:if test='${ login eq "failed" }' >
        			<c:set var="login_error" value="has-error" scope="page" />
        			<div class="panel panel-danger">
						<div class="panel-heading">
							Login failed. Username or Password is incorrect.
						</div>
					</div>
	        	</c:if>

    			<div class="panel-body">
    			    <div class="leftinput" ng-app="indexApp" ng-controller="indexCtrl">
    				    <html:form action="login" >
    				    
    							<div class="form-group ${ login_error }"> 
    	                        	<input type="text" name="username" placeholder="Enter your username" 
    	                        	value="" size="15" maxlength="15" autocomplete="off" 
    	                        	class="form-control" ng-model="username"/>
    	                        </div>
    	                        
    	                        <div class="form-group ${ login_error }">               
    	                        	<input type="password" name="password" placeholder="Enter your password" 
    	                        	value="" size="15" maxlength="32" autocomplete="off" 
    	                        	class="form-control" ng-model="password"/>
    	                        </div>
    	                        
    	                        <div class="form-group ${ login_error }">
    	                        	<input type="password" name="pin" placeholder="Enter your PIN" value="" 
    	                        	size="15" maxlength="15" autocomplete="off" class="form-control" ng-model="pin"/>
    	                        	<span class="extrasmall">
    		                            <bean:message key="loginApplication.formCmt"/>
    		                        </span>
    	                        </div>
    	                        
    	                        <input type="hidden" id="oneIdKey" name="nameId" value="${ nameId }"/>
    	                        <input type="hidden" id="email" name="email" value="${ email }"/>
								<input type="hidden" id="loginType" name="loginType" value=""/>
    	                        <input type=hidden name='propname' value='<bean:message key="loginApplication.propertyFile"/>' />
								
								<div id="buttonContainer">	
									<input class="btn btn-oscar btn-primary btn-block" name="submit" type="submit" 
										onclick="enhancedOrClassic('C');" value="Login" />
								</div>
								
    						</html:form>
    						
							<oscar:oscarPropertiesCheck property="oneid.enabled" value="true" defaultVal="false">
    							<a href="${ backendEconsultUrl }/SAML2/login?oscarReturnURL=<c:url value='${ requestURL }/ssoLogin.do' />?loginStart=" 
	    							id="oneIdLogin" onclick="addStartTime()" class="btn btn-primary btn-block oneIDLogin" >
	    								<span class="oneIDLogo"></span>
	    								<span class="oneIdText">ONE ID Login</span>
    							</a>
							</oscar:oscarPropertiesCheck>
    			          
	    			         <c:if test="${ AcceptableUseAgreementManager['hasAUA'] }">
	    			            <span class="extrasmall">
		                        	<bean:message key="global.aua" /> &nbsp; 
		                        	<a href="javascript:void(0);" onclick="showHideItem('auaText');">
		                        		<bean:message key="global.showhide"/>
		                        	</a>
		                        </span>
	    			         </c:if> 
    			                
<%--                         <%if (AcceptableUseAgreementManager.hasAUA() && !AcceptableUseAgreementManager.auaAlwaysShow()){ %>
	                        <span class="extrasmall">
	                        	<bean:message key="global.aua" /> &nbsp; <a href="javascript:void(0);" 
	                        	onclick="showHideItem('auaText');"><bean:message key="global.showhide"/></a>
	                        </span>
                        <%} %>   --%>     
			        </div>
			  	</div>
			</div>
		</div>
		<c:choose>
			<c:when test="${ AcceptableUseAgreementManager['hasAUA'] or AcceptableUseAgreementManager['auaAlwaysShow'] }">
				<div id="auaText" class="auaContainer" style="display:none;" >
					<div class="panel panel-default">
						<c:out value="${ AcceptableUseAgreementManager['getAUAText'] }" />
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<script type="text/javascript">document.getElementById('auaText').style.display = 'block';</script>
			</c:otherwise>
		</c:choose>
<%-- 		<%if (AcceptableUseAgreementManager.hasAUA() || AcceptableUseAgreementManager.auaAlwaysShow()){ %>
			<div id="auaText" class="auaContainer" style="display:none;" >
				<div class="panel panel-default">
					<%=AcceptableUseAgreementManager.getAUAText()%>
				</div>
			</div>
		<%}
		if (AcceptableUseAgreementManager.auaAlwaysShow()) { %>
			<script type="text/javascript">document.getElementById('auaText').style.display = 'block';</script>
		<% } %> --%>

		<div class="powered">
			<span class="details">
				<div>Powered</div>
				<div>by</div>
			</span>
			<a href="${ OscarProperties.supportLink }" >
 				<img width="150px" src="${ pageContext.request.contextPath }/loginResource/supportLogo.png">
 				<c:out value="${ OscarProperties.supportText }" />
 			</a>
		</div>
      
    </body>
</html:html>
