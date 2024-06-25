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

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html:html lang="en">
<security:oscarSec roleName="${ sessionScope.userrole }" objectName="_admin" rights="r" reverse="${ false }">

<head>
	<title><bean:message key="oscarMessenger.config.MessengerAdmin.title" /></title>
	<link href="${pageContext.request.contextPath}/js/jquery_css/smoothness/jquery-ui-1.10.2.custom.min.css" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="${pageContext.request.contextPath}/library/jquery/jquery-ui-1.12.1.min.js" ></script>
	<style type="text/css">
		summary{ cursor:pointer; }
		.contact-group-buttons{
			padding-top: 10px;
		}
		
		i.group-member {
			display:block;
			float:left;
			clear:right;
			width: 20px;
			margin-top:3px;
			margin-bottom: 3px;
		}
		#remote-contacts summary {
			padding 5px 10px;
			background-color: #fafafa;
			background-image: -moz-linear-gradient(top, #ffffff, #f2f2f2);
			background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#ffffff), to(#f2f2f2));
			background-image: -webkit-linear-gradient(top, #ffffff, #f2f2f2);
			background-image: -o-linear-gradient(top, #ffffff, #f2f2f2);
			background-image: linear-gradient(to bottom, #ffffff, #f2f2f2);
			background-repeat: repeat-x;
			border-top: 1px solid #d4d4d4;
			border-bottom: 1px solid #d4d4d4;
		}
		
		#addContacts .tab-content, #manageGroups .group-member-list {
			background-color: #ccc;
			border-left:#ccc thin solid;
			border-right:#ccc thin solid;
			height: auto;
			max-height: 900px;
			overflow-y: auto;
			overflow-x: hidden;
		}
		
		#addContacts .contact-entry, #manageGroups .group-member-list .contact-entry {
			background-color: white;
			margin:1px auto;
			padding:5px 0px 0px 10px;
		}
		
		span.provider-name {
			display: block;
		}
	</style>
	
	<script type="text/javascript">
		var ctx = '${pageContext.request.contextPath}';
	
		$("input:checkbox").change(function(){
			if(this.checked) 
			{
				addMember(this.value, 0);
			}
			else
			{
				removeMember(this.value, 0)
			}
		});
		
		$(".add-member-btn").click(function(){
			var groupId = this.id;
			groupId = groupId.replace("add-", '');
			var memberId = $("#add-member-id-" + groupId).val(); 
			if(memberId)
			{
				addMember(memberId, groupId)
				$(".search-provider").val('');
			}
		});
		
		$("i.group-member").click(function(){
			var memberId = this.id;
			if(memberId)
			{
				removeMember(memberId, 0);
				$(this).parent().parent().remove();
			}
		});
		
		$("#add-group-btn").click(function(){
			var groupName = $("#new-group-name").val();
			if(groupName){
				createGroup(groupName);
			}
		});
		
		$(".delete-group-btn").click(function(){
			var groupId = this.id;
			if(groupId)
			{
				groupId = groupId.replace("delete-", '');
				deleteGroup(groupId);
			}
		});

		function addMember(memberId, groupId) {
			$.post(ctx + "/oscarMessenger.do?method=add&member=" + memberId + "&group=" + groupId);
			$('#group-' + groupId).load(ctx + '/oscarMessenger.do?method=fetch #group-' + groupId);
		}
		
		function removeMember(memberId, groupId) {
			$.post(ctx + "/oscarMessenger.do?method=remove&member=" + memberId + "&group=" + groupId);
		}
		
		function createGroup(groupName) {
			$.post(ctx + "/oscarMessenger.do?method=create&groupName=" + groupName);
			$('#manageGroups').load(ctx + '/oscarMessenger.do?method=fetch #manageGroups');
		}
		
		function deleteGroup(groupId) {
			$.post(ctx + "/oscarMessenger.do?method=remove&group=" + groupId);
			$('#manageGroups').load(ctx + '/oscarMessenger.do?method=fetch #manageGroups');
		}

		$(document).ready(function(){
			// create the provider name array
			var providers = new Array();
			
			$("span.provider-name").each(function(){
				var provider = {value:this.id, label:$(this).text().trim()}
				providers.push(provider);
			});
			
			$(".search-provider").autocomplete({
		      	source: providers,
		        focus: function( event, ui ) {
		            $( this ).val( ui.item.label );
		            return false;
		        },
				select: function( event, ui ) {
				    $( this ).val( ui.item.label );
				    $( "#add-member-id-" + this.id ).val( ui.item.value );
				    return false;
		        }
		    });
		});
	</script>
	
</head>

<body>

<div class="container-fluid">

    <div class="navbar">
    <div class="navbar-inner">
    	<a class="brand" href="#">
    		Messenger Group Admin
    	</a>
	    <ul class="nav nav-tabs">
	    	<li class="active">
	    		<a href="#addContacts" data-toggle="tab">Manage Contacts</a>
	    	</li>  
	    	<li>
	    		<a href="#manageGroups" data-toggle="tab">Manage Contact Groups</a>
	    	</li>   
	    </ul>
    </div>
    </div>

	<div class="row-fluid tab-content">
		<div class="tab-pane active" id="addContacts">
			<p>Enable or disable (check or uncheck) local and remote clinic providers as a contact in the Oscar Messenger address book.</p>
			<ul class="nav nav-tabs">
				<li class="active">
					<a data-toggle="tab" href="#local-contacts" >
						Local Providers
					</a>
				</li>
				<c:if test="${ not empty remoteContacts }">
					<li>
						<a data-toggle="tab" href="#remote-contacts" >
							Remote Providers
						</a>
					</li>
				</c:if>
			</ul>
		
			<div class="tab-content">
				<div class="tab-pane active" id="local-contacts" >
					<c:forEach items="${ localContacts }" var="contact" varStatus="count">	
						<div class="row-fluid contact-entry">
							<label class="checkbox ${ count.index%2 == 0 ? 'even' : 'odd' }">
								<input type="checkbox" value="${ contact.id.compositeId }"
									${ contact.member ? 'checked="checked"' : '' } />
								<span id="${ contact.id.compositeId }" class="provider-name" >
									<c:out value="${ contact.lastName }" />, <c:out value="${ contact.firstName }" />
								</span>
								<span class="muted">
									<c:out value="${ contact.providerType }" />
								</span>
							</label>
						</div>				
					</c:forEach>					
				</div>
				
				<c:if test="${ not empty remoteContacts }">
					<div class="tab-pane" id="remote-contacts">
						<c:forEach items="${ remoteContacts }" var="location">
							<details>
								<summary>	
									<strong><c:out value="${ location.key }" /></strong>
								</summary>
								<c:forEach items="${ location.value }" var="contact" varStatus="count">	
									<div class="row-fluid contact-entry">						
										<label class="checkbox ${ count.index%2 == 0 ? 'even' : 'odd' }" >
											<input type="checkbox" value="${ contact.id.compositeId }"
												${ contact.member ? 'checked="checked"' : '' }/>
											<span id="${ contact.id.compositeId }" class="provider-name" >
												<c:out value="${ contact.lastName }" />, <c:out value="${ contact.firstName }" />
											</span>
											<span class="muted">
												<c:out value="${ contact.providerType }" />
											</span>
										</label>
									</div>							
								</c:forEach>
							</details>
						</c:forEach> 
					</div>
				</c:if>
			</div>	
		</div>
		<div class="tab-pane" id="manageGroups">
			<p>Manage Oscar Messenger contact groups</p>
			<ul class="nav nav-tabs">
				<c:forEach items="${ groups }" var="group" varStatus="count">
					<li ${ count.index eq 0 ? 'class="active"' : '' } >
						<a data-toggle="tab" href="#group-${ group.key.id }" >
							<c:out value="${ group.key.groupDesc }" />	
						</a>
					</li>
				</c:forEach>
				<li>
					<a data-toggle="tab" href="#new-group" class="muted">
						<i class="icon-plus add-group-tab" title="New Group" ></i>
					</a>
				</li>
			</ul>
		
			<div class="tab-content">
				<c:forEach items="${ groups }" var="group" varStatus="count">					
					<div class="tab-pane form-check ${ count.index eq 0 ? 'active' : '' }" id="group-${ group.key.id }">
						<div class="group-member-list">
							<c:forEach items="${ group.value }" var="member">
								<div class="row-fluid contact-entry">																	
									<label class="checkbox">								
										<i class="icon-trash group-member"
											title="Remove Contact" id="${ member.id.compositeId }" ></i>
										<span class="provider-name" >
											<c:out value="${ member.lastName }" />, <c:out value="${ member.firstName }" />
										</span>
										<span class="muted">
											<c:out value="${ member.providerType }" />
										</span>
									</label>
								</div>
							</c:forEach>
						</div>
						<div class="control-group contact-group-buttons">
							<div class="input-append">
								<div class="autocomplete">							
									<input type='text' placeholder="Last, First" id="${ group.key.id }" class="search-provider" /> 
									<input type='hidden' id="add-member-id-${ group.key.id }" value="" />
									<button id="add-${ group.key.id }" class="btn add-member-btn">Add Contact</button>	
								</div>						
							</div>
						</div>
						<div class="row-fluid" style="background-color:white;">
							<button id="delete-${ group.key.id }" class="btn delete-group-btn pull-right">Delete Group</button>	
						</div>
					</div>
				</c:forEach>
				
				<div class="tab-pane form-check" id="new-group">
					<div class="control-group">
						<div class="input-append">						
							<input type='text' placeholder="Group Name" class="group-name-input" id="new-group-name" /> 
							<button id="add-group-btn" class="btn">
								add
							</button>								
						</div>
					</div>
				</div>
				
			</div> 
		</div>			
	</div>
</div>	
</body>
</security:oscarSec>
</html:html>
