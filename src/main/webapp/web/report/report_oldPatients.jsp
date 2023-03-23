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
<h1>Old Patient List</h1>

<div class="row" ng-controller="ReportOldPatientsCtrl">
	<div class="col-md-4">
		<form role="form">
			<div class="form-group">
				<label>Provider:</label> 
				
				<div class="input-group">
					<input type="text"
						ng-model="data.providerNo" placeholder="Provider"
						uib-typeahead="pt.providerNo as pt.name for pt in searchProviders($viewValue)"
						typeahead-on-select="updateProviderNo($item, $model, $label)"
						class="form-control"/>
						<span class="input-group-addon"><span class="glyphicon glyphicon-remove" ng-click="params.providerNo='';data.providerNo=''"></span></span>
					</div>
			</div>
			<div class="form-group">
				<label for="age">Age &gt; </label>
				<input ng-model="params.age" ng-init="params.age='65'" type="text" class="form-control"/>
			</div>
  			<button type="submit" class="btn btn-default" ng-click="generateReport()">Generate Report</button>
		</form>
	</div>
</div>
