/*

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

*/
angular.module("providerServices", [])
	.service("providerService", function ($http,$q,$log) {
		return {
		apiPath:'../ws/rs/providerService',
		configHeaders: {headers: {"Content-Type": "application/json","Accept":"application/json"}},
		configHeadersWithCache: {headers: {"Content-Type": "application/json","Accept":"application/json"},cache: true},
        getMe: function(){
        	var deferred = $q.defer();
        	 $http.get(this.apiPath+'/provider/me',this.configHeaders).then(function(response){
            	console.log("me "+response.data);
            	deferred.resolve(response.data);
            },function(){
            	console.log("error fetching myself");
            	deferred.reject("An error occured while getting user data");
            });
     
          return deferred.promise;
        },
		getProvider: function(id){
			var deferred = $q.defer();
			$http.get(this.apiPath+'/provider/'+id,this.configHeaders).then(function (response){
				console.log("get provider: "+response.data);
				deferred.resolve(response.data);
			},function(){
				console.log("error fetching provider "+id);
				deferred.reject("An error occured while getting user data");
			});
			
			return deferred.promise;
		},
        searchProviders: function (filter) {
        	var deferred = $q.defer();
            
            $http({
                url: this.apiPath+'/providers/search',
                method: "POST",
                data: JSON.stringify(filter),
                headers: {'Content-Type': 'application/json'}
             }).then(function (response){
            	 deferred.resolve(response.data.content);
             },function (data, status, headers, config) {
            	 deferred.reject("An error occured while fetching provider list");
             });

          return deferred.promise;
        },
        getSettings: function () {
        	var deferred = $q.defer();
            
            $http({
                url: this.apiPath+'/settings/get',
                method: "GET"
             }).then(function (response){
            	 deferred.resolve(response.data.content[0]);
             },function (data, status, headers, config) {
            	 deferred.reject("An error occured while fetching provider settings");
             });

          return deferred.promise;
        },
        saveSettings: function (providerNo,settings) {
        	var deferred = $q.defer();
            
            $http({
                url: this.apiPath+'/settings/'+providerNo+'/save',
                method: "POST",
                data: JSON.stringify(settings),
                headers: {'Content-Type': 'application/json'}
             }).then(function (response){
            	 deferred.resolve(response.data);
             },function (data, status, headers, config) {
            	 deferred.reject("An error occured while saving settings");
             });

          return deferred.promise;
        },
        getActiveTeams: function () {
        	var deferred = $q.defer();
            
            $http({
                url: this.apiPath+'/getActiveTeams',
                method: "GET"
             }).then(function (response){
            	 deferred.resolve(response.data.content);
             },function (data, status, headers, config) {
            	 deferred.reject("An error occured while fetching provider teams");
             });

          return deferred.promise;
        },
        getAllActiveProviders: function(){
        		var deferred = $q.defer();
            
            $http({
                url: this.apiPath+'/providers_json',
                method: "GET"
             }).then(function (data, status, headers, config) {
            	 	deferred.resolve(data.data.content);
             },function (data, status, headers, config) {
            	 	deferred.reject("An error occured while fetching provider teams");
             });

            return deferred.promise;
        },
        suggestProviderNo: function(){
        		var deferred = $q.defer();
            
            $http({
                url: this.apiPath+'/suggestProviderNo',
                method: "GET"
             }).then(function (data, status, headers, config) {
            	 	deferred.resolve(data.data);
             },function (data, status, headers, config) {
            	 	deferred.reject("An error occured while fetching provider teams");
             });

            return deferred.promise;
        }
    };
});